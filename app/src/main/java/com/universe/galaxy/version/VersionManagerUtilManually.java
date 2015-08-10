package com.universe.galaxy.version;

import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.qing.browser.utils.Constants;
import com.qing.browser.utils.ConstantsUrl;
import com.universe.galaxy.util.Tools;
import com.universe.galaxy.util.URLUtil;

public class VersionManagerUtilManually implements Runnable {

	private static VersionManagerUtilManually network;
	private Handler handler;
	private Context context;

	public static VersionManagerUtilManually getInstance(Context context,
			Handler handler) {
		if (network == null)
			network = new VersionManagerUtilManually(context, handler);
		return network;
	}

	private VersionManagerUtilManually(Context context, Handler handler) {
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			if (Tools.isConnectInternet(context)) {
				String postString = Tools.getPoststring(context);
				Map<String, String> map = URLUtil.getInstance().getMap(
						ConstantsUrl.CHECKVERSION, postString);
				if (map != null) {
					VersionInfo versionInfo = new VersionInfo(
							map.get(Constants.VTIT), map.get(Constants.VNUM),
							map.get(Constants.VSIZ), map.get(Constants.VTEX),
							map.get(Constants.VPAT));
					Message msg = new Message();
					msg.obj = versionInfo;
					msg.what = 0;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
