package com.universe.galaxy.util;

import android.util.Log;

public class LLL {

	//log 开关控制
	private static boolean logSwitch = true;

	public static void i(String tag, String msg) {
		if (logSwitch)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (logSwitch)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (logSwitch)
			Log.e(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (logSwitch)
			Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (logSwitch)
			Log.w(tag, msg);
	}
}
