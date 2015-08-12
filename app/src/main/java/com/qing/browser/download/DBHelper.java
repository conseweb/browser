package com.qing.browser.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 建立一个数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper {

	public final static String downloadTab = "download_info";
	public final static String sql = "create table " + downloadTab
			+ "(_id integer PRIMARY KEY AUTOINCREMENT, "
			+ "start_pos integer, end_pos integer, compelete_size integer, "
			+ "downing integer, url char, fileName char)";

	private static final int DATABASE_VERSION = 1;

	// appdownload.db-->数据库名
	public DBHelper(Context context) {
		super(context, "qingdownload.db", null, DATABASE_VERSION);
	}

	/**
	 * 在appdownload.db数据库下创建一个download_info表存储下载信息
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion <= oldVersion) {
			// 数据库不升级
			return;
		} else {
			Log.i("H", "DBHelper 下载数据库升级  oldVersion=" + oldVersion
					+ " newVersion=" + newVersion);

			// 1, Rename table.
			db.execSQL("ALTER TABLE " + downloadTab + " RENAME TO _temp_"
					+ downloadTab + ";");

			// 2, Create table
			db.execSQL(sql);

			// 3, Load data
			db.execSQL("INSERT INTO " + downloadTab + " SELECT * FROM _temp_"
					+ downloadTab + ";");

			// 4, Drop the temporary table.
			db.execSQL("DROP TABLE _temp_" + downloadTab + ";");

		}
	}

}
