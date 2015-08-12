package com.qing.browser.net;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qing.browser.ui.launcher.FolderInfo;
import com.qing.browser.ui.launcher.ItemInfo;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.qing.browser.ui.launcher.LauncherModel;
import com.qing.browser.ui.launcher.LauncherSettings;
import com.qing.browser.ui.launcher.ShortcutInfo;
import com.qing.browser.ui.launcher.UserFolderInfo;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.IOUtils;
import com.qing.browser.utils.Tools;

public class BookmarkMannager {

	private static final String TAG = "NetMannager";

	private final static ArrayList<ItemInfo> mMiddleItems = new ArrayList<ItemInfo>();
	private final static ArrayList<ItemInfo> mRightItems = new ArrayList<ItemInfo>();

	private final static HashMap<Long, FolderInfo> mFolders = new HashMap<Long, FolderInfo>();
	private static ArrayList<Attach> addlist = new ArrayList<Attach>();

	private static Handler Imagehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 通知更新图标
				break;
			case 1:
				int id = msg.getData().getInt("id");
				Log.i(TAG, "update ID:" + id);
				break;
			case -1:
				// 显示下载错误信息
				Log.i(TAG, "Error");
				break;
			}
		}
	};

	public static void loadMiddleScreen(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String MiddleScreenJsonString = sp.getString(
				Constants.MiddleScreenJsonString, "");

		try {
			if ("".equals(MiddleScreenJsonString)) {
				return;
			}
			JSONObject o = new JSONObject(MiddleScreenJsonString);
			String vid = o.optString("vid");

			if ("".equals(vid)) {
				return;
			}
			int nowVersion = Tools.getMiddleUpdateVersion(context);
			if (nowVersion >= Integer.valueOf(vid)) {
				return;
			}
			// 保存更新版本
			Tools.setMiddleUpdateVersion(context, Integer.valueOf(vid));

			mMiddleItems.clear();
			JSONArray bmJsonArray = o.getJSONArray("bm");
			for (int i = 0; i < bmJsonArray.length(); i++) {
				JSONObject bmJsonObject = (JSONObject) bmJsonArray.opt(i);
				int flag = bmJsonObject.optInt("flag");
				if (flag == 0) {
					// 快捷方式
					addShortcut(bmJsonObject, 1);
				} else if (flag == 1) {
					// 文件夹
					int id = addFolder(bmJsonObject, 1);
					addShortcutToFolder(bmJsonObject.toString(), id, 1);
				}

			}

			// 对items 进行排序
			orderMiddleItems();
			Launcher.mMiddleCustomItems_flag = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updateMiddleScreen(final Activity activity) {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					switch (msg.what) {
					case 0:
						// 无更新
						break;
					case 1:
						// 更新

						try {
							if (msg.obj != null) {
								JSONObject o = (JSONObject) msg.obj;

								SharedPreferences sp = activity
										.getSharedPreferences(
												Constants.PREFERENCES_NAME,
												Context.MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putString(
										Constants.MiddleScreenJsonString,
										msg.obj.toString());
								editor.commit();

								String vid = o.optString("vid");

								Date datenow = new Date();
								long nowTime = datenow.getTime();
								Tools.setMiddleUpdateTime(activity, nowTime);

								JSONArray bmJsonArray = o.getJSONArray("bm");
								for (int i = 0; i < bmJsonArray.length(); i++) {
									JSONObject bmJsonObject = (JSONObject) bmJsonArray
											.opt(i);
									int flag = bmJsonObject.optInt("flag");
									if (flag == 0) {
										// 快捷方式
										addShortcut(bmJsonObject, 1);
									} else if (flag == 1) {
										// 文件夹
										int id = addFolder(bmJsonObject, 1);
										addShortcutToFolder(
												bmJsonObject.toString(), id, 1);
									}

								}

								// 下载图片
								new UpdateImg(addlist, 3, Imagehandler).start();
							}
						} catch (Exception e) {

						}
						break;
					}
				}
			}
		};
		Thread thread = new Thread(BookmarkMiddleUtil.getInstance(activity,
				handler));
		thread.start();
	}

	public static void loadRightScreen(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String RightScreenJsonString = sp.getString(
				Constants.RightScreenJsonString, "");

		try {
			if ("".equals(RightScreenJsonString)) {
				return;
			}
			JSONObject o = new JSONObject(RightScreenJsonString);
			String vid = o.optString("vid");

			if ("".equals(vid)) {
				return;
			}
			int nowVersion = Tools.getRightUpdateVersion(context);
			if (nowVersion >= Integer.valueOf(vid)) {
				return;
			}
			// 保存更新版本
			Tools.setRightUpdateVersion(context, Integer.valueOf(vid));

			mRightItems.clear();
			JSONArray bmJsonArray = o.getJSONArray("bm");
			for (int i = 0; i < bmJsonArray.length(); i++) {
				JSONObject bmJsonObject = (JSONObject) bmJsonArray.opt(i);
				int flag = bmJsonObject.optInt("flag");
				if (flag == 0) {
					// 快捷方式
					addShortcut(bmJsonObject, 2);
				} else if (flag == 1) {
					// 文件夹
					int id = addFolder(bmJsonObject, 2);
					addShortcutToFolder(bmJsonObject.toString(), id, 2);
				}

			}

			// 对items 进行排序
			orderRightItems();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updateRightScreen(final Activity activity) {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					switch (msg.what) {
					case 0:
						// 无更新
						break;
					case 1:
						// 更新
						try {
							if (msg.obj != null) {
								JSONObject o = (JSONObject) msg.obj;

								SharedPreferences sp = activity
										.getSharedPreferences(
												Constants.PREFERENCES_NAME,
												Context.MODE_PRIVATE);
								Editor editor = sp.edit();
								editor.putString(
										Constants.RightScreenJsonString,
										msg.obj.toString());
								editor.commit();

								String vid = o.optString("vid");

								Date datenow = new Date();
								long nowTime = datenow.getTime();
								Tools.setRightUpdateTime(activity, nowTime);

								JSONArray bmJsonArray = o.getJSONArray("bm");
								for (int i = 0; i < bmJsonArray.length(); i++) {
									JSONObject bmJsonObject = (JSONObject) bmJsonArray
											.opt(i);
									int flag = bmJsonObject.optInt("flag");
									if (flag == 0) {
										// 快捷方式
										addShortcut(bmJsonObject, 2);
									} else if (flag == 1) {
										// 文件夹
										int id = addFolder(bmJsonObject, 2);
										addShortcutToFolder(
												bmJsonObject.toString(), id, 2);
									}

								}

								// 下载图片
								new UpdateImg(addlist, 3, Imagehandler).start();
							}
						} catch (Exception e) {

						}
						break;
					}
				}
			}
		};
		Thread thread = new Thread(BookmarkRightUtil.getInstance(activity,
				handler));
		thread.start();
	}

	private static void orderMiddleItems() {
		Context context = LauncherApplication.getInstance();
		Collections.sort(mMiddleItems,
				LauncherModel.SHORTCUT_INDEX_COMPARATOR_APP_DESC);

		LauncherModel.deleteMiddleItemToDatabase(context, false);

		int count = LauncherModel.QueryCount(context, 1, false);

		// 排序后，遍历给itemIndex赋值
		// 写入数据库
		for (int i = 0; i < mMiddleItems.size(); i++) {
			ItemInfo itemInfo = mMiddleItems.get(i);

			// 排序处理
			// 先获取当前屏幕中最大的索引值，以次为标准+i
			itemInfo.itemIndex = count + i;
			switch (itemInfo.itemType) {
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
				ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
				LauncherModel.addOrUpdateItemToDatabase(context, shortcutInfo,
						false);
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
				UserFolderInfo userFolderInfo = (UserFolderInfo) itemInfo;
				LauncherModel.addItemToDatabase(context, userFolderInfo, false);
				for (int j = 0; j < userFolderInfo.contents.size(); j++) {
					ShortcutInfo folderShortcutInfo = userFolderInfo.contents
							.get(j);
					LauncherModel.addOrUpdateItemToDatabase(context,
							folderShortcutInfo, false);
				}
				break;
			}
		}

	}

	private static void orderRightItems() {
		Context context = LauncherApplication.getInstance();
		Collections.sort(mRightItems,
				LauncherModel.SHORTCUT_INDEX_COMPARATOR_APP_DESC);

		LauncherModel.deleteRightItemToDatabase(context, false);
		// 排序后，遍历给itemIndex赋值
		// 写入数据库
		for (int i = 0; i < mRightItems.size(); i++) {
			ItemInfo itemInfo = mRightItems.get(i);
			itemInfo.itemIndex = i;
			switch (itemInfo.itemType) {
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
				ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
				LauncherModel.addOrUpdateItemToDatabase(context, shortcutInfo,
						false);
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
				UserFolderInfo userFolderInfo = (UserFolderInfo) itemInfo;
				LauncherModel.addItemToDatabase(context, userFolderInfo, false);
				for (int j = 0; j < userFolderInfo.contents.size(); j++) {
					ShortcutInfo folderShortcutInfo = userFolderInfo.contents
							.get(j);
					LauncherModel.addOrUpdateItemToDatabase(context,
							folderShortcutInfo, false);
				}
				break;
			}
		}

	}

	private static int addFolder(JSONObject jsonObject, int screen) {
		int shortid = jsonObject.optInt("id");

		UserFolderInfo folderInfo = findOrMakeUserFolder(mFolders, shortid,
				screen);

		folderInfo.shortid = shortid;
		folderInfo.title = jsonObject.optString("name");
		folderInfo.itemIndex = jsonObject.optInt("orders");
		String pic = jsonObject.optString("pic");

		String picMD5 = Tools.generateMD5(pic);
		// 判断图片是否已存在
		if (!checkFile(picMD5)) {
			Attach attach = new Attach();
			attach.setId(jsonObject.optInt("id"));
			attach.setTitle(picMD5);
			attach.setUrl(jsonObject.optString("pic"));
			addlist.add(attach);
		}

		folderInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		folderInfo.screen = screen;
		folderInfo.userType = LauncherSettings.Favorites.USER_TYPE_SERVER;
		if (screen == 1) {
			mMiddleItems.add(folderInfo);
		} else {
			mRightItems.add(folderInfo);
		}

		mFolders.put(folderInfo.shortid, folderInfo);

		return shortid;

	}

	private static void addShortcutToFolder(String jsonObject, int folderId,
			int screen) {
		try {
			JSONObject jsonInfo = new JSONObject(jsonObject);
			JSONArray jsonArray = new JSONArray(jsonInfo.getString("bookmark"));
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject o = (JSONObject) jsonArray.get(i);

				UserFolderInfo folderInfo = findOrMakeUserFolder(mFolders,
						folderId, screen);

				ShortcutInfo info = new ShortcutInfo();
				info.shortid = o.optInt("id");
				info.title = o.optString("name");
				info.itemIndex = o.optInt("orders");
				String img = o.optString("img");
				info.iconUrl = img;
				String picMD5 = Tools.generateMD5(img);
				info.iconResource = picMD5;
				// 判断图片是否已存在
				if (!checkFile(picMD5)) {
					Attach attach = new Attach();
					attach.setId(o.optInt("id"));
					attach.setTitle(picMD5);
					attach.setUrl(o.optString("img"));
					addlist.add(attach);
				}

				info.url = o.optString("url");
				info.container = folderId;
				info.screen = screen;
				if (folderInfo.screen == screen) {
					folderInfo.add(info);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static UserFolderInfo findOrMakeUserFolder(
			HashMap<Long, FolderInfo> folders, long id, int screen) {
		// See if a placeholder was created for us already
		FolderInfo folderInfo = folders.get(id);
		if (folderInfo == null || !(folderInfo instanceof UserFolderInfo)
				|| folderInfo.screen != screen) {
			// No placeholder -- create a new instance
			folderInfo = new UserFolderInfo();
			folders.put(id, folderInfo);
		}
		return (UserFolderInfo) folderInfo;
	}

	private static void addShortcut(JSONObject jsonObject, int screen) {
		ShortcutInfo info = new ShortcutInfo();
		info.shortid = jsonObject.optInt("id");
		info.title = jsonObject.optString("name");
		info.itemIndex = jsonObject.optInt("orders");

		String pic = jsonObject.optString("pic");
		info.iconUrl = pic;

		String picMD5 = Tools.generateMD5(pic);
		info.iconResource = picMD5;

		// 判断图片是否已存在
		if (!checkFile(picMD5)) {

			Attach attach = new Attach();
			attach.setId(jsonObject.optInt("id"));
			attach.setTitle(picMD5);
			attach.setUrl(jsonObject.optString("pic"));
			addlist.add(attach);
		}

		info.url = jsonObject.optString("url");
		info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		info.screen = screen;
		info.iconType = LauncherSettings.Favorites.ICON_TYPE_RESOURCE;

		if (screen == 1) {
			mMiddleItems.add(info);
		} else {
			mRightItems.add(info);
		}

	}

	private static boolean checkFile(String fileName) {
		File file = IOUtils.getIconFolder();
		if (!file.exists()) {
			return false;
		}
		File imageFile = new File(file, fileName + ".png");
		if (imageFile.exists()) {
			return true;
		}
		return false;
	}
}
