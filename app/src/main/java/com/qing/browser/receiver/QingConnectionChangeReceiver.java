package com.qing.browser.receiver;

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
import com.qing.browser.utils.Tools;

public class QingConnectionChangeReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	PackageManager packageManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		packageManager = context.getPackageManager();

		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		sp = context.getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		boolean salenet = sp.getBoolean(Constants.Salenet, false);

		int NetworkStatus = sp.getInt(LiuLiangTongji.NetworkStatus,
				LiuLiangTongji.NetworkStatus_Not);
		switch (NetworkStatus) {
		case ConnectivityManager.TYPE_MOBILE:
			// �ϴ�����������

			Log.d("H",
					"�����л� 3Gʹ��������w="
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp)
							+ " getData()="
							+ LiuLiangTongji.getDataStr(getData()));

			if(0 != LiuLiangTongji.temp){
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"�����л� 3Gʹ��������"
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
			if (mobNetInfo.isConnected()) {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_MOBILE).commit();
			} else if (wifiNetInfo.isConnected()) {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_WIFI).commit();
			} else {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								LiuLiangTongji.NetworkStatus_Not).commit();
			}

			break;
		case ConnectivityManager.TYPE_WIFI:
			// �ϴ���wifi����
			Log.d("H",
					"�����л� wifiʹ��������w="
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp)
							+ " getData()="
							+ LiuLiangTongji.getDataStr(getData()));

			if(0 != LiuLiangTongji.temp){
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"�����л� wifiʹ��������"
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
			if (mobNetInfo.isConnected()) {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_MOBILE).commit();
			} else if (wifiNetInfo.isConnected()) {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_WIFI).commit();
			} else {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								LiuLiangTongji.NetworkStatus_Not).commit();
			}
			break;

		default:
			// �ϴ�δ����
			LiuLiangTongji.temp = getData();
			if (mobNetInfo.isConnected()) {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_MOBILE).commit();
			} else if (wifiNetInfo.isConnected()) {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								ConnectivityManager.TYPE_WIFI).commit();
			} else {
				sp.edit()
						.putInt(LiuLiangTongji.NetworkStatus,
								LiuLiangTongji.NetworkStatus_Not).commit();
			}
		}

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			// unconnect network
		} else {
			boolean isWifiConnected = false;
			if (mobNetInfo.isConnected()) {
				isWifiConnected = false;
			}
			if (wifiNetInfo.isConnected()) {
				isWifiConnected = true;
			}

			Log.v("LS","QingConnectionChangeReceiver isWifiConnected="
							+ isWifiConnected + " salenet=" + salenet 
							+ " ����=" + Tools.getIsSystemApp(context));
			// �ж��Ƿ�Ҫ������ȡ�·� �����á� ����һ�� �ɹ��󲻷���
			if (isWifiConnected && !salenet
					/*&& ("1".equals(Tools.getIsSystemApp(context)))*/) {
				QingConnectionChange.ConnectionChangeAction(context);
			}

		}
	}

	public long getData() {
		// TrafficStats�����Ӧ�õ�UID��ȡ���������������
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
			return 0;
		}

	}
}