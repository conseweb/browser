package com.universe.galaxy.util;


public class Constants {
	

	/**
	 * 渠道编码默认值,默认为空,首次出包要设定厂家编码
	 */
	public static final String MANU_DEFAULT_EXT = "1000";
	
	/*
	 * SharedPreferences 存储名称
	 */
	public static final String MANU_DEFAULT_EXT_NAME = "MANU_DEFAULT_EXT_NAME";

	/**
	 * 渠道名称默认值,默认为空,首次出包要设定厂家名称
	 */
	public static final String MANU_NAME_DEFAULT_EXT = "浏览器";
	
	/*
	 * SharedPreferences 存储名称
	 */
	public static final String MANU_NAME_DEFAULT_EXT_NAME = "MANU_NAME_DEFAULT_EXT_NAME";

	/**
	 * 机型编码截取长度
	 */
	public static final int MACH_LENGTH = 15;

	/**
	 * 厂家需求机型默认值,默认为空,如有需要,根据厂家需要赋值
	 */
	public static final String MACH_DEFAULT_EXT = "DT001";
	/**
	 * SharedPreferences 存储名称
	 */
	public static final String MACH_DEFAULT_EXT_NAME = "MACH_DEFAULT_EXT_NAME";
	/**
	 * 机型编码
	 */
	// public static final String MACH = Tools.getModel();

	/**
	 * 出包日期：年后两位MMDD。格式：120905
	 */
	public static final String CHU_BAO_RI_QI = "120905";

	/**
	 * 主动 销量发送标识
	 */
	public static final String ZHU_DONG_SMS_FLAG = "A";

	/**
	 * 待机时间 销量发送标识
	 */
	public static final String DAI_JI_SALE_SMS_FLAG = "B";

	/**
	 * 拨号次数 销量发送标识
	 */
	public static final String BAO_HAO_SALE_SMS_FLAG = "C";

	/**
	 * 解屏次数 销量发送标识
	 */
	public static final String JIE_PING_SALE_SMS_FLAG = "E";

	/**
	 * 补发销量发送标识
	 */
	public static final String SALE_SMS_FLAG_INTERNET = "N";

	/**
	 * 销量发送key
	 */
	public static final String IS_SEND_BE = "IS_SEND_BE";

	/**
	 * Preferences 文件名
	 */
	public static final String PREFERENCES_NAME = "Qing";
	/**
	 * 用户个人资料完成falg，保证只提交一次积分
	 */
	public static final String USEZILIAOWANCHENG = "USEZILIAOWANCHENG";
	/**
	 * 待机时间key
	 */
	public static final String DAI_JI_SHI_JIAN = "DAI_JI_SHI_JIAN";

	/**
	 * 唤醒屏幕次数key
	 */
	public static final String USER_PRESENT_KEY = "USER_PRESENT_KEY";
	/**
	 * 通话次数 key
	 */
	public static final String CALL_NUMBER_KEY = "CALL_NUMBER_KEY";

	/**
	 * 唤醒屏幕次数
	 */
	public static final int USER_PRESENT_COUNT = 50;

	/**
	 * 自动采集销量待机时间 5 小时
	 */
	public static int AUTO_SEND_SALESMS_TIME = 5 * 60;
	/**
	 * 通话的次数
	 */
	public static final int CALL_NUMBER = 10;

	/**
	 * 计时器计时周期 正常为20分钟
	 */
	public static int TIMEING_CYCLE = 20;

	/**
	 * 自动联网周期20分钟
	 */
	public static final long NETWORKING_CYCLE = 20 * 60 * 1000;

	/**
	 * 销量检查周期20分钟
	 */
	public static final long AUTO_SEND_SALESMS_CHECK_TIME = 20 * 60 * 1000;

	/**
	 * 测试模式销量检查周期2分钟
	 */
	public static final long AUTO_SEND_SALESMS_CHECK_TIME_TEST = 1 * 60 * 1000;

	/**
	 * 是否允许向SD卡中写日志
	 */
	public static final boolean WRITE_LOG_SD_CARD = false;

	/**
	 * 版本更新通知ID
	 */
	public static final int VERSION_STATUSBAR_ID = 1;

	/**
	 * 客服中心SD卡根目录
	 */
	public static final String CUSTOMERSERVICE_LOCAL_DIRECTORY = "sdcard/Qing/downloads";

	/**
	 * 版本信息Bean
	 */
	public static final String VERSION_INFO = "VERSION_INFO";

	// ///////////通信协议字段定义/////////////////
	public static final String VTIT = "VTIT";

	public static final String VNUM = "VNUM";

	public static final String VSIZ = "VSIZ";

	public static final String VTEX = "VTEX";

	public static final String VPAT = "VPAT";

	public static final String CMFL = "CMFL";

	public static final String VTIME = "VTIME";

	/**
	 * 电子保卡验证下发信息 指令
	 */
	public static final String CMRT = "CMRT";

	/**
	 * 销量目的号码、广告链接地址 指令
	 */
	public static final String PORT = "PORT";

	/**
	 * 广告图片链接地址 指令
	 */
	public static final String REDC = "REDC";

	/**
	 * 推送广告小图标链接地址 指令
	 */
	public static final String GGTB = "GGTB";

	/**
	 * 销量短信内容、广告文字内容 指令
	 */
	public static final String COMM = "COMM";
	
