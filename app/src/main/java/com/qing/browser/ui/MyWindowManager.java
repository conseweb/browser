package com.qing.browser.ui;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.utils.Constants;

public class MyWindowManager {

	/**
	 * 翻页悬浮窗View的实例
	 */
	private static FloatWindowTurnthepageView TurnthepageWindow;
	private static LayoutParams TurnthepageWindowParams;

	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private static WindowManager mWindowManager;

	/**
	 * 全屏工具菜单View
	 */
	private static FloatWindowToolView floatwindowtoolview;
	private static LayoutParams toolWindowParams;

	/**
	 * 创建一个全屏菜单悬浮窗。位置为屏幕正中间。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createToolWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		if (floatwindowtoolview == null) {
			floatwindowtoolview = new FloatWindowToolView(context);
			if (toolWindowParams == null) {
				Log.e("H", "createToolWindow");
				toolWindowParams = new LayoutParams();
				toolWindowParams.type = LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
				toolWindowParams.format = PixelFormat.RGBA_8888;
				toolWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				toolWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				toolWindowParams.width = FloatWindowToolView.viewWidth;
				toolWindowParams.height = FloatWindowToolView.viewHeight;
				toolWindowParams.x = screenWidth - screenWidth / 5;
				toolWindowParams.y = screenHeight - screenHeight / 4;
				toolWindowParams.token = Launcher.mAllView.getWindowToken();
			}
			floatwindowtoolview.setParams(toolWindowParams);
			windowManager.addView(floatwindowtoolview, toolWindowParams);
		}
	}

	/**
	 * 将全屏菜单悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeToolWindow(Context context) {
		if (floatwindowtoolview != null) {
			Log.e("H", "removeToolWindow");
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(floatwindowtoolview);
			floatwindowtoolview = null;
		}
	}

	/**
	 * 创建一个翻页悬浮窗。初始位置为屏幕的右部中间位置。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createTurePageWindow(Context context) {
		WindowManager windowManager = getWindowManager(context);
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		if (TurnthepageWindow == null) {
			TurnthepageWindow = new FloatWindowTurnthepageView(context);
			if (TurnthepageWindowParams == null) {
				Log.e("H", "createTurePageWindow");
				TurnthepageWindowParams = new LayoutParams();
				TurnthepageWindowParams.type = LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
				TurnthepageWindowParams.format = PixelFormat.RGBA_8888;
				TurnthepageWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
						| LayoutParams.FLAG_NOT_FOCUSABLE;
				TurnthepageWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				TurnthepageWindowParams.width = FloatWindowTurnthepageView.viewWidth;
				TurnthepageWindowParams.height = FloatWindowTurnthepageView.viewHeight;
				TurnthepageWindowParams.x = screenWidth;
				TurnthepageWindowParams.y = screenHeight / 2;
				TurnthepageWindowParams.token = Launcher.mAllView
						.getWindowToken();
			}
			TurnthepageWindow.setParams(TurnthepageWindowParams);
			windowManager.addView(TurnthepageWindow, TurnthepageWindowParams);
		}
	}

	/**
	 * 将翻页悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeTurePageWindow(Context context) {
		if (TurnthepageWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(TurnthepageWindow);
			TurnthepageWindow = null;
		}
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

}
