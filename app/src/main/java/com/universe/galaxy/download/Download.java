package com.universe.galaxy.download;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.qing.browser.R;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.universe.galaxy.util.StringUtil;
import com.universe.galaxy.util.Tools;

public class Download {
	public static void DownLoadFile(String url) {
		DownLoadFile(url, null);
	}

	public static void DownLoadFile(String url, String downLoadName) {
		final Context context = LauncherApplication.getInstance();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		Notification notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = "开始下载";
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_item);

		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.contentIntent = pendingIntent;

		final int id = 2 + Tools.r.nextInt(10000);

		mNotificationManager.notify(id, notification);

		final NotificationManager nManager = mNotificationManager;
		final Notification n = notification;
		final RemoteViews cv = contentView;
		final PendingIntent pi = pendingIntent;

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					String fileName = "", contentType = "", suffix = "", prompt = "下载成功";
					switch (msg.what) {
					case 0:
						fileName = (String) msg.obj;
						suffix = StringUtil.getSuffixOfFileName(fileName);
						contentType = StringUtil.getHttpContentType(suffix);
						if (StringUtil.isNull(contentType))
							prompt += "，未知的文件类型";
						File file = new File(StringUtil.getPath(suffix)
								+ fileName);
						Uri uri = Uri.fromFile(file);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(uri, contentType);
						PendingIntent p = PendingIntent.getActivity(context, 0,
								intent, 0);
						n.setLatestEventInfo(context, fileName, prompt, p);
						nManager.notify(id, n);
						if (".apk".equals(suffix)) {
							openFile(file, id);
						}
						break;
					case 1:
						int updateCount = (Integer) msg.arg1;
						fileName = (String) msg.obj;
						cv.setTextViewText(R.id.notificationPercent,
								updateCount + "%");
						cv.setTextViewText(R.id.name, fileName);
						cv.setProgressBar(R.id.notificationProgress, 100,
								updateCount, false);
						n.contentView = cv;
						nManager.notify(id, n);
						break;
					case 2:
						n.setLatestEventInfo(context, fileName, "下载失败", pi);
						break;
					default:
						n.setLatestEventInfo(context, fileName, "下载失败", pi);
						break;
					}
				}
			}
		};
		DownLoadFileThread downLoad = null;
		if (StringUtil.isNull(downLoadName))
			downLoad = DownLoadFileThread.getInstance(handler, url);
		else
			downLoad = DownLoadFileThread.getInstance(handler, url,
					downLoadName);
		Thread thread = new Thread(downLoad);
		thread.start();
	}

	private static void openFile(File file, int id) {
		Context context = LauncherApplication.getInstance();
		Log.e("OpenFile", file.getName());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
}