	/**
	 * 广告文字内容
	 */
	public static final String COMN = "COMN";

	/**
	 * 下次联网间隔时间(小时)指令
	 */
	public static final String FEEC = "FEEC";

	/**
	 * 广告简短内容说明指令
	 */
	public static final String SHIU = "SHIU";

	/**
	 * 指令
	 */
	public static final String CMDT = "CMDT";

	/**
	 * 补发销量：1，推送广告：2，新机上市推送：3 指令
	 */
	public static final String CODE = "CODE";

	/**
	 * 广告类型：1,纯文字。2，小图标。3，图片填充
	 */
	public static final String JFLX = "JFLX";

	/**
	 * 推送铃声: 0,不响 1，响
	 */
	public static final String RING = "RING";

	/**
	 * 推送震动：0，不震动 1，震动
	 */
	public static final String MOVE = "MOVE";

	/**
	 * 推送是否可 清楚： 0，可删 1，不可删
	 */
	public static final String DEL = "DEL";

	/**
	 * 推送弹出框的榜单地址
	 */
	public static final String GURL = "GURL";

	/**
	 * 推送弹出框的APK下载地址
	 */
	public static final String APKURL = "APKURL";

	/**
	 * 推送弹出框 0：不弹出 1：弹框
	 */
	public static final String W_W = "W";

	/**
	 * 广告列表内容指令
	 */
	public static final String advertisingList = "advertisingList";

	public static final String APK = "APK";

	public static final String CMID = "CMID";

	// //////////////////通信协议字段定义结束/////////////

	/**
	 * 跟新百分比
	 */
	public static final int DOWN_STEP = 5;

	/**
	 * 下载更新 延迟时间
	 */
	public static final int DOWN_DELAY_TIME = 500;

	/**
	 * 联网验证按钮下载延时长度
	 */
	public static final int BUTTON_WAIT_TIME = 20;

	/**
	 * 最近一次联网时间标记
	 */
	public static final String BEFORE_TIME = "BEFORE_TIME";

	/**
	 * 最近一次联网时间标记
	 */
	public static final String SERVICE_BEFORE_TIME = "SERVICE_BEFORE_TIME";

	/**
	 * 联网下发时间间隔
	 */
	public static final String NET_TOTAL_TIME = "NET_TOTAL_TIME";
	/**
	 * 联网24小时间隔时间 微秒
	 */
	public static final long TOTALTIME = 24 * 60 * 60 * 1000;
	/**
	 * 联网24小时间隔时间 下限
	 */
	public static final int TOTALTIME_DOWN = 0;
	/**
	 * 联网24小时间隔时间 上限 单位分钟
	 */
	public static final int TOTALTIME_UP = 500 * 60;

	/**
	 * 联网销量统计 时间间隔为60分钟
	 */
	public static final int NET_SALES_INTERVAL_TIME = 60 * 60 * 1000;

	/**
	 * 产品标识
	 */
	public static final String PROD = "100";

	/**
	 * 产品标识
	 */
	public static final String APP_PROD = "101";

	/**
	 * 联网下发指令-联网补发SMS
	 */
	public static final String NET_SEND_SMS = "1";

	/**
	 * 联网下发指令-广告推送2
	 */
	public static final String NET_AD_TWO = "2";

	/**
	 * 联网下发指令-广告推送1 新机上市
	 */
	public static final String NET_AD_ONE = "3";

	/**
	 * 联网下发指令-广告显示文字
	 */
	public static final String NET_SHOW_TEXT = "1";
	/**
	 * 联网下发指令-广告显示图片+文字
	 */
	public static final String NET_SHOW_ALL = "2";

	/**
	 * 联网下发指令-广告显示图片
	 */
	public static final String NET_SHOW_PIC = "3";

	/**
	 * 联网下发指令-联网补发MMS 空闲
	 */
	public static final String NET_SEND_MMS = "4";

	/**
	 * SharedPreferences GPS信息存储名称
	 */
	public static final String SSJD = "SSJD";

	/**
	 * SharedPreferences GPS信息存储名称
	 */
	public static final String SSWD = "SSWD";

	/**
	 * SharedPreferences GPS信息存储名称,存储手机当前位置
	 */
	public static final String SJWZ = "SJWZ";

	/**
	 * 推送广告小图标 默认0
	 */
	public static String TUISONG_MR0 = "0";

	/**
	 * 推送广告小图标 默认1
	 */
	public static String TUISONG_MR1 = "1";

	/**
	 * 推送广告小图标 未读短信
	 */
	public static String TUISONG_WDDX = "2";

	/**
	 * 推送广告小图标 未接电话
	 */
	public static String TUISONG_WJDH = "3";

	/**
	 * 推送广告小图标 电子邮件
	 */
	public static String TUISONG_DZYJ = "4";

	/**
	 * 推送广告小图标 蓝牙耳机
	 */
	public static String TUISONG_LYEJ = "5";

	public static String APP_URLSTR = "APP_URLSTR";

	public static String APP_CMD = "APP_CMD";

	public static final String APP_INSTALL_DIRECTORY = "sdcard/hot/";

	/**
	 * 保存升级提示的时间间隔 （对后台升级有效）
	 */
	public static final String SHENG_JI_JIAN_GE = "SHENG_JI_JIAN_GE";

	/**
	 * 升级提醒的时间间隔
	 */
	public static final String SHENG_JI_TIME = "SHENG_JI_TIME";

}
