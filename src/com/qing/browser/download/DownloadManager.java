package com.qing.browser.download;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.activities.DownloadsListActivity;
import com.qing.browser.events.EventConstants;
import com.qing.browser.events.EventController;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.DialogUtil;
import com.qing.browser.utils.IOUtils;
import com.qing.browser.utils.Tools;
import com.universe.galaxy.util.StringUtil;

/**
 * ���ع���
 **/
public class DownloadManager {

	// ����״̬����������ͣ�������У������أ��Ŷ���
	public static final int DOWNLOAD_STATE_NORMAL = 0x00;
	public static final int DOWNLOAD_STATE_PAUSE = 0x01;
	public static final int DOWNLOAD_STATE_DOWNLOADING = 0x02;
	public static final int DOWNLOAD_STATE_FINISH = 0x03;
	public static final int DOWNLOAD_STATE_WAITING = 0x04;

	// ��������������������
	private ArrayList<HashMap<String, Object>> taskList = new ArrayList<HashMap<String, Object>>();

	private final static Object syncObj = new Object();
	private static DownloadManager instance;
	private ExecutorService executorService;
	private static NotificationManager mNotificationManager;
	private final static int mNotificationBaseId = 1000;
	private static Notification mNotification;
	private Context mContext;
	private Dao dao;
	RemoteViews contentView;
	PendingIntent pendingIntent;
	public SharedPreferences sp;
	public static long startTime;
	private DialogUtil dialogUtil;
	private final static int ThreadCount = 3;

	private DownloadManager() {
		// ���ֻ��ͬʱ����3����������������Ŷӵȴ�
		executorService = Executors.newFixedThreadPool(ThreadCount);
		mNotification = new Notification();
	}

	public static DownloadManager getInstance() {
		if (null == instance) {
			synchronized (syncObj) {
				instance = new DownloadManager();
			}
			return instance;
		}
		return instance;
	}

	// ��ʼ���أ�����һ�������߳�
	public void beginDownload(DownloadItem item) {
		if (executorService.isShutdown()) {
			Log.e("H", "beginDownload executorService.isShutdown()");
			executorService = Executors.newFixedThreadPool(ThreadCount);
		}

		try {
			Log.d("H",
					"beginDownload " + item.getFileName() + " "
							+ item.getdownloadID());

			dao.updataInfos(item.getCompeleteSize(), DOWNLOAD_STATE_WAITING,
					item.getUrl());
			dao.closeDb();
			DownloadTask task = new DownloadTask(item);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", item.getFileName());
			map.put("DownloadTask", task);
			taskList.add(map);

			executorService.submit(task);
		} catch (RejectedExecutionException ignored) {
			Log.e("H", "beginDownload  " + ignored);
		}

	}

