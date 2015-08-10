package com.qing.browser.jnibase;

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
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary("QING_Constans");
		} catch (UnsatisfiedLinkError ule) {
		}
	}

	// �������ش��뺯�� �C ����ƥ��Android.mk�ж�ӦԴ�ļ� LOCAL_SRC_FILESֵ

	/**
	 * Ĭ�ϵ�ַ����
	 * 
	 * @return
	 */
	private native static String getDEFAULTDOMAIN();

	/**
	 * Ĭ�ϵ�ַ����
	 * 
	 * @return
	 */
	public static String getJniDEFAULTDOMAIN() {
		return getDEFAULTDOMAIN();
	}

	/**
	 * ��ǩ��ַ����
	 * 
	 * @return
	 */
	private native static String getBOOKMARKACTIONPROTOCOL();

	/**
	 * ��ǩ��ַ����
	 * 
	 * @return
	 */
	public static String getJniBOOKMARKACTIONPROTOCOL() {
		return getDEFAULTDOMAIN() + getBOOKMARKACTIONPROTOCOL();
	}

	/**
	 * ����Ļ��ַ����
	 */
	private native static String getONEWEBVIEWURL();

	/**
	 * ����Ļ��ַ����
	 */
	public static String getJnigetONE_WEBVIEW_URL() {
		return getONEWEBVIEWURL();
	}
	
	/**
	 * �������ӿ�
	 */
	private native static String getADSHOW();
	
	/**
	 * �������ӿ�
	 */
	public static String getJnigetADSHOW() {
		return getADSHOW();
	}
	
	/**
	 * �����ؼ���
	 * 
	 * @return
	 */
	private native static String getGETKEYWORDSEARCH();

	/**
	 * �����ؼ���
	 * 
	 * @return
	 */
	public static String getJnigetGETKEY_WORD_SEARCHL() {
		return getDEFAULTDOMAIN() + getGETKEYWORDSEARCH();
	}
	
}
