package com.qing.browser.ui;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.qing.browser.utils.StringUtil;

public class FloatWindowService extends Service {

	public static final String OPERATION = "operation";
	public static final String TYPE = "type";
	public static final String TYPE_TOOLMENU = "TYPE_TOOLMENU";
	public static final String TYPE_TURNPAGE = "TYPE_TURNPAGE";
	public static final int OPERATION_SHOW = 100;
	public static final int OPERATION_HIDE = 101;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if(intent == null)
			return;
		int operation = intent.getIntExtra(OPERATION, 0);
		String type = intent.getStringExtra(TYPE);
		if (StringUtil.isNull(type) || operation == 0) {
			Log.e("H", "悬浮窗error StringUtil.isNull(type) || operation == 0");
			return;
		}

		Message msg = new Message();
		switch (operation) {
		case OPERATION_SHOW:
			if (type.equals(TYPE_TOOLMENU)) {
				msg.what = 2;
			} else {
				msg.what = 0;
			}
			break;
		case OPERATION_HIDE:
			if (type.equals(TYPE_TOOLMENU)) {
				msg.what = 3;
			} else {
				msg.what = 1;
			}
			break;
		}
		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				MyWindowManager.createTurePageWindow(FloatWindowService.this);
				break;
			case 1:
				MyWindowManager.removeTurePageWindow(FloatWindowService.this);
				break;

			case 2:
				MyWindowManager.createToolWindow(FloatWindowService.this);
				break;

			case 3:
				MyWindowManager.removeToolWindow(FloatWindowService.this);
				break;
			}
		}
	};

}