	public void startDownload(final DownloadItem item, Context mContext) {
		this.mContext = mContext;

		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(mContext.getApplicationContext(),
				DownloadsListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

		sp = mContext.getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		dao = new Dao(mContext);
		if (sp.getBoolean(
				Constants.PREFERENCES_BROWSER_DOWNLOAD_NOT_WIFI_REMIND, true)) {
			if (1 != Tools.getConnectionMethod(mContext)) {
				dialogUtil = new DialogUtil.Builder(mContext)
						.setTitleText("��ܰ��ʾ").setText("��ǰ���粻��wifi���������أ�")
						.setPositiveButton("ȷ��", new View.OnClickListener() {
							public void onClick(View v) {
								dialogUtil.dismiss();
								beginDownload(item);
							}
						}).setNegativeButton("ȡ��", new View.OnClickListener() {
							public void onClick(View v) {
								mNotificationManager.cancelAll();
								dialogUtil.dismiss();
								return;
							}
						}).create();
				dialogUtil.show();
			} else {
				beginDownload(item);
			}

		} else {
			beginDownload(item);
		}

	}

	public void stopDownloadItem(String filename) {
		for (int i = 0; i < taskList.size(); i++) {
			if (filename.equals(taskList.get(i).get("name"))) {
				DownloadTask task = (DownloadTask) taskList.get(i).get(
						"DownloadTask");
				// �����������������Ĵ���
				task.stopTask();
			}
		}

	}

	public void stopAllDownloadTask() {
		for (int i = 0; i < taskList.size(); i++) {
			DownloadTask task = (DownloadTask) taskList.get(i).get(
					"DownloadTask");
			// �����������������Ĵ���
			task.stopTask();

		}
		// ��ֹͣ���ڽ��е�����;ܾ������µ�����
		executorService.shutdownNow();
		mNotificationManager.cancelAll();
	}

	// ��������
	class DownloadTask implements Runnable {
		private String localfile;// ����·��
		private int startPos = 0;
		private int endPos;
		private int compeleteSize;
		private String urlstr;
		DownloadItem info;
		private boolean isWorking = false;
		private Dao dao;

		public DownloadTask(DownloadItem item) {
			this.isWorking = true;
			this.dao = new Dao(mContext);
			this.info = item;
			localfile = IOUtils.getDownloadFolder().getAbsolutePath()
					+ File.separator + info.getFileName();
			urlstr = info.getUrl();
			endPos = info.getEndPos();
			compeleteSize = info.getCompeleteSize();
			startPos = info.getStartPos();
		}

		public void stopTask() {
			this.isWorking = false;
		}

		public void downloadFailSocketTimeout() {
			dao.updataInfos(compeleteSize,
					DownloadManager.DOWNLOAD_STATE_PAUSE, urlstr);
			dao.closeDb();

			info.setdownloadID((int) Thread.currentThread().getId());
			info.setCompeleteSize(compeleteSize);
			info.setProgress(compeleteSize * 100 / endPos);
			Message message = mHandler.obtainMessage();
			message.obj = info;
			message.what = 5;
			mHandler.sendMessage(message);
			Log.i("H", "DownloadTask ����ʧ��" + info.getFileName());
		}
		
		public void downloadFail() {
			dao.updataInfos(compeleteSize,
					DownloadManager.DOWNLOAD_STATE_PAUSE, urlstr);
			dao.closeDb();

			info.setdownloadID((int) Thread.currentThread().getId());
			info.setCompeleteSize(compeleteSize);
			info.setProgress(compeleteSize * 100 / endPos);
			Message message = mHandler.obtainMessage();
			message.obj = info;
			message.what = 2;
			mHandler.sendMessage(message);
			Log.i("H", "DownloadTask ����ʧ��" + info.getFileName());
		}

		public void downloadSuccess() {
			dao.closeDb();
			info.setdownloadID((int) Thread.currentThread().getId());
			Message message = mHandler.obtainMessage();
			message.obj = info;
			message.what = 0;
			mHandler.sendMessage(message);
			taskList.remove(this);
			Log.i("H", "DownloadTask �������" + info.getFileName());
		}

		public void run() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			Message message = null;
			int downloadCount = 0;
			int updateCount = 0;
			OutputStream outputStream = null;

			info.setdownloadID((int) Thread.currentThread().getId());
			message = Message.obtain();
			message.obj = info;
			message.what = 4;
			mHandler.sendMessage(message);

			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Range", "bytes="
						+ (startPos + compeleteSize) + "-");

				if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
					return;

				// ����ֻ��ռ䲻�㣬����ʾ�����ء�
				if (connection.getContentLength() / 1024 > Tools
						.getSDFreeSize()) {
					info.setCompeleteSize(0);
					info.setErrorMessage("�ֻ��ڴ治��");
					info.setdownloadID((int) Thread.currentThread().getId());
					message = mHandler.obtainMessage();
					message.what = 3;
					message.obj = info;
					mHandler.sendMessage(message);
					dao.updataInfos(compeleteSize,
							DownloadManager.DOWNLOAD_STATE_PAUSE, urlstr);
					dao.closeDb();
					if (connection != null) {
						connection.disconnect();
						connection = null;
					}
					return;
				}

				is = connection.getInputStream();

				randomAccessFile = new RandomAccessFile(localfile, "rwd");
				randomAccessFile.seek(startPos + compeleteSize);
				// ��Ҫ���ص��ļ�д�������ڱ���·���µ��ļ���
				byte[] buffer = new byte[4096];
				int length = -1;

				int idx = urlstr.lastIndexOf("/");
				if (idx <= 0) {
					return;
				}

				while ((length = is.read(buffer)) != -1) {
					downloadCount += length;
					randomAccessFile.write(buffer, 0, length);
					compeleteSize += length;

					// ����Ϣ��������Ϣ�������������Խ��������и���
					if (updateCount == 0
							|| (downloadCount * 100
									/ connection.getContentLength() - Constants.DOWN_STEP) >= updateCount) {
						updateCount += Constants.DOWN_STEP;

						Thread.sleep(Constants.DOWN_DELAY_TIME);

						// ��������
						info.setDownloadState(DOWNLOAD_STATE_DOWNLOADING);
						info.setCompeleteSize(compeleteSize);
						info.setProgress(compeleteSize * 100 / endPos);
						info.setdownloadID((int) Thread.currentThread().getId());
						message = mHandler.obtainMessage();
						message.what = 1;
						message.obj = info;
						mHandler.sendMessage(message);

						if (compeleteSize * 100 / endPos == 100) {
							downloadSuccess();
						}
						// TODO ��ͣ
						if (!isWorking) {
							try {
								is.close();
								if (outputStream != null)
									outputStream.close();
								randomAccessFile.close();
								connection.disconnect();
							} catch (Exception e) {
								downloadFail();
								Log.e("H", "DownloadTask ��ͣ " + e.toString());
							}
						}
					}
				}
			} catch (SocketTimeoutException e) {
				info.setErrorMessage(e.getMessage());
				downloadFailSocketTimeout();
				Log.e("H", "DownloadTask catch " + e.toString());
			} catch (Exception e) {
				info.setErrorMessage(e.getMessage());
				downloadFail();
				Log.e("H", "DownloadTask catch " + e.toString());
			} finally {
				try {
					is.close();
					if (outputStream != null)
						outputStream.close();
					randomAccessFile.close();
					connection.disconnect();

				} catch (Exception e) {
					Log.e("H", "DownloadTask finally catch " + e.toString());
				}

			}
		}

	}

	private static void openFile(File file) {
		Context context = LauncherApplication.getInstance();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg != null) {
				DownloadItem info = (DownloadItem) msg.obj;
				String filename = info.getFileName().replace(
						Constants.DownLoadFileName, "");
				int mNotificationID = mNotificationBaseId
						+ info.getdownloadID();
				switch (msg.what) {
				case 0:
					// TODO ������� �����ļ�������
					File downloadFolder = IOUtils.getDownloadFolder();
					File from = new File(downloadFolder, info.getFileName());
					File to = new File(downloadFolder, info.getFileName()
							.replace(Constants.DownLoadFileName, ""));
					from.renameTo(to);

					String contentType = "",
					suffix = "",
					prompt = "���سɹ�";
					suffix = StringUtil.getSuffixOfFileName(filename);
					contentType = StringUtil.getHttpContentType(suffix);
					if (StringUtil.isNull(contentType)) {
						prompt += "��δ֪���ļ�����";
					} else if (".apk".equals(suffix)) {
						prompt += "�������װ";
					}

					Uri uri = Uri.fromFile(to);
					contentView = new RemoteViews(mContext.getPackageName(),
							R.layout.notification_item_finish);
					contentView.setTextViewText(R.id.notificationPercent,
							prompt);
					contentView.setTextViewText(R.id.name, filename);

					mNotification.contentView = contentView;
					mNotification.icon = android.R.drawable.stat_sys_download_done;
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, contentType);
					PendingIntent p = PendingIntent.getActivity(mContext, 0,
							intent, 0);
					mNotification.contentIntent = p;
					mNotificationManager.cancel(mNotificationID);
					mNotificationManager.notify((int) Math.random() * 10 + 100,
							mNotification);

					if (".apk".equals(suffix)) {
						openFile(to);
					}
					// ɾ�����ݿ��¼
					dao.delete(info.getUrl());
					EventController.getInstance().fireDownloadEvent(
							EventConstants.EVT_DOWNLOAD_ON_FINISHED, info);
					break;
				case 1:
					// TODO ��������
					int updateCount = info.getProgress();
					contentView = new RemoteViews(mContext.getPackageName(),
							R.layout.notification_item);
					contentView.setTextViewText(R.id.notificationPercent,
							"��������" + updateCount + "%");
					contentView.setTextViewText(R.id.name, filename);
					contentView.setProgressBar(R.id.notificationProgress, 100,
							updateCount, false);
					contentView.setViewVisibility(R.id.notificationProgress,
							View.VISIBLE);
					mNotification.contentView = contentView;
					mNotification.contentIntent = pendingIntent;
					mNotificationManager.notify(mNotificationID, mNotification);
					EventController.getInstance().fireDownloadEvent(
							EventConstants.EVT_DOWNLOAD_ON_PROGRESS, info);
					break;
				case 2:
					// TODO ������ͣ��ʧ��
					Log.d("H",
							"mHandler ��ͣ" + info.getFileName() + " "
									+ info.getdownloadID());
					contentView = new RemoteViews(mContext.getPackageName(),
							R.layout.notification_item);
					contentView
							.setTextViewText(R.id.notificationPercent, "����ͣ");

					mNotification.contentView = contentView;
					mNotification.contentIntent = pendingIntent;
					mNotificationManager.notify(mNotificationID, mNotification);
					EventController.getInstance().fireDownloadEvent(
							EventConstants.EVT_DOWNLOAD_ON_PAUSE, info);
					break;
				case 3:
					// TODO �ֻ��ռ䲻�㣬����ʾ�����ء�
					Log.d("H", "mHandler �ֻ��ռ䲻��" + info.getFileName());
					contentView = new RemoteViews(mContext.getPackageName(),
							R.layout.notification_item_finish);
					contentView.setTextViewText(R.id.notificationPercent,
							"�ֻ��ռ䲻�㣬����ʧ��");
					mNotification.contentView = contentView;
					mNotification.contentIntent = pendingIntent;
					mNotificationManager.cancel(mNotificationID);
					mNotificationManager.notify(mNotificationID, mNotification);
					EventController.getInstance().fireDownloadEvent(
							EventConstants.EVT_DOWNLOAD_ON_PAUSE, info);
					break;
				case 4:
					// ��ʼ����

					dao.updataInfos(info.getCompeleteSize(),
							DOWNLOAD_STATE_DOWNLOADING, info.getUrl());
					dao.closeDb();
					startTime = System.currentTimeMillis();
					Log.d("H",
							"��ʼ���� " + info.getFileName() + " "
									+ info.getdownloadID());

					contentView = new RemoteViews(mContext.getPackageName(),
							R.layout.notification_item);
					contentView.setProgressBar(R.id.notificationProgress, 100,
							0, false);

					mNotification.icon = android.R.drawable.stat_sys_download;
					mNotification.tickerText = "��������";
					mNotification.contentView = contentView;
					mNotification.contentIntent = pendingIntent;

					mNotificationManager.notify(mNotificationID, mNotification);
					EventController.getInstance().fireDownloadEvent(
							EventConstants.EVT_DOWNLOAD_ON_START, info);
					break;
					
				case 5:
					Toast.makeText(mContext, "���ӳ�ʱ�������ԣ�", Toast.LENGTH_SHORT).show();
					Log.d("H",
							"mHandler ���ӳ�ʱ��ͣ" + info.getFileName() + " "
									+ info.getdownloadID());
					contentView = new RemoteViews(mContext.getPackageName(),
							R.layout.notification_item);
					contentView
							.setTextViewText(R.id.notificationPercent, "���ӳ�ʱ����ͣ");
					mNotification.contentView = contentView;
					mNotification.contentIntent = pendingIntent;
					mNotificationManager.notify(mNotificationID, mNotification);
					EventController.getInstance().fireDownloadEvent(
							EventConstants.EVT_DOWNLOAD_ON_PAUSE, info);
					break;

				}
			}
		}
	};

}
