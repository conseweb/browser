package com.qing.browser.utils;


/**
 * Defines constants.
 */
public class Constants {

	/**
	 * ��Ʒ��ʶ
	 */
	public static final String PROD = "100";

	/**
	 * Preferences �ļ���
	 */
	public static final String PREFERENCES_NAME = "Qing";

	/**
	 * SharedPreferences �洢����
	 */
	public static final String MANU_DEFAULT_EXT_NAME = "MANU_DEFAULT_EXT_NAME";

	/**
	 * SharedPreferences �洢����
	 */
	public static final String MANU_NAME_DEFAULT_EXT_NAME = "MANU_NAME_DEFAULT_EXT_NAME";

	/**
	 * ���ͱ����ȡ����
	 */
	public static final int MACH_LENGTH = 15;
	
	/**
	 * SharedPreferences �洢����
	 */
	public static final String MACH_DEFAULT_EXT_NAME = "MACH_DEFAULT_EXT_NAME";

	/**
	 * ��Ļ�汾
	 */
	public static final String SCREEN_VERSION = "1.0.0";

	/**
	 * ����ID
	 */
	public static final int UPDATE_VERSION_MIDDLE = 0;
	/**
	 * SharedPreferences �洢����
	 */
	public static final String UPDATE_VERSION_NAME_MIDDLE = "UPDATE_VERSION_NAME_MIDDLE";

	public static final String UPDATE_VERSION_NAME_MIDDLE_UPDATETIME = "UPDATE_VERSION_NAME_MIDDLE_UPDATETIME";

	/**
	 * ����ID
	 */
	public static final int UPDATE_VERSION_RIGHT = 0;
	/**
	 * SharedPreferences �洢����
	 */
	public static final String UPDATE_VERSION_NAME_RIGHT = "UPDATE_VERSION_NAME_RIGHT";

	public static final String UPDATE_VERSION_NAME_RIGHT_UPDATETIME = "UPDATE_VERSION_NAME_RIGHT_UPDATETIME";

	public static final long UPDATE_TIME_INTERVAL = 24 * 60 * 60 * 1000;
	/**
	 * ����ID
	 */
	public static final int UPDATE_VERSION_FAXIAN = 0;
	/**
	 * SharedPreferences �洢����
	 */
	public static final String UPDATE_VERSION_NAME_FAXIAN = "UPDATE_VERSION_NAME_FAXIAN";

	public static final String MiddleScreenJsonString = "MiddleScreenJsonString";

	public static final String RightScreenJsonString = "RightScreenJsonString";

	/**
	 * �Ƿ�������SD����д��־
	 */
	public static final boolean WRITE_LOG_SD_CARD = true;

	public static final String QINGBROWSER_USER_DIRECTORY = "user/";

	public static boolean UnlockForgetPassword = false;

	/**
	 * �����ļ���ʱ��׺��
	 */
	public static final String DownLoadFileName = ".cache";

	/**
	 * ����汾����
	 */
	public static final String VERSION_INFO = "VERSION_INFO";
	
	/**
	 * ���°ٷֱ�
	 */
	public static final int DOWN_STEP = 1;

	/**
	 * ���ظ��� �ӳ�ʱ��
	 */
	public static final int DOWN_DELAY_TIME = 500;
	
	/**
	 * ���������С
	 */
	public static final String FontSize = "FontSize";
	

	// ///////////ͨ��Э���ֶζ���/////////////////
	public static final String VTIT = "VTIT";

	public static final String VNUM = "VNUM";

	public static final String VSIZ = "VSIZ";

	public static final String VTEX = "VTEX";

	public static final String VPAT = "VPAT";

	public static final String CMFL = "CMFL";

	/**
	 * webView ����url
	 */
	public static final String WEB_VIEW_URL = "WEB_VIEW_URL";

	/**
	 * �˵�����
	 */
	public static final String MENU_NAME = "MENU_NAME";
	
	/**
	 * cookie����
	 */
	public static final String CookieType = "CookieType";
	public static final int CookieTypeNull = 0;
	public static final int CookieTypeLogin = 1;
	public static final int CookieTypeShop = 2;
	
	// //////////////////ͨ��Э���ֶζ������/////////////
 
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
	 * �ϴμ���Ƿ�����ΪĬ�������ʱ��
	 */
	public static final String lastUpdateTime = "lastUpdateTime";
	/**
	 * �Ƿ񼤻� true �Ѽ���
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
	public static final String URL_SEARCH_YAMAXUN = "http://www.amazon.cn/gp/aw/s/ref=is_box_?__mk_zh_CN=����ѷ��վ&k=%s";

	/**
	 * User agents.
	 */
	public static String USER_AGENT_DEFAULT = "";
	public static String USER_AGENT_DESKTOP = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Chrome/7.0.517.44 Safari/534.7";

