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
import android.text.TextUtils;
import android.util.Log;


public class HistoryProvider  extends ContentProvider {
	private static final String TAG = "HistoryProvider";

	static final String AUTHORITY = "com.qing.browser.providers.history";

	static final String TABLE_FAVORITES = "history";
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


		private final static String TAB_history = "history";
		private final static String createSQLstr = "create table "
				+ TAB_history
				+ "(_id integer PRIMARY KEY AUTOINCREMENT,title TEXT,"
				+ "url TEXT,date LONG,created integer,visits integer,user_entered integer,favicon BLOB,snapshot TEXT)";

		private static final int DATABASE_VERSION = 3;

		public DatabaseHelper(Context context) {
			super(context, "history.db", null, DATABASE_VERSION);
		}

		/**
		 * 在history.db数据库下创建一个history表存储下载信息
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createSQLstr);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			if (newVersion <= oldVersion) {
				// 数据库不升级
				return;
			} else {
				Log.i("H", "HistoryDBHelper 历史数据库升级  oldVersion=" + oldVersion
						+ " newVersion=" + newVersion);

				// 1, Rename table.
				db.execSQL("ALTER TABLE " + TAB_history + " RENAME TO _temp_"
						+ TAB_history + ";");

				// 2, Create table
				db.execSQL(createSQLstr);

				// 3, Load data
				db.execSQL("INSERT INTO "
						+ TAB_history
						+ "(title,url,date,created,visits,user_entered,favicon) SELECT title,url,date,created,visits,user_entered,favicon FROM _temp_"
						+ TAB_history + ";");

				// 4, Drop the temporary table.
				db.execSQL("DROP TABLE _temp_" + TAB_history + ";");

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
