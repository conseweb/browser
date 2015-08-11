package com.qing.browser.net;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.qing.browser.utils.ConstantsUrl;
import com.qing.browser.utils.Tools;

public class BookmarkRightUtil implements Runnable {

	private static BookmarkRightUtil bookmarkUtil;
	private Handler handler;
	private Context context;
	private String poststring;

	private final String STATUS_KEY = "status";
	private final String STATUS_NULL = "version is NULL";

	private BookmarkRightUtil(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	public static BookmarkRightUtil getInstance(Context context, Handler handler) {
		if (bookmarkUtil == null)
			return new BookmarkRightUtil(context, handler);
		return bookmarkUtil;
	}

	@Override
	public void run() {
		try {
			if (Tools.isConnectInternet(context)) {
				poststring = Tools.getPoststring(context)
						+ Tools.getRightBookmarkString(context);
				String json = URLUtil.getInstance().getJson(
						ConstantsUrl.BOOKMARK_ACTION_PROTOCOL, poststring);
				JSONObject jsonObject = new JSONObject(json);
				if (STATUS_NULL.equals(jsonObject.optString(STATUS_KEY))) {
					// 无更新
					Message msg = new Message();
					msg.obj = STATUS_NULL;
					msg.what = 0;
					handler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.obj = jsonObject;
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
