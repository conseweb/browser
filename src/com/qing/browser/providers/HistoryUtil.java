package com.qing.browser.providers;

import java.io.ByteArrayOutputStream;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.qing.browser.R;

public class HistoryUtil {

	static final String PARAMETER_NOTIFY = "notify";

	public static final String HistoryColumns_TITTLE = "title";
	public static final String HistoryColumns_URL = "url";
	public static final String HistoryColumns_DATE = "date";
	public static final String HistoryColumns_CREATED = "created";
	public static final String HistoryColumns_VISITS = "visits";
	public static final String HistoryColumns_USER_ENTERED = "user_entered";
	public static final String HistoryColumns_FAVICON = "favicon";
	public static final String HistoryColumns_SNAPSHOT= "snapshot";

	public static int insert(Context context, ContentValues values) {
		int historyid = 0;
		final ContentResolver cr = context.getContentResolver();
		Uri result = cr.insert(HistoryProvider.CONTENT_URI, values);
		if (result != null) {
			historyid = Integer.parseInt(result.getPathSegments().get(1));
		}
		return historyid;
	}

	public static Cursor query(Context context, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		final ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(HistoryProvider.CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);

		return c;
	}

	public static void update(Context context, ContentValues values, long id) {
		final ContentResolver cr = context.getContentResolver();
		cr.update(HistoryProvider.getContentUri(id), values, null, null);
	}
	
	public static void update(Context context, ContentValues values, String url) {
		final ContentResolver cr = context.getContentResolver();
		String whereClause = HistoryColumns_URL + " = \'"+url+"\'";
		cr.update(HistoryProvider.CONTENT_URI, values, whereClause, null);
	}
	

	/**
	 * 保存 下载的具体信息
	 */
	public static void saveName(Context context, String title, String url,
			long date, Bitmap icon) {
		if (icon == null) {
			icon = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.hotseat_browser_bg);
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 将Bitmap压缩成PNG编码，质量为100%存储
		icon.compress(Bitmap.CompressFormat.PNG, 100, os);

		ContentValues values = new ContentValues();
		if (title != null) {
			values.put(HistoryColumns_TITTLE, title);
		}
		if (url != null) {
			values.put(HistoryColumns_URL, url);
		}
		values.put(HistoryColumns_DATE, date);

		values.put(HistoryColumns_FAVICON, os.toByteArray());

		insert(context, values);
	}

	public static boolean isName(Context context, String url) {

		ContentResolver cr = context.getContentResolver();
		String whereClause = HistoryColumns_URL + " = \'"+url+"\'";
		Cursor c = cr.query(HistoryProvider.CONTENT_URI, null, whereClause,
				null, null);
		if (c != null) {
			if (c.moveToFirst() == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public static void delete(Context context, String url) {
		ContentResolver cr = context.getContentResolver();
		String whereClause = HistoryColumns_URL + " = \'"+url+"\'";
		cr.delete(HistoryProvider.CONTENT_URI, whereClause, null);
		
	}
	
	public static void deleteAll(Context context) {
		ContentResolver cr = context.getContentResolver();
		cr.delete(HistoryProvider.CONTENT_URI, null, null);
		
	}

	public static void updateHistory(Context context, String title, String url,
			long date) {

		if (url.equals("")) {
			return;
		}
		ContentResolver cr = context.getContentResolver();
		String whereClause = HistoryColumns_URL + " = \'"+url+"\'";

		ContentValues values = new ContentValues();
		values.put(HistoryColumns_TITTLE, title);
		values.put(HistoryColumns_URL, url);
		values.put(HistoryColumns_DATE, date);

		cr.update(HistoryProvider.CONTENT_URI, values, whereClause, null);

	}

}
