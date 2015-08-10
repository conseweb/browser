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

	// 应用动态库
	static {
		try {
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary("QING_Constans");
		} catch (UnsatisfiedLinkError ule) {
		}
	}

	// 声明本地代码函数 – 必须匹配Android.mk中对应源文件 LOCAL_SRC_FILES值

	/**
	 * 默认地址域名
	 * 
	 * @return
	 */
	private native static String getDEFAULTDOMAIN();

	/**
	 * 默认地址域名
	 * 
	 * @return
	 */
	public static String getJniDEFAULTDOMAIN() {
		return getDEFAULTDOMAIN();
	}

	/**
	 * 书签地址域名
	 * 
	 * @return
	 */
	private native static String getBOOKMARKACTIONPROTOCOL();

	/**
	 * 书签地址域名
	 * 
	 * @return
	 */
	public static String getJniBOOKMARKACTIONPROTOCOL() {
		return getDEFAULTDOMAIN() + getBOOKMARKACTIONPROTOCOL();
	}

	/**
	 * 左屏幕网址导航
	 */
	private native static String getONEWEBVIEWURL();

	/**
	 * 左屏幕网址导航
	 */
	public static String getJnigetONE_WEBVIEW_URL() {
		return getONEWEBVIEWURL();
	}
	
	/**
	 * 广告请求接口
	 */
	private native static String getADSHOW();
	
	/**
	 * 广告请求接口
	 */
	public static String getJnigetADSHOW() {
		return getADSHOW();
	}
	
	/**
	 * 搜索关键字
	 * 
	 * @return
	 */
	private native static String getGETKEYWORDSEARCH();

	/**
	 * 搜索关键字
	 * 
	 * @return
	 */
	public static String getJnigetGETKEY_WORD_SEARCHL() {
		return getDEFAULTDOMAIN() + getGETKEYWORDSEARCH();
	}
	
}
