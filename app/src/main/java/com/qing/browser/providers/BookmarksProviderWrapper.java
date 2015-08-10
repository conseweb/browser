package com.qing.browser.providers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Browser;
import android.util.Log;

import com.qing.browser.R;
import com.qing.browser.activities.AddShortcutActivity;
import com.qing.browser.download.DownloadItem;
import com.qing.browser.model.UrlSuggestionItemComparator;
import com.qing.browser.model.adapters.UrlSuggestionCursorAdapter;
import com.qing.browser.model.items.BookmarkItem;
import com.qing.browser.model.items.HistoryItem;
import com.qing.browser.model.items.UrlSuggestionItem;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.qing.browser.ui.launcher.ShortcutInfo;
import com.qing.browser.utils.Constants;

public class BookmarksProviderWrapper {

	public enum BookmarksSource {
		STOCK, INTERNAL
	}

	private static final Uri STOCK_BOOKMARKS_URI = Browser.BOOKMARKS_URI;

	private static Uri BOOKMARKS_URI = STOCK_BOOKMARKS_URI;

	private static String[] sHistoryBookmarksProjection = new String[] {
			Browser.BookmarkColumns._ID, Browser.BookmarkColumns.TITLE,
			Browser.BookmarkColumns.URL, Browser.BookmarkColumns.VISITS,
			Browser.BookmarkColumns.DATE, Browser.BookmarkColumns.CREATED,
			Browser.BookmarkColumns.BOOKMARK, Browser.BookmarkColumns.FAVICON };

	public static void setBookmarksSource(BookmarksSource source) {
		switch (source) {
		case STOCK:
			BOOKMARKS_URI = STOCK_BOOKMARKS_URI;
			break;

		case INTERNAL:
			BOOKMARKS_URI = BookmarksProvider.CONTENT_URI;
			break;

		default:
			break;
		}
	}

	private static String FILE_PATH = "/data/data/com.qing.browser/databases/history.db";

	/**
	 * Stock History/Bookmarks management.
	 */
	/**
	 * Get a Cursor on the whole content of the history/bookmarks database.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @return A Cursor.
	 * @see Cursor
	 */
	public static Cursor getAllStockRecords(ContentResolver contentResolver) {
		return contentResolver.query(BOOKMARKS_URI,
				sHistoryBookmarksProjection, null, null, null);
	}

	/**
	 * Get a list of most visited bookmarks items, limited in size.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @param limit
	 *            The size limit.
	 * @return A list of BookmarkItem.
	 */
	public static List<BookmarkItem> getStockBookmarksWithLimit(
			ContentResolver contentResolver, int limit) {
		List<BookmarkItem> result = new ArrayList<BookmarkItem>();

		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC";
		String[] colums = new String[] { Browser.BookmarkColumns._ID,
				Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL,
				Browser.BookmarkColumns.FAVICON };

		Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums,
				whereClause, null, orderClause);

		if (cursor != null) {
			if (cursor.moveToFirst()) {

				int columnTitle = cursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = cursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int columnUUID = cursor
						.getColumnIndex(BookmarksUtil.BookmarkColumns_UUID);

				int count = 0;
				while (!cursor.isAfterLast() && (count < limit)) {

					BookmarkItem item = new BookmarkItem(
							cursor.getString(columnTitle),
							cursor.getString(columnUrl),
							cursor.getString(columnUUID));

					result.add(item);

					count++;
					cursor.moveToNext();
				}
			}

			cursor.close();
		}