	/**
	 * ��ǰ��Ļ���ȵ�ģʽ
	 */
	public static final String screenMode = "screenMode";

	/**
	 * ��ǰ��Ļ����ֵ 0--255
	 */
	public static final String screenBrightness = "screenBrightness";
	
	public static String memid = "memid";; //�û�ID
	public static boolean tuiguangflag;
	
	/**
	 * Preferences.
	 */
	public static final String PREFERENCES_TIME = "PREFERENCES_TIME";//��ǰ�������ʱ��
	public static final String PREFERENCES_NIGHT_MODE = "Night_mode";// ҹ��ģʽ
	public static final String PREFERENCES_INCOGNITO_MODE = "incognito_mode";// �޺�
	public static final String PREFERENCES_GENERAL_HOME_PAGE = "GeneralHomePage";
	public static final String PREFERENCES_GENERAL_SEARCH_URL = "GeneralSearchUrl";
	public static final String PREFERENCES_GENERAL_SWITCH_TABS_METHOD = "GeneralSwitchTabMethod";
	public static final String PREFERENCES_SHOW_FULL_SCREEN = "GeneralFullScreen";// ȫ��
	public static final String PREFERENCES_BROWSER_ENABLE_IMAGES = "BrowserEnableImages";// ��ͼ
	public static final String PREFERENCES_BROWSER_ENABLE_WIFI_IMAGES = "BrowserEnableWifiImages";// ����wifi����ʾͼƬ
	public static final String PREFERENCES_BROWSER_ENABLE_FORM_DATA = "BrowserEnableFormData";// ��ס������
	public static final String PREFERENCES_BROWSER_ENABLE_COOKIES = "BrowserEnableCookies";// ����cookies
	public static final String PREFERENCES_BROWSER_DOWNLOAD_NOT_WIFI_REMIND = "DownloadNotWifiRemind";//��wifi���������ļ�����
	public static final String PREFERENCES_SCREEN_ORIENTATION = "GeneralScreenOrientation";// ��Ļ����  ����0 ����1 ����ϵͳ2  
	
	
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
	 * �°�����ҳ����ʾ
	 */
	public static final String WelcomePageShow_sp = "WelcomePageShow";
	public static final int WelcomePageShow = 1;//��Ҫ��ʾ ֵ+1
	
	/**
	 * �Ƿ���ʾ���û�������� ����ǰ�汾��
	 */
	public static final String newUserDialogShow = "newUserDialogShow";
	
	/**
	 * �Ƿ���Ҫ�����������ݿ�-ѡ�����
	 */
	public static final String UPDATE_WEATHER_DATABASE = "UPDATA_WEATHER_DATABASE";
	
	/**
	 * �ж��Ƿ��һ�� ����
	 */
	public static final String IS_FIRST = "IS_FIRST";
	
	/**
	 * ���� ������ҳ��ʱ�� 
	 */
	public static final String ONE_WEBVIEW_TIME = "ONE_WEBVIEW_TIME";
	
	public static final String ONE_WEBVIEW_MOBILEPATH = "/data/data/com.qing.browser/files/";
	
	
	//shortcut commamd list
	public static final String SHORTCUT_TIANQI="command:tianqi";
	
	public static final String SHORTCUT_ZHUANKE="command:zhuanke";
	
	public static final String SHORTCUT_LAUNCHER="command:launcher";

	/**
	 * ���ͳһ��ʱ�����  һ������
	 */
	public static final String ALL_AD_SHOW_TIME = "ALL_AD_SHOW_TIME";
	
	/**
	 * ��¼�ײ��������������
	 */
	public static final String AD_HENGTIAO_SHOUCI = "AD_HENGTIAO_SHOUCI";
	
	/**
	 * ��¼�м�������������
	 */
	public static final String AD_CHAPING_SHOUCI = "AD_CHAPING_SHOUCI";
	
	/**
	 * ����ҳ����������
	 */
	public static final String AD_XINZENG_SHOUCI = "AD_XINZENG_SHOUCI";
	
	/**
	 * �ײ����������
	 */
	public static final int AD_HENGTIAO_FLAG = 101;
	
	/**
	 * �м���������
	 */
	public static final int AD_CHAPING_FLAG = 102;
	
	/**
	 * ����ҳ������
	 */
	public static final int AD_XINZENG_FLAG = 103;
	
	/**
	 * ���ͼƬ��С���� 70*70��СͼƬ��
	 */
	public static final int AD_SMALL_PIC_FLAG = 1;
	
	/**
	 * ���ͼƬ��С����  ��ͼƬ
	 */
	public static final int AD_BIG_PIC_FLAG = 2;
	
	public static final String UPDATE_AD_SWEEP_UPDATETIME="UPDATE_AD_SWEEP_UPDATETIME";

	/**
	 * �ϴμ����Ч��ҳ����ʱ��
	 */
	public static final String snaplastUpdateTime = "snaplastUpdateTime";
	
}
