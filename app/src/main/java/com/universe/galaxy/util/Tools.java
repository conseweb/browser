package com.universe.galaxy.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.StringBody;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class Tools {

	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static Random r = new Random();

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

	public static boolean isServiceWorked(Context context, String serviceName) {
		if (StringUtil.isNull(serviceName))
			return false;
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(20000);
		for (RunningServiceInfo service : runningService) {
			if (service.service.getClassName().trim()
					.equals(serviceName.trim())) {
				return true;
			}
		}
		return false;
	}

	public static boolean sdCardExist() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static String getRandomLetter() {
		char[] temp = new char[26];
		int count = 0;
		for (char i = 'a'; i <= 'z'; i++) {
			temp[count] = i;
			count++;
		}
		return String.valueOf(temp[r.nextInt(temp.length)]);
	}
	/**
	 * @return
	 */
	public static String getModel(Context context) {
		return Constants.MACH_DEFAULT_EXT;
	}

	/**
	 * @return
	 */
	public static String getManu(Context context) {
		return Constants.MANU_DEFAULT_EXT; 
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

		float density = dm.density; // ��Ļ�ܶȣ����ر�����0.75/1.0/1.5/2.0��
		int densityDPI = dm.densityDpi; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��

		int width = display.getWidth();
		int height = display.getHeight();

		// int width = (int) (dm.widthPixels*density);
		// int height = (int) (dm.heightPixels*density);
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

	public static String readLatitude(Context context) {
		// ��ȡSharedPreferences������
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String latitudeString = sp.getString(Constants.SSJD, "");
		return latitudeString;
	}

	public static String readLongitude(Context context) {
		// ��ȡSharedPreferences������
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String longitudeString = sp.getString(Constants.SSWD, "");
		return longitudeString;
	}

	public static String readSJWZ(Context context) {
		// ��ȡSharedPreferences������
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String sjwzString = sp.getString(Constants.SJWZ, "");
		return sjwzString;
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
	 * ��װFormBodyPart����
	 * 
	 * @param context
	 * @return
	 */
	public static List<FormBodyPart> getPostFormBodyPart(Context context) {
		// ���趨��������,����String
		List<FormBodyPart> postParams = new ArrayList<FormBodyPart>();
		String manu = getManu(context);
		String mach = getModel(context);
		String imei = getIMEI(context);
		String imsi = getIMSI(context);
		String version = getVersion(context);
		String prod = Constants.PROD;
		String sjhm = getNativeNumber(context);
		String pmcc = getDisplayInfo(context);
		String xtyy = getLanguage();
		String xtbb = getSystembVersion();
		String oper = getProviders(context);
		String ssjd = readLatitude(context);
		String sswd = readLongitude(context);
		String lwfs = getConnectionMethod(context);
		String sjwz = readSJWZ(context);

		try {
			if (!(StringUtil.isNull(manu)))
				postParams.add(new FormBodyPart("MANU", new StringBody(manu)));
			if (!(StringUtil.isNull(mach)))
				postParams.add(new FormBodyPart("MACH", new StringBody(mach)));
			if (!(StringUtil.isNull(imei)))
				postParams.add(new FormBodyPart("IMEI", new StringBody(imei)));
			if (!(StringUtil.isNull(imsi)))
				postParams.add(new FormBodyPart("IMSI", new StringBody(imsi)));
			if (!(StringUtil.isNull(version)))
				postParams
						.add(new FormBodyPart("VERS", new StringBody(version)));
			if (!(StringUtil.isNull(prod)))
				postParams.add(new FormBodyPart("PROD", new StringBody(prod)));
			if (!(StringUtil.isNull(sjhm)))
				postParams.add(new FormBodyPart("SJHM", new StringBody(sjhm)));
			if (!(StringUtil.isNull(pmcc)))
				postParams.add(new FormBodyPart("PMCC", new StringBody(pmcc)));
			if (!(StringUtil.isNull(xtyy)))
				postParams.add(new FormBodyPart("XTYY", new StringBody(xtyy)));
			if (!(StringUtil.isNull(xtbb)))
				postParams.add(new FormBodyPart("XTBB", new StringBody(xtbb)));
			if (!(StringUtil.isNull(oper)))
				postParams.add(new FormBodyPart("OPER", new StringBody(oper)));
			if (!(StringUtil.isNull(ssjd)))
				postParams.add(new FormBodyPart("SSJD", new StringBody(ssjd)));
			if (!(StringUtil.isNull(sswd)))
				postParams.add(new FormBodyPart("SSWD", new StringBody(sswd)));
			if (!(StringUtil.isNull(lwfs)))
				postParams.add(new FormBodyPart("LWFS", new StringBody(lwfs)));
			if (!(StringUtil.isNull(sjwz)))
				postParams.add(new FormBodyPart("SJWZ", new StringBody(sjwz)));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return postParams;

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
		String prod = Constants.PROD;
		String sjhm = getNativeNumber(context);
		String pmcc = getDisplayInfo(context);
		String xtyy = getLanguage();
		String xtbb = getSystembVersion();
		String oper = getProviders(context);
		String ssjd = readLatitude(context);
		String sswd = readLongitude(context);
		String lwfs = getConnectionMethod(context);
		String sjwz = readSJWZ(context);

		postString += "&IMEI=" + imei;
		postString += "&IMSI=" + imsi;
		postString += "&VERS=" + version;
		postString += "&PROD=" + prod;
		postString += "&SJHM=" + sjhm;
		postString += "&PMCC=" + pmcc;
		postString += "&XTYY=" + xtyy;
		postString += "&XTBB=" + xtbb;
		postString += "&OPER=" + oper;
		postString += "&SSJD=" + ssjd;
		postString += "&SSWD=" + sswd;
		postString += "&LWFS=" + lwfs;
		postString += "&SJWZ=" + sjwz;

		return postString;

	}

	/**
	 * �������͹�� ��װPOST�ִ�
	 * 
	 * @param context
	 * @return
	 */
	public static String getADPoststring(Context context) {
		// ���趨��������,����String
		String postString = "";
		String manu = getManu(context);
		String mach = getModel(context);
		String imei = getIMEI(context);
		String imsi = getIMSI(context);
		String version = getVersion(context);
		String prod = Constants.PROD;
		String phone = getNativeNumber(context);
		String ver = getDisplayInfo(context);
		String xtyy = getLanguage();
		String xtbb = getSystembVersion();
		String op = getProviders(context);
		String ssjd = readLatitude(context);
		String sswd = readLongitude(context);
		String iswifi = getConnectionMethod(context);
		String sjwz = readSJWZ(context);
		String status = Tools.getIsSystemApp(context);
		
		postString += "&manu=" + manu;
		postString += "&mach=" + mach;
		postString += "&imei=" + imei;
		postString += "&imsi=" + imsi;
		postString += "&vers=" + version;
		postString += "&prod=" + prod;
		postString += "&phone=" + phone;
		postString += "&ver=" + ver;
		postString += "&xtyy=" + xtyy;
		postString += "&xtbb=" + xtbb;
		postString += "&op=" + op;
		postString += "&ssjd=" + ssjd;
		postString += "&sswd=" + sswd;
		postString += "&iswifi=" + iswifi;
		postString += "&sjwz=" + sjwz;
		postString += "&status=" + status;
		
		return postString;

	}

	/**
	 * �ж�������ʽWIFI GPRS WIFI 1 GPRS 2
	 */
	public static String getConnectionMethod(Context context) {
		String method = "0";
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetInfo.isConnected()) {
			method = "1";
		}
		if (mobNetInfo.isConnected()) {
			method = "2";
		}
		return method;

	}

	/**
	 * �õ�����ǰ��ʱ��
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * �õ�������ʱ��
	 */
	public static Date getDateAfterDays(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
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
	 * ��ȡ�ֻ���mac��ַ
	 */
	public static String getMac(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();

	}

}
