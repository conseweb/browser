#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>


/**
 * 默认地址域名
 */
jstring DEFAULT_DOMAIN = "http://115.28.72.152:8181";

jstring BOOKMARK_ACTION_PROTOCOL = "/protocol/v1/bookMarkAction?";

jstring ONE_WEBVIEW_URL = "http://m.3600.com/";

jstring ADSHOW = "http://115.28.72.152:8181/protocol/pagead?";

jstring GETKEY_WORD_SEARCH = "/protocol/v1/showKeyWord?";


jstring Java_com_qing_browser_jnibase_JniConstants_getDEFAULTDOMAIN(
	JNIEnv* env, jobject obj) {
		return (*env)->NewStringUTF(env, DEFAULT_DOMAIN);
}

jstring Java_com_qing_browser_jnibase_JniConstants_getBOOKMARKACTIONPROTOCOL(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, BOOKMARK_ACTION_PROTOCOL);
}


/**
 * 左屏幕网址导航
 */
jstring Java_com_qing_browser_jnibase_JniConstants_getONEWEBVIEWURL(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, ONE_WEBVIEW_URL);
}

/**
 * 广告接口
 */
jstring Java_com_qing_browser_jnibase_JniConstants_getADSHOW(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, ADSHOW);
}

/**
 * 关键字
 */
jstring Java_com_qing_browser_jnibase_JniConstants_getGETKEYWORDSEARCH(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, GETKEY_WORD_SEARCH);
}
