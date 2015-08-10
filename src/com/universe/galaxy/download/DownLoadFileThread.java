package com.universe.galaxy.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.universe.galaxy.util.Constants;
import com.universe.galaxy.util.StringUtil;

public class DownLoadFileThread implements Runnable {
	private Handler handler;

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}

	public void setDownLoadFileName(String downLoadFileName) {
		this.downLoadFileName = downLoadFileName;
	}

	private String downLoadUrl;
	private String downLoadFileName;

	private static DownLoadFileThread instance;

	private DownLoadFileThread() {
	}

	public static DownLoadFileThread getInstance(Handler handler,
			String downLoadUrl) {
		instance = new DownLoadFileThread();
		instance.setDownLoadFileName(null);
		instance.setDownLoadUrl(downLoadUrl);
		instance.setHandler(handler);
		return instance;
	}

	public static DownLoadFileThread getInstance(Handler handler,
			String downLoadUrl, String downLoadFileName) {
		instance = new DownLoadFileThread();
		instance.setDownLoadFileName(downLoadFileName);
		instance.setDownLoadUrl(downLoadUrl);
		instance.setHandler(handler);
		return instance;
	}

	@Override
	public void run() {
		downLoad();
	}

	private boolean downLoad() {
		boolean ret = true;
		URL url = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		HttpURLConnection httpUrlConnection = null;
		File file = null;
		Message msg = null;
		int totalSize;
		int downloadCount = 0;
		int updateCount = 0;
		try {
			url = new URL(downLoadUrl);
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			if (httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
				return false;
			inputStream = httpUrlConnection.getInputStream();
			HttpURLConnection.getFileNameMap();
			if (StringUtil.isNull(downLoadFileName))
				downLoadFileName = this
						.getBingFengLeYuanDownLoadFileName(httpUrlConnection
								.getHeaderFields());

			String suffix = StringUtil.getSuffixOfFileName(downLoadFileName), path = Constants.CUSTOMERSERVICE_LOCAL_DIRECTORY;
			path = StringUtil.getPath(suffix);
			file = new File(path);
			if (!file.exists())
				file.mkdirs();

			downLoadFileName = StringUtil
					.replaceIllegalFileSymbol(downLoadFileName);
			downLoadFileName = new String(
					downLoadFileName.getBytes("ISO-8859-1"), "GBK");
			outputStream = new FileOutputStream(path + downLoadFileName, false);
			byte buffer[] = new byte[1024];
			int readSize = 0;
			totalSize = httpUrlConnection.getContentLength();
			Log.i("AAA", "Thread: " + Thread.currentThread().getName()
					+ "  totalSize: " + totalSize);
			while ((readSize = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readSize);
				downloadCount += readSize;
				if (updateCount == 0
						|| (downloadCount * 100 / totalSize - Constants.DOWN_STEP) >= updateCount) {
					updateCount += Constants.DOWN_STEP;
					Thread.sleep(Constants.DOWN_DELAY_TIME);
					msg = new Message();
					msg.what = 1;
					msg.obj = downLoadFileName;
					msg.arg1 = updateCount;
					handler.sendMessage(msg);
				}

			}
		} catch (Exception ex) {
			ret = false;
			ex.printStackTrace();
		} finally {
			try {
				if (httpUrlConnection != null) {
					httpUrlConnection.disconnect();
					httpUrlConnection = null;
				}
				if (inputStream != null)
					inputStream.close();
				if (outputStream != null)
					outputStream.close();
				url = null;
				file = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (ret) {
			msg = new Message();
			msg.obj = downLoadFileName;
			msg.what = 0;
			handler.sendMessage(msg);
		} else {
			msg = new Message();
			msg.obj = downLoadFileName;
			msg.what = 2;
			handler.sendMessage(msg);
		}
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getBingFengLeYuanDownLoadFileName(
			Map<String, List<String>> map) {
		Iterator it = map.entrySet().iterator();
		Map.Entry entry = null;
		List<String> list = null;
		String fileName = String.valueOf(System.currentTimeMillis()) + ".apk";
		String str = "";
		for (; it.hasNext();) {
			entry = (Map.Entry) it.next();
			list = (List<String>) entry.getValue();
			str = list.get(0);
			int temp = str.indexOf("filename=");
			if (temp != -1) {
				fileName = str.substring(str.indexOf("=") + 1, str.length());
				break;
			}
		}
		return fileName;
	}
}
