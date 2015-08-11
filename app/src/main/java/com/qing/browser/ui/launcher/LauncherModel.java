/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qing.browser.ui.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.nostra13.universalimageloader.utils.StorageUtils;
import com.qing.browser.R;
import com.qing.browser.net.Attach;
import com.qing.browser.net.ImageLoadUtil;
import com.qing.browser.utils.IOUtils;
import com.qing.browser.utils.Tools;

/**
 * Maintains in-memory state of the Launcher. It is expected that there should
 * be only one LauncherModel object held in a static. Also provide APIs for
 * updating the database state for the Launcher.
 */
public class LauncherModel {
	static final boolean DEBUG_LOADERS = true;
	static final boolean PROFILE_LOADERS = false;
	static final String TAG = "Launcher.Model";

	private int mBatchSize; // 0 is all apps at once
	private int mAllAppsLoadDelay; // milliseconds between batches

	private final LauncherApplication mApp;
	private final Object mLock = new Object();
	private DeferredHandler mHandler = new DeferredHandler();
	private Loader mLoader = new Loader();

	// We start off with everything not loaded. After that, we assume that
	// our monitoring of the package manager provides all updates and we never
	// need to do a requery. These are only ever touched from the loader thread.
	private boolean mWorkspaceLoaded;
	private boolean mAllAppsLoaded;

	private boolean mBeforeFirstLoad = true; // only access this from main
												// thread
	private WeakReference<Callbacks> mCallbacks;

	private final Object mAllAppsListLock = new Object();

	private Bitmap mDefaultIcon;

	public static boolean is_runing_Loader = true;

	public interface Callbacks {
		public int getCurrentWorkspaceScreen();

		public void startBinding(int flag);

		public void bindDockBarItems(ArrayList<ItemInfo> shortcuts, int start,
				int end);

		public void bindMiddleItems(ArrayList<ItemInfo> shortcuts, int start,
				int end);

		public void bindRightItems(ArrayList<ItemInfo> shortcuts, int start,
				int end);

		public void bindFolders(
				HashMap<HashMap<Long, Integer>, FolderInfo> folders);

		public void finishBindingItems();

		public boolean isAllAppsVisible();
	}

	LauncherModel(LauncherApplication app) {
		mApp = app;
	}

	/**
	 * Adds an item to the DB if it was not created previously, or move it to a
	 * new <container, screen, cellX, cellY>
	 */
	static void addOrMoveItemInDatabase(Context context, ItemInfo item,
			long container, int screen, int itemindex) {
		if (item.container == ItemInfo.NO_ID) {
			// From all apps
			addItemToDatabase(context, item, container, screen, itemindex,
					false);
		} else {
			if (LauncherModel.QueryFolderDuplicate(context, item.screen,
					container, true)) {
				// From somewhere else
				moveItemInDatabase(context, item, container, screen, itemindex);
			}

		}
	}

	/**
	 * Move an item in the DB to a new <container, screen, cellX, cellY>
	 */
	static void moveItemInDatabase(Context context, ItemInfo item,
			long container, int screen, int itemindex) {
		item.container = container;
		item.screen = screen;
		item.itemIndex = itemindex;

		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		values.put(LauncherSettings.Favorites.CONTAINER, item.container);
		values.put(LauncherSettings.Favorites.ITEM_INDEX, item.itemIndex);
		values.put(LauncherSettings.Favorites.SCREEN, item.screen);

		cr.update(LauncherSettings.Favorites.getContentUri(item.id, true),
				values, null, null);
	}

	/**
	 * Find a folder in the db, creating the FolderInfo if necessary, and adding
	 * it to folderList.
	 */
	FolderInfo getFolderById(Context context,
			HashMap<HashMap<Long, Integer>, FolderInfo> folderList, long id,
			int screen) {
		final ContentResolver cr = context.getContentResolver();
		Cursor c = cr
				.query(LauncherSettings.Favorites.CONTENT_URI,
						null,
						"_id=? and (itemType=?)",
						new String[] {
								String.valueOf(id),
								String.valueOf(LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER) },
						null);

		try {
			if (c.moveToFirst()) {
				final int itemTypeIndex = c
						.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
				final int titleIndex = c
						.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
				final int containerIndex = c
						.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
				final int screenIndex = c
						.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);

				FolderInfo folderInfo = null;
				switch (c.getInt(itemTypeIndex)) {
				case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
					folderInfo = findOrMakeUserFolder(folderList, id, screen);
					break;
				}

				folderInfo.title = c.getString(titleIndex);
				folderInfo.id = id;
				folderInfo.container = c.getInt(containerIndex);
				folderInfo.screen = c.getInt(screenIndex);

				return folderInfo;
			}
		} finally {
			c.close();
		}

