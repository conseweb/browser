package com.universe.galaxy.receiver;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.deta.global.IPlatformService;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.universe.galaxy.util.Constants;
import com.universe.galaxy.version.Version;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	String packnameString = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		packnameString = context.getPackageName();

		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

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
			// connect network
			boolean isConnected = ConnectionChange.mathHours(context,
					isWifiConnected);
			// 判断是否要联网获取下发
			if (true) {
				Log.v("LS","ConnectionChange.ConnectionChangeAction(context)");
				ConnectionChange.ConnectionChangeAction(context);
				//Version.checkVersion(context);
				
			} else {
				
			}

		}
	}
	
}