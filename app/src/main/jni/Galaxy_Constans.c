#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

/**
 * 联网验证域名
 */
jstring VALID_MACHINE_DOMAIN = "http://www.data123.com.cn/validMachine";

/**
 * 新版apk下载域名
 */
jstring NEW_VERSION_APK_DOWNLOAD_URL = "http://up.data123.com.cn";


/**
 * 联网验证域名
 */
jstring Java_com_universe_galaxy_jnibase_JniConstants_getVALIDMACHINEDOMAIN(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, VALID_MACHINE_DOMAIN);
}

/**
 * 新版apk下载域名
 */
jstring Java_com_universe_galaxy_jnibase_JniConstants_getNEWVERSIONAPKDOWNLOADURL(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, NEW_VERSION_APK_DOWNLOAD_URL);
}