		return null;
	}

	public static void addItemToDatabase(Context context,
			UserFolderInfo userFolderInfo, boolean notify) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		values.put(LauncherSettings.Favorites.SHORTID, userFolderInfo.shortid);
		values.put(LauncherSettings.Favorites.CONTAINER,
				userFolderInfo.container);
		values.put(LauncherSettings.Favorites.SCREEN, userFolderInfo.screen);
		values.put(LauncherSettings.Favorites.ICON_TYPE, "0");
		// values.put(LauncherSettings.Favorites.ICON_RESOURCE, "");
		values.put(LauncherSettings.Favorites.ITEM_INDEX,
				userFolderInfo.itemIndex);
		values.put(LauncherSettings.Favorites.ITEM_TYPE,
				userFolderInfo.itemType);
		values.put(LauncherSettings.Favorites.TITLE,
				userFolderInfo.title.toString());
		values.put(LauncherSettings.Favorites.USER_TYPE,
				userFolderInfo.userType);

		Uri result = cr.insert(notify ? LauncherSettings.Favorites.CONTENT_URI
				: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
				values);
	}

	public static void addOrUpdateItemToDatabase(Context context,
			ShortcutInfo shortcutInfo, boolean notify) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		String shortid = String.valueOf(shortcutInfo.shortid);
		String screen = String.valueOf(shortcutInfo.screen);
		String container = String.valueOf(shortcutInfo.container);
		String type = String.valueOf(shortcutInfo.itemType);

		values.put(LauncherSettings.Favorites.SHORTID, shortcutInfo.shortid);
		values.put(LauncherSettings.Favorites.CONTAINER, shortcutInfo.container);
		values.put(LauncherSettings.Favorites.SCREEN, shortcutInfo.screen);
		values.put(LauncherSettings.Favorites.ICON_TYPE, shortcutInfo.iconType);
		values.put(LauncherSettings.Favorites.ITEM_INDEX,
				shortcutInfo.itemIndex);
		values.put(LauncherSettings.Favorites.ITEM_TYPE, shortcutInfo.itemType);
		values.put(LauncherSettings.Favorites.TITLE,
				shortcutInfo.title.toString());
		values.put(LauncherSettings.Favorites.URL, shortcutInfo.url);
		values.put(LauncherSettings.Favorites.ICON_RESOURCE,
				shortcutInfo.iconResource);
		values.put(LauncherSettings.Favorites.USER_TYPE,
				LauncherSettings.Favorites.USER_TYPE_SERVER);
		values.put(LauncherSettings.Favorites.UPDATE_FLAG,
				LauncherSettings.Favorites.UPDATE_FLAG_UPDATE);
		values.put(LauncherSettings.Favorites.ICON_URL, shortcutInfo.iconUrl);
		// writeBitmap(values, shortcutInfo.getIcon());

		String where = LauncherSettings.Favorites.SHORTID + " = ?" + " and "
				+ LauncherSettings.Favorites.SCREEN + " = ?" + " and "
				+ LauncherSettings.Favorites.CONTAINER + " = ?" + " and "
				+ LauncherSettings.Favorites.ITEM_TYPE + " = ?";
		Cursor cursor = null;
		try {
			cursor = cr.query(
					LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
					null, where, new String[] { shortid, screen, container,
							type }, null);
			if (cursor.moveToFirst()) {
				// 更新

			} else {
				Uri result = cr
						.insert(notify ? LauncherSettings.Favorites.CONTENT_URI
								: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
								values);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public static void deleteMiddleItemToDatabase(Context context,
			boolean notify) {
		final ContentResolver cr = context.getContentResolver();
		cr.delete(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
				LauncherSettings.Favorites.USER_TYPE + "="
						+ LauncherSettings.Favorites.USER_TYPE_SERVER + " and "
						+ LauncherSettings.Favorites.SCREEN + "=1", null);
	}

	public static void deleteRightItemToDatabase(Context context, boolean notify) {
		final ContentResolver cr = context.getContentResolver();
		cr.delete(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
				LauncherSettings.Favorites.USER_TYPE + "="
						+ LauncherSettings.Favorites.USER_TYPE_SERVER + " and "
						+ LauncherSettings.Favorites.SCREEN + "=2", null);
	}

	public static Uri addItemToDatabase(Context context,
			ShortcutInfo shortcutInfo, boolean notify) {
		// if (-1 != QueryDuplicate(context, shortcutInfo.url, notify)) {
		// Toast.makeText(context, "已经添加过", Toast.LENGTH_SHORT).show();
		// return;
		// }

		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		values.put(LauncherSettings.Favorites.SHORTID, shortcutInfo.shortid);
		values.put(LauncherSettings.Favorites.CONTAINER, shortcutInfo.container);
		values.put(LauncherSettings.Favorites.SCREEN, shortcutInfo.screen);
		values.put(LauncherSettings.Favorites.ICON_TYPE, shortcutInfo.iconType);
		values.put(LauncherSettings.Favorites.ITEM_INDEX,
				shortcutInfo.itemIndex);
		values.put(LauncherSettings.Favorites.ITEM_TYPE, shortcutInfo.itemType);
		values.put(LauncherSettings.Favorites.TITLE,
				shortcutInfo.title.toString());
		values.put(LauncherSettings.Favorites.URL, shortcutInfo.url);
		values.put(LauncherSettings.Favorites.ICON_RESOURCE,
				shortcutInfo.iconResource);
		// writeBitmap(values, shortcutInfo.getIcon());

		Uri result = cr.insert(notify ? LauncherSettings.Favorites.CONTENT_URI
				: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
				values);
		return result;
	}

	static void writeBitmap(ContentValues values, Bitmap bitmap) {
		if (bitmap != null) {
			byte[] data = flattenBitmap(bitmap);
			values.put(LauncherSettings.Favorites.ICON, data);
		}
	}

	static byte[] flattenBitmap(Bitmap bitmap) {
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			Log.w("Favorite", "Could not write icon");
			return null;
		}
	}

	/**
	 * 查询是否有记录
	 * 
	 * @param context
	 * @param url
	 * @param notify
	 * @return -1 无记录
	 */
	public static long QueryDuplicate(Context context, String url,
			boolean notify) {
		final ContentResolver cr = context.getContentResolver();

		String where = LauncherSettings.Favorites.URL + " = ?" + " and "
				+ LauncherSettings.Favorites.SCREEN + " = 1";
		Cursor cursor = cr
				.query(notify ? LauncherSettings.Favorites.CONTENT_URI
						: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
						null, where, new String[] { url }, null);
		int _ID = -1;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				_ID = cursor.getInt(cursor
						.getColumnIndex(LauncherSettings.Favorites._ID));
			}
		}
		cursor.close();
		return _ID;
	}

	/**
	 * 查询该屏幕有多少记录
	 * 
	 * @param context
	 * @param screen
	 * @param notify
	 * @return true 有记录
	 */
	public static int QueryCount(Context context, int screen, boolean notify) {
		final ContentResolver cr = context.getContentResolver();

		Cursor cursor = cr
				.query(notify ? LauncherSettings.Favorites.CONTENT_URI
						: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
						null, LauncherSettings.Favorites.SCREEN + "=?"
								+ " and "
								+ LauncherSettings.Favorites.CONTAINER + " = "
								+ LauncherSettings.Favorites.CONTAINER_DESKTOP,
						new String[] { screen + "" }, null);
		String[] result = new String[cursor.getCount()];
		int j = 0;
		while (cursor.moveToNext()) {
			result[j] = cursor.getString(cursor
					.getColumnIndex(LauncherSettings.Favorites.URL));
			j++;
		}
		cursor.close();
		return result.length;

	}

	public static int QueryFolderCount(Context context, int screen,
			boolean notify) {
		final ContentResolver cr = context.getContentResolver();

		String where = LauncherSettings.Favorites.ITEM_TYPE + " = "
				+ LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER + " and "
				+ LauncherSettings.Favorites.SCREEN + " = ?";
		Cursor cursor = cr
				.query(notify ? LauncherSettings.Favorites.CONTENT_URI
						: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
						null, where, new String[] { screen + "" }, null);

		String[] result = new String[cursor.getCount()];
		int j = 0;
		while (cursor.moveToNext()) {
			result[j] = cursor.getString(cursor
					.getColumnIndex(LauncherSettings.Favorites.TITLE));
			j++;
		}
		cursor.close();
		return result.length;

	}

	/**
	 * 查询是否有文件夹记录
	 * 
	 * @param context
	 * @param screen
	 * @param shortid
	 * @param notify
	 * @return
	 */
	public static boolean QueryFolderDuplicate(Context context, int screen,
			long shortid, boolean notify) {
		final ContentResolver cr = context.getContentResolver();

		String where = LauncherSettings.Favorites.ITEM_TYPE + " = "
				+ LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER + " and "
				+ LauncherSettings.Favorites.SCREEN + " = ?" + " and "
				+ LauncherSettings.Favorites.SHORTID + " = ?";
		Cursor cursor = cr
				.query(notify ? LauncherSettings.Favorites.CONTENT_URI
						: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
						null, where,
						new String[] { screen + "", shortid + "" }, null);

		while (cursor.moveToNext()) {
			return true;
		}
		cursor.close();
		return false;

	}

	public static int QueryCountInFolder(Context context, int screen,
			long shortid, boolean notify) {
		final ContentResolver cr = context.getContentResolver();

		String where = LauncherSettings.Favorites.CONTAINER + " = ? and "
				+ LauncherSettings.Favorites.SCREEN + " = ?";
		Cursor cursor = cr
				.query(notify ? LauncherSettings.Favorites.CONTENT_URI
						: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
						null, where,
						new String[] { shortid + "", screen + "" }, null);

		String[] result = new String[cursor.getCount()];
		int j = 0;
		while (cursor.moveToNext()) {
			result[j] = cursor.getString(cursor
					.getColumnIndex(LauncherSettings.Favorites.TITLE));
			j++;
		}
		cursor.close();
		return result.length;

	}

	/**
	 * Add an item to the database in a specified container. Sets the container,
	 * screen, cellX and cellY fields of the item. Also assigns an ID to the
	 * item.
	 */
	public static void addItemToDatabase(Context context, ItemInfo item,
			long container, int screen, int itemindex, boolean notify) {
		item.container = container;
		item.screen = screen;
		item.itemIndex = itemindex;

		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		item.onAddToDatabase(values);

		Uri result = cr.insert(notify ? LauncherSettings.Favorites.CONTENT_URI
				: LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
				values);

		if (result != null) {
			item.id = Integer.parseInt(result.getPathSegments().get(1));
		}
	}
	/**
	 * Update an item to the database in a specified container.
	 */
	static void updateShortcutInDatabase(Context context, ShortcutInfo shortcutInfo) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();
		
		values.put(LauncherSettings.Favorites.URL, shortcutInfo.url);
		values.put(LauncherSettings.Favorites.ICON_RESOURCE,
				shortcutInfo.iconResource);

		cr.update(LauncherSettings.Favorites.getContentUri(shortcutInfo.id, true),
				values, null, null);
	}
	/**
	 * Update an item to the database in a specified container.
	 */
	static void updateItemInDatabase(Context context, ItemInfo item) {
		final ContentValues values = new ContentValues();
		final ContentResolver cr = context.getContentResolver();

		item.onAddToDatabase(values);

		cr.update(LauncherSettings.Favorites.getContentUri(item.id, true),
				values, null, null);
	}

	/**
	 * Removes the specified item from the database
	 * 
	 * @param context
	 * @param item
	 */
	public static void deleteItemFromDatabase(Context context, long itemid) {
		final ContentResolver cr = context.getContentResolver();

		cr.delete(LauncherSettings.Favorites.getContentUri(itemid, true), null,
				null);
	}

	/**
	 * Remove the contents of the specified folder from the database
	 */
	static void deleteUserFolderContentsFromDatabase(Context context,
			UserFolderInfo info) {
		final ContentResolver cr = context.getContentResolver();

		cr.delete(LauncherSettings.Favorites.getContentUri(info.id, true),
				null, null);
		cr.delete(LauncherSettings.Favorites.CONTENT_URI,
				LauncherSettings.Favorites.CONTAINER + "=" + info.shortid, null);
	}

	/**
	 * Set this as the current Launcher activity object for the loader.
	 */
	public void initialize(Callbacks callbacks) {
		synchronized (mLock) {
			mCallbacks = new WeakReference<Callbacks>(callbacks);
		}
	}

	public void startLoader(Context context, boolean isLaunching) {
		is_runing_Loader = true;
		mLoader.startLoader(context, isLaunching);
	}

	public void stopLoader() {
		mLoader.stopLoader();
	}

	public void ReleaseLoader() {
		mLoader.ReleaseItem();
	}

	public class Loader {
		private static final int ITEMS_CHUNK = 6;

		private LoaderThread mLoaderThread;
		final ArrayList<ItemInfo> mDockBarItems = new ArrayList<ItemInfo>();
		final ArrayList<ItemInfo> mRightItems = new ArrayList<ItemInfo>();
		final ArrayList<ItemInfo> mMiddleItems = new ArrayList<ItemInfo>();
		final HashMap<HashMap<Long, Integer>, FolderInfo> mFolders = new HashMap<HashMap<Long, Integer>, FolderInfo>();

		/**
		 * Call this from the ui thread so the handler is initialized on the
		 * correct thread.
		 */
		public Loader() {
		}

		public void startLoader(Context context, boolean isLaunching) {
			synchronized (mLock) {
				if (DEBUG_LOADERS) {
					Log.d(TAG, "startLoader isLaunching=" + isLaunching);
				}

				// Don't bother to start the thread if we know it's not going to
				// do anything
				if (mCallbacks != null && mCallbacks.get() != null) {
					LoaderThread oldThread = mLoaderThread;
					if (oldThread != null) {
						if (oldThread.isLaunching()) {
							// don't downgrade isLaunching if we're already
							// running
							isLaunching = true;
						}
						oldThread.stopLocked();
					}
					mLoaderThread = new LoaderThread(context, oldThread,
							isLaunching);
					mLoaderThread.start();
				}
			}
		}

		public void stopLoader() {
			synchronized (mLock) {
				if (mLoaderThread != null) {
					mLoaderThread.stopLocked();
				}
			}
		}

		/**
		 * Runnable for the thread that loads the contents of the launcher: -
		 * workspace icons - widgets - all apps icons
		 */
		private class LoaderThread extends Thread {
			private Context mContext;
			private Thread mWaitThread;
			private boolean mIsLaunching;
			private boolean mStopped;
			private boolean mLoadAndBindStepFinished;

			LoaderThread(Context context, Thread waitThread, boolean isLaunching) {
				mContext = context;
				mWaitThread = waitThread;
				mIsLaunching = isLaunching;
			}

			boolean isLaunching() {
				return mIsLaunching;
			}

			/**
			 * If another LoaderThread was supplied, we need to wait for that to
			 * finish before we start our processing. This keeps the ordering of
			 * the setting and clearing of the dirty flags correct by making
			 * sure we don't start processing stuff until they've had a chance
			 * to re-set them. We do this waiting the worker thread, not the ui
			 * thread to avoid ANRs.
			 */
			private void waitForOtherThread() {
				if (mWaitThread != null) {
					boolean done = false;
					while (!done) {
						try {
							mWaitThread.join();
							done = true;
						} catch (InterruptedException ex) {
							// Ignore
						}
					}
					mWaitThread = null;
				}
			}

			private void loadAndBindWorkspace() {
				// Load the workspace

				// Other other threads can unset mWorkspaceLoaded, so atomically
				// set it,
				// and then if they unset it, or we unset it because of
				// mStopped, it will
				// be unset.
				boolean loaded;
				synchronized (this) {
					loaded = mWorkspaceLoaded;
					mWorkspaceLoaded = true;
				}

				// For now, just always reload the workspace. It's ~100 ms vs.
				// the
				// binding which takes many hundreds of ms.
				// We can reconsider.
				if (DEBUG_LOADERS)
					Log.d(TAG, "loadAndBindWorkspace loaded=" + loaded);
				if (true || !loaded) {
					loadWorkspace();
					if (mStopped) {
						mWorkspaceLoaded = false;
						return;
					}
				}

				// Bind the workspace
				// bindAppWidget
				bindWorkspace();
			}

			public void run() {
				waitForOtherThread();

				// Optimize for end-user experience: if the Launcher is up and
				// // running with the
				// All Apps interface in the foreground, load All Apps first.
				// Otherwise, load the
				// workspace first (default).
				final Callbacks cbk = mCallbacks.get();
				final boolean loadWorkspaceFirst = cbk != null ? (!cbk
						.isAllAppsVisible()) : true;

				// Elevate priority when Home launches for the first time to
				// avoid
				// starving at boot time. Staring at a blank home is not cool.
				synchronized (mLock) {
					android.os.Process
							.setThreadPriority(mIsLaunching ? Process.THREAD_PRIORITY_DEFAULT
									: Process.THREAD_PRIORITY_BACKGROUND);
				}

				if (PROFILE_LOADERS) {
					android.os.Debug.startMethodTracing(Environment
							.getExternalStorageDirectory()
							+ "/launcher-loaders");
				}

				if (DEBUG_LOADERS)
					Log.d(TAG, "step 1: loading workspace");

				loadAndBindWorkspace();

				// Clear out this reference, otherwise we end up holding it
				// until all of the
				// callback runnables are done.
				mContext = null;

				synchronized (mLock) {
					// Setting the reference is atomic, but we can't do it
					// inside the other critical
					// sections.
					mLoaderThread = null;
				}

				if (PROFILE_LOADERS) {
					android.os.Debug.stopMethodTracing();
				}

				// Trigger a gc to try to clean up after the stuff is done,
				// since the
				// renderscript allocations aren't charged to the java heap.
				mHandler.post(new Runnable() {
					public void run() {
						System.gc();
					}
				});
			}

			public void stopLocked() {
				synchronized (LoaderThread.this) {
					mStopped = true;
					this.notify();
				}
			}

			/**
			 * Gets the callbacks object. If we've been stopped, or if the
			 * launcher object has somehow been garbage collected, return null
			 * instead. Pass in the Callbacks object that was around when the
			 * deferred message was scheduled, and if there's a new Callbacks
			 * object around then also return null. This will save us from
			 * calling onto it with data that will be ignored.
			 */
			Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
				synchronized (mLock) {
					if (mStopped) {
						return null;
					}

					if (mCallbacks == null) {
						return null;
					}

					final Callbacks callbacks = mCallbacks.get();
					if (callbacks != oldCallbacks) {
						return null;
					}
					if (callbacks == null) {
						Log.w(TAG, "no mCallbacks");
						return null;
					}

					return callbacks;
				}
			}

			private void loadWorkspace() {
				final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

				final Context context = mContext;
				final ContentResolver contentResolver = context
						.getContentResolver();

				if (CellLayout.update_screen == 1) {
					mMiddleItems.clear();
				} else if (CellLayout.update_screen == 2) {
					mRightItems.clear();
				} else if (CellLayout.update_screen == 0) {
					mMiddleItems.clear();
					mRightItems.clear();
				}
				mDockBarItems.clear();
				mFolders.clear();

				if (true) {
					final ArrayList<Long> itemsToRemove = new ArrayList<Long>();

					final Cursor c = contentResolver
							.query(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION,
									null, null, null, null);

					try {
						final int idIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
						final int shortidIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.SHORTID);
						final int urlIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.URL);
						final int itemIndexIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_INDEX);
						final int titleIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
						final int iconTypeIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_TYPE);
						final int iconIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON);
						final int iconResourceIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE);
						final int containerIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
						final int itemTypeIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
						final int screenIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
						final int iconUrlIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_URL);
						final int userTypeIndex = c
								.getColumnIndexOrThrow(LauncherSettings.Favorites.USER_TYPE);

						ShortcutInfo info;
						int container;
						long id;
						long shortid;

						while (!mStopped && c.moveToNext()) {
							try {
								int itemType = c.getInt(itemTypeIndex);

								int screen_number = c.getInt(screenIndex);
								String name = c.getString(titleIndex);

								if (CellLayout.update_screen != 0) {
									if (screen_number != CellLayout.update_screen)
										continue;
								}

								switch (itemType) {
								case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
								case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
									info = new ShortcutInfo();
									Bitmap icon = null;
									info.title = c.getString(titleIndex);
									info.url = c.getString(urlIndex);
									info.id = c.getLong(idIndex);
									info.shortid = c.getLong(shortidIndex);
									container = c.getInt(containerIndex);
									info.container = container;
									info.screen = c.getInt(screenIndex);
									info.itemIndex = c.getInt(itemIndexIndex);
									info.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
									info.iconResource = c.getString(iconResourceIndex);
									info.userType = c.getInt(userTypeIndex);
									int iconType = c.getInt(iconTypeIndex);
									switch (iconType) {

									case LauncherSettings.Favorites.ICON_TYPE_BITMAP:
										icon = getIconFromCursor(c, iconIndex);
										if (icon == null) {
											icon = Utilities
													.drawableToBitmap(R.drawable.hotseat_browser_bg);
											info.customIcon = false;
											info.usingFallbackIcon = true;
										} else {
											info.customIcon = true;

											if (icon.getHeight() < 60
													&& icon.getWidth() < 60) { // ADD
																				// LS
												icon = zuHeBitMap(icon, context);
											}
										}
										break;
									case LauncherSettings.Favorites.ICON_TYPE_RESOURCE:
										if (Tools.sdCardExist()) {

											String SDCarePath = IOUtils
													.getIconFolder().getPath();
											String filePath = SDCarePath + "/"
													+ info.iconResource
													+ ".png";

											icon = Utilities
													.filePathToBitmap(filePath);
											info.iconUrl = c
													.getString(iconUrlIndex);
											if (icon == null) {
												String picMD5 = Tools
														.generateMD5(info.iconUrl);
												// 判断图片是否已存在
												Attach attach = new Attach();
												attach.setId((int) info.shortid);
												attach.setTitle(picMD5);
												attach.setUrl(info.iconUrl);
												if (!checkAddlist(attach)) {
													addlist.add(attach);
												}

												icon = Utilities
														.drawableToBitmap(R.drawable.hotseat_browser_bg);
												info.customIcon = false;
												info.usingFallbackIcon = true;
											} else {
												info.customIcon = true;
											}
										} else {
											icon = Utilities
													.drawableToBitmap(R.drawable.hotseat_browser_bg);
											info.customIcon = false;
											info.usingFallbackIcon = true;
										}
										break;
									case LauncherSettings.Favorites.ICON_TYPE_RESOURCE_SNAPSHOT:
										if (Tools.sdCardExist()) {

											String SDCarePath = IOUtils
													.getIconFolder().getPath();
											String filePath = SDCarePath + "/"
													+ info.iconResource
													+ ".png";

											icon = Utilities
													.filePathToBitmap(filePath);
											if (icon == null) {
												icon = Utilities
														.drawableToBitmap(R.drawable.hotseat_browser_bg);
												info.customIcon = false;
												info.usingFallbackIcon = true;
											} else {
												info.customIcon = true;
											}
										} else {
											icon = Utilities
													.drawableToBitmap(R.drawable.hotseat_browser_bg);
											info.customIcon = false;
											info.usingFallbackIcon = true;
										}
										break;
									case LauncherSettings.Favorites.ICON_TYPE_RESOURCE_UNIVERSAL:
										if (Tools.sdCardExist()) {
											File fileDir = StorageUtils
													.getIndividualCacheDirectory(context);

											String filePath = fileDir
													.getAbsolutePath()
													+ "/"
													+ info.iconResource;
											Log.i("LauncherModel",
													"LauncherModel " + filePath);
											int size = Utilities
													.getIconResourcesSize(R.drawable.ic_launcher_folder);
											Bitmap bitmap = Utilities
													.filePathToBitmap(filePath);
											if (bitmap != null) {
												icon = Bitmap
														.createScaledBitmap(
																bitmap, size,
																size, true);
											}
											if (icon == null) {
												icon = Utilities
														.drawableToBitmap(R.drawable.hotseat_browser_bg);
												info.customIcon = false;
												info.usingFallbackIcon = true;
											} else {
												info.customIcon = true;
											}
										} else {
											icon = Utilities
													.drawableToBitmap(R.drawable.hotseat_browser_bg);
											info.customIcon = false;
											info.usingFallbackIcon = true;
										}
										break;

									default:
										icon = Utilities
												.drawableToBitmap(R.drawable.hotseat_browser_bg);
										info.usingFallbackIcon = true;
										info.customIcon = false;
										break;
									}
									info.setIcon(icon);

									switch (container) {
									case LauncherSettings.Favorites.CONTAINER_DESKTOP:
										if (info.screen == Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE) {
											mMiddleItems.add(info);
										} else if (info.screen == Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT) {
											mRightItems.add(info);
										}
										break;
									case LauncherSettings.Favorites.CONTAINER_DOCKBAR:
										mDockBarItems.add(info);
										break;
									default:
										// Item is in a user folder
										UserFolderInfo folderInfo = findOrMakeUserFolder(
												mFolders, container,
												info.screen);
										folderInfo.add(info);
										break;
									}

									break;

								case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
									id = c.getLong(idIndex);
									shortid = c.getInt(shortidIndex);
									UserFolderInfo folderInfo = findOrMakeUserFolder(
											mFolders, shortid,
											c.getInt(screenIndex));
									folderInfo.title = c.getString(titleIndex);
									folderInfo.id = id;
									folderInfo.shortid = shortid;
									container = c.getInt(containerIndex);
									folderInfo.container = container;
									folderInfo.screen = c.getInt(screenIndex);
									folderInfo.itemIndex = c
											.getInt(itemIndexIndex);

									switch (container) {
									case LauncherSettings.Favorites.CONTAINER_DESKTOP:
										if (folderInfo.screen == Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE) {
											mMiddleItems.add(folderInfo);
										} else if (folderInfo.screen == Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT) {
											mRightItems.add(folderInfo);
										}
										break;
									case LauncherSettings.Favorites.CONTAINER_DOCKBAR:
										mDockBarItems.add(folderInfo);
										break;
									}
									HashMap<Long, Integer> hashId = new HashMap<Long, Integer>();
									hashId.put(folderInfo.shortid,
											folderInfo.screen);
									mFolders.put(hashId, folderInfo);
									break;
								}
							} catch (Exception e) {
								Log.w(TAG,
										"Desktop items loading interrupted:", e);
							}
						}
					} finally {
						c.close();
					}

					if (itemsToRemove.size() > 0) {
						ContentProviderClient client = contentResolver
								.acquireContentProviderClient(LauncherSettings.Favorites.CONTENT_URI);
						// Remove dead items
						for (long id : itemsToRemove) {
							if (DEBUG_LOADERS) {
								Log.d(TAG, "Removed id = " + id);
							}
							// Don't notify content observers
							try {
								client.delete(LauncherSettings.Favorites
										.getContentUri(id, false), null, null);
							} catch (RemoteException e) {
								Log.w(TAG, "Could not remove id = " + id);
							}
						}
					}
				}
				// 添加下载图片功能
				updateImageLoad(context);
			}

			/**
			 * Read everything out of our database.
			 */
			private void bindWorkspace() {
				final long t = SystemClock.uptimeMillis();

				// Don't use these two variables in any of the callback
				// runnables.
				// Otherwise we hold a reference to them.
				final Callbacks oldCallbacks = mCallbacks.get();
				if (oldCallbacks == null) {
					// This launcher has exited and nobody bothered to tell us.
					// Just bail.
					Log.w(TAG, "LoaderThread running with no launcher");
					return;
				}

				int N;
				// Tell the workspace that we're about to start firing items at
				// it
				mHandler.post(new Runnable() {
					public void run() {
						Callbacks callbacks = tryGetCallbacks(oldCallbacks);
						if (callbacks != null) { // 清除all view
							callbacks.startBinding(CellLayout.update_screen);
						}
					}
				});
				// Add the items to the dockbar.
				N = mDockBarItems.size();

				mHandler.post(new Runnable() {
					public void run() {
						Callbacks callbacks = tryGetCallbacks(oldCallbacks);
						if (callbacks != null) {
							callbacks.bindDockBarItems(mDockBarItems, 0,
									mDockBarItems.size());
						}
					}
				});

				// Add the items to the workspace.
				N = mRightItems.size();
				mHandler.post(new Runnable() {
					public void run() {
						Callbacks callbacks = tryGetCallbacks(oldCallbacks);
						if (callbacks != null
								&& (CellLayout.update_screen == 2 || CellLayout.update_screen == 0)) {
							callbacks.bindRightItems(mRightItems, 0,
									mRightItems.size());
						}
					}
				});

				mHandler.post(new Runnable() {
					public void run() {
						Callbacks callbacks = tryGetCallbacks(oldCallbacks);
						if (callbacks != null && CellLayout.update_screen == 1
								|| CellLayout.update_screen == 0) {
							callbacks.bindMiddleItems(mMiddleItems, 0,
									mMiddleItems.size());
						}
					}
				});

				mHandler.post(new Runnable() {
					public void run() {
						Callbacks callbacks = tryGetCallbacks(oldCallbacks);
						if (callbacks != null) {
							callbacks.bindFolders(mFolders);
						}
					}
				});
				// Wait until the queue goes empty.
				mHandler.post(new Runnable() {
					public void run() {
						if (DEBUG_LOADERS) {
							Log.d(TAG, "Going to start binding widgets soon.");
						}
					}
				});

				// Tell the workspace that we're done.
				mHandler.post(new Runnable() {
					public void run() {
						Callbacks callbacks = tryGetCallbacks(oldCallbacks);
						if (callbacks != null) {
							callbacks.finishBindingItems();
						}
					}
				});
				// If we're profiling, this is the last thing in the queue.
				mHandler.post(new Runnable() {
					public void run() {
						if (DEBUG_LOADERS) {
							Log.d(TAG,
									"bound workspace in "
											+ (SystemClock.uptimeMillis() - t)
											+ "ms");
						}
					}
				});
			}

			public void dumpState() {
				Log.d(TAG, "mLoader.mLoaderThread.mContext=" + mContext);
				Log.d(TAG, "mLoader.mLoaderThread.mWaitThread=" + mWaitThread);
				Log.d(TAG, "mLoader.mLoaderThread.mIsLaunching=" + mIsLaunching);
				Log.d(TAG, "mLoader.mLoaderThread.mStopped=" + mStopped);
				Log.d(TAG, "mLoader.mLoaderThread.mLoadAndBindStepFinished="
						+ mLoadAndBindStepFinished);
			}
		}

		public void dumpState() {
			Log.d(TAG, "mLoader.mItems size=" + mLoader.mRightItems.size());
			if (mLoaderThread != null) {
				mLoaderThread.dumpState();
			} else {
				Log.d(TAG, "mLoader.mLoaderThread=null");
			}
		}

		public void ReleaseItem() {
			if (mMiddleItems != null) {
				mMiddleItems.clear();
			}
			if (mRightItems != null) {
				mRightItems.clear();
			}
		}

	}

	Bitmap getIconFromCursor(Cursor c, int iconIndex) {
		byte[] data = c.getBlob(iconIndex);
		try {
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return an existing UserFolderInfo object if we have encountered this ID
	 * previously, or make a new one.
	 */
	public static UserFolderInfo findOrMakeUserFolder(
			HashMap<HashMap<Long, Integer>, FolderInfo> folders, long id,
			int screen) {
		// See if a placeholder was created for us already
		HashMap<Long, Integer> hashId = new HashMap<Long, Integer>();
		hashId.put(id, screen);
		FolderInfo folderInfo = folders.get(hashId);
		if (folderInfo == null || !(folderInfo instanceof UserFolderInfo)) {
			// No placeholder -- create a new instance
			folderInfo = new UserFolderInfo();
			folders.put(hashId, folderInfo);
		}
		return (UserFolderInfo) folderInfo;
	}

	public static final Comparator<ItemInfo> SHORTCUT_INDEX_COMPARATOR_APP = new Comparator<ItemInfo>() {
		public final int compare(ItemInfo a, ItemInfo b) {
			return ((Integer) a.itemIndex).compareTo((Integer) b.itemIndex);
		}
	};

	/**
	 * 排序 由大到小
	 */
	public static final Comparator<ItemInfo> SHORTCUT_INDEX_COMPARATOR_APP_DESC = new Comparator<ItemInfo>() {
		public final int compare(ItemInfo a, ItemInfo b) {
			return ((Integer) b.itemIndex).compareTo((Integer) a.itemIndex);
		}
	};

	/**
	 * 排序 由大到小
	 */
	public static final Comparator<HashMap<String, Object>> FAXIAN_INDEX_COMPARATOR_APP_DESC = new Comparator<HashMap<String, Object>>() {
		public final int compare(HashMap<String, Object> a,
				HashMap<String, Object> b) {
			return (Integer.valueOf(b.get("level").toString()))
					.compareTo(Integer.valueOf(a.get("level").toString()));
		}
	};

	public static Bitmap zuHeBitMap(Bitmap bitmap2, Context context) {

		// 防止出现Immutable bitmap passed to Canvas constructor错误
		Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.hotseat_browser_bg).copy(Bitmap.Config.ARGB_8888,
				true);
		// Bitmap bitmap2 = ((BitmapDrawable)
		// context.getResources().getDrawable(
		// R.drawable.go)).getBitmap();

		Bitmap newBitmap = null;

		newBitmap = Bitmap.createBitmap(bitmap1);
		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();

		int w = bitmap1.getWidth();
		int h = bitmap1.getHeight();

		int w_2 = bitmap2.getWidth();
		int h_2 = bitmap2.getHeight();

		paint.setColor(Color.TRANSPARENT);
		paint.setAlpha(0);
		canvas.drawRect(0, 0, bitmap1.getWidth(), bitmap1.getHeight(), paint);

		paint = new Paint();
		canvas.drawBitmap(bitmap2, Math.abs(w - w_2) / 2,
				Math.abs(h - h_2) / 2, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		// 存储新合成的图片
		canvas.restore();

		return newBitmap;
	}

	private static ArrayList<Attach> addlist = new ArrayList<Attach>();

	public static void updateImageLoad(Context mContext) {
		Handler handler = new Handler(mContext.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					switch (msg.what) {
					case 0:
						// 无更新
						break;
					case 1:
						// 更新

						break;
					}
				}
			}
		};
		Thread thread = new Thread(ImageLoadUtil.getInstance(addlist, 3,
				handler));
		thread.start();
	}

	private static boolean checkAddlist(Attach attach) {

		if (addlist.contains(attach)) {
			return true;
		} else {
			return false;
		}
	}
}
