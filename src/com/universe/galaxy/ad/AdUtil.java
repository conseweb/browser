package com.universe.galaxy.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.universe.galaxy.util.Tools;
import com.universe.galaxy.util.URLUtil;

public class AdUtil implements Runnable {

	private Handler handler;
	private Context context;
	private String address;

	@Override
	public void run() {
		try {
			if (Tools.isConnectInternet(context)) {
				AdInfo info = URLUtil.getInstance().getAd(address);
				if (info != null) {
					Message msg = new Message();
					msg.obj = info;
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private AdUtil(Context context, Handler handler, String address) {
		this.context = context;
		this.handler = handler;
		this.address = address;
	}

	private static AdUtil adUtil;

	public static AdUtil getInstance(Context context, Handler handler,
			String address) {
		if (adUtil == null)
			return new AdUtil(context, handler, address);
		return adUtil;
	}
}
