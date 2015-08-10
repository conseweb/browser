package com.qing.browser.receiver;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.util.Log;

import com.qing.browser.tongji.LiuLiangTongji;
import com.qing.browser.utils.Constants;

public class AlarmReceiver extends BroadcastReceiver {
	public SharedPreferences sp;
	PackageManager packageManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		packageManager = context.getPackageManager();
		sp = context.getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		Calendar calendar = Calendar.getInstance();
		String nowStr = calendar.get(Calendar.YEAR) + "-"
				+ calendar.get(Calendar.MONTH) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " "
				+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":"
				+ calendar.get(Calendar.SECOND);

		// 保存流量数据
		saveData();

		// 当天流量清零

		if (calendar.get(Calendar.HOUR_OF_DAY) == 23) {
			Log.d("H", "AlarmReceiver  今日流量清零  当前时间：" + nowStr);
			sp.edit().putLong(LiuLiangTongji.G_Today, 0).commit();
			sp.edit().putLong(LiuLiangTongji.W_Today, 0).commit();
		}

		if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			// 当月流量清零
			Log.d("H", "AlarmReceiver 本月流量清零 当前时间：" + nowStr);
			sp.edit().putLong(LiuLiangTongji.G_Month, 0).commit();
			sp.edit().putLong(LiuLiangTongji.W_Month, 0).commit();

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
			Log.e("H", "AlarmReceiver " + e.toString());
			return 0;
		}
	}

	public void saveData() {

		int NetworkStatus = sp.getInt(LiuLiangTongji.NetworkStatus,
				LiuLiangTongji.NetworkStatus_Not);

		Log.d("H", "AlarmReceiver  saveData");

		switch (NetworkStatus) {
		case ConnectivityManager.TYPE_MOBILE:
			// 上次是数据连接

			if (0 != LiuLiangTongji.temp) {
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"AlarmReceiver 本次 3G使用流量："
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
			LiuLiangTongji.temp = getData();
			break;

		case ConnectivityManager.TYPE_WIFI:
			// 上次是wifi连接
			if (0 != LiuLiangTongji.temp) {
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"AlarmReceiver本次 Wifi使用流量："
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
			LiuLiangTongji.temp = getData();
			break;

		default:
			// 上次未连接
			LiuLiangTongji.temp = getData();

		}
	}
}
