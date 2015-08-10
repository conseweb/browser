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
	 * 获取IMEI串号 Returns the unique device ID, for example, the IMEI for GSM and
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

	// 判断网络是否可用
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
	 * 获取手机系统的版本
	 */
	public static String getSystembVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机的语言
	 */
	public static String getLanguage() {
		String language = Locale.getDefault().getLanguage();
		return language;
	}

	/**
	 * 获取屏幕尺寸
	 */
	public static String getDisplayInfo(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）

		int width = display.getWidth();
		int height = display.getHeight();

		// int width = (int) (dm.widthPixels*density);
		// int height = (int) (dm.heightPixels*density);
		return width + "X" + height;
	}

	/**
	 * 获取手机号码
	 */
	public static String getNativeNumber(Context context) {
		String nativeNumber = "";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		nativeNumber = telephonyManager.getLine1Number();
		return nativeNumber;
	}

	/**
	 * 获取运行商
	 */
	public static String getProviders(Context context) {
		String providers = "0";
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = telephonyManager.getSimOperator();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (simOperator != null) {
			if (simOperator.equals("46000") || simOperator.equals("46002")) {
				providers = "1"; // "中国移动";
			} else if (simOperator.equals("46001")) {
				providers = "2"; // "中国联通";
			} else if (simOperator.equals("46003")) {
				providers = "3"; // "中国电信";
			}
		}
		return providers;

	}

	public static String readLatitude(Context context) {
		// 读取SharedPreferences中数据
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String latitudeString = sp.getString(Constants.SSJD, "");
		return latitudeString;
	}

	public static String readLongitude(Context context) {
		// 读取SharedPreferences中数据
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String longitudeString = sp.getString(Constants.SSWD, "");
		return longitudeString;
	}

	public static String readSJWZ(Context context) {
		// 读取SharedPreferences中数据
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
	 * 组装FormBodyPart数组
	 * 
	 * @param context
	 * @return
	 */
	public static List<FormBodyPart> getPostFormBodyPart(Context context) {
		// 先设定公共内容,返回String
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
	 * 组装POST字串
	 * 
	 * @param context
	 * @return
	 */
	public static String getPoststring(Context context) {
		// 先设定公共内容,返回String
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
	 * 请求推送广告 组装POST字串
	 * 
	 * @param context
	 * @return
	 */
	public static String getADPoststring(Context context) {
		// 先设定公共内容,返回String
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
	 * 判断联网方式WIFI GPRS WIFI 1 GPRS 2
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
	 * 得到几天前的时间
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
	 * 得到几天后的时间
	 */
	public static Date getDateAfterDays(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	 
	/**
	 * 查看SD卡的剩余空间
	 */
	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		return (freeBlocks * blockSize) / 1024; // 单位KB
		// return (freeBlocks * blockSize)/1024 /1024; //单位MB
	}

	/**
	 * 获取手机的mac地址
	 */
	public static String getMac(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();

	}

}
