package com.universe.galaxy.jnibase;

public class JniConstants {

	private static JniConstants jniConstants;

	private JniConstants() {

	}

	public static JniConstants getInstance() {
		if (jniConstants == null)
			jniConstants = new JniConstants();
		return jniConstants;
	}

	// 应用动态库
	static {
		try {
			System.loadLibrary("Galaxy_Constans");
		} catch (UnsatisfiedLinkError ule) {
		}
	}

	// 声明本地代码函数 – 必须匹配Android.mk中对应源文件 LOCAL_SRC_FILES值
}
