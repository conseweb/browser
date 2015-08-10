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

	// Ӧ�ö�̬��
	static {
		try {
			System.loadLibrary("Galaxy_Constans");
		} catch (UnsatisfiedLinkError ule) {
		}
	}

	// �������ش��뺯�� �C ����ƥ��Android.mk�ж�ӦԴ�ļ� LOCAL_SRC_FILESֵ
}
