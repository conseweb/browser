package com.qing.browser.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.R.color;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.utils.L;
import com.qing.browser.R;
import com.qing.browser.activities.AboutActivity;
import com.qing.browser.ui.launcher.Launcher;

public class Tools {
	static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
	public static final int MB_2_BYTE = 1024 * 1024;
	public static final int KB_2_BYTE = 1024;

	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static CharSequence getAppSize(long size) {
		if (size <= 0) {
			return "0M";
		}
		if (size >= MB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE))
					.append("M");
		} else if (size >= KB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE))
					.append("K");
		} else {
			return size + "B";
		}
	}

	/**
	 * ��ȡָ���ļ���С
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
			Log.e("��ȡ�ļ���С", "�ļ�������!");
		}
		return size;
	}

	/**
	 * Get the current package version.
	 * 
	 * @return The current version.
	 */
	public static String getVersion(Activity context) {
		String result = "";
		try {

			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);

			result = String.format("%s", info.versionName);

		} catch (NameNotFoundException e) {
			Log.w(AboutActivity.class.toString(),
					"Unable to get application version: " + e.getMessage());
			result = "Unable to get application version.";
		}

		return result;
	}

	/**
	 * ��ȡIMEI���� Returns the unique device ID, for example, the IMEI for GSM and
	 * the MEID for CDMA phones. Return null if device ID is not available.
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static int getSimStatus(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimState();
	}

	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	public static String getVersion(Context context) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getApplicationContext().getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return packageInfo == null ? "100" : packageInfo.versionName;
	}

	// �ж������Ƿ����
	public static boolean isConnectInternet(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	public static boolean sdCardExist() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * ��ȡ�ֻ�ϵͳ�İ汾
	 */
	public static String getSystembVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * ��ȡ�ֻ�������
	 */
	public static String getLanguage() {
		String language = Locale.getDefault().getLanguage();
		return language;
	}

	/**
	 * ��ȡ��Ļ�ߴ�
	 */
	public static String getDisplayInfo(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		int width = display.getWidth();
		int height = display.getHeight();

		return width + "X" + height;
	}

	/**
	 * ��ȡ�ֻ�����
	 */
	public static String getNativeNumber(Context context) {
		String nativeNumber = "";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		nativeNumber = telephonyManager.getLine1Number();
		return nativeNumber;
	}

	/**
	 * ��ȡ������
	 */
	public static String getProviders(Context context) {
		String providers = "0";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = telephonyManager.getSimOperator();
		// IMSI��ǰ��3λ460�ǹ��ң������ź���2λ00 02���й��ƶ���01���й���ͨ��03���й����š�
		if (simOperator != null) {
			if (simOperator.equals("46000") || simOperator.equals("46002")) {
				providers = "1"; // "�й��ƶ�";
			} else if (simOperator.equals("46001")) {
				providers = "2"; // "�й���ͨ";
			} else if (simOperator.equals("46003")) {
				providers = "3"; // "�й�����";
			}
		}
		return providers;

	}

	public static String getIsSystemApp(Context context) {
		String status = "0";
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getApplicationContext().getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			int flags = packageInfo.applicationInfo.flags;
			if (flags == ApplicationInfo.FLAG_SYSTEM
					|| flags == ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) {
				status = "1";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return status;
	}

	/**
	 * ��ȡ��Ļ�汾
	 */
	public static String getScreenVersion(Context context) {
		return Constants.SCREEN_VERSION;
	}

	/**
	 * ��ȡ���±��
	 */
	public static void setMiddleUpdateVersion(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(Constants.UPDATE_VERSION_NAME_MIDDLE, value);
		editor.commit();
	}

	/**
	 * ��ȡ����ʱ��
	 */
	public static void setMiddleUpdateTime(Context context, long value) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong(Constants.UPDATE_VERSION_NAME_MIDDLE_UPDATETIME, value);
		editor.commit();
	}

	public static boolean isMiddleUpdate(Context context) {
		boolean ret = false;
		Date datenow = new Date();
		long nowTime = datenow.getTime();
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		long right_before_time = sp.getLong(
				Constants.UPDATE_VERSION_NAME_MIDDLE_UPDATETIME, 0);

		if (nowTime - right_before_time >= Constants.UPDATE_TIME_INTERVAL) {
			ret = true;
		}
		return ret;
	}

	/**
	 * ��ȡ���±��
	 */
	public static void setRightUpdateVersion(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(Constants.UPDATE_VERSION_NAME_RIGHT, value);
		editor.commit();
	}

	/**
	 * ��ȡ����ʱ��
	 */
	public static void setRightUpdateTime(Context context, long value) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong(Constants.UPDATE_VERSION_NAME_RIGHT_UPDATETIME, value);
		editor.commit();
	}

	public static boolean isRightUpdate(Context context) {
		boolean ret = false;
		Date datenow = new Date();
		long nowTime = datenow.getTime();
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		long right_before_time = sp.getLong(
				Constants.UPDATE_VERSION_NAME_RIGHT_UPDATETIME, 0);

		if (nowTime - right_before_time >= Constants.UPDATE_TIME_INTERVAL) {
			ret = true;
		}
		return ret;
	}

	/**
	 * ��ȡ���±��
	 */
	public static void setFaxianUpdateVersion(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(Constants.UPDATE_VERSION_NAME_FAXIAN, value);
		editor.commit();

	}

	/**
	 * ��ȡ���±��
	 */
	public static int getMiddleUpdateVersion(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		int ver = sp.getInt(Constants.UPDATE_VERSION_NAME_MIDDLE,
				Constants.UPDATE_VERSION_MIDDLE);
		return ver;
	}

	/**
	 * ��ȡ���±��
	 */
	public static int getRightUpdateVersion(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		int ver = sp.getInt(Constants.UPDATE_VERSION_NAME_RIGHT,
				Constants.UPDATE_VERSION_RIGHT);
		return ver;
	}

	/**
	 * ��ȡ���±��
	 */
	public static int getFaxianUpdateVersion(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		int ver = sp.getInt(Constants.UPDATE_VERSION_NAME_FAXIAN,
				Constants.UPDATE_VERSION_FAXIAN);
		return ver;
	}

	private final static String MIDDLE_SCREEN = "1";
	private final static String RIGHT_SCREEN = "2";
	private final static String FAXIAN_SCREEN = "3";

	public static String getMiddleBookmarkString(Context context) {
		String postString = "";
		String ver = getScreenVersion(context);
		int id = getMiddleUpdateVersion(context);
		postString += "&ver=" + ver;
		postString += "&id=" + id;
		postString += "&loc=" + MIDDLE_SCREEN;

		return postString;
	}

	public static String getRightBookmarkString(Context context) {
		String postString = "";
		String ver = getScreenVersion(context);
		int id = getRightUpdateVersion(context);
		postString += "&ver=" + ver;
		postString += "&id=" + id;
		postString += "&loc=" + RIGHT_SCREEN;

		return postString;
	}

	public static String getFaxianBookmarkString(Context context) {
		String postString = "";
		String ver = getScreenVersion(context);
		int id = getFaxianUpdateVersion(context);
		postString += "&ver=" + ver;
		postString += "&id=" + id;
		postString += "&loc=" + FAXIAN_SCREEN;

		return postString;
	}

	/**
	 * ��װPOST�ִ�
	 * 
	 * @param context
	 * @return
	 */
	public static String getPoststring(Context context) {
		// ���趨��������,����String
		String postString = "";
		String imei = getIMEI(context);
		String imsi = getIMSI(context);
		String version = getVersion(context);
		String sjhm = getNativeNumber(context);
		String pmcc = getDisplayInfo(context);
		String xtyy = getLanguage();
		String xtbb = getSystembVersion();
		String oper = getProviders(context);
		String lwfs = getConnectionMethod(context) + "";
		String status = getIsSystemApp(context);
		String prod = Constants.PROD;

		postString += "&imei=" + imei;
		postString += "&imsi=" + imsi;
		postString += "&VERS=" + version;
		postString += "&phone=" + sjhm;
		postString += "&PMCC=" + pmcc;
		postString += "&XTYY=" + xtyy;
		postString += "&XTBB=" + xtbb;
		postString += "&op=" + oper;
		postString += "&iswifi=" + lwfs;
		postString += "&status=" + status;
		postString += "&prod=" + prod;

		return postString;

	}

	/**
	 * �ж�������ʽWIFI GPRS WIFI 1 GPRS 2
	 */
	public static int getConnectionMethod(Context context) {
		int method = 0;
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetInfo.isConnected()) {
			method = 1;
		}
		if (mobNetInfo.isConnected()) {
			method = 2;
		}
		return method;

	}

	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 10 + 26; // 10 digits + 26 letters

	public static String generateMD5(String imageUri) {
		byte[] md5 = getMD5(imageUri.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(RADIX);
	}

	private static byte[] getMD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			L.e(e);
		}
		return hash;
	}

	/**
	 * ��ȡlistview item�߶�
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			// ListView��ÿ��Item������LinearLayout�������������ģ���Ϊ������Layout(��RelativeLayout)û����дonMeasure()������onMeasure()ʱ�׳��쳣��
			listItem.measure(0, 0);

			totalHeight += listItem.getMeasuredHeight();
		}
		totalHeight += 30;

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		listView.setLayoutParams(params);
	}

	/**
	 * ���ַ���תΪ����
	 */
	public static Date ConverToDate(String strDate, String str)
			throws Exception {
		DateFormat df = new SimpleDateFormat(str);
		return df.parse(strDate);
	}

	/**
	 * ������ת��Ϊ�ִ�
	 */
	public static String ConverToStr(Date StrDate, String str) throws Exception {
		DateFormat df = new SimpleDateFormat(str);
		return df.format(StrDate);

	}

	/**
	 * �鿴SD����ʣ��ռ�
	 */
	public static long getSDFreeSize() {
		// ȡ��SD���ļ�·��
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// ��ȡ�������ݿ�Ĵ�С(Byte)
		long blockSize = sf.getBlockSize();
		// ���е����ݿ������
		long freeBlocks = sf.getAvailableBlocks();
		// ����SD�����д�С
		// return freeBlocks * blockSize; //��λByte
		return (freeBlocks * blockSize) / 1024; // ��λKB
		// return (freeBlocks * blockSize)/1024 /1024; //��λMB
	}

	/**
	 * ֻ�ڵ�һ�δ�����ݷ�ʽ
	 */
	public static void AddShortcut(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		boolean is_first = sp.getBoolean(Constants.IS_FIRST, false);
		if (!is_first) {
			Editor editor = sp.edit();
			editor.putBoolean(Constants.IS_FIRST, true);
			editor.commit();
			shortcutCreate(context);
			shortcutCreateSaoYiSao(context, R.string.saoyisao,
					R.drawable.erweima, Launcher.class);
		}
	}

	/**
	 * ���������ݷ�ʽ
	 */
	public static void shortcutCreate(Context context) {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// �������ؽ�
		shortcut.putExtra("duplicate", false);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				context.getString(R.string.app_name));
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context,
						R.drawable.ic_launcher_home));

		Intent intent = new Intent(context, Launcher.class);

		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");

		Uri uri = Uri.parse(Constants.SHORTCUT_LAUNCHER);
		intent.setData(uri);
		
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// ���͹㲥
		context.sendBroadcast(shortcut);

	}

	/**
	 * ����ɨһɨ��ݷ�ʽ
	 */
	public static void shortcutCreateSaoYiSao(Context context, int resId,
			int resourceId, Class<?> cls) {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// �������ؽ�
		shortcut.putExtra("duplicate", false);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(resId));
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context, resourceId));

		Intent intent = new Intent(context, cls);
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");

		Uri uri = Uri.parse("saoyisao");
		intent.setData(uri);

		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// ���͹㲥
		context.sendBroadcast(shortcut);
	}

	public static void startSavePage(final String url, final String menuid,
			final Context context) {
		new Thread() {
			public void run() {
				try {
					WebPageSave.saveWebPage(context, url,
							Constants.ONE_WEBVIEW_MOBILEPATH, menuid);
				} catch (final Exception e) {
					if (e instanceof UnknownHostException
							|| e instanceof SocketTimeoutException) {
						// ������
					} else if (e instanceof MalformedURLException) {
						// ��ЧURL
					} else {
						// �ڲ�����
					}
				}
			}
		}.start();
	}

	/**
	 * ���������ݷ�ʽ
	 */
	public static void shortcutCreate(Context context, String title,
			Bitmap bitmap, String url) {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// �������ؽ�
		shortcut.putExtra("duplicate", false);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);

		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.hotseat_browser_bg);
		}
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);

		Intent intent = new Intent(context, Launcher.class);

		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		Uri uri = Uri.parse(url);
		intent.setData(uri);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// ���͹㲥
		context.sendBroadcast(shortcut);

	}

	public static void storeInSD(Bitmap bitmap, String fileName) {
		if (bitmap == null)
			return;
		File file = IOUtils.getIconFolder();
		if (!file.exists()) {
			file.mkdirs();
		}
		File imageFile = new File(file, fileName);
		try {
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ��ͼƬ���Բ��
	 * 
	 * @param bitmap
	 *            ��Ҫ�޸ĵ�ͼƬ
	 * @param pixels
	 *            Բ�ǵĻ���
	 * @return Բ��ͼƬ
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		int border = 2;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffeeeded;
		final Paint paint = new Paint();
		final Rect src_rect = new Rect(0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		final Rect dst_rect = new Rect(border, border, bitmap.getWidth()
				- border, bitmap.getHeight() - border);
		final RectF src_rectF = new RectF(src_rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		paint.setStrokeWidth(border);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawBitmap(bitmap, src_rect, dst_rect, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		canvas.drawRoundRect(src_rectF, roundPx, roundPx, paint);

		return output;
	}

	/**
	 * ͼƬ����
	 * 
	 */
	public static Bitmap toCorner(Bitmap bitmap, int Width, int Height) {
		Bitmap bitmap1 = Bitmap.createBitmap(Width, Height, Config.ARGB_8888);
        Bitmap newBitmap = null;

        newBitmap = Bitmap.createBitmap(bitmap1);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint(); 
        paint.setColor( color.white);
        canvas.drawRect(0, 0, bitmap1.getWidth(), bitmap1.getHeight(), paint);

        paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        
        canvas.save(Canvas.ALL_SAVE_FLAG);
         
        canvas.restore();

		return newBitmap;
	}

}
