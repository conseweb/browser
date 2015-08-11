package com.qing.browser.utils;


/**
 * Defines constants.
 */
public class Constants {

	/**
	 * 产品标识
	 */
	public static final String PROD = "100";

	/**
	 * Preferences 文件名
	 */
	public static final String PREFERENCES_NAME = "Qing";

	/**
	 * SharedPreferences 存储名称
	 */
	public static final String MANU_DEFAULT_EXT_NAME = "MANU_DEFAULT_EXT_NAME";

	/**
	 * SharedPreferences 存储名称
	 */
	public static final String MANU_NAME_DEFAULT_EXT_NAME = "MANU_NAME_DEFAULT_EXT_NAME";

	/**
	 * 机型编码截取长度
	 */
	public static final int MACH_LENGTH = 15;
	
	/**
	 * SharedPreferences 存储名称
	 */
	public static final String MACH_DEFAULT_EXT_NAME = "MACH_DEFAULT_EXT_NAME";

	/**
	 * 屏幕版本
	 */
	public static final String SCREEN_VERSION = "1.0.0";

	/**
	 * 更新ID
	 */
	public static final int UPDATE_VERSION_MIDDLE = 0;
	/**
	 * SharedPreferences 存储名称
	 */
	public static final String UPDATE_VERSION_NAME_MIDDLE = "UPDATE_VERSION_NAME_MIDDLE";

	public static final String UPDATE_VERSION_NAME_MIDDLE_UPDATETIME = "UPDATE_VERSION_NAME_MIDDLE_UPDATETIME";

	/**
	 * 更新ID
	 */
	public static final int UPDATE_VERSION_RIGHT = 0;
	/**
	 * SharedPreferences 存储名称
	 */
	public static final String UPDATE_VERSION_NAME_RIGHT = "UPDATE_VERSION_NAME_RIGHT";

	public static final String UPDATE_VERSION_NAME_RIGHT_UPDATETIME = "UPDATE_VERSION_NAME_RIGHT_UPDATETIME";

	public static final long UPDATE_TIME_INTERVAL = 24 * 60 * 60 * 1000;
	/**
	 * 更新ID
	 */
	public static final int UPDATE_VERSION_FAXIAN = 0;
	/**
	 * SharedPreferences 存储名称
	 */
	public static final String UPDATE_VERSION_NAME_FAXIAN = "UPDATE_VERSION_NAME_FAXIAN";

	public static final String MiddleScreenJsonString = "MiddleScreenJsonString";

	public static final String RightScreenJsonString = "RightScreenJsonString";

	/**
	 * 是否允许向SD卡中写日志
	 */
	public static final boolean WRITE_LOG_SD_CARD = true;

	public static final String QINGBROWSER_USER_DIRECTORY = "user/";

	public static boolean UnlockForgetPassword = false;

	/**
	 * 下载文件临时后缀名
	 */
	public static final String DownLoadFileName = ".cache";

	/**
	 * 软件版本更新
	 */
	public static final String VERSION_INFO = "VERSION_INFO";
	
	/**
	 * 跟新百分比
	 */
	public static final int DOWN_STEP = 1;

	/**
	 * 下载更新 延迟时间
	 */
	public static final int DOWN_DELAY_TIME = 500;
	
	/**
	 * 设置字体大小
	 */
	public static final String FontSize = "FontSize";
	

	// ///////////通信协议字段定义/////////////////
	public static final String VTIT = "VTIT";

	public static final String VNUM = "VNUM";

	public static final String VSIZ = "VSIZ";

	public static final String VTEX = "VTEX";

	public static final String VPAT = "VPAT";

	public static final String CMFL = "CMFL";

	/**
	 * webView 加载url
	 */
	public static final String WEB_VIEW_URL = "WEB_VIEW_URL";

	/**
	 * 菜单名称
	 */
	public static final String MENU_NAME = "MENU_NAME";
	
	/**
	 * cookie类型
	 */
	public static final String CookieType = "CookieType";
	public static final int CookieTypeNull = 0;
	public static final int CookieTypeLogin = 1;
	public static final int CookieTypeShop = 2;
	
	// //////////////////通信协议字段定义结束/////////////
 
	public static final String Turn_Page_Kye = "Turn_Page_Kye";
	public static final String Turn_Page_VolumeKey = "Turn_Page_VolumeKey";
	public static final String EXTRA_ID_NEW_TAB = "EXTRA_ID_NEW_TAB";
	public static final String EXTRA_ID_URL = "EXTRA_ID_URL";

	public static final String EXTRA_ID_BOOKMARK_ID = "EXTRA_ID_BOOKMARK_ID";
	public static final String EXTRA_ID_BOOKMARK_URL = "EXTRA_ID_BOOKMARK_URL";
	public static final String EXTRA_ID_BOOKMARK_TITLE = "EXTRA_ID_BOOKMARK_TITLE";
	public static final String EXTRA_ID_BOOKMARK_UUID = "EXTRA_ID_BOOKMARK_UUID";

