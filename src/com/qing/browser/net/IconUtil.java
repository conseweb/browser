package com.qing.browser.net;

import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qing.browser.R;
import com.qing.browser.net.IconInfo.FlushedInputStream;
import com.qing.browser.ui.launcher.Utilities;

public class IconUtil implements Runnable {

	private final static String TAG = "IconUtil";
	private static IconUtil iconUtil;
	private Handler handler;
	private String iconUrl;

	private Bitmap bitmap = null;

	private IconUtil(Handler handler, String iconUrl) {
		this.handler = handler;
		this.iconUrl = iconUrl;
	}

	public static IconUtil getInstance(Handler handler, String iconUrl) {
		if (iconUtil == null)
			return new IconUtil(handler, iconUrl);
		return iconUtil;
	}

	@Override
	public void run() {
		URL url = null;
		URLConnection conn = null;
		try {
			Log.i(TAG, "setDrawable");
			url = new URL(iconUrl);
			conn = url.openConnection();
			Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(
					conn.getInputStream()));

			if (bitmap != null) {
				Canvas canvas = new Canvas();
				// canvas.drawBitmap(bitmap, null, new Rect(0, 0, 40, 40),
				// null);
				int size = Utilities
						.getIconResourcesSize(R.drawable.ic_launcher_folder);
				Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, size, size,
						true);
				Message msg = new Message();
				msg.obj = mBitmap;
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.obj = null;
				msg.what = 0;
				handler.sendMessage(msg);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			url = null;
			conn = null;
		}
	}
}
