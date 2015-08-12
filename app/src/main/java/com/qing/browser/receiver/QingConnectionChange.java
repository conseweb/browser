package com.qing.browser.receiver;

import com.qing.browser.utils.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

public class QingConnectionChange {
 
	public static void ConnectionChangeAction(final Context context) {
		
		
		Handler handler = new Handler(context.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					SharedPreferences sp = context.getSharedPreferences(Constants.PREFERENCES_NAME,
							Context.MODE_PRIVATE);
					switch (msg.what) {
					case 0:
						//fail
						sp.edit().putBoolean(Constants.Salenet, false).commit();
						break;
					case 1:
						//success
						sp.edit().putBoolean(Constants.Salenet, true).commit();
						break;
					}
				}
			}
		};
		Thread thread = new Thread(QingConnectionChangeUtil.getInstance(context,
				handler));
		thread.start();
	}
}
