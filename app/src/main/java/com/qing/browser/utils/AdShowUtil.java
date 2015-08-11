package com.qing.browser.utils;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.universe.galaxy.util.Tools;
import com.universe.galaxy.util.URLUtil;

public class AdShowUtil implements Runnable {

	private static AdShowUtil network;
	private Handler handler;
	private Context context;
	private int ad_flag;
	
	/**
	 * ad_flag 
	 * 101：底部横条广告   102：插屏广告   103：代表新增页面广告
	 * 
	 * 对应的图片大小
	 * 1:70*70   2:大图
	 */
	public static AdShowUtil getInstance(Context context,
			Handler handler,int ad_flag) {
		//if (network == null)
			network = new AdShowUtil(context, handler, ad_flag);
		return network;
	}

	private AdShowUtil(Context context, Handler handler,int ad_flag) {
		this.handler = handler;
		this.context = context;
		this.ad_flag = ad_flag;
	}

	@Override
	public void run() {
		try {
			if (Tools.isConnectInternet(context)) {
				int logsize ;
				if(ad_flag==Constants.AD_HENGTIAO_FLAG){
					logsize =Constants.AD_SMALL_PIC_FLAG;
				}else {
					logsize =Constants.AD_BIG_PIC_FLAG;
				}
				String postString = "&locid="+ad_flag+"&logsize="+logsize;
				String result = URLUtil.getInstance().invokeURL(
						ConstantsUrl.getADSHOW, postString);

				try {
					JSONObject jsonObj = new JSONObject(result);

					if ("success".equals(jsonObj.optString("status"))) {
						Message msg = new Message();
						msg.obj = result;
						msg.what = ad_flag;
						handler.sendMessage(msg);

					} else if ("fail".equals(jsonObj.optString("status"))) {
						Message msg = new Message();
						msg.obj = "";
						msg.what = 0;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//Log.v("LS","广告请求的结果=="+result);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
