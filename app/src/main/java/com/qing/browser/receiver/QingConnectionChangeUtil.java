package com.qing.browser.receiver;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qing.browser.net.URLUtil;
import com.qing.browser.utils.ConstantsUrl;
import com.qing.browser.utils.StringUtil;
import com.qing.browser.utils.Tools;

public class QingConnectionChangeUtil implements Runnable {

	private static QingConnectionChangeUtil network;
	private Handler handler;
	private Context context;

	public static QingConnectionChangeUtil getInstance(Context context,
			Handler handler) {
		if (network == null)
			network = new QingConnectionChangeUtil(context, handler);
		return network;
	}

	private QingConnectionChangeUtil(Context context, Handler handler) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			if (Tools.isConnectInternet(context)) {
				String poststring = Tools.getPoststring(context);
				
				String json = URLUtil.getInstance().getJson(
						ConstantsUrl.getSALENETSETUP, poststring);
				
				Log.v("LS","安装一次的统计 json=="+json);
				
				if (StringUtil.isNull(json)) {
					Message msg = new Message();
					msg.obj = "";
					msg.what = 0;
					handler.sendMessage(msg);
				} else {
					JSONObject jsonObject = new JSONObject(json);
					if ("success".equals(jsonObject.optString("status"))) {
						Message msg = new Message();
						msg.obj = jsonObject.optString("ret");
						msg.what = 1;
						handler.sendMessage(msg);

					} else {
						Message msg = new Message();
						msg.obj = jsonObject.optString("ret");
						msg.what = 0;
						handler.sendMessage(msg);

					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
