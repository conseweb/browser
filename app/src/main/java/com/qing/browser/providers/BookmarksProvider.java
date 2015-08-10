package com.qing.browser.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;

public class BookmarksProvider extends ContentProvider {
	private static final String TAG = "BookmarksProvider";
	private static final boolean LOGD = false;

	private static final String DATABASE_NAME = "bookmarks.db";
	public static final String BOOKMARKS_TABLE = "bookmarks";

	private static final int DATABASE_VERSION = 2;

	static final String AUTHORITY = "com.qing.browser.providers.bookmarks";

	static final String TABLE_FAVORITES = "bookmarks";
	static final String PARAMETER_NOTIFY = "notify";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_FAVORITES);

	private SQLiteOpenHelper mOpenHelper;

	static Uri getContentUri(long id) {
		return Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAVORITES + "/"
				+ id);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		SqlArguments args = new SqlArguments(uri, null, null);
		if (TextUtils.isEmpty(args.where)) {
			return "vnd.android.cursor.dir/" + args.table;
		} else {
			return "vnd.android.cursor.item/" + args.table;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(args.table);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor result = qb.query(db, projection, args.where, args.args, null,
				null, sortOrder);
		result.setNotificationUri(getContext().getContentResolver(), uri);

		return result;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final long rowId = db.insert(args.table, null, initialValues);
		if (rowId <= 0)
			return null;

		uri = ContentUris.withAppendedId(uri, rowId);
		sendNotify(uri);

		return uri;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			int numValues = values.length;
			for (int i = 0; i < numValues; i++) {
				if (db.insert(args.table, null, values[i]) < 0)
					return 0;
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		sendNotify(uri);
		return values.length;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.delete(args.table, args.where, args.args);
		if (count > 0)
			sendNotify(uri);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.update(args.table, values, args.where, args.args);
		if (count > 0)
			sendNotify(uri);

		return count;
	}

	private void sendNotify(Uri uri) {
		String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
		if (notify == null || "true".equals(notify)) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final String BOOKMARKS_TABLE_CREATE = "CREATE TABLE "
				+ BOOKMARKS_TABLE + " (" + Browser.BookmarkColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ Browser.BookmarkColumns.TITLE + " TEXT, "
				+ Browser.BookmarkColumns.URL + " TEXT, "
				+ Browser.BookmarkColumns.VISITS + " INTEGER, "
				+ Browser.BookmarkColumns.DATE + " LONG, "
				+ Browser.BookmarkColumns.CREATED + " LONG, "
				+ Browser.BookmarkColumns.BOOKMARK + " INTEGER, "
				+ BookmarksUtil.BookmarkColumns_UUID + " TEXT, "
				+ Browser.BookmarkColumns.FAVICON
				+ " BLOB DEFAULT NULL,snapshot TEXT);";

		private final Context mContext;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (LOGD)
				Log.d(TAG, "creating new launcher database");

			db.execSQL(BOOKMARKS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion <= oldVersion) {
				// 数据库不升级
				return;
			} else {
				Log.i("H", " BookmarksDataBaseUtil 书签数据库升级  oldVersion="
						+ oldVersion + " newVersion=" + newVersion);
				// 1, Rename table.
				db.execSQL("ALTER TABLE " + BOOKMARKS_TABLE
						+ " RENAME TO _temp_" + BOOKMARKS_TABLE + ";");

				// 2, Create table
				db.execSQL(BOOKMARKS_TABLE_CREATE);

				// 3, Load data
				db.execSQL("INSERT INTO " + BOOKMARKS_TABLE + "("
						+ Browser.BookmarkColumns.TITLE + ","
						+ Browser.BookmarkColumns.URL + ","
						+ Browser.BookmarkColumns.VISITS + ","
						+ Browser.BookmarkColumns.DATE + ","
						+ Browser.BookmarkColumns.CREATED + ","
						+ Browser.BookmarkColumns.BOOKMARK + ","
						+ BookmarksUtil.BookmarkColumns_UUID + ","
						+ Browser.BookmarkColumns.FAVICON + ") SELECT "
						+ Browser.BookmarkColumns.TITLE + ","
						+ Browser.BookmarkColumns.URL + ","
						+ Browser.BookmarkColumns.VISITS + ","
						+ Browser.BookmarkColumns.DATE + ","
						+ Browser.BookmarkColumns.CREATED + ","
						+ Browser.BookmarkColumns.BOOKMARK + ","
						+ BookmarksUtil.BookmarkColumns_UUID + ","
						+ Browser.BookmarkColumns.FAVICON + " FROM _temp_"
						+ BOOKMARKS_TABLE + ";");

				// 4, Drop the temporary table.
				db.execSQL("DROP TABLE _temp_" + BOOKMARKS_TABLE + ";");

			}
		}
	}

	/**
	 * Build a query string that will match any row where the column matches
	 * anything in the values list.
	 */
	static String buildOrWhereString(String column, int[] values) {
		StringBuilder selectWhere = new StringBuilder();
		for (int i = values.length - 1; i >= 0; i--) {
			selectWhere.append(column).append("=").append(values[i]);
			if (i > 0) {
				selectWhere.append(" OR ");
			}
		}
		return selectWhere.toString();
	}

	static class SqlArguments {
		public final String table;
		public final String where;
		public final String[] args;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 1) {
				this.table = url.getPathSegments().get(0);
				this.where = where;
				this.args = args;
			} else if (url.getPathSegments().size() != 2) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException(
						"WHERE clause not supported: " + url);
			} else {
				this.table = url.getPathSegments().get(0);
				this.where = "_id=" + ContentUris.parseId(url);
				this.args = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 1) {
				table = url.getPathSegments().get(0);
				where = null;
				args = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}
	}
}