		return result;
	}

	/**
	 * Get a list of most recent history items, limited in size.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @param limit
	 *            The size limit.
	 * @return A list of HistoryItem.
	 */
	public static List<HistoryItem> getStockHistoryWithLimit(
			ContentResolver contentResolver, int limit) {
		List<HistoryItem> result = new ArrayList<HistoryItem>();

		String whereClause = Browser.BookmarkColumns.VISITS + " > 0";
		String orderClause = Browser.BookmarkColumns.DATE + " DESC";

		Cursor cursor = contentResolver.query(BOOKMARKS_URI,
				sHistoryBookmarksProjection, whereClause, null, orderClause);

		if (cursor != null) {
			if (cursor.moveToFirst()) {

				int columnId = cursor
						.getColumnIndex(Browser.BookmarkColumns._ID);
				int columnTitle = cursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = cursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int columnBookmark = cursor
						.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);

				int count = 0;
				while (!cursor.isAfterLast() && (count < limit)) {

					HistoryItem item = new HistoryItem(
							cursor.getLong(columnId),
							cursor.getString(columnTitle),
							cursor.getString(columnUrl),
							cursor.getInt(columnBookmark) >= 1 ? true : false,
							null);

					result.add(item);

					count++;
					cursor.moveToNext();
				}
			}

			cursor.close();
		}

		return result;
	}

	public static ArrayList<ShortcutInfo> getRecentHistoryWithLimit(
			ContentResolver contentResolver, int limit) {
		ArrayList<ShortcutInfo> result = new ArrayList<ShortcutInfo>();

		Context context = LauncherApplication.getInstance();

		String sortOrder = "date DESC";

		Cursor historyCursor = HistoryUtil.query(context, null, null, null,
				sortOrder);

		if (historyCursor != null) {
			if (historyCursor.moveToFirst()) {
				int titleId = historyCursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int urlId = historyCursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int faviconId = historyCursor
						.getColumnIndex(Browser.BookmarkColumns.FAVICON);
				int count = 0;
				do {

					ShortcutInfo info = new ShortcutInfo();
					info.shortid = -1;
					info.title = historyCursor.getString(titleId);
					info.url = historyCursor.getString(urlId);
					info.mIcon = zuHeBitMap(
							getIconFromCursor(historyCursor, faviconId),
							context);
					info.itemIndex = count;

					count++;
					if (result.size() < 12) {
						result.add(info);
					} else {
						break;
					}
				} while (historyCursor.moveToNext());
			}

		}
		historyCursor.close();

		return result;
	}

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

	static Bitmap getIconFromCursor(Cursor c, int iconIndex) {
		byte[] data = c.getBlob(iconIndex);
		try {
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e) {
			return null;
		}
	}

	public static ArrayList<HashMap<String, Object>> getStockHistory(
			Context context) {

		ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

		String sortOrder = "date DESC";
		Cursor mCursor = HistoryUtil
				.query(context, null, null, null, sortOrder);

		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				int columnTitle = mCursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);
				int columnUrl = mCursor
						.getColumnIndex(Browser.BookmarkColumns.URL);
				int id = mCursor.getColumnIndex(Browser.BookmarkColumns._ID);
				int columnImage = mCursor
						.getColumnIndex(Browser.BookmarkColumns.FAVICON); // add
																			// LS
				int dateIndex = mCursor
						.getColumnIndex(Browser.BookmarkColumns.DATE);
				int count = 0;
				while (!mCursor.isAfterLast() && (count < mCursor.getCount())) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(Browser.BookmarkColumns._ID, mCursor.getLong(id));
					map.put(Browser.BookmarkColumns.FAVICON,
							mCursor.getBlob(columnImage));
					map.put(Browser.BookmarkColumns.TITLE,
							mCursor.getString(columnTitle));
					map.put(Browser.BookmarkColumns.URL,
							mCursor.getString(columnUrl));
					map.put(Browser.BookmarkColumns.DATE,
							mCursor.getLong(dateIndex));
					mData.add(map);
					count++;
					mCursor.moveToNext();
				}
			}
			mCursor.close();
		}
		return mData;
	}

	public static ArrayList<HashMap<String, Object>> getStockHistoryForAddShortcut(
			Context context) {

		ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

		String sortOrder = "date DESC";
		Cursor mCursor = HistoryUtil
				.query(context, null, null, null, sortOrder);

		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				int columnTitle = mCursor
						.getColumnIndex(Browser.BookmarkColumns.TITLE);

				int columnUrl = mCursor
						.getColumnIndex(Browser.BookmarkColumns.URL);

				int id = mCursor.getColumnIndex(Browser.BookmarkColumns._ID);

				int columnImage = mCursor
						.getColumnIndex(Browser.BookmarkColumns.FAVICON);

				int columnPic = mCursor.getColumnIndex("snapshot");// add
																	// LS

				int count = 0;
				while (!mCursor.isAfterLast() && (count < mCursor.getCount())) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("id", mCursor.getLong(id));
					map.put("ItemImageType",
							AddShortcutActivity.IMAGE_TYPE_HISTORY);

					map.put("ItemSnap", mCursor.getString(columnPic));
					map.put("ItemImage", mCursor.getBlob(columnImage));
					map.put("ItemText", mCursor.getString(columnTitle));
					map.put("ItemUrl", mCursor.getString(columnUrl));
					// map.put("ItemHistory",mCursor.getBlob(columnImage) );
					mData.add(map);
					count++;
					mCursor.moveToNext();
				}
			}
			mCursor.close();
		}
		return mData;
	}

	/**
	 * Delete an history record, e.g. reset the visited count and visited date
	 * if its a bookmark, or delete it if not.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @param id
	 *            The history id.
	 */
	public static void deleteHistoryRecord(String url, Context context) {
		HistoryUtil.delete(context, url);
	}

	/**
	 * Update the history: visit count and last visited date.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @param title
	 *            The title.
	 * @param url
	 *            The url.
	 * @param originalUrl
	 *            The original url
	 */
	public static void updateHistory(ContentResolver contentResolver,
			String title, String url, String originalUrl) {
		String[] colums = new String[] { Browser.BookmarkColumns._ID,
				Browser.BookmarkColumns.URL, Browser.BookmarkColumns.BOOKMARK,
				Browser.BookmarkColumns.VISITS };
		String whereClause = Browser.BookmarkColumns.URL + " = \"" + url
				+ "\" OR " + Browser.BookmarkColumns.URL + " = \""
				+ originalUrl + "\"";

		Cursor cursor = contentResolver.query(BOOKMARKS_URI, colums,
				whereClause, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {

				long id = cursor.getLong(cursor
						.getColumnIndex(Browser.BookmarkColumns._ID));
				int visits = cursor.getInt(cursor
						.getColumnIndex(Browser.BookmarkColumns.VISITS)) + 1;

				ContentValues values = new ContentValues();

				// If its not a bookmark, we can update the title. If we were
				// doing it on bookmarks, we would override the title choosen by
				// the user.
				if (cursor.getInt(cursor
						.getColumnIndex(Browser.BookmarkColumns.BOOKMARK)) != 1) {
					values.put(Browser.BookmarkColumns.TITLE, title);
				}

				values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
				values.put(Browser.BookmarkColumns.VISITS, visits);

				contentResolver.update(BOOKMARKS_URI, values,
						Browser.BookmarkColumns._ID + " = " + id, null);

			} else {
				ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.TITLE, title);
				values.put(Browser.BookmarkColumns.URL, url);
				values.put(Browser.BookmarkColumns.DATE, new Date().getTime());
				values.put(Browser.BookmarkColumns.VISITS, 1);
				values.put(Browser.BookmarkColumns.BOOKMARK, 0);

				contentResolver.insert(BOOKMARKS_URI, values);
			}

			cursor.close();
		}
	}

	/**
	 * Remove from history values prior to now minus the number of days defined
	 * in preferences. Only delete history items, not bookmarks.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 */
	public static void truncateHistory(ContentResolver contentResolver,
			String prefHistorySize) {
		int historySize;
		try {
			historySize = Integer.parseInt(prefHistorySize);
		} catch (NumberFormatException e) {
			historySize = 90;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DAY_OF_YEAR, -historySize);

		String whereClause = "(" + Browser.BookmarkColumns.BOOKMARK
				+ " = 0 OR " + Browser.BookmarkColumns.BOOKMARK
				+ " IS NULL) AND " + Browser.BookmarkColumns.DATE + " < "
				+ c.getTimeInMillis();

		try {
			contentResolver.delete(BOOKMARKS_URI, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("BookmarksProviderWrapper", "Unable to truncate history: "
					+ e.getMessage());
		}
	}

	/**
	 * Update the favicon in history/bookmarks database.
	 * 
	 * @param currentActivity
	 *            The current acitivity.
	 * @param url
	 *            The url.
	 * @param originalUrl
	 *            The original url.
	 * @param favicon
	 *            The favicon.
	 */
	public static void updateFavicon(Activity currentActivity, String url,
			String originalUrl, Bitmap favicon) {

		if (url.equals("") || favicon == null) {
			return;
		}

		BitmapDrawable icon = new BitmapDrawable(favicon);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		icon.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, os);

		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.FAVICON, os.toByteArray());
		HistoryUtil.update(currentActivity, values, url);
	}

	public static void updateSnapshot(Context context, String url,
			String originalUrl, String snapMD5) {

		if (url == null || url.equals("") || snapMD5 == null) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put("snapshot", snapMD5);

		HistoryUtil.update(context, values, url);
	}

	/**
	 * 清除本次浏览记录
	 */
	public static void cleanTheHistory(Context context) {

		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		long opentime = sp.getLong(Constants.PREFERENCES_TIME, 0);

		String selection = " date > " + opentime;
		Cursor cursor = HistoryUtil.query(context, null, selection, null, null);

		while (cursor.moveToNext()) {
			HistoryUtil.delete(context,
					cursor.getString(cursor.getColumnIndex("url")));

		}
	}

	/**
	 * Clear the history/bookmarks table.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @param clearHistory
	 *            If true, history items will be cleared.
	 * @param clearBookmarks
	 *            If true, bookmarked items will be cleared.
	 */
	public static void clearHistory(Context context) {

		HistoryUtil.deleteAll(context);
		// File path = new File(FILE_PATH);
		// if (path.exists()) {
		// path.delete();
		// }
	}

	/**
	 * Insert a full record in history/bookmarks database.
	 * 
	 * @param contentResolver
	 *            The content resolver.
	 * @param title
	 *            The record title.
	 * @param url
	 *            The record url.
	 * @param visits
	 *            The record visit count.
	 * @param date
	 *            The record last visit date.
	 * @param created
	 *            The record bookmark creation date.
	 * @param bookmark
	 *            The bookmark flag.
	 */
	public static void insertRawRecord(ContentResolver contentResolver,
			String title, String url, int visits, long date, long created,
			int bookmark) {
		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.TITLE, title);
		values.put(Browser.BookmarkColumns.URL, url);
		values.put(Browser.BookmarkColumns.VISITS, visits);

		if (date > 0) {
			values.put(Browser.BookmarkColumns.DATE, date);
		} else {
			values.putNull(Browser.BookmarkColumns.DATE);
		}

		if (created > 0) {
			values.put(Browser.BookmarkColumns.CREATED, created);
		} else {
			values.putNull(Browser.BookmarkColumns.CREATED);
		}

		if (bookmark > 0) {
			values.put(Browser.BookmarkColumns.BOOKMARK, 1);
		} else {
			values.put(Browser.BookmarkColumns.BOOKMARK, 0);
		}

		contentResolver.insert(BOOKMARKS_URI, values);
	}

	/**
	 * Suggestions.
	 */
	/**
	 * Get a cursor for suggestions, given a search pattern. Search on history
	 * and bookmarks, on title and url. The result list is sorted based on each
	 * result note.
	 * 
	 * @see UrlSuggestionItem for how a note is computed.
	 * @param contentResolver
	 *            The content resolver.
	 * @param pattern
	 *            The pattern to search for.
	 * @param lookInWeaveBookmarks
	 *            If true, suggestions will include bookmarks from weave.
	 * @return A cursor of suggections.
	 */
	public static Cursor getUrlSuggestions(ContentResolver contentResolver,
			String pattern, boolean lookInWeaveBookmarks) {
		MatrixCursor cursor = new MatrixCursor(new String[] {
				UrlSuggestionCursorAdapter.URL_SUGGESTION_ID,
				UrlSuggestionCursorAdapter.URL_SUGGESTION_TITLE,
				UrlSuggestionCursorAdapter.URL_SUGGESTION_URL,
				UrlSuggestionCursorAdapter.URL_SUGGESTION_TYPE });

		if ((pattern != null) && (pattern.length() > 0)) {

			String sqlPattern = "%" + pattern + "%";

			List<UrlSuggestionItem> results = new ArrayList<UrlSuggestionItem>();

			Cursor stockCursor = contentResolver.query(BOOKMARKS_URI,
					sHistoryBookmarksProjection, Browser.BookmarkColumns.TITLE
							+ " LIKE '" + sqlPattern + "' OR "
							+ Browser.BookmarkColumns.URL + " LIKE '"
							+ sqlPattern + "'", null, null);

			if (stockCursor != null) {
				if (stockCursor.moveToFirst()) {
					int titleId = stockCursor
							.getColumnIndex(Browser.BookmarkColumns.TITLE);
					int urlId = stockCursor
							.getColumnIndex(Browser.BookmarkColumns.URL);
					int bookmarkId = stockCursor
							.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);

					do {
						boolean isFolder = stockCursor.getInt(bookmarkId) > 0 ? true
								: false;
						results.add(new UrlSuggestionItem(pattern, stockCursor
								.getString(titleId), stockCursor
								.getString(urlId), isFolder ? 2 : 1));

					} while (stockCursor.moveToNext());
				}

				stockCursor.close();
			}

			// Sort results.
			Collections.sort(results, new UrlSuggestionItemComparator());

			// Log.d("Results", Integer.toString(results.size()));

			// Copy results to the output MatrixCursor.
			int idCounter = -1;
			for (UrlSuggestionItem item : results) {
				idCounter++;

				String[] row = new String[4];
				row[0] = Integer.toString(idCounter);
				row[1] = item.getTitle();
				row[2] = item.getUrl();
				row[3] = Integer.toString(item.getType());

				cursor.addRow(row);
			}
		}

		return cursor;
	}

	public static Cursor getUrlSuggestions_history(
			ContentResolver contentResolver, String pattern, Context context) {
		MatrixCursor cursor = new MatrixCursor(new String[] {
				UrlSuggestionCursorAdapter.URL_SUGGESTION_ID,
				UrlSuggestionCursorAdapter.URL_SUGGESTION_TITLE,
				UrlSuggestionCursorAdapter.URL_SUGGESTION_URL,
				UrlSuggestionCursorAdapter.URL_SUGGESTION_TYPE });

		ArrayList<String> map = new ArrayList<String>();

		if ((pattern != null) && (pattern.length() > 0)) {

			String sqlPattern = "%" + pattern + "%";

			List<UrlSuggestionItem> results = new ArrayList<UrlSuggestionItem>();

			String bookmarksSelection = Browser.BookmarkColumns.TITLE
					+ " like ? or " + Browser.BookmarkColumns.URL + " like ?";
			Cursor stockCursor = contentResolver.query(BOOKMARKS_URI,
					sHistoryBookmarksProjection, bookmarksSelection,
					new String[] { sqlPattern, sqlPattern }, null);

			if (stockCursor != null) {
				if (stockCursor.moveToFirst()) {
					int titleId = stockCursor
							.getColumnIndex(Browser.BookmarkColumns.TITLE);
					int urlId = stockCursor
							.getColumnIndex(Browser.BookmarkColumns.URL);
					int bookmarkId = stockCursor
							.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);

					do {
						boolean isFolder = stockCursor.getInt(bookmarkId) > 0 ? true
								: false;
						if ((!("".equals(stockCursor.getString(titleId))))
								&& (stockCursor.getString(titleId) != null)
								&& (!("".equals(stockCursor.getString(urlId))))
								&& (stockCursor.getString(urlId) != null)) {
							results.add(new UrlSuggestionItem(pattern,
									stockCursor.getString(titleId), stockCursor
											.getString(urlId), isFolder ? 2 : 1));
							map.add(stockCursor.getString(urlId));
						}

					} while (stockCursor.moveToNext());
				}

				stockCursor.close();
			}

			String selection = "title like ? or url like ?";
			Cursor historyCursor = HistoryUtil.query(context, null, selection,
					new String[] { sqlPattern, sqlPattern }, null);

			if (historyCursor != null) {
				if (historyCursor.moveToFirst()) {
					int titleId = historyCursor.getColumnIndex("title");
					int urlId = historyCursor.getColumnIndex("url");

					do {
						if (!map.contains(historyCursor.getString(urlId))) {
							results.add(new UrlSuggestionItem(pattern,
									historyCursor.getString(titleId),
									historyCursor.getString(urlId), 1));
						}
					} while (historyCursor.moveToNext());
				}

				historyCursor.close();
			}

			// Sort results.
			Collections.sort(results, new UrlSuggestionItemComparator());

			// Copy results to the output MatrixCursor.
			int idCounter = -1;
			for (UrlSuggestionItem item : results) {
				idCounter++;

				String[] row = new String[4];
				row[0] = Integer.toString(idCounter);
				row[1] = item.getTitle();
				row[2] = item.getUrl();
				row[3] = Integer.toString(item.getType());

				cursor.addRow(row);
			}
		}

		return cursor;
	}

}
