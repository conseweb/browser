#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

/**
 * ������֤����
 */
jstring VALID_MACHINE_DOMAIN = "http://www.data123.com.cn/validMachine";

/**
 * �°�apk��������
 */
jstring NEW_VERSION_APK_DOWNLOAD_URL = "http://up.data123.com.cn";


/**
 * ������֤����
 */
jstring Java_com_universe_galaxy_jnibase_JniConstants_getVALIDMACHINEDOMAIN(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, VALID_MACHINE_DOMAIN);
}

/**
 * �°�apk��������
 */
jstring Java_com_universe_galaxy_jnibase_JniConstants_getNEWVERSIONAPKDOWNLOADURL(
		JNIEnv* env, jobject obj) {
	return (*env)->NewStringUTF(env, NEW_VERSION_APK_DOWNLOAD_URL);
}


