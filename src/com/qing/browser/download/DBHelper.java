package com.qing.browser.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * ����һ�����ݿ������
 */
public class DBHelper extends SQLiteOpenHelper {

	public final static String downloadTab = "download_info";
	public final static String sql = "create table " + downloadTab
			+ "(_id integer PRIMARY KEY AUTOINCREMENT, "
			+ "start_pos integer, end_pos integer, compelete_size integer, "
			+ "downing integer, url char, fileName char)";

	private static final int DATABASE_VERSION = 1;

	// appdownload.db-->���ݿ���
	public DBHelper(Context context) {
		super(context, "qingdownload.db", null, DATABASE_VERSION);
	}

	/**
	 * ��appdownload.db���ݿ��´���һ��download_info��洢������Ϣ
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion <= oldVersion) {
			// ���ݿⲻ����
			return;
		} else {
			Log.i("H", "DBHelper �������ݿ�����  oldVersion=" + oldVersion
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