	public static final String EXTRA_SAVED_URL = "EXTRA_SAVED_URL";

	public static final int BOOKMARK_THUMBNAIL_WIDTH_FACTOR = 70;
	public static final int BOOKMARK_THUMBNAIL_HEIGHT_FACTOR = 60;

	/**
	 * 上次检查是否设置为默认浏览器时间
	 */
	public static final String lastUpdateTime = "lastUpdateTime";
	/**
	 * 是否激活 true 已激活
	 */
	public static final String Salenet = "salenet";

	/**
	 * Specials urls.
	 */
	public static final String URL_ABOUT_BLANK = "about:blank";
	public static final String URL_ABOUT_START = "about:start";
	public static final String URL_ACTION_SEARCH = "action:search?q=";
	public static final String URL_GOOGLE_MOBILE_VIEW = "http://www.google.com/gwt/x?u=%s";
	public static final String URL_GOOGLE_MOBILE_VIEW_NO_FORMAT = "http://www.google.com/gwt/x?u=";

	/**
	 * Search urls.
	 */
	public static final String URL_SEARCH_GOOGLE = "http://www.google.com/search?q=%s";
	public static final String URL_SEARCH_BAIDU = "http://m.baidu.com/s?word=%s";
	public static final String URL_SEARCH_SOUGOU = "http://wap.sogou.com/web/searchList.jsp?keyword=%s";
	public static final String URL_SEARCH_YICHA = "http://page.yicha.cn/tp/s.y?key=%s";
	public static final String URL_SEARCH_YISOU = "http://i.easou.com/s.m?q=%s";
	public static final String URL_SEARCH_DANGDANG = "http://m.dangdang.com/gw_search.php?key=%s";
	public static final String URL_SEARCH_TAOBAO = "http://s.m.taobao.com/search.htm?q=%s";
	public static final String URL_SEARCH_XINLANG = "http://search.sina.com.cn/?q=%s&c=news";
	public static final String URL_SEARCH_YAMAXUN = "http://www.amazon.cn/gp/aw/s/ref=is_box_?__mk_zh_CN=亚马逊网站&k=%s";

	/**
	 * User agents.
	 */
	public static String USER_AGENT_DEFAULT = "";
	public static String USER_AGENT_DESKTOP = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.44 Safari/534.7";

	/**
	 * 当前屏幕亮度的模式
	 */
	public static final String screenMode = "screenMode";

	/**
	 * 当前屏幕亮度值 0--255
	 */
	public static final String screenBrightness = "screenBrightness";
	
	public static String memid = "memid";; //用户ID
	public static boolean tuiguangflag;
	
	/**
	 * Preferences.
	 */
	public static final String PREFERENCES_TIME = "PREFERENCES_TIME";//当前打开浏览器时间
	public static final String PREFERENCES_NIGHT_MODE = "Night_mode";// 夜间模式
	public static final String PREFERENCES_INCOGNITO_MODE = "incognito_mode";// 无痕
	public static final String PREFERENCES_GENERAL_HOME_PAGE = "GeneralHomePage";
	public static final String PREFERENCES_GENERAL_SEARCH_URL = "GeneralSearchUrl";
	public static final String PREFERENCES_GENERAL_SWITCH_TABS_METHOD = "GeneralSwitchTabMethod";
	public static final String PREFERENCES_SHOW_FULL_SCREEN = "GeneralFullScreen";// 全屏
	public static final String PREFERENCES_BROWSER_ENABLE_IMAGES = "BrowserEnableImages";// 无图
	public static final String PREFERENCES_BROWSER_ENABLE_WIFI_IMAGES = "BrowserEnableWifiImages";// 仅在wifi下显示图片
	public static final String PREFERENCES_BROWSER_ENABLE_FORM_DATA = "BrowserEnableFormData";// 记住表单数据
	public static final String PREFERENCES_BROWSER_ENABLE_COOKIES = "BrowserEnableCookies";// 接收cookies
	public static final String PREFERENCES_BROWSER_DOWNLOAD_NOT_WIFI_REMIND = "DownloadNotWifiRemind";//非wifi环境下载文件提醒
	public static final String PREFERENCES_SCREEN_ORIENTATION = "GeneralScreenOrientation";// 屏幕方向  竖屏0 横屏1 跟随系统2  
	
	
	public static final String PREFERENCES_GENERAL_BARS_DURATION = "GeneralBarsDuration";
	public static final String PREFERENCES_GENERAL_BUBBLE_POSITION = "GeneralBubblePosition";
	public static final String PREFERENCES_GENERAL_HIDE_TITLE_BARS = "GeneralHideTitleBars";
	public static final String PREFERENCES_SHOW_TOAST_ON_TAB_SWITCH = "GeneralShowToastOnTabSwitch";

	public static final String PREFERENCES_UI_VOLUME_KEYS_BEHAVIOUR = "GeneralVolumeKeysBehaviour";

	public static final String PREFERENCES_DEFAULT_ZOOM_LEVEL = "DefaultZoomLevel";

