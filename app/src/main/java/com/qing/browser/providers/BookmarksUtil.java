package com.qing.browser.providers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Browser;
import android.util.Log;

import com.qing.browser.activities.AddShortcutActivity;
import com.qing.browser.model.items.BookmarkItem;

public class BookmarksUtil {

	public static final String BookmarkColumns_UUID = "uuid";
	public static final String BookmarkColumns_SNAPSHOT = "snapshot";

	static final String TABLE_FAVORITES = "bookmarks";
	static final String PARAMETER_NOTIFY = "notify";


	public static String[] sHistoryBookmarksProjection = new String[] {
			Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE,
			Browser.BookmarkColumns.URL, Browser.BookmarkColumns.VISITS,
			Browser.BookmarkColumns.DATE, Browser.BookmarkColumns.CREATED,
			Browser.BookmarkColumns.BOOKMARK, BookmarkColumns_UUID,
			Browser.BookmarkColumns.FAVICON, BookmarkColumns_SNAPSHOT };

	public static int insert(Context context, ContentValues values) {
		int bookmarkid = 0;
		final ContentResolver cr = context.getContentResolver();
		Uri result = cr.insert(BookmarksProvider.CONTENT_URI, values);
		if (result != null) {
			bookmarkid = Integer.parseInt(result.getPathSegments().get(1));
		}
		return bookmarkid;
	}

