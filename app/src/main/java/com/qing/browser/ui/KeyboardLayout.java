package com.qing.browser.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class KeyboardLayout extends RelativeLayout {
	public static final byte KEYBOARD_STATE_SHOW = -3;
	public static final byte KEYBOARD_STATE_HIDE = -2;
	public static final byte KEYBOARD_STATE_INIT = -1;
	private boolean mHasInit;
	private boolean mHasKeybord;
	private int mHeight;
	private onKybdsChangeListener mListener;

	public KeyboardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KeyboardLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeyboardLayout(Context context) {
		super(context);
	}
	/**
	 * set keyboard state listener
	 */
	public void setOnkbdStateListener(onKybdsChangeListener listener){
		mListener = listener;
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!mHasInit) {
			mHasInit = true;
			mHeight = b;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
			}
		} else {
			mHeight = mHeight < b ? b : mHeight;// 取最大
		}
		if (mHasInit && mHeight > b) {
			mHasKeybord = true;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
			}
		}
		if (mHasInit && mHasKeybord && mHeight == b) {
			mHasKeybord = false;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
			}
		}
	}
	
	public interface onKybdsChangeListener{
		public void onKeyBoardStateChange(int state);
	}

}