	public static final String PREFERENCES_BROWSER_HISTORY_SIZE = "BrowserHistorySize";
	public static final String PREFERENCES_BROWSER_USE_WIDE_VIEWPORT = "BrowserUseWideViewPort";
	public static final String PREFERENCES_BROWSER_LOAD_WITH_OVERVIEW = "BrowserLoadWithOverview";
	public static final String PREFERENCES_BROWSER_ENABLE_PASSWORDS = "BrowserEnablePasswords";
	public static final String PREFERENCES_BROWSER_USER_AGENT = "BrowserUserAgent";
	public static final String PREFERENCES_BROWSER_ENABLE_PLUGINS_ECLAIR = "BrowserEnablePluginsEclair";
	public static final String PREFERENCES_BROWSER_ENABLE_PROXY_SETTINGS = "BrowserEnableProxySettings";
	public static final String PREFERENCES_BROWSER_ENABLE_PLUGINS = "BrowserEnablePlugins";
	public static final String PREFERENCES_BROWSER_RESTORE_LAST_PAGE = "PREFERENCES_BROWSER_RESTORE_LAST_PAGE";

	public static final String PREFERENCES_ADBLOCKER_ENABLE = "AdBlockerEnable";

	public static final String PREFERENCES_BOOKMARKS_SORT_MODE = "BookmarksSortMode";

	public static final String PREFERENCES_LAST_VERSION_CODE = "LastVersionCode";

	public static final String PREFERENCES_START_PAGE_SHOW_SEARCH = "StartPageEnableSearch";
	public static final String PREFERENCES_START_PAGE_SHOW_BOOKMARKS = "StartPageEnableBookmarks";
	public static final String PREFERENCES_START_PAGE_SHOW_HISTORY = "StartPageEnableHistory";
	public static final String PREFERENCES_START_PAGE_BOOKMARKS_LIMIT = "StartPageBookmarksLimit";
	public static final String PREFERENCES_START_PAGE_HISTORY_LIMIT = "StartPageHistoryLimit";

	public static final String PREFERENCE_BOOKMARKS_DATABASE = "PREFERENCE_BOOKMARKS_DATABASE";

	/**
	 * 新版引导页面显示
	 */
	public static final String WelcomePageShow_sp = "WelcomePageShow";
	public static final int WelcomePageShow = 1;//需要显示 值+1
	
	/**
	 * 是否显示新用户礼包弹框 看当前版本号
	 */
	public static final String newUserDialogShow = "newUserDialogShow";
	
	/**
	 * 是否需要更新天气数据库-选择城市
	 */
	public static final String UPDATE_WEATHER_DATABASE = "UPDATA_WEATHER_DATABASE";
	
	/**
	 * 判断是否第一次 启动
	 */
	public static final String IS_FIRST = "IS_FIRST";
	
	/**
	 * 保存 本地网页的时间 
	 */
	public static final String ONE_WEBVIEW_TIME = "ONE_WEBVIEW_TIME";
	
	public static final String ONE_WEBVIEW_MOBILEPATH = "/data/data/com.qing.browser/files/";
	
	
	//shortcut commamd list
	public static final String SHORTCUT_TIANQI="command:tianqi";
	
	public static final String SHORTCUT_ZHUANKE="command:zhuanke";
	
	public static final String SHORTCUT_LAUNCHER="command:launcher";

	/**
	 * 广告统一的时间控制  一天间隔。
	 */
	public static final String ALL_AD_SHOW_TIME = "ALL_AD_SHOW_TIME";
	
	/**
	 * 记录底部横条广告间隔次数
	 */
	public static final String AD_HENGTIAO_SHOUCI = "AD_HENGTIAO_SHOUCI";
	
	/**
	 * 记录中间插屏广告间隔次数
	 */
	public static final String AD_CHAPING_SHOUCI = "AD_CHAPING_SHOUCI";
	
	/**
	 * 新增页面广告间隔次数
	 */
	public static final String AD_XINZENG_SHOUCI = "AD_XINZENG_SHOUCI";
	
	/**
	 * 底部横条广告标记
	 */
	public static final int AD_HENGTIAO_FLAG = 101;
	
	/**
	 * 中间插屏广告标记
	 */
	public static final int AD_CHAPING_FLAG = 102;
	
	/**
	 * 新增页面广告标记
	 */
	public static final int AD_XINZENG_FLAG = 103;
	
	/**
	 * 广告图片大小类型 70*70（小图片）
	 */
	public static final int AD_SMALL_PIC_FLAG = 1;
	
	/**
	 * 广告图片大小类型  大图片
	 */
	public static final int AD_BIG_PIC_FLAG = 2;
	
	public static final String UPDATE_AD_SWEEP_UPDATETIME="UPDATE_AD_SWEEP_UPDATETIME";

	/**
	 * 上次检查无效网页快照时间
	 */
	public static final String snaplastUpdateTime = "snaplastUpdateTime";
	
}
