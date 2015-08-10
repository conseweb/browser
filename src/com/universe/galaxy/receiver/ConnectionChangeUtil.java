package com.universe.galaxy.receiver;

import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.universe.galaxy.util.Tools;
import com.universe.galaxy.util.URLUtil;

public class ConnectionChangeUtil implements Runnable {

	private static ConnectionChangeUtil network;
	private Handler handler;
	private Context context;

	public static ConnectionChangeUtil getInstance(Context context,
			Handler handler) {
		if (network == null)
			network = new ConnectionChangeUtil(context, handler);
		return network;
	}

	private ConnectionChangeUtil(Context context, Handler handler) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			if (Tools.isConnectInternet(context)) {
				Map<String, String> netinfomap = URLUtil.getInstance()
						.getNetContetnt(context);
				if (netinfomap != null) {
					Message msg = new Message();
					msg.obj = netinfomap;
					msg.what = 0;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
