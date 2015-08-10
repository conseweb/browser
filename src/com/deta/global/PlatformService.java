package com.deta.global;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.qing.browser.ui.launcher.LauncherApplication;
import com.universe.galaxy.receiver.ConnectionChange;

public class PlatformService extends Service {

	private static String tag = "PlatformService";

	@Override
	public IBinder onBind(Intent intent) {
		return new PlatformServiceImpl();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public class PlatformServiceImpl extends IPlatformService.Stub {

		@Override
		public void startGlobalService() throws RemoteException {

			Context context = LauncherApplication.getInstance();

			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {

			} else {
				boolean isWifiConnected = false;
				if (mobNetInfo.isConnected()) {
					isWifiConnected = false;
				}
				if (wifiNetInfo.isConnected()) {
					isWifiConnected = true;
				}
				isWifiConnected = true;

			}
		}

	}

}