	public static ArrayList<HashMap<String, Object>> queryForaddShortcut(
			Context context, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final ContentResolver cr = context.getContentResolver();

		Cursor mCursor = cr.query(BookmarksProvider.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				int columnTitle = mCursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = mCursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int id = mCursor.getColumnIndex(Browser.BookmarkColumns._ID);
				int columnImage = mCursor
						.getColumnIndex(Browser.BookmarkColumns.FAVICON);

				int columnPic = mCursor
						.getColumnIndex(BookmarkColumns_SNAPSHOT);

				int count = 0;
				while (!mCursor.isAfterLast() && (count < mCursor.getCount())) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", mCursor.getLong(id));
					map.put("ItemImageType",
							AddShortcutActivity.IMAGE_TYPE_BOOKMARK);
					map.put("ItemSnap", mCursor.getString(columnPic));
					map.put("ItemImage", mCursor.getBlob(columnImage));
					map.put("ItemText", mCursor.getString(columnTitle));
					map.put("ItemUrl", mCursor.getString(columnUrl));

					mData.add(map);
					count++;
					mCursor.moveToNext();
				}
			}
			mCursor.close();
		}
		return mData;
	}

	public static ArrayList<HashMap<String, Object>> queryForBookmarkList(
			Context context, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final ContentResolver cr = context.getContentResolver();

		Cursor mCursor = cr.query(BookmarksProvider.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);
		ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				int columnId = mCursor
						.getColumnIndex(Browser.BookmarkColumns._ID);
				int columnTitle = mCursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = mCursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int columnFavicon = mCursor
						.getColumnIndex(Browser.BookmarkColumns.FAVICON);
				int count = 0;
				while (!mCursor.isAfterLast() && (count < mCursor.getCount())) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(Browser.BookmarkColumns._ID,
							mCursor.getInt(columnId));
					map.put(Browser.BookmarkColumns.TITLE,
							mCursor.getString(columnTitle));
					map.put(Browser.BookmarkColumns.URL,
							mCursor.getString(columnUrl));
					map.put(Browser.BookmarkColumns.FAVICON,
							mCursor.getBlob(columnFavicon));
					mData.add(map);
					count++;
					mCursor.moveToNext();
				}
			}
			mCursor.close();
		}
		return mData;
	}

	public static void update(Context context, ContentValues values, long id) {
		final ContentResolver cr = context.getContentResolver();
		cr.update(BookmarksProvider.getContentUri(id), values, null, null);
	}

	public static BookmarkItem getStockBookmarkById(Context context, long id) {
		final ContentResolver cr = context.getContentResolver();
		BookmarkItem result = null;
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;

		Cursor c = cr.query(BookmarksProvider.CONTENT_URI,
				sHistoryBookmarksProjection, whereClause, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				String title = c.getString(c
						.getColumnIndex(Browser.BookmarkColumns.TITLE));
				String url = c.getString(c
						.getColumnIndex(Browser.BookmarkColumns.URL));
				String uuid = c.getString(c
						.getColumnIndex(BookmarkColumns_UUID));
				result = new BookmarkItem(title, url, uuid);
			}

			c.close();
		}
		return result;
	}

	public static void deleteStockBookmark(Context context, long id) {
		final ContentResolver cr = context.getContentResolver();
		String whereClause = Browser.BookmarkColumns._ID + " = " + id;
		Cursor c = cr.query(BookmarksProvider.CONTENT_URI,
				sHistoryBookmarksProjection, whereClause, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.BOOKMARK, 0);
				values.putNull(Browser.BookmarkColumns.CREATED);

				cr.update(BookmarksProvider.CONTENT_URI, values, whereClause,
						null);

			}
			c.close();
		}
	}

	public static void deleteStockBookmarkByUUID(Context context, String uuid) {
		final ContentResolver cr = context.getContentResolver();
		String whereClause = BookmarkColumns_UUID + " = '" + uuid + "'";

		Cursor c = cr.query(BookmarksProvider.CONTENT_URI,
				sHistoryBookmarksProjection, "uuid=?", new String[] { uuid },
				null);
		if (c != null) {
			if (c.moveToFirst()) {
				ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.BOOKMARK, 0);
				values.putNull(Browser.BookmarkColumns.CREATED);

				cr.update(BookmarksProvider.CONTENT_URI, values, whereClause,
						null);

			}
			c.close();
		}

		Log.d("H", "deleteStockBookmarkByUUID ");
	}

	/**
	 * 根据uuid查找是否有记录
	 */
	public static boolean findUUID_isBookmark(Context context, String uuid) {
		final ContentResolver cr = context.getContentResolver();
		boolean name = false;

		Cursor cursor = cr.query(BookmarksProvider.CONTENT_URI, null, "uuid=?",
				new String[] { uuid }, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				if (cursor.getInt(cursor
						.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) == 1) {
					name = true;
				} else {
					name = false;
				}
			}
		}
		cursor.close();
		return name;
	}

	/**
	 * 根据uuid查找是否有记录
	 */
	public static boolean findUUID(Context context, String uuid) {
		final ContentResolver cr = context.getContentResolver();
		boolean name = false;
		Cursor cursor = cr.query(BookmarksProvider.CONTENT_URI, null, "uuid=?",
				new String[] { uuid }, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				name = true;
			}
		}
		cursor.close();
		return name;
	}

	public static boolean updabookmarks(Context context, String str) {
		boolean flag = false;
		final ContentResolver cr = context.getContentResolver();
		try {
			JSONArray jsonArray = new JSONArray(str);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject info = jsonArray.getJSONObject(i);

				if (!findUUID(context, info.getString("uuid"))) {
					ContentValues values = new ContentValues();
					values.put(Browser.BookmarkColumns.TITLE,
							info.getString("name"));
					values.put(Browser.BookmarkColumns.URL,
							info.getString("url"));
					values.put(BookmarkColumns_UUID, info.getString("uuid"));
					values.put(Browser.BookmarkColumns.BOOKMARK, 1);// TODO
																	// info.getString("bookmark"));
					values.put(Browser.BookmarkColumns.CREATED,
							new Date().getTime());
					cr.insert(BookmarksProvider.CONTENT_URI, values);
				}

				if (i == (jsonArray.length() - 1)) {
					flag = true;
				}
			}

			String whereClause_del = Browser.BookmarkColumns.BOOKMARK + " = 0 ";
			cr.delete(BookmarksProvider.CONTENT_URI, whereClause_del, null);

		} catch (JSONException e) {

		}
		return flag;
	}

	public static int setAsBookmark(Context context, long id, String title,
			String url, boolean isBookmark, Bitmap favicon, String snapshot) {
		int bookmarkid = (int) id;
		boolean bookmarkExist = false;

		UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
		if (findUUID(context, uuid + "")) {
			bookmarkExist = true;
		} else {
			bookmarkExist = false;
		}

		ContentValues values = new ContentValues();
		if (title != null) {
			values.put(Browser.BookmarkColumns.TITLE, title);
		}

		if (url != null) {
			values.put(Browser.BookmarkColumns.URL, url);
		}

		if (uuid != null) {
			values.put(BookmarkColumns_UUID, uuid.toString());
		}

		if (favicon != null) {
			try {
				BitmapDrawable icon = new BitmapDrawable(favicon);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);
				values.put(Browser.BookmarkColumns.FAVICON, os.toByteArray());
			} catch (Exception e) {
				Log.e("H", "保存书签 " + e);
			}
		}
		if (snapshot != null) {
			values.put(BookmarkColumns_SNAPSHOT, snapshot);
		}

		if (isBookmark) {
			values.put(Browser.BookmarkColumns.BOOKMARK, 1);
			values.put(Browser.BookmarkColumns.CREATED, new Date().getTime());
		} else {
			values.put(Browser.BookmarkColumns.BOOKMARK, 0);
		}

		if (bookmarkExist) {

			Log.i("H", "保存书签   update");
			final ContentResolver cr = context.getContentResolver();
			String whereClause = BookmarkColumns_UUID + " = '" + uuid + "'";
			cr.update(BookmarksProvider.CONTENT_URI, values, whereClause, null);
		} else {
			bookmarkid = insert(context, values);
			Log.i("H", "保存书签  insert  bookmarkid=" + bookmarkid);
		}

		return bookmarkid;
	}

	public static JSONArray queryForBookmarksListToJson(Context context,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		final ContentResolver cr = context.getContentResolver();
		Cursor mCursor = cr.query(BookmarksProvider.CONTENT_URI, projection,
				selection, selectionArgs, sortOrder);

		JSONArray list = new JSONArray();
		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				int columnTitle = mCursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = mCursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int isBookmark = mCursor
						.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
				int count = 0;
				while (!mCursor.isAfterLast() && (count < mCursor.getCount())) {
					JSONObject map = new JSONObject();
					try {
						map.put("name", mCursor.getString(columnTitle));
						map.put("url", mCursor.getString(columnUrl));
						map.put("bookmark", mCursor.getString(isBookmark));
						map.put("uuid",
								UUID.nameUUIDFromBytes(mCursor.getString(
										columnUrl).getBytes()));
						list.put(map);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					count++;
					mCursor.moveToNext();
				}
			}
			mCursor.close();
		}
		return list;
	}

	public static String getBookmarksListToJson(Context context) {
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";

		String orderClause = Browser.BookmarkColumns.VISITS + " DESC, "
				+ Browser.BookmarkColumns.TITLE + " COLLATE NOCASE";

		JSONArray list = new JSONArray();
		list = queryForBookmarksListToJson(context,
				sHistoryBookmarksProjection, whereClause, null, orderClause);

		return list.toString();
	}
}
