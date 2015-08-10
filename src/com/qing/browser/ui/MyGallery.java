package com.qing.browser.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class MyGallery extends Gallery {

	private static final String TAG = "MyGallery";
	private final int sleepTime = 100;

	public MyGallery(Context paramContext) {
		super(paramContext);
	}

	public MyGallery(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public MyGallery(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	private boolean isScrollingLeft(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2) {
		Log.v("L", "isScrollingLeft");
		float f2 = paramMotionEvent2.getX();
		float f1 = paramMotionEvent1.getX();
		if (f2 > f1)
			return true;
		return false;
	}

	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		int keyCode;
		if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return true;
	}

	private static final int timerAnimation = 1;

	public void autoMove(final int position) {
		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case timerAnimation:
					onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
					break;
				default:
					break;
				}
			};
		};
		new Thread(new Runnable() {
			public void run() {
				Log.i(TAG, "position=" + position + " SelectedItemPosition="
						+ getSelectedItemPosition());
				while (position != getSelectedItemPosition()) {
					handler.sendEmptyMessage(timerAnimation);
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}