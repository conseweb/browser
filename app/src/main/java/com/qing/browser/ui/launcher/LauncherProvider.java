package com.qing.browser.ui.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.qing.browser.R;
import com.qing.browser.xml.XmlUtils;

public class LauncherProvider extends ContentProvider {
	private static final String TAG = "Launcher.LauncherProvider";
	private static final boolean LOGD = false;

	private static final String DATABASE_NAME = "launcher2.db";

	private static final int DATABASE_VERSION = 13;

	static final String AUTHORITY = "com.qing.browser.ui.launcher.settings";

	static final String TABLE_FAVORITES = "favorites";
	static final String PARAMETER_NOTIFY = "notify";

	private SQLiteOpenHelper mOpenHelper;

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
		private static final String TAG_FAVORITES = "favorites";
		private static final String TAG_FAVORITE = "favorite";

		private final static String createTabstr = "CREATE TABLE  "
				+ TAG_FAVORITES + " (" + "_id INTEGER PRIMARY KEY,"
				+ "shortid INTEGER," + "title TEXT," + "url TEXT,"
				+ "container INTEGER," + "screen INTEGER,"
				+ "itemIndex INTEGER," + "itemType INTEGER,"
				+ "iconType INTEGER," + "iconResource TEXT," + "iconUrl TEXT,"
				+ "icon BLOB," + "userType INTEGER," + "updateFlag INTEGER"
				+ ");";

		private final Context mContext;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (LOGD)
				Log.d(TAG, "creating new launcher database");

			db.execSQL(createTabstr);

			loadFavorites(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			if (newVersion <= oldVersion) {
				// 数据库不升级
				return;
			} else {
				Log.i("H", "LauncherProvider 桌面数据库升级  oldVersion=" + oldVersion
						+ " newVersion=" + newVersion);

				// 1, Rename table.
				db.execSQL("ALTER TABLE " + TAG_FAVORITES + " RENAME TO _temp_"
						+ TAG_FAVORITES + ";");

				// 2, Create table
				db.execSQL(createTabstr);

				// 3, Load data
				db.execSQL("INSERT INTO " + TAG_FAVORITES
						+ " SELECT * FROM _temp_" + TAG_FAVORITES + ";");

				if (oldVersion == 10 && newVersion == 11) {
					db.execSQL("delete from favorites where userType=2;");
					loadFavorites(db);
				}
				if (oldVersion == 11 && newVersion == 12) {
					db.execSQL("delete from favorites where userType=2;");
					loadFavorites(db);
				}
				if (oldVersion == 12 && newVersion == 13) {
					db.execSQL("delete from favorites where userType=2;");
					loadFavorites(db);
				}

				// 4, Drop the temporary table.
				db.execSQL("DROP TABLE _temp_" + TAG_FAVORITES + ";");

			}
		}

		/**
		 * Loads the default set of favorite packages from an xml file.
		 * 
		 * @param db
		 *            The database to write the values into
		 */
		private int loadFavorites(SQLiteDatabase db) {
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ContentValues values = new ContentValues();

			int i = 0;
			try {
				XmlResourceParser parser = mContext.getResources().getXml(
						R.xml.default_workspace);
				AttributeSet attrs = Xml.asAttributeSet(parser);
				XmlUtils.beginDocument(parser, TAG_FAVORITES);

				final int depth = parser.getDepth();

				int type;
				while (((type = parser.next()) != XmlPullParser.END_TAG || parser
						.getDepth() > depth)
						&& type != XmlPullParser.END_DOCUMENT) {

					if (type != XmlPullParser.START_TAG) {
						continue;
					}

					boolean added = false;
					final String name = parser.getName();

					TypedArray a = mContext.obtainStyledAttributes(attrs,
							R.styleable.Favorite);

					values.clear();
					values.put(LauncherSettings.Favorites.SHORTID,
							a.getInt(R.styleable.Favorite_shortid, 0));
					values.put(LauncherSettings.Favorites.CONTAINER,
							a.getString(R.styleable.Favorite_container));
					values.put(LauncherSettings.Favorites.SCREEN,
							a.getString(R.styleable.Favorite_screen));
					values.put(LauncherSettings.Favorites.ICON_TYPE,
							a.getString(R.styleable.Favorite_icontype));
					values.put(LauncherSettings.Favorites.ICON_RESOURCE,
							a.getString(R.styleable.Favorite_iconresource));
					values.put(LauncherSettings.Favorites.ITEM_INDEX,
							a.getString(R.styleable.Favorite_itemindex));
					values.put(LauncherSettings.Favorites.ITEM_TYPE,
							a.getString(R.styleable.Favorite_itemtype));
					values.put(LauncherSettings.Favorites.TITLE,
							a.getString(R.styleable.Favorite_title));
					values.put(LauncherSettings.Favorites.URL,
							a.getString(R.styleable.Favorite_url));
					values.put(LauncherSettings.Favorites.USER_TYPE,
							LauncherSettings.Favorites.USER_TYPE_DEFAULT);

					Context context = LauncherApplication.getInstance();
					Resources res = context.getResources();
					Drawable drawable = res.getDrawable(a.getResourceId(
							R.styleable.Favorite_icon,
							R.drawable.hotseat_browser_bg));
					Bitmap bitmap = Utilities.drawableToBitmap(drawable);

					writeBitmap(values, bitmap);
					if (TAG_FAVORITE.equals(name)) {
						added = addShortcut(db, values, a);
					}

					if (added)
						i++;

					a.recycle();
				}
			} catch (XmlPullParserException e) {
				Log.w(TAG, "Got exception parsing favorites.", e);
			} catch (IOException e) {
				Log.w(TAG, "Got exception parsing favorites.", e);
			}

			return i;
		}

		static byte[] flattenBitmap(Bitmap bitmap) {
			// Try go guesstimate how much space the icon will take when
			// serialized
			// to avoid unnecessary allocations/copies during the write.
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

		static void writeBitmap(ContentValues values, Bitmap bitmap) {
			if (bitmap != null) {
				byte[] data = flattenBitmap(bitmap);
				values.put(LauncherSettings.Favorites.ICON, data);
			}
		}

		private boolean addShortcut(SQLiteDatabase db, ContentValues values,
				TypedArray a) {
			db.insert(TABLE_FAVORITES, null, values);
			return true;
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
