package com.qing.browser.receiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.util.Log;

import com.qing.browser.tongji.LiuLiangTongji;
import com.qing.browser.utils.Constants;

public class BootOrShutDownReceiver extends BroadcastReceiver {

	// PackageManager 包管理类
	PackageManager packageManager;
	public SharedPreferences sp;
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {

		packageManager = context.getPackageManager();
		sp = context.getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		mContext = context;
		// 开机监听
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			setAlarm();

			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (mobNetInfo.isConnected()) {
				// 开机打开数据连接
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_MOBILE).commit();
			} else if (wifiNetInfo.isConnected()) {
				// 开机打开wifi连接
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_WIFI).commit();
			} else {
				// 开机未打开联网
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								LiuLiangTongji.NetworkStatus_Not).commit();
			}
		}

		// 在关机监听
		if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {

			// 关机取消闹钟
			Intent mIntent = new Intent(mContext, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(mContext, 0,
					mIntent, 0);
			AlarmManager am = (AlarmManager) mContext
					.getSystemService(Activity.ALARM_SERVICE);
			am.cancel(sender);

			// 记录uid应用 本次流量结余
			saveData();

		}
	}

	public long getData() {
		// TrafficStats类根据应用的UID获取到流量的相关数据
		try {
			ApplicationInfo ai = packageManager.getApplicationInfo(
					"com.qing.browser", PackageManager.GET_ACTIVITIES);

			long recv = TrafficStats.getUidRxBytes(ai.uid);
			long sent = TrafficStats.getUidTxBytes(ai.uid);

			if (recv < 0 || sent < 0) {
				return 0;
			}
			return recv + sent;

		} catch (NameNotFoundException e) {
			Log.e("H", "关机监听  " + e.toString());
			return 0;
		}
	}

	public void saveData() {

		int NetworkStatus = sp.getInt(LiuLiangTongji.NetworkStatus,
				LiuLiangTongji.NetworkStatus_Not);
		switch (NetworkStatus) {
		case ConnectivityManager.TYPE_MOBILE:
			// 上次是数据连接

			if (0 != LiuLiangTongji.temp) {
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"关机监听 本次 3G使用流量"
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp));
			sp.edit()
					.putLong(
							LiuLiangTongji.G_Today,
							sp.getLong(LiuLiangTongji.G_Today, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.G_Month,
							sp.getLong(LiuLiangTongji.G_Month, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.G_Total,
							sp.getLong(LiuLiangTongji.G_Total, 0)
									+ LiuLiangTongji.temp).commit();
			LiuLiangTongji.temp = 0;
			break;

		case ConnectivityManager.TYPE_WIFI:
			// 上次是wifi连接
			if (0 != LiuLiangTongji.temp) {
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"关机监听 本次 WIFI使用流量"
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp));
			sp.edit()
					.putLong(
							LiuLiangTongji.W_Today,
							sp.getLong(LiuLiangTongji.W_Today, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.W_Month,
							sp.getLong(LiuLiangTongji.W_Month, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.W_Total,
							sp.getLong(LiuLiangTongji.W_Total, 0)
									+ LiuLiangTongji.temp).commit();
			LiuLiangTongji.temp = 0;
			break;

		default:
			// 上次未连接
			LiuLiangTongji.temp = 0;

		}
	}

	private void setAlarm() {
		AlarmManager alarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(mContext, AlarmReceiver.class); 
		PendingIntent setPendIntent = PendingIntent.getBroadcast(
				mContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// 1分钟后发送广播，然后每隔1小时秒重复发广播
		int triggerAtTime = (int) (System.currentTimeMillis() + 60 * 1000);
		int interval = 60 * 60 * 1000;
 
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, interval, setPendIntent);

	}

}