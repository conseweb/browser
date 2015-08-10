package com.qing.browser.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * һ��ҵ����
 */
public class Dao {
	private DBHelper dbHelper;

	public Dao(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * ��ѯ�ȴ�����
	 */
	public DownloadItem query(Context context) {
		DownloadItem info = null;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select _id, start_pos, end_pos,compelete_size,url, downing, fileName from download_info  where downing != "
				+ DownloadManager.DOWNLOAD_STATE_PAUSE;
		Cursor cursor = database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			info = new DownloadItem(cursor.getInt(0), cursor.getInt(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getString(4),
					cursor.getInt(5), cursor.getString(6));
		}
		cursor.close();
		return info;

	}

	public void queryAndUpdateInfos(Context context) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select _id, start_pos, end_pos,compelete_size,url, downing, fileName from download_info ";
		Cursor cursor = database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DownloadItem info = new DownloadItem(cursor.getInt(0),
					cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
					cursor.getString(4), cursor.getInt(5), cursor.getString(6));

			String update = "update download_info set downing = ? where url=?";
			Object[] bindArgs = { DownloadManager.DOWNLOAD_STATE_PAUSE,
					info.getUrl() };
			database.execSQL(update, bindArgs);

		}
		cursor.close();
		database.close();
	}

	/**
	 * ���� ���صľ�����Ϣ
	 */
	public void saveInfo(DownloadItem info) {
		Log.d("H", "saveInfo " + info.toString());
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		String sql = "insert into download_info( start_pos, end_pos,compelete_size,url,downing, fileName) values (?,?,?,?,?,?)";
		Object[] bindArgs = { info.getStartPos(), info.getEndPos(),
				info.getCompeleteSize(), info.getUrl(),
				info.getDownloadState(), info.getFileName() };
		database.execSQL(sql, bindArgs);
		database.close();
	}

	/**
	 * ���� ���صľ�����Ϣ
	 */
	public void saveInfos(List<DownloadItem> infos) {
		SQLiteDatabase database = dbHelper.getWritableDatabase();
		for (DownloadItem info : infos) {
			String sql = "insert into download_info(start_pos, end_pos,compelete_size,url, downing, fileName) values (?,?,?,?,?,?)";
			Object[] bindArgs = { info.getStartPos(), info.getEndPos(),
					info.getCompeleteSize(), info.getUrl(),
					info.getDownloadState(), info.getFileName() };
			database.execSQL(sql, bindArgs);
		}
		database.close();
	}

	/**
	 * �õ����ؾ�����Ϣ
	 */
	public DownloadItem getInfo(String urlstr) {
		DownloadItem info = null;
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select _id, start_pos, end_pos,compelete_size,url,downing, fileName from download_info where url=?";
		Cursor cursor = database.rawQuery(sql, new String[] { urlstr });
		while (cursor.moveToNext()) {
			info = new DownloadItem(cursor.getInt(0), cursor.getInt(1),
					cursor.getInt(2), cursor.getInt(3), cursor.getString(4),
					cursor.getInt(5), cursor.getString(6));
		}
		cursor.close();
		database.close();
		return info;
	}

	/**
	 * �õ����ؾ�����Ϣ
	 */
	public List<DownloadItem> getInfos(Context context) {
		List<DownloadItem> list = new ArrayList<DownloadItem>();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "select _id, start_pos, end_pos,compelete_size,url, downing, fileName from download_info";
		Cursor cursor = database.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			DownloadItem info = new DownloadItem(cursor.getInt(0),
					cursor.getInt(1), cursor.getInt(2), cursor.getInt(3),
					cursor.getString(4), cursor.getInt(5), cursor.getString(6));
			list.add(0, info);
		}
		cursor.close();
		database.close();
		return list;
	}

	/**
	 * �������ݿ��е�������Ϣ
	 */
	public void updataInfos(int compeleteSize, int downing, String urlstr) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		String sql = "update download_info set compelete_size = ?, downing = ? where url=?";
		Object[] bindArgs = { compeleteSize, downing, urlstr };
		database.execSQL(sql, bindArgs);
	}

	/**
	 * �ر����ݿ�
	 */
	public void closeDb() {
		dbHelper.close();
	}

	/**
	 * ������ɺ�ɾ�����ݿ��е�����
	 */
	public void delete(String url) {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		database.delete("download_info", "url=?", new String[] { url });

	}

	/**
	 * �������
	 */
	public void clean() {
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		database.execSQL("DROP TABLE download_info;");
		database.execSQL(DBHelper.sql);
	}
}
