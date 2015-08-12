package com.qing.browser.download;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.qing.browser.utils.Constants;
import com.qing.browser.utils.IOUtils;

public class DownloadAppIn {
	private Context mContext;
	private String urlStr;
	private Dao dao;
	private String localfile = null;// 保存路径
	private int fileSize = 0;
	private String mFileName = null;
	private DownloadManager downloadManager;

	public DownloadAppIn(String urlstr, Context context) {
		this.urlStr = urlstr;
		this.mContext = context;
		dao = new Dao(context);

		Log.d("H", "urlStr = " + urlStr);

		this.downloadManager = DownloadManager.getInstance();

		DownloadItem info = dao.getInfo(urlStr);

		if (info == null) {
			new InitThread().start();
		} else {
			dao.updataInfos(info.getCompeleteSize(),
					DownloadManager.DOWNLOAD_STATE_WAITING, urlStr);
			dao.closeDb();
		}
	}

	private static void checkFileName(String mFileName) {
		int queryParamStart = mFileName.indexOf("?");
		if (queryParamStart > 0) {
			mFileName = mFileName.substring(0, queryParamStart);
		}
	}

	public class InitThread extends Thread {
		public void run() {
			try {
				URL url = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				connection.setRequestMethod("GET");
				fileSize = connection.getContentLength();
				if (localfile == null) {
					// 打印输出服务器Header信息
					Map<String, List<String>> map = connection
							.getHeaderFields();
					for (String str : map.keySet()) {
						if (str != null) {
//							Log.e("H", str + map.get(str));
						}
					}
					mFileName = connection
							.getHeaderField("Content-Disposition");
					if (mFileName == null || mFileName.length() < 1) {
						mFileName = connection.getURL().getFile();
						mFileName = mFileName.substring(mFileName
								.lastIndexOf("/") + 1);
					} else {
						String spStr[] = mFileName.split("=");
						mFileName = spStr[1];
					}

					mFileName = mFileName.replace("\"", "");// 去掉下载文件名中的双引号
					mFileName = mFileName + Constants.DownLoadFileName;
					Log.i("H", "InitThread mFileName = " + mFileName);
					checkFileName(mFileName);
					localfile = IOUtils.getDownloadFolder().getAbsolutePath()
							+ File.separator + mFileName;
				}

				File file = new File(localfile);
				if (!file.exists()) {
					file.createNewFile();
				}
				// 本地访问文件
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(fileSize);
				accessFile.close();
				connection.disconnect();

				// 创建info保存info数据到数据库
				DownloadItem info = new DownloadItem(0,0, fileSize - 1, 0,
						urlStr, DownloadManager.DOWNLOAD_STATE_NORMAL,
						mFileName);
				dao.saveInfo(info);
				dao.closeDb();
				downloadManager.startDownload(info, mContext);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
