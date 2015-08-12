package com.qing.browser.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.qing.browser.R;
import com.qing.browser.ui.launcher.Utilities;
import com.qing.browser.utils.IOUtils;
import com.qing.browser.utils.Tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpdateImg extends Thread {

	private int httpNum = 0;
	private int maxThreadNum = 3;
	private List<Attach> httpList;
	private Handler handler;

	public UpdateImg(List<Attach> httpList, int maxThreadNum, Handler handler) {
		// TODO Auto-generated constructor stub
		this.httpList = httpList;
		this.maxThreadNum = maxThreadNum;
		this.handler = handler;
	}

	public void run() {
		try {
			while (httpList != null && httpList.size() > 0) {
				if (httpNum < maxThreadNum) {
					getHttpBitmap(httpList.get(0));
					System.out.println("remove " + httpList.size());
					httpList.remove(0);
					httpNum++;
				}
				Thread.sleep(500);
			}
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);// 发送消息
		} catch (Exception e) {
			Log.e("UpdateImg run ", e.getMessage());
		}
	}

	public Bitmap getHttpBitmap(Attach attach) {
		Bitmap bitmap = null;
		// 下载的目标文件在服务器上的地址
		int size = Utilities
				.getIconResourcesSize(R.drawable.ic_launcher_folder);
		HttpGet httpGet = new HttpGet(attach.getUrl());
		HttpClient hc = new DefaultHttpClient();
		try {
			HttpResponse ht = hc.execute(httpGet);

			if (ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity he = ht.getEntity();
				InputStream in = he.getContent();
				BufferedInputStream bis = new BufferedInputStream(in);
				bitmap = BitmapFactory.decodeStream(bis);
				bis.close();
				in.close();
				httpNum--;

				Message msg = new Message();
				msg.what = 1;
				msg.getData().putInt("id", attach.getId());
				handler.sendMessage(msg);// 发送消息
			} else {
				httpNum--;
			}

		} catch (Exception e) {
			httpNum--;
			e.printStackTrace();
		}
		if (bitmap != null) {
			Bitmap mBitmap = Bitmap
					.createScaledBitmap(bitmap, size, size, true);
			Tools.storeInSD(mBitmap, attach.getTitle() + ".png");
		}
		return bitmap;
	}
}
