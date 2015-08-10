LOCAL_PATH := $(call my-dir)



#######添加模块##########################
include $(CLEAR_VARS)

# Here we give our module name and source file(s)

#可以考虑做成java文件类名
LOCAL_MODULE          := QING_Constans

#添加源文件
#注意符号“\”

LOCAL_SRC_FILES       :=QING_Constans.c


#表示编译成动态库 
include $(BUILD_SHARED_LIBRARY)

#######添加模块##########################
include $(CLEAR_VARS)

# Here we give our module name and source file(s)

#可以考虑做成java文件类名
LOCAL_MODULE          := Galaxy_Constans

#添加源文件
#注意符号“\”

LOCAL_SRC_FILES       :=Galaxy_Constans.c


#表示编译成动态库 
include $(BUILD_SHARED_LIBRARY)

#######添加模块##########################
include $(CLEAR_VARS)

LOCAL_MODULE := locSDK3

LOCAL_SRC_FILES := liblocSDK3.so
   
include $(PREBUILT_SHARED_LIBRARY)
