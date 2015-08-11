package com.qing.browser.utils;

import com.qing.browser.jnibase.JniConstants;

public class ConstantsUrl {

/*	*//**
	 * 书签内容接口
	 *//*
	public static final String BOOKMARK_ACTION_PROTOCOL = JniConstants
			.getJniBOOKMARKACTIONPROTOCOL();

	*//**
	 * 左屏幕网址导航
	 *//*
	public static final String ONE_WEBVIEW_URL = JniConstants.getJnigetONE_WEBVIEW_URL();

	*//**
	 * 广告请求接口
	 *//*
	public static final String getADSHOW = JniConstants.getJnigetADSHOW();
	
	*//**
	 * 关键字 搜索
	 *//*
	public static final String GETKEY_WORD_SEARCH = JniConstants
			.getJnigetGETKEY_WORD_SEARCHL();*/
	
	/**
	 * 书签内容接口
	 */
	public static final String BOOKMARK_ACTION_PROTOCOL = 
			"http://115.28.72.152:8181/protocol/v1/bookMarkAction?";

	/**
	 * 左屏幕网址导航
	 */
	public static final String ONE_WEBVIEW_URL = 
			"http://m.3600.com/";

	/**
	 * 广告请求接口
	 */
	public static final String getADSHOW = 
			"http://115.28.72.152:8181/protocol/pagead?";
	
	/**
	 * 关键字 搜索
	 */
	public static final String GETKEY_WORD_SEARCH = 
			"http://115.28.72.152:8181/protocol/v1/showKeyWord?";
	
	/**
	 * 获取广告信息地址
	 */
	public static final String  ADVERTISING_DOMAIN_QING = 
			"http://115.28.72.152:8181/protocol/ad?";
	
	/**
	 * 更新版本
	 */
	public static final String  CHECKVERSION = 
			"http://115.28.72.152:8181/protocol/upVer?type=0";
	
	/**
	 * 用户统计
	 */
	public static final String YONG_HU_TONG_JI_URL = 
			"http://115.28.72.152:8181/protocol/v1/saveAccessDetail";
	
	/**
	 * 每次 打开软件
	 */
	public static final String getSALENET = 
			"http://115.28.72.152:8181/protocol/salenet?";
	
	/**
	 * 内置 访问一次 成功后不访问
	 */
	public static final String getSALENETSETUP = 
			"http://115.28.72.152:8181/protocol/salenetsetup?";
	
}
