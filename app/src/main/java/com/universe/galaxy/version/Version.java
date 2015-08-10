package com.universe.galaxy.version;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.universe.galaxy.util.Constants;
import com.universe.galaxy.util.StringUtil;
import com.universe.galaxy.util.Tools;

public class Version {
	public static void checkVersion(final Context context) {
		final String versionName = Tools.getVersion(context);
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					VersionInfo versionInfo;
					switch (msg.what) {
					case 0:
						versionInfo = (VersionInfo) msg.obj;
						String newVersion = versionInfo.getVersion();
						if (!StringUtil.isNull(newVersion)
								&& !versionName.equals(newVersion)) {
							Intent notificationIntent = new Intent(context,
									NewVersionActivity.class);
							notificationIntent.putExtra(Constants.VERSION_INFO,
									versionInfo);
							createStatusBarNotifications(context,
									notificationIntent);
						}
						break;
					}
				}
			}
		};
		Log.i("CNCOMAN", "checkVersion-1");
		Thread thread = new Thread(VersionManagerUtil.getInstance(context,
				handler));
		thread.start();
	}

	private static void createStatusBarNotifications(Context context,
			Intent notificationIntent) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		int icon = android.R.drawable.stat_notify_sync;
		CharSequence tickerText = "有新版本需待升级，升级赢大奖";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		CharSequence contentTitle = "ruby浏览器需更新升级，新体验新惊喜";
		CharSequence contentText = "点击查看!";
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		// final int notifyId = 2 + Tools.r.nextInt(100);
		// Log.i("CNCOMAN", "checkVersion notifyId:"+notifyId);
		mNotificationManager.notify(Constants.VERSION_STATUSBAR_ID,
				notification);
	}
}
