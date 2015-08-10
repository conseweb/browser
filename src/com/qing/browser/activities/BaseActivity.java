package com.qing.browser.activities;

import com.qing.browser.utils.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {

	private TextView mNightView;
	public SharedPreferences sp;
	private WindowManager mWindowManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		sp = getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 夜间模式开启
		if (sp.getBoolean(Constants.PREFERENCES_NIGHT_MODE, false)) {
			night();
		} else {
			day();
		}
		if (0 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (1 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (2 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		day();
	}

	public void night() {
		if (mNightView == null) {
			mNightView = new TextView(this);
			mNightView.setBackgroundColor(0xaa000000);
		}

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		lp.gravity = Gravity.BOTTOM;
		lp.y = 10;

		try {
			mWindowManager.addView(mNightView, lp);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void day() {
		if (mNightView != null) { 
			try {
				mWindowManager.removeView(mNightView);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			mNightView = null;
		}
		
	}
	
	public void Toast(String msg){
		Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
}
