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
	 * ��ҳ������View��ʵ��
	 */
	private static FloatWindowTurnthepageView TurnthepageWindow;
	private static LayoutParams TurnthepageWindowParams;

	/**
	 * ���ڿ�������Ļ����ӻ��Ƴ�������
	 */
	private static WindowManager mWindowManager;

	/**
	 * ȫ�����߲˵�View
	 */
	private static FloatWindowToolView floatwindowtoolview;
	private static LayoutParams toolWindowParams;

	/**
	 * ����һ��ȫ���˵���������λ��Ϊ��Ļ���м䡣
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
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
	 * ��ȫ���˵�����������Ļ���Ƴ���
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
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
	 * ����һ����ҳ����������ʼλ��Ϊ��Ļ���Ҳ��м�λ�á�
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
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
	 * ����ҳ����������Ļ���Ƴ���
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
	 */
	public static void removeTurePageWindow(Context context) {
		if (TurnthepageWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(TurnthepageWindow);
			TurnthepageWindow = null;
		}
	}

	/**
	 * ���WindowManager��δ�������򴴽�һ���µ�WindowManager���ء����򷵻ص�ǰ�Ѵ�����WindowManager��
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
	 * @return WindowManager��ʵ�������ڿ�������Ļ����ӻ��Ƴ���������
	 */
	private static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

}
