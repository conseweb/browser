LOCAL_PATH := $(call my-dir)



#######���ģ��##########################
include $(CLEAR_VARS)

# Here we give our module name and source file(s)

#���Կ�������java�ļ�����
LOCAL_MODULE          := QING_Constans

#���Դ�ļ�
#ע����š�\��

LOCAL_SRC_FILES       :=QING_Constans.c


#��ʾ����ɶ�̬�� 
include $(BUILD_SHARED_LIBRARY)

#######���ģ��##########################
include $(CLEAR_VARS)

# Here we give our module name and source file(s)

#���Կ�������java�ļ�����
LOCAL_MODULE          := Galaxy_Constans

#���Դ�ļ�
#ע����š�\��

LOCAL_SRC_FILES       :=Galaxy_Constans.c


#��ʾ����ɶ�̬�� 
include $(BUILD_SHARED_LIBRARY)

#######���ģ��##########################
include $(CLEAR_VARS)

LOCAL_MODULE := locSDK3

LOCAL_SRC_FILES := liblocSDK3.so
   
include $(PREBUILT_SHARED_LIBRARY)
