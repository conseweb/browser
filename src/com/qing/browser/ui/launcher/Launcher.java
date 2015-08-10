package com.qing.browser.ui.launcher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.ErWeiMaChaKanActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.qing.browser.R;
import com.qing.browser.activities.AddShortcutActivity;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.activities.BookmarksHistoryActivity;
import com.qing.browser.activities.DownloadsListActivity;
import com.qing.browser.activities.EditBookmarkActivity;
import com.qing.browser.activities.PageDownOrUpActivity;
import com.qing.browser.activities.QuitDialogActivity;
import com.qing.browser.activities.ScreenSwitchDialogActivity;
import com.qing.browser.activities.SettingActivity;
import com.qing.browser.activities.WelcomePageActivity;
import com.qing.browser.components.CustomWebView;
import com.qing.browser.components.CustomWebViewClient;
import com.qing.browser.controllers.Controller;
import com.qing.browser.download.Dao;
import com.qing.browser.download.DownloadAppIn;
import com.qing.browser.download.DownloadItem;
import com.qing.browser.download.DownloadManager;
import com.qing.browser.download.IDownloadEventsListener;
import com.qing.browser.events.EventConstants;
import com.qing.browser.events.EventController;
import com.qing.browser.model.adapters.UrlSuggestionCursorAdapter;
import com.qing.browser.net.BookmarkMannager;
import com.qing.browser.net.URLUtil;
import com.qing.browser.providers.BookmarksProvider;
import com.qing.browser.providers.BookmarksProviderWrapper;
import com.qing.browser.providers.BookmarksProviderWrapper.BookmarksSource;
import com.qing.browser.providers.BookmarksUtil;
import com.qing.browser.providers.HistoryProvider;
import com.qing.browser.providers.HistoryUtil;
import com.qing.browser.receiver.AlarmReceiver;
import com.qing.browser.tongji.LiuLiangTongji;
import com.qing.browser.ui.FloatWindowService;
import com.qing.browser.ui.KeyboardLayout;
import com.qing.browser.ui.KeyboardLayout.onKybdsChangeListener;
import com.qing.browser.ui.KeywordsFlow;
import com.qing.browser.ui.MyPagerAdapter;
import com.qing.browser.ui.menu.GridButtonInfo;
import com.qing.browser.ui.menu.OnViewChangeListener;
import com.qing.browser.ui.menu.PopmenuDateAdapter;
import com.qing.browser.ui.recent.RecentsPanelView;
import com.qing.browser.ui.recent.TaskDescription;
import com.qing.browser.ui.runnables.FaviconUpdaterRunnable;
import com.qing.browser.ui.runnables.HideToolbarsRunnable;
import com.qing.browser.ui.runnables.SnapshotUpdaterRunnable;
import com.qing.browser.utils.AnimationManager;
import com.qing.browser.utils.ApplicationUtils;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.ConstantsUrl;
import com.qing.browser.utils.DialogEditUtil;
import com.qing.browser.utils.DialogImageUtil;
import com.qing.browser.utils.DialogSearchEngineUtil;
import com.qing.browser.utils.DialogUtil;
import com.qing.browser.utils.IOUtils;
import com.qing.browser.utils.Loading_Dialog;
import com.qing.browser.utils.StringUtil;
import com.qing.browser.utils.Tools;
import com.qing.browser.utils.UrlUtils;
import com.universe.galaxy.ad.AdInfo;
import com.universe.galaxy.ad.AdUtil;
import com.universe.galaxy.util.TongJi;
import com.universe.galaxy.version.NewVersionActivity;
import com.universe.galaxy.version.VersionInfo;
import com.universe.galaxy.version.VersionManagerUtilManually;

public final class Launcher extends BaseActivity implements
		View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
		OnTouchListener, IDownloadEventsListener {
	// static final String TAG = "Launcher";

	String mUrlText = "";
	static final boolean LOGD = false;
	Loading_Dialog LoadDialog = null;
	static final boolean PROFILE_STARTUP = false;
	static final boolean DEBUG_WIDGETS = false;
	static final boolean DEBUG_USER_INTERFACE = false;

	public static final int RESULT_OPEN_NEW_BOOKMARKS_HISTORY = 1;

	static final int SCREEN_COUNT = 5;
	static final int DEFAULT_SCREEN = 2;
	static final int NUMBER_CELLS_X = 4;
	static final int NUMBER_CELLS_Y = 4;

	static final int DIALOG_RENAME_FOLDER = 2;

	private static final Object sLock = new Object();
	private static int sScreen = DEFAULT_SCREEN;

	private LayoutInflater mInflater;

	private DragController mDragController;
	public static Workspace mWorkspace;

	private FolderInfo mFolderInfo;

	private Zone mZone;

	private SendZone mSendZone;

	private DeleteZone mDeleteZone;

	private Bundle mSavedState;

	private SpannableStringBuilder mDefaultKeySsb = null;

	private boolean mWorkspaceLoading = true;

	private boolean mPaused = true;
	private boolean mRestoring;
	private boolean mWaitingForResult;

	private Bundle mSavedInstanceState;

	private LauncherModel mModel;

	private ArrayList<ItemInfo> mDockBarLayoutItems = new ArrayList<ItemInfo>();
	private ArrayList<ItemInfo> mDesktopItems = new ArrayList<ItemInfo>();
	private static HashMap<HashMap<Long, Integer>, FolderInfo> mFolders = new HashMap<HashMap<Long, Integer>, FolderInfo>();

	private static final int CONTEXT_MENU_OPEN = Menu.FIRST + 10;
	private static final int CONTEXT_MENU_OPEN_IN_NEW_TAB = Menu.FIRST + 11;
	private static final int CONTEXT_MENU_DOWNLOAD = Menu.FIRST + 12;
	private static final int CONTEXT_MENU_COPY = Menu.FIRST + 13;
	private static final int CONTEXT_MENU_SEND_MAIL = Menu.FIRST + 14;
	private static final int CONTEXT_MENU_SHARE = Menu.FIRST + 15;

	private static final int OPEN_FILE_CHOOSER_ACTIVITY = 2;

	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	// All Layout
	public static LinearLayout mAllView;

	// top input
	public static boolean top_bar_input_Flag;
	private RelativeLayout top_bar_input;
	public ImageView search_engine;
	private LinearLayout search_engine_layout;

	private static LinearLayout top_bar_main;
	private static LinearLayout top_bar_main_layout;
	private TextView top_folder_view;
	public static EditText urlText;
	private ImageView addbookmark;
	UrlSuggestionCursorAdapter adapter;
	//
	public static EditText mUrlEditText;
	private Button mGoButton;
	private static ProgressBar mProgressBar;
	private KeyboardLayout key_input_layout;
	private ListView key_listview;
	private Button Top_sao_yi_sao;
	private Cursor input_cursor = null;

	// top browser
	private LinearLayout top_bar_browser;
	private LinearLayout mBottomBar;

	// top find
	private LinearLayout mFindBar;
	private ImageView mFindPreviousButton;
	private ImageView mFindNextButton;
	private Button mFindCloseButton;
	private EditText mFindText;

	// TabViewSwitch
	// private ImageView mPreviousTabView;
	// private ImageView mNextTabView;

	// WebView
	// 是否支持浏览页面左右滑动切换
	// 现阶段未实现
	public static boolean mutilPageFlag = false;
	public static Tabspace mTabspace;
	// private WebViewspace mWebViewspace;
	public static boolean newWebView = true;

	public static String[] beforeUrl = new String[20];

	public static int mCurrentIndex = 0;
	public static int mWebViewCurrentIndex = 0;
	private CustomWebView mCurrentWebView;
	private List<HashMap<String, Object>> mCurrentWebViews;
	private List<Object> mViews;
	private List<Object> mWebViews;
	public static List<HashMap<String, Object>> mWebViewBitmaps;
	// private List<Boolean> mWebViewStates;
	public static List<HashMap<String, Object>> mWebViewStates;

	// Bottom bar
	private static LinearLayout bottom_bar;
	private TextView bottom_folder_view;
	private ImageView bottom_bar_back;
	private ImageView bottom_bar_forward;
	private ImageView bottom_bar_menu;
	private ImageView bottom_bar_homepage;
	private ImageView bottom_refresh;
	private ImageView bottom_bar_tabs;
	public static TextView bottom_tabs_num;
	public static int tabs_num;

	// Popmenu
	GridView gridview1, gridview2, gridview3;
	ViewPager menuLayout;
	private List<View> listViews; // Tab页面列表
	private PopupWindow popmenu;
	private boolean menuShowed;
	private View viewpage1, viewpage2;
	private List<GridButtonInfo> lstDate1, lstDate2;
	private View page_1, page_2;
	private ImageView mainpage_1, mainpage_2, mainpage_3;
	public String[] content1 = { "全屏", "书签/历史", "加书签", "夜间", "分享", "下载", "更新",
			"退出" };
	public String[] content2 = { "设置", "屏幕", "翻页模式", "复制链接", "无痕", "无图",
			"页内查找", "关于" };
	private BaseAdapter gridviewPop1, gridviewPop2;
	private RelativeLayout set_default_browser;
	DialogImageUtil dialogimageutil;
	private DialogUtil dialogUtil;
	private DialogEditUtil dialogeditUtil;
	ResolveInfo mInfo;
	PackageInfo info;
	PackageManager pm;
	public boolean setDefault = false;
	public boolean cleanDefault = false;

	private Folder mFolder;

	public static int tabCurrentItem = 0;

	// tabs gallery
	public static RelativeLayout tabs_gallery;
	private RecentsPanelView mRecentsPanelView;
	private TextView tabs_close_all;
	private TextView tabs_new_tab;
	public static boolean tabs_gallery_Flag;

	// snapshot webview
	private static WebView snapshotwebview;

	private Drawable mCircularProgress;

	private boolean mUrlBarVisible;
	private boolean mToolsActionGridVisible = false;
	private boolean mFindDialogVisible = false;

	public static TextWatcher mUrlTextWatcher;

	private HideToolbarsRunnable mHideToolbarsRunnable;

	// Main Container 主容器
	// 设置为只包含两个组件 workspace和webviewspace
	// private ViewFlipper mViewFlipper;

	// Main Container 主容器
	public static Homespace mHomespace;
	public static int CURRENT_SCREEN_WORKSPACE = 0;
	public static int CURRENT_SCREEN_WORKSPACE_LEFT = 0;
	public static int CURRENT_SCREEN_WORKSPACE_MIDDLE = 1;
	public static int CURRENT_SCREEN_WORKSPACE_RIGHT = 2;
	public static int CURRENT_SCREEN_WORKSPACE_NOT = -1;
	public static int CURRENT_SCREEN_TABSPACE = 1;

	// 主容器手势
	private GestureDetector mGestureDetector;
	// 页面切换方式设置 默认为滑动
	private SwitchTabsMethod mSwitchTabsMethod = SwitchTabsMethod.FLING;

	private enum SwitchTabsMethod {
		BUTTONS, FLING, BOTH
	}

	private ValueCallback<Uri> mUploadMessage;

	private OnSharedPreferenceChangeListener mPreferenceChangeListener;

	private View mCustomView;
	private Bitmap mDefaultVideoPoster = null;
	private View mVideoProgressView = null;
	private static boolean mFullscreenFlag;
	private FrameLayout mFullscreenContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;

	public static int mWidth, mHeight;

	private double scale = 1.8;
	public SharedPreferences sp;
	private QuitDialogActivity quitDialog;
	private ProgressDialog mProgressDialog;

	public static CustomWebView One_webView;
	private KeywordsFlow keywordsFlow;
	private DialogSearchEngineUtil SearWindow;

	private PopupWindow ShowBookmark;
	public DisplayImageOptions options;
	protected static ImageLoader imageLoader = ImageLoader.getInstance();
	private ArrayList<HashMap<String, Object>> groups = new ArrayList<HashMap<String, Object>>();

	public final static String STATUS_KEY = "status";
	public final static String STATUS_NULL = "version is NULL";

	public static Launcher mLauncher_ErWeiMa;
	public static RelativeLayout top_bar_inpu_ErWeiMa;
	public static LinearLayout top_bar_mai_ErWeiMa;
	public static Homespace mHomespac_ErWeiMa;

	private PageDownOrUpActivity PageDownOrUpActivity = null;

	private boolean isCancel = false;

	// Browser End

	private boolean tianqi_flag = false;
	private int add_ad_tab_index = 0;
	private int waitingTime = 3;
	private View ADview = null;
	private String LINKURL = null;
	public int ADshowflag = 0;
	public static boolean mMiddleCustomItems_flag = false;

	private WindowHandler mWindowHandler;
	
	private Button addToLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LauncherApplication app = ((LauncherApplication) getApplication());
		mModel = app.setLauncher(this);
		mDragController = new DragController(this);
		mInflater = getLayoutInflater();

		mWindowHandler = new WindowHandler();
		// mWidth = getScreenWidth(this);
		// mHeight = getScreenHeight(this);

		LauncherApplication.getInstance().addActivity(Launcher.this);
		sp = getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {

			requestWindowFeature(Window.FEATURE_NO_TITLE);// 1、隐藏标题栏
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);// 2、隐藏状态栏
		}

		if (PROFILE_STARTUP) {
			android.os.Debug.startMethodTracing(Environment
					.getExternalStorageDirectory() + "/launcher");
		}
		
		// 这里进入应用时候的展示页面
		startActivity(new Intent(Launcher.this, WelcomeActivity.class));
		

		if (sp.getInt(Constants.WelcomePageShow_sp, 0) < Constants.WelcomePageShow) {
			sp.edit()
					.putInt(Constants.WelcomePageShow_sp,
							Constants.WelcomePageShow).commit();
			startActivity(new Intent(Launcher.this, WelcomePageActivity.class));
		}

		setContentView(R.layout.launcher_main);

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.hotseat_browser_bg)
				.showImageForEmptyUri(R.drawable.hotseat_browser_bg)
				.showImageOnFail(R.drawable.hotseat_browser_bg)
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		mSavedState = savedInstanceState;
		mMiddleCustomItems_flag = false;
		new Dao(Launcher.this).queryAndUpdateInfos(Launcher.this);

		if (PROFILE_STARTUP) {
			android.os.Debug.stopMethodTracing();
		}

		// 加载保存的书签信息
		BookmarkMannager.loadMiddleScreen(Launcher.this);
		BookmarkMannager.loadRightScreen(Launcher.this);

		if (!mRestoring) {
			mModel.startLoader(this, true);
		}

		// For handling default keys
		mDefaultKeySsb = new SpannableStringBuilder();
		Selection.setSelection(mDefaultKeySsb, 0);

		// Browser Begin
		Controller.getInstance().setPreferences(
				PreferenceManager.getDefaultSharedPreferences(this));

		setProgressBarVisibility(true);
		mCircularProgress = getResources().getDrawable(R.drawable.spinner);
		EventController.getInstance().addDownloadListener(this);
		mHideToolbarsRunnable = null;

		buildComponents();

		//
		updateSwitchTabsMethod();
		updateBookmarksDatabaseSource();
		//
		registerPreferenceChangeListener();
		//

		initializeWebIconDatabase();
		// Browser End


		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.i("TAG", "BookmarkMannager");
				if (Tools.isMiddleUpdate(Launcher.this)) {
					BookmarkMannager.updateMiddleScreen(Launcher.this);
				}
				if (Tools.isRightUpdate(Launcher.this)) {
					BookmarkMannager.updateRightScreen(Launcher.this);
				}
			}
		}, 5000);

		// 访问统计
		if (Tools.isConnectInternet(Launcher.this)) {
			new Thread(new GetSalenet_Thread()).start();
		}
		
		// 无效网页快照定期清除管理 每天清除一次?
		checkSnapShotClean();

		Intent i = getIntent();
		if (i.getData() != null) {

			if (i.getDataString().equals("saoyisao")
					|| i.getDataString().equals("扫一扫")) {
				if (i.getDataString().equals("saoyisao")) {
				} else {
				}
				startActivity(new Intent(Launcher.this, CaptureActivity.class));
			} else if (i.getDataString().equals(Constants.SHORTCUT_TIANQI)) {
				startActivity(new Intent(Launcher.this, CaptureActivity.class));
			} else if (i.getDataString().equals(Constants.SHORTCUT_ZHUANKE)) {
				startActivity(new Intent(Launcher.this, SettingActivity.class));
			} else if (i.getDataString().equals(Constants.SHORTCUT_LAUNCHER)) {
				TongJi.AddAnalyticsData(TongJi.d_qingliulanqi);
			} else {
				addTab(i.getDataString());
			}
		} else {
		}

		mLauncher_ErWeiMa = this;// 用于其他页面直接添加网页
		CellLayout.update_screen = 0;

		Tools.AddShortcut(Launcher.this);

		// 检查是否设置为默认浏览器
		checkDefaultBrowser();

		// 左侧网页更新规则
		// 左侧网页更新规则
		// 左侧网页更新规则
		long one_webview_time = sp.getLong(Constants.ONE_WEBVIEW_TIME, 0);
		long nowTime = new Date().getTime();
		sp.edit().putLong(Constants.PREFERENCES_TIME, nowTime).commit();
		if (one_webview_time == 0
				|| (nowTime - one_webview_time >= 24 * 60 * 60 * 7000 )) {
			Editor editor = sp.edit();
			editor.putLong(Constants.ONE_WEBVIEW_TIME, nowTime);
			editor.commit();
			Tools.startSavePage(ConstantsUrl.ONE_WEBVIEW_URL, "one_webview",
					Launcher.this);
		}

		
		// 记录广告时间
		long ALL_AD_SHOW_TIME = sp.getLong(Constants.ALL_AD_SHOW_TIME, 0);
		if (ALL_AD_SHOW_TIME == 0
				|| (nowTime - ALL_AD_SHOW_TIME >= 24 * 60 * 60 * 1000/* 1天 */)) {
			Editor editor = sp.edit();
			editor.putLong(Constants.ALL_AD_SHOW_TIME, nowTime);
			editor.putInt(Constants.AD_HENGTIAO_SHOUCI, 0);
			editor.putInt(Constants.AD_CHAPING_SHOUCI, 0);
			editor.putInt(Constants.AD_XINZENG_SHOUCI, 0);
			editor.commit();
		}

		setAlarm();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		Intent i = getIntent();
		if (i.getData() != null) {

		}
		super.onWindowFocusChanged(hasFocus);
	}

	static int getScreen() {
		synchronized (sLock) {
			return sScreen;
		}
	}

	static void setScreen(int screen) {
		synchronized (sLock) {
			sScreen = screen;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mWaitingForResult = false;
		
		if (data != null) {
			if (requestCode == RESULT_OPEN_NEW_BOOKMARKS_HISTORY) {
				String url = data.getStringExtra(Constants.EXTRA_ID_URL);
				if (!StringUtil.isNull(url)) {
					if (data.getBooleanExtra(Constants.EXTRA_ID_NEW_TAB, false)) {

						newTabAndThumbnail();

						addTab(url);
						navigateToUrl(url);
						tabCurrentItem = mTabspace.getChildCount() - 1;
					} else {
						addTab(url);
						navigateToUrl(url);
					}
				}

			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("H", "onResume()--");
		mPaused = false;

		if (mRestoring) {
			mWorkspaceLoading = true;
			mModel.startLoader(this, true);
			mRestoring = false;
		}
		if (0 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (1 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (2 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

		if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()
				|| mWorkspace.getCurrentScreen() == 0) {
			if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
				bottom_bar.setVisibility(View.GONE);
				top_bar_main.setVisibility(View.GONE);
				ShowOrHideFloatWindow(FloatWindowService.OPERATION_SHOW,
						FloatWindowService.TYPE_TOOLMENU);

			}

			if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
				ShowOrHideFloatWindow(FloatWindowService.OPERATION_SHOW,
						FloatWindowService.TYPE_TURNPAGE);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mDragController.cancelDrag();

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Flag the loader to stop early before switching
		mModel.stopLoader();
		// mAllAppsGrid.surrender();
		return Boolean.TRUE;
	}

	// We can't hide the IME if it was forced open. So don't bother
	/*
	 * @Override public void onWindowFocusChanged(boolean hasFocus) {
	 * super.onWindowFocusChanged(hasFocus);
	 * 
	 * if (hasFocus) { final InputMethodManager inputManager =
	 * (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	 * WindowManager.LayoutParams lp = getWindow().getAttributes();
	 * inputManager.hideSoftInputFromWindow(lp.token, 0, new
	 * android.os.ResultReceiver(new android.os.Handler()) { protected void
	 * onReceiveResult(int resultCode, Bundle resultData) { Log.d(TAG,
	 * "ResultReceiver got resultCode=" + resultCode); } }); Log.d(TAG,
	 * "called hideSoftInputFromWindow from onWindowFocusChanged"); } }
	 */

	private boolean acceptFilter() {
		final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		return !inputManager.isFullscreenMode();
	}

	public boolean queryDownload() {
		Dao dao = new Dao(Launcher.this);
		DownloadItem info = dao.query(Launcher.this);
		if (info != null) {
			dialogUtil = new DialogUtil.Builder(Launcher.this)
					.setTitleText("温馨提示").setText("有下载任务正在进行，继续完成下载？")
					.setPositiveButton("继续下载", new View.OnClickListener() {
						public void onClick(View v) {
							dialogUtil.dismiss();
						}
					}).setNegativeButton("不，退出", new View.OnClickListener() {
						public void onClick(View v) {
							dialogUtil.dismiss();
							DownloadManager.getInstance().stopAllDownloadTask();
							new Dao(Launcher.this)
									.queryAndUpdateInfos(Launcher.this);
							saveLiuLiangData();
							finish();
						}
					}).create();
			dialogUtil.show();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = super.onKeyDown(keyCode, event);
		if (!handled && acceptFilter() && keyCode != KeyEvent.KEYCODE_ENTER) {
			boolean gotKey = TextKeyListener.getInstance().onKeyDown(
					mWorkspace, mDefaultKeySsb, keyCode, event);
			if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {
				// something usable has been typed - start a search
				// the typed text will be retrieved and cleared by
				// showSearchDialog()
				// If there are multiple keystrokes before the search dialog
				// takes focus,
				// onSearchRequested() will be called for every keystroke,
				// but it is idempotent, so it's fine.
				return onSearchRequested();
			}
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (mFullscreenFlag) {
				hideCustomView();
				return true;
			}
			if (isFolderOpen()) {
				closeFolder();
				return true;
			}

			if (tabs_gallery_Flag) {

				int position = tabCurrentItem;

				HashMap<String, Object> hashmap = mWebViewStates.get(position);

				if (CURRENT_SCREEN_WORKSPACE_NOT == Integer.parseInt((hashmap
						.get("isHome").toString()))) {
					mHomespace.setCurrentScreen(CURRENT_SCREEN_TABSPACE);
				} else {
					if (CURRENT_SCREEN_WORKSPACE_LEFT == Integer
							.parseInt((hashmap.get("isHome").toString()))) {
						mWorkspace
								.moveToDefaultScreen(CURRENT_SCREEN_WORKSPACE_LEFT);
					} else if (CURRENT_SCREEN_WORKSPACE_MIDDLE == Integer
							.parseInt((hashmap.get("isHome").toString()))) {
						mWorkspace
								.moveToDefaultScreen(CURRENT_SCREEN_WORKSPACE_MIDDLE);
					} else if (CURRENT_SCREEN_WORKSPACE_RIGHT == Integer
							.parseInt((hashmap.get("isHome").toString()))) {
						mWorkspace
								.moveToDefaultScreen(CURRENT_SCREEN_WORKSPACE_RIGHT);
					}
					mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
					mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
					mUrlEditText.setText("");
					mUrlEditText.addTextChangedListener(mUrlTextWatcher);
					urlText.setText("");
					mProgressBar.setVisibility(View.GONE);
					onPageStarted("");
					CheckIsBookmark("");
				}
				if (false == (Boolean) hashmap.get("isWebview")) {
					mTabspace.setVisibility(View.GONE);
				} else {
					mTabspace.setChildVisibility(position);
					mTabspace.setVisibility(View.VISIBLE);
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(position);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					urlText.setText(mCustomWebView.getTitle());
					mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
					mUrlEditText.setText(mCustomWebView.getUrl());
					mUrlEditText.addTextChangedListener(mUrlTextWatcher);
				}

				tabs_gallery.setVisibility(View.GONE);
				tabs_gallery_Flag = false;
				mCurrentIndex = position;
				Toast.makeText(Launcher.this, "" + position, 1000).show();
				return true;
			}
			if (top_bar_input_Flag) {
				top_bar_main.setVisibility(View.VISIBLE);
				mHomespace.setVisibility(View.VISIBLE);
				top_bar_input.setVisibility(View.GONE);
				top_bar_input_Flag = false;

				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()
						|| mWorkspace.getCurrentScreen() == 0) {
					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.GONE);
						top_bar_main.setVisibility(View.GONE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_SHOW,
								FloatWindowService.TYPE_TOOLMENU);

					}

					if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_SHOW,
								FloatWindowService.TYPE_TURNPAGE);
					}
				}

				getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
										| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				return true;
			}

			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCurrentWebView = null;
			if (childView != null) {
				mCurrentWebView = (CustomWebView) childView
						.findViewById(R.id.webview);
			}
			if (mCurrentWebView != null) {
				if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {

					if (mWorkspace.getCurrentScreen() == 0) { // LS
						if (One_webView != null && One_webView.canGoBack()) {
							One_webView.goBack();
							return true;
						}
					}
					if (queryDownload()) {
						return true;
					}

					QuitDialogShow();

				}
				if (mCurrentWebView.canGoBack()) {
					mCurrentWebView.goBack();
					return true;
				} else {
					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.VISIBLE);
						top_bar_main.setVisibility(View.VISIBLE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TOOLMENU);

					}
					// TODO 清除浏览页面，返回桌面
					// 如果当前显示为桌面，则进入退出提示页面
					mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
					mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
					mUrlEditText.setText("");
					mUrlEditText.addTextChangedListener(mUrlTextWatcher);
					urlText.setText("");
					mProgressBar.setVisibility(View.GONE);
					onPageStarted("");
					CheckIsBookmark("");

					if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TURNPAGE);
					}
					return true;
				}
			}

			if (mWorkspace.getCurrentScreen() == 0) {
				if (One_webView != null && One_webView.canGoBack()) {
					One_webView.goBack();
					return true;
				}
			}

			if (queryDownload()) {
				return true;
			}
			QuitDialogShow();
			return true;

		}
		// Eat the long press event so the keyboard doesn't come up.
		if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {

			if (popmenu == null || !popmenu.isShowing()) {
				if (tabs_gallery_Flag) {
					return true;
				}
				menuShowed = true;
				showPopMenuWindow();
				if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
					if (CURRENT_SCREEN_TABSPACE == mHomespace
							.getCurrentScreen()) {
						bottom_bar.setVisibility(View.VISIBLE);
						top_bar_main.setVisibility(View.VISIBLE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TOOLMENU);
					}
				}
			}

			return true;
		}

		if (sp.getBoolean(Constants.Turn_Page_VolumeKey, false)) {
			if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
				TurnNextPage();
				return true;
			}
			if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
				TurnPrePage();
				return true;
			}
		}

		return handled;
	}

	private String getTypedText() {
		return mDefaultKeySsb.toString();
	}

	private void clearTypedText() {
		mDefaultKeySsb.clear();
		mDefaultKeySsb.clearSpans();
		Selection.setSelection(mDefaultKeySsb, 0);
	}

	/**
	 * Creates a view representing a shortcut.
	 * 
	 * @param info
	 *            The data structure describing the shortcut.
	 * 
	 * @return A View inflated from R.layout.application.
	 */
	View createShortcutIconView(ShortcutInfo info, Launcher mLauncher) {

		return ShortcutIcon
				.fromXml(R.layout.shortcut_icon, mLauncher,
						(ViewGroup) mWorkspace.getChildAt(mWorkspace
								.getCurrentScreen()), ((ShortcutInfo) info));

	}

	public int addFolder(int screen, int index) {
		Context context = LauncherApplication.getInstance();
		int shortid = LauncherModel.QueryFolderCount(context, screen, true) + 1;
		UserFolderInfo folderInfo = LauncherModel.findOrMakeUserFolder(
				mFolders, shortid, screen);

		folderInfo.shortid = shortid;
		folderInfo.title = "文件夹";
		folderInfo.itemIndex = index;
		folderInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
		folderInfo.screen = screen;

		LauncherModel.addItemToDatabase(context, folderInfo, true);
		return shortid;
	}

	private FolderIcon createFolderIconView(ItemInfo info, Launcher mLauncher) {

		return FolderIcon
				.fromXml(R.layout.folder_icon, mLauncher,
						(ViewGroup) mWorkspace.getChildAt(mWorkspace
								.getCurrentScreen()), ((UserFolderInfo) info));
	}

	void closeSystemDialogs() {
		getWindow().closeAllPanels();

		try {
			dismissDialog(DIALOG_RENAME_FOLDER);
			// Unlock the workspace if the dialog was showing
		} catch (Exception e) {
			// An exception is thrown if the dialog is not visible, which is
			// fine
		}

		// Whatever we were doing is hereby canceled.
		mWaitingForResult = false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);

		closeSystemDialogs();

		Intent i = getIntent();
		if (i.getData() != null) {

			if (i.getDataString().equals(SettingActivity.setDefaultFail)) {
				Intent browserIntent = (new Intent(Intent.ACTION_VIEW,
						Uri.parse(SettingActivity.setDefaultFail)));
				PackageManager pm = getPackageManager();
				ResolveInfo mInfo = pm.resolveActivity(browserIntent, 0);

				try {
					PackageInfo info = getPackageManager().getPackageInfo(
							getPackageName(), 0);
					Log.d("H", "onCreate " + info.packageName + "  package:"
							+ mInfo.activityInfo.packageName);

					if (!mInfo.activityInfo.packageName
							.equals(info.packageName)) {
						Toast.makeText(Launcher.this, "设置默认浏览器失败",
								Toast.LENGTH_SHORT).show();
						addTab(i.getDataString());
					} else {
						Toast.makeText(Launcher.this, "设置默认浏览器成功",
								Toast.LENGTH_SHORT).show();
						if (SettingActivity.setDefault) {
							startActivity(new Intent(Launcher.this,
									SettingActivity.class));
						}
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			} else if (i.getDataString().equals("saoyisao")
					|| i.getDataString().equals("扫一扫")) {
				if (i.getDataString().equals("saoyisao")) {
				} else {
				}
				startActivity(new Intent(Launcher.this, CaptureActivity.class));
			} else if (i.getDataString().equals(Constants.SHORTCUT_TIANQI)) {
				startActivity(new Intent(Launcher.this, CaptureActivity.class));
			} else if (i.getDataString().equals(Constants.SHORTCUT_ZHUANKE)) {
				startActivity(new Intent(Launcher.this, SettingActivity.class));
			} else if (i.getDataString().equals(Constants.SHORTCUT_LAUNCHER)) {
				TongJi.AddAnalyticsData(TongJi.d_qingliulanqi);
			} else {
				addTab(i.getDataString());
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		TextKeyListener.getInstance().release();

		mModel.stopLoader();
		mModel.ReleaseLoader();

		unbindDesktopItems();

		mCurrentIndex = 0;
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (requestCode >= 0)
			mWaitingForResult = true;
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startSearch(String initialQuery, boolean selectInitialQuery,
			Bundle appSearchData, boolean globalSearch) {

		if (initialQuery == null) {
			// Use any text typed in the launcher as the initial query
			initialQuery = getTypedText();
			clearTypedText();
		}
		if (appSearchData == null) {
			appSearchData = new Bundle();
			appSearchData.putString("launcher-search", "launcher-search");
		}

		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchManager.startSearch(initialQuery, selectInitialQuery,
				getComponentName(), appSearchData, globalSearch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isWorkspaceLocked()) {
			return false;
		}
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	/**
	 * Indicates that we want global search for this activity by setting the
	 * globalSearch argument for {@link #startSearch} to true.
	 */

	@Override
	public boolean onSearchRequested() {
		startSearch(null, false, null, true);
		return true;
	}

	public boolean isWorkspaceLocked() {
		return mWorkspaceLoading || mWaitingForResult;
	}

	void removeFolder(FolderInfo folder) {
		HashMap<Long, Integer> hashId = new HashMap<Long, Integer>();
		hashId.put(folder.shortid, folder.screen);
		mFolders.remove(hashId);
	}

	@Override
	public void onBackPressed() {

	}

	/**
	 * Go through the and disconnect any of the callbacks in the drawables and
	 * the views or we leak the previous Home screen on orientation change.
	 */
	private void unbindDesktopItems() {
		for (ItemInfo item : mDesktopItems) {
			item.unbind();
		}
	}

	/**
	 * Launches the intent referred by the clicked shortcut.
	 * 
	 * @param v
	 *            The view representing the clicked shortcut.
	 */
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag instanceof ShortcutInfo) {
			// Open shortcut
			ShortcutInfo shortcutInfo = (ShortcutInfo) tag;
			if (Constants.SHORTCUT_TIANQI.equals(shortcutInfo.url)) {
				startActivity(new Intent(Launcher.this, CaptureActivity.class));
			} else if ("添加".equals(shortcutInfo.title)) {
				startActivity(new Intent(Launcher.this,
						AddShortcutActivity.class));
			} else if (Constants.SHORTCUT_ZHUANKE.equals(shortcutInfo.url)) {
				startActivity(new Intent(Launcher.this, SettingActivity.class));
			} else {
				addTab(shortcutInfo.url);
			}
		} else if (tag instanceof FolderInfo) {
			FolderInfo folderInfo = (FolderInfo) tag;
			if ("最常访问".equals(folderInfo.title)) {
				// 打开文件夹
				UserFolderInfo userFolderInfo = (UserFolderInfo) folderInfo;
				userFolderInfo.contents.clear();
				userFolderInfo.contents = BookmarksProviderWrapper
						.getRecentHistoryWithLimit(
								Launcher.this.getContentResolver(), 12);
				if (userFolderInfo.contents.size() > 0) {
					handleFolderClick(folderInfo);
				} else {
					Toast.makeText(Launcher.this, "最常访问中无记录", 1000).show();
				}
			} else {
				// openFolder((FolderInfo) tag);
				handleFolderClick((FolderInfo) tag);
			}

		}
	}

	private void handleFolderClick(FolderInfo folderInfo) {
		if (!folderInfo.opened) {
			// Close any open folder
			closeFolder();
			// Open the requested folder
			openFolder(folderInfo);
		} else {
			// Find the open folder...
			Folder openFolder = mWorkspace.getFolderForTag(folderInfo);
			int folderScreen;
			if (openFolder != null) {
				folderScreen = mWorkspace.getScreenForView(openFolder);
				// .. and close it
				closeFolder(openFolder);
				if (folderScreen != mWorkspace.getCurrentScreen()) {
					// Close any folder open on the current screen
					closeFolder();
					// Pull the folder onto this screen
					openFolder(folderInfo);
				}
			}
		}
	}

	/**
	 * Opens the user fodler described by the specified tag. The opening of the
	 * folder is animated relative to the specified View. If the View is null,
	 * no animation is played.
	 * 
	 * @param folderInfo
	 *            The FolderInfo describing the folder to open.
	 */
	private void openFolder(FolderInfo folderInfo) {

		Folder openFolder = mFolder;

		openFolder.setDragController(mDragController);
		openFolder.setLauncher(this);

		openFolder.bind(folderInfo);
		folderInfo.opened = true;

		openFolder.onOpen();
		openFolder.setVisibility(View.VISIBLE);
		top_folder_view.setHeight(top_bar_main.getHeight());
		top_folder_view.setVisibility(View.VISIBLE);
		top_folder_view.setOnClickListener(null);

		if (bottom_folder_view != null) {
			bottom_folder_view.setHeight(bottom_bar.getHeight());
			bottom_folder_view.setVisibility(View.VISIBLE);
			bottom_folder_view.setOnClickListener(null);
		}
		openFolder.setBackgroundColor(getResources().getColor(
				R.color.Folder_bg_color));

		openFolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	private boolean isFolderOpen() {
		Folder folder = mFolder;
		if (folder != null) {
			if (View.VISIBLE == folder.getVisibility()) {
				return true;
			}
		}
		return false;
	}

	private void closeFolder() {
		Folder folder = mFolder;
		if (folder != null) {
			closeFolder(folder);
		}
	}

	void closeFolder(Folder folder) {
		if (folder != null) {
			if (folder.getInfo() != null) {
				folder.getInfo().opened = false;
			}
			folder.setVisibility(View.GONE);
			folder.onClose();
			if (bottom_folder_view != null) {
				bottom_folder_view.setVisibility(View.GONE);
			}
			top_folder_view.setVisibility(View.GONE);

		}
	}

	public boolean onLongClick(View v) {
		if (isWorkspaceLocked()) {
			return false;
		}
		if (!(v instanceof CellLayout)) {
			v = (View) v.getParent();
		}
		View view = (View) v.getTag();

		// This happens when long clicking an item with the dpad/trackball
		if (view == null) {
			return true;
		}

		if (mWorkspace.allowLongPress()) {
			if (view instanceof TextView) {
				if (("添加").equals(((TextView) view).getText())) {
					return true;
				} else {
					// User long pressed on an item
					mWorkspace.performHapticFeedback(
							HapticFeedbackConstants.LONG_PRESS,
							HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
					((CellLayout) mWorkspace.getChildAt(mWorkspace
							.getCurrentScreen())).startDrag(view);
				}
			}

		}
		return true;
	}

	Workspace getWorkspace() {
		return mWorkspace;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_RENAME_FOLDER:
			return new RenameFolder().createDialog();
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_RENAME_FOLDER:
			if (mFolderInfo != null) {
				EditText input = (EditText) dialog
						.findViewById(R.id.folder_name);
				final CharSequence text = mFolderInfo.title;
				input.setText(text);
				input.setSelection(0, text.length());
			}
			break;
		}
	}

	void showRenameDialog(FolderInfo info) {
		mFolderInfo = info;
		mWaitingForResult = true;
		showDialog(DIALOG_RENAME_FOLDER);
	}

	private class RenameFolder {
		private EditText mInput;

		Dialog createDialog() {
			View layout = View.inflate(Launcher.this, R.layout.rename_folder,
					null);
			mInput = (EditText) layout.findViewById(R.id.folder_name);
			TextView title = (TextView) layout.findViewById(R.id.title);
			title.setText("重命名文件夹");

			Button ok = (Button) layout.findViewById(R.id.dialog_ok);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					changeFolderName();
				}

			});
			Button cancel = (Button) layout.findViewById(R.id.dialog_cancel);
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cleanup();
				}

			});
			Dialog dialog = new Dialog(Launcher.this, R.style.waitdailog);// 这里应用了你的自定义样式

			dialog.setContentView(layout);

			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					mWaitingForResult = true;
					mInput.requestFocus();
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(mInput, 0);
				}
			});

			return dialog;
		}

		private void changeFolderName() {
			final String name = mInput.getText().toString();
			if (!TextUtils.isEmpty(name)) {
				// Make sure we have the right folder info
				HashMap<Long, Integer> hashId = new HashMap<Long, Integer>();
				hashId.put(mFolderInfo.shortid, mFolderInfo.screen);
				mFolderInfo = mFolders.get(hashId);
				mFolderInfo.title = name;
				LauncherModel.updateItemInDatabase(Launcher.this, mFolderInfo);

				if (mWorkspaceLoading) {
					mModel.startLoader(Launcher.this, false);
				} else {
					final FolderIcon folderIcon = (FolderIcon) mWorkspace
							.getViewForTag(mFolderInfo);
					if (folderIcon != null) {
						// folderIcon.setText(name);
						getWorkspace().requestLayout();
					} else {
						mWorkspaceLoading = true;
						mModel.startLoader(Launcher.this, false);
					}
				}
			}
			cleanup();
		}

		private void cleanup() {
			dismissDialog(DIALOG_RENAME_FOLDER);
			mWaitingForResult = false;
			mFolderInfo = null;
		}
	}

	/**
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public int getCurrentWorkspaceScreen() {
		if (mWorkspace != null) {
			return mWorkspace.getCurrentScreen();
		} else {
			return SCREEN_COUNT / 2;
		}
	}

	/**
	 * Refreshes the shortcuts shown on the workspace.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 * 
	 * 0 清除全部， 1清除左屏幕 ，2 清除右屏幕
	 * 
	 */
	public void startBinding(int flag) {
		final Workspace workspace = mWorkspace;
		int count = workspace.getChildCount();

		if (flag == 0) {
			for (int i = 0; i < count; i++) { //
				((ViewGroup) workspace.getChildAt(i)).removeAllViewsInLayout();
			}
		} else if (flag == 1) {
			((ViewGroup) workspace.getChildAt(1)).removeAllViewsInLayout();
		} else if (flag == 2) {
			((ViewGroup) workspace.getChildAt(2)).removeAllViewsInLayout();
		}
	}

	public void bindDockBarItems(ArrayList<ItemInfo> shortcuts, int start,
			int end) {
	}

	/**
	 * Bind the items start-end from the list.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindRightItems(ArrayList<ItemInfo> shortcuts, int start, int end) {

		// Log.i(TAG, "bindItems");
		final Workspace workspace = mWorkspace;
		Collections
				.sort(shortcuts, LauncherModel.SHORTCUT_INDEX_COMPARATOR_APP);
		if (true) {
			for (int i = 0; i < shortcuts.size(); i++) {
				final ItemInfo item = shortcuts.get(i);
				if (!mDesktopItems.contains(item)) {
					mDesktopItems.add(item);
					switch (item.itemType) {
					case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
					case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
						final View shortcut = createShortcutIconView(
								(ShortcutInfo) item, this);

						workspace.addInScreen(shortcut, item.screen,
								item.itemIndex);
						break;
					case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:

						if (1 == LauncherModel.QueryCountInFolder(
								Launcher.this, item.screen, item.shortid, true)) {

							final UserFolderInfo userFolderInfo = (UserFolderInfo) item;
							ShortcutInfo folderShortcutInfo = userFolderInfo.contents
									.get(0);
							folderShortcutInfo.iconType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
							folderShortcutInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
							LauncherModel.updateItemInDatabase(Launcher.this,
									folderShortcutInfo);

							final View shortcut2 = createShortcutIconView(
									folderShortcutInfo, this);
							workspace.addInScreen(shortcut2, item.screen,
									item.itemIndex);

							LauncherModel.deleteUserFolderContentsFromDatabase(
									Launcher.this, userFolderInfo);

						} else if (0 == LauncherModel.QueryCountInFolder(
								Launcher.this, item.screen, item.shortid, true)) {

							final UserFolderInfo userFolderInfo = (UserFolderInfo) item;
							LauncherModel.deleteUserFolderContentsFromDatabase(
									Launcher.this, userFolderInfo);

						} else {

							final FolderIcon newFolder = createFolderIconView(
									item, this);
							workspace.addInScreen(newFolder, item.screen,
									item.itemIndex);
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * Bind the items start-end from the list.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindMiddleItems(ArrayList<ItemInfo> shortcuts, int start,
			int end) {

		// Log.i(TAG, "bindItems");
		final Workspace workspace = mWorkspace;
		ArrayList<ItemInfo> mMiddleCustomItems = new ArrayList<ItemInfo>();
		ArrayList<FolderIcon> mMiddleCustomFolder = new ArrayList<FolderIcon>();

		Collections
				.sort(shortcuts, LauncherModel.SHORTCUT_INDEX_COMPARATOR_APP);

		for (int i = 0; i < shortcuts.size(); i++) {
			final ItemInfo item = shortcuts.get(i);

			if (!mDesktopItems.contains(item)) {
				mDesktopItems.add(item);
				switch (item.itemType) {
				case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
				case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
					final View shortcut = createShortcutIconView(
							(ShortcutInfo) item, this);

					if (item.userType == 0 && mMiddleCustomItems_flag) {
						mMiddleCustomItems.add(item);
						continue;
					}
					workspace
							.addInScreen(shortcut, item.screen, item.itemIndex);
					break;
				case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
					if (1 == LauncherModel.QueryCountInFolder(Launcher.this,
							item.screen, item.shortid, true)) {

						final UserFolderInfo userFolderInfo = (UserFolderInfo) item;
						ShortcutInfo folderShortcutInfo = userFolderInfo.contents
								.get(0);
						folderShortcutInfo.iconType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
						folderShortcutInfo.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
						LauncherModel.updateItemInDatabase(Launcher.this,
								folderShortcutInfo);

						final View shortcut2 = createShortcutIconView(
								folderShortcutInfo, this);
						if (folderShortcutInfo.userType == 0
								&& mMiddleCustomItems_flag) {
							mMiddleCustomItems.add(folderShortcutInfo);
							continue;
						}
						workspace.addInScreen(shortcut2, item.screen,
								item.itemIndex);

						LauncherModel.deleteUserFolderContentsFromDatabase(
								Launcher.this, userFolderInfo);

					} else if (0 == LauncherModel.QueryCountInFolder(
							Launcher.this, item.screen, item.shortid, true)) {

						final UserFolderInfo userFolderInfo = (UserFolderInfo) item;
						LauncherModel.deleteUserFolderContentsFromDatabase(
								Launcher.this, userFolderInfo);

					} else {

						final FolderIcon newFolder = createFolderIconView(item,
								this);

						final UserFolderInfo userFolderInfo = (UserFolderInfo) item;
						ShortcutInfo folderShortcutInfo = userFolderInfo.contents
								.get(0);

						if (mMiddleCustomItems_flag
								&& folderShortcutInfo.userType == 0) {
							mMiddleCustomFolder.add(newFolder);
							continue;
						}

						workspace.addInScreen(newFolder, item.screen,
								item.itemIndex);
					}
					break;
				}
			}
		}

		if (mMiddleCustomItems.size() != 0) {
			for (int j = 0; j < mMiddleCustomFolder.size(); j++) {
				workspace.addInScreen(mMiddleCustomFolder.get(j), 1, 0);

			}
			for (int j = 0; j < mMiddleCustomItems.size(); j++) {
				View shortcut = createShortcutIconView(
						(ShortcutInfo) mMiddleCustomItems.get(j), this);
				workspace.addInScreen(shortcut,
						mMiddleCustomItems.get(j).screen,
						mMiddleCustomItems.get(j).itemIndex);
			}

			mMiddleCustomItems_flag = false;
		}

		final View shortcut = createShortcutIconView(
				(ShortcutInfo) AddItemAddShort(), this);
		workspace.addInScreen(shortcut, AddItemAddShort().screen,
				AddItemAddShort().itemIndex);
	}

	public ShortcutInfo AddItemAddShort() {
		final ShortcutInfo info = new ShortcutInfo();
		info.title = "添加";
		info.screen = 1;
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
		info.customIcon = true;
		Bitmap icon = Utilities.drawableToBitmap(R.drawable.home_icon_add);
		info.setIcon(icon);

		return info;

	}

	/**
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void bindFolders(HashMap<HashMap<Long, Integer>, FolderInfo> folders) {
		mFolders.clear();
		mFolders.putAll(folders);
	}

	/**
	 * Callback saying that there aren't any more items to bind.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void finishBindingItems() {
		if (mSavedState != null) {
			if (!mWorkspace.hasFocus()) {
				mWorkspace.getChildAt(mWorkspace.getCurrentScreen())
						.requestFocus();
			}

			mSavedState = null;
		}

		try {
			super.onRestoreInstanceState(mSavedInstanceState);
		} catch (Exception e) {

		}
		mSavedInstanceState = null;
		mWorkspaceLoading = false;
		LauncherModel.is_runing_Loader = false;
	}

	/**
	 * GridView的监听事件
	 */
	public OnItemClickListener gridListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			popmenu.dismiss();
			Bitmap bitmap = getViewBitmap(mHomespace);
			GridButtonInfo buttonInfo = (GridButtonInfo) arg0
					.getItemAtPosition(arg2);

			if (buttonInfo.getButtonTitle().equals(content1[0])) {
				// 全屏
				Editor editor = sp.edit();
				if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
					// 退出全屏
					editor.putBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false);
					WindowManager.LayoutParams attr = getWindow()
							.getAttributes();
					attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
					getWindow().setAttributes(attr);
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
					if (CURRENT_SCREEN_TABSPACE == mHomespace
							.getCurrentScreen()
							|| (mWorkspace.getCurrentScreen() == 0)) {
						bottom_bar.setVisibility(View.VISIBLE);
						top_bar_main.setVisibility(View.VISIBLE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TOOLMENU);
					}

				} else {
					// 全屏
					editor.putBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							true);
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
					getWindow().setAttributes(lp);
					getWindow().addFlags(
							WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

					if (CURRENT_SCREEN_TABSPACE == mHomespace
							.getCurrentScreen()
							|| (mWorkspace.getCurrentScreen() == 0)) {
						bottom_bar.setVisibility(View.GONE);
						top_bar_main.setVisibility(View.GONE);
						top_bar_input.setVisibility(View.GONE);

						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_SHOW,
								FloatWindowService.TYPE_TOOLMENU);

					}

				}

				editor.commit();
			} else if (buttonInfo.getButtonTitle().equals(content1[1])) {
				// 书签/历史
				Intent i = new Intent(Launcher.this,
						BookmarksHistoryActivity.class);
				startActivityForResult(i, RESULT_OPEN_NEW_BOOKMARKS_HISTORY);
			} else if (buttonInfo.getButtonTitle().equals(content1[2])) {
				// 添加书签
				Intent i = new Intent(Launcher.this, EditBookmarkActivity.class);
				if ((CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen())) {

					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE,
							mCustomWebView.getTitle());
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL,
							mCustomWebView.getUrl());
				} else {
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, "");// mCurrentWebView.getTitle());
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, "");// mCurrentWebView.getUrl());
				}

				startActivity(i);

			} else if (buttonInfo.getButtonTitle().equals(content1[3])) {
				// 夜间模式
				Editor editor = sp.edit();
				if (sp.getBoolean(Constants.PREFERENCES_NIGHT_MODE, false)) {
					editor.putBoolean(Constants.PREFERENCES_NIGHT_MODE, false);
					day();
				} else {
					editor.putBoolean(Constants.PREFERENCES_NIGHT_MODE, true);
					night();
				}
				editor.commit();

			} else if (buttonInfo.getButtonTitle().equals(content1[4])) {
				// 分享
				
				if ((CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen())) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);

				} 

			} else if (buttonInfo.getButtonTitle().equals(content1[5])) {
				// 下载
				startActivity(new Intent(Launcher.this,
						DownloadsListActivity.class));

			} else if (buttonInfo.getButtonTitle().equals(content1[6])) {
				// 检查更新
				if (Tools.isConnectInternet(Launcher.this)) {
					Toast.makeText(Launcher.this, "正在检测，请稍后",
							Toast.LENGTH_SHORT).show();
					ManuallyCheckVersion();
				} else {
					Toast.makeText(Launcher.this, "请检查网络设置", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (buttonInfo.getButtonTitle().equals(content1[7])) {
				// 退出
				Quit();

			} else if (buttonInfo.getButtonTitle().equals(content2[0])) {
				// 设置
				startActivityForResult(new Intent(Launcher.this,
						SettingActivity.class),
						RESULT_OPEN_NEW_BOOKMARKS_HISTORY);
			} else if (buttonInfo.getButtonTitle().equals(content2[1])) {
				// 屏幕
				startActivity(new Intent(Launcher.this,
						ScreenSwitchDialogActivity.class));

			} else if (buttonInfo.getButtonTitle().equals(content2[2])) {
				// 翻页模式
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()
						|| mWorkspace.getCurrentScreen() == 0) {
					PageDownOrUpActivity = new PageDownOrUpActivity.Builder(
							Launcher.this)
							.setPositiveButton("确定",
									new View.OnClickListener() {
										public void onClick(View v) {

											Editor editor = sp.edit();
											editor.putBoolean(
													Constants.Turn_Page_VolumeKey,
													PageDownOrUpActivity
															.checkBox_volumeKeyisCheck());
											editor.putBoolean(
													Constants.Turn_Page_Kye,
													PageDownOrUpActivity
															.checkBox_keyisCheck());
											editor.commit();
											if (PageDownOrUpActivity
													.checkBox_keyisCheck()) {
												ShowOrHideFloatWindow(
														FloatWindowService.OPERATION_SHOW,
														FloatWindowService.TYPE_TURNPAGE);

											} else {
												ShowOrHideFloatWindow(
														FloatWindowService.OPERATION_HIDE,
														FloatWindowService.TYPE_TURNPAGE);

											}
											PageDownOrUpActivity.dismiss();
										}
									})
							.setNegativeButton("取消",
									new View.OnClickListener() {
										public void onClick(View v) {
											PageDownOrUpActivity.dismiss();
										}
									}).create();
					PageDownOrUpActivity.show();

				}

			} else if (buttonInfo.getButtonTitle().equals(content2[3])) {
				// 复制链接
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					ApplicationUtils.copyTextToClipboard(Launcher.this,
							mCustomWebView.getUrl(),
							getString(R.string.Commons_UrlCopyToastMessage));
				}

			} else if (buttonInfo.getButtonTitle().equals(content2[4])) {
				// 无痕
				Editor editor = sp.edit();
				if (sp.getBoolean(Constants.PREFERENCES_INCOGNITO_MODE, false)) {
					editor.putBoolean(Constants.PREFERENCES_INCOGNITO_MODE,
							false);
					Toast.makeText(Launcher.this, "已切换到正常模式",
							Toast.LENGTH_SHORT).show();
				} else {
					editor.putBoolean(Constants.PREFERENCES_INCOGNITO_MODE,
							true);

					Toast.makeText(Launcher.this, "已切换到无痕模式",
							Toast.LENGTH_SHORT).show();
				}
				editor.commit();
			} else if (buttonInfo.getButtonTitle().equals(content2[5])) {
				// 无图
				Editor editor = sp.edit();
				if (sp.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_IMAGES,
						true)) {
					editor.putBoolean(
							Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, false);
					Toast.makeText(Launcher.this, "已切换到无图，更省流量",
							Toast.LENGTH_SHORT).show();
				} else {
					editor.putBoolean(
							Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true);
					Toast.makeText(Launcher.this, "已切换到有图", Toast.LENGTH_SHORT)
							.show();
				}
				editor.commit();
				applyPreferences();
			} else if (buttonInfo.getButtonTitle().equals(content2[6])) {
				// 页内查找
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					startShowFindDialogRunnable();
				}
			}

		}
	};

	/**
	 * 退出
	 */
	private void Quit() {
		if (sp.getBoolean(Constants.PREFERENCES_NIGHT_MODE, false)) {
			// 设置屏幕亮度值为原来的
			setScreenBrightness((float) sp.getFloat(Constants.screenBrightness,
					255.0F));
			// 设置当前屏幕亮度的模式 为原来的
			setScreenMode(sp.getInt(Constants.screenMode, 0));

		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
			ShowOrHideFloatWindow(FloatWindowService.OPERATION_HIDE,
					FloatWindowService.TYPE_TOOLMENU);

		}

		if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
			ShowOrHideFloatWindow(FloatWindowService.OPERATION_HIDE,
					FloatWindowService.TYPE_TURNPAGE);
		}
		One_webView = null;
		tabs_num = 0;
		tabCurrentItem = 0;
		saveLiuLiangData();
		finish();
	}

	private void QuitDialogShow() {
		quitDialog = new QuitDialogActivity.Builder(Launcher.this)
				.setCleanButton(new View.OnClickListener() {
					public void onClick(View v) {
						// 清除全部浏览记录
						// BookmarksProviderWrapper.clearHistory();

						// 清除本次浏览记录
						BookmarksProviderWrapper.cleanTheHistory(Launcher.this);

						for (Object object : Controller.getInstance()
								.getWebViewList()) {
							if (object instanceof CustomWebView) {
								CustomWebView webView = (CustomWebView) object;
								webView.clearHistory();
							}
						}
						// 退出
						Quit();
						quitDialog.dismiss();
					}
				}).setPositiveButton(new View.OnClickListener() {
					public void onClick(View v) {
						// 退出
						Quit();
						quitDialog.dismiss();
					}
				}).setNegativeButton(new View.OnClickListener() {
					public void onClick(View v) {
						quitDialog.dismiss();
					}
				}).create();
		quitDialog.setCanceledOnTouchOutside(true);// 点击区域外关闭
		quitDialog.show();
	}

	/**
	 * Apply preferences to the current UI objects.
	 */
	public void applyPreferences() {

		updateSwitchTabsMethod();

		// for (CustomWebView view : mWebViews) {
		// view.initializeOptions();
		// }

		for (Object object : mWebViews) {
			if (object instanceof CustomWebView) {
				CustomWebView webView = (CustomWebView) object;
				webView.initializeOptions();
			}
		}
	}

	public static void TurnPrePage() {
		if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			mCustomWebView.pageUp(false);
		} else {
			One_webView.pageUp(false);
		}
	}

	public static void TurnNextPage() {
		if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {

			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			mCustomWebView.pageDown(false);
		} else {
			One_webView.pageDown(false);
		}

	}

	/**
	 * 更新当前页码
	 */
	public void setCurPage(int page) {

		menuLayout.setCurrentItem(page);
		switch (page) {
		case 0:
			page_1.setBackgroundColor(Color.parseColor("#fd461b"));
			page_2.setBackgroundColor(Color.parseColor("#d5d4db"));
			break;

		case 1:
			page_1.setBackgroundColor(Color.parseColor("#d5d4db"));
			page_2.setBackgroundColor(Color.parseColor("#fd461b"));
			break;

		}

	}

	private void setcurrentPoint(int position) {
		switch (position) {
		case 0:
			mainpage_1.setImageResource(R.drawable.workspace_indicator_focused);
			mainpage_2
					.setImageResource(R.drawable.workspace_indicator_unfocused);
			mainpage_3
					.setImageResource(R.drawable.workspace_indicator_unfocused);
			break;

		case 1:
			mUrlEditText.setText("");
			urlText.setText("");
			mainpage_1
					.setImageResource(R.drawable.workspace_indicator_unfocused);
			mainpage_2.setImageResource(R.drawable.workspace_indicator_focused);
			mainpage_3
					.setImageResource(R.drawable.workspace_indicator_unfocused);
			break;

		case 2:
			mUrlEditText.setText("");
			urlText.setText("");
			mainpage_1
					.setImageResource(R.drawable.workspace_indicator_unfocused);
			mainpage_2
					.setImageResource(R.drawable.workspace_indicator_unfocused);
			mainpage_3.setImageResource(R.drawable.workspace_indicator_focused);
			break;
		}
	}

	private void initAllView() {
		mAllView = (LinearLayout) findViewById(R.id.all_view);
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (setDefault) {
			Intent i = (new Intent(Intent.ACTION_VIEW,
					Uri.parse(SettingActivity.setDefaultFail)));
			pm = getPackageManager();
			mInfo = pm.resolveActivity(i, 0);

			try {
				info = this.getPackageManager().getPackageInfo(
						this.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			Log.d("H", "onRestart " + info.packageName + "  package:"
					+ mInfo.activityInfo.packageName);

			setDefault = false;
			if (mInfo.activityInfo.packageName.equals(info.packageName)) {
				Toast.makeText(Launcher.this, "设置成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(Launcher.this, "设置失败", Toast.LENGTH_SHORT)
						.show();
			}

		}

		if (cleanDefault) {
			cleanDefault = false;
			setDefault();
		}
	}

	public void setDefault() {
		Intent i = (new Intent(Intent.ACTION_VIEW,
				Uri.parse(SettingActivity.setDefaultFail)));
		pm = getPackageManager();
		mInfo = pm.resolveActivity(i, 0);
		try {
			info = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if ("android".equals(mInfo.activityInfo.packageName)) {
			// 没有设置过，直接设置
			Log.i("H", "没有设置过，直接设置  " + info.packageName + "  package:"
					+ mInfo.activityInfo.packageName);
			dialogimageutil = new DialogImageUtil.Builder(Launcher.this)
					.setTitleText("如何设置默认")
					.setImageResource(R.drawable.setdefault)
					.setPositiveButton("去设置", new View.OnClickListener() {
						public void onClick(View v) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(SettingActivity.setDefaultFail)));
							setDefault = true;
							dialogimageutil.dismiss();
						}
					}).create();
			dialogimageutil.show();
		} else {
			// 已经设置过，先清除设置
			Log.i("H", "已经设置过，先清除设置 " + info.packageName + "  package:"
					+ mInfo.activityInfo.packageName);
			dialogimageutil = new DialogImageUtil.Builder(Launcher.this)
					.setTitleText("请先清除默认设置")
					.setImageResource(R.drawable.cleandefault)
					.setPositiveButton("去清除", new View.OnClickListener() {
						public void onClick(View v) {
							cleanDefault = true;
							Uri uri = Uri.parse("package:"
									+ mInfo.activityInfo.packageName);// 包名，指定该应用
							Intent intent = new Intent(
									"android.settings.APPLICATION_DETAILS_SETTINGS",
									uri);
							startActivity(intent);
							dialogimageutil.dismiss();
						}
					}).create();
			dialogimageutil.show();

		}

	}

	private void initsetDefaultBrowser() {
		set_default_browser = (RelativeLayout) findViewById(R.id.set_default_browser);
		Button button_ok = (Button) set_default_browser
				.findViewById(R.id.button_ok);
		Button button_cancle = (Button) set_default_browser
				.findViewById(R.id.button_cancel);

		button_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setDefault();
				set_default_browser.setVisibility(View.GONE);
			}
		});

		button_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				set_default_browser.setVisibility(View.GONE);
			}
		});

	}

	private void initTabsGallery() {
		tabs_gallery = (RelativeLayout) findViewById(R.id.tabs_gallery);
		tabs_gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}

		});

		tabs_gallery.setBackgroundColor(getResources().getColor(
				R.color.Folder_bg_color));

		tabs_close_all = (TextView) tabs_gallery
				.findViewById(R.id.tabs_close_all);

		tabs_close_all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabCurrentItem = 0;
				mCurrentIndex = 0;
				mRecentsPanelView.clearRecentTasksList();

				mWebViewBitmaps = new ArrayList<HashMap<String, Object>>();
				mWebViewStates = new ArrayList<HashMap<String, Object>>();
				mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);

				for (int position = 0; position < mTabspace.getChildCount(); position++) {
					WebViewspace mmWebViewspace = (WebViewspace) mTabspace
							.getChildAt(position);
					View mchildView = mmWebViewspace.getChildAt(0);
					if (mchildView != null) {
						CustomWebView mmCustomWebView = (CustomWebView) mchildView
								.findViewById(R.id.webview);
						mmCustomWebView.loadUrl("");
						mmCustomWebView.stopLoading();
					}
				}

				// 只是未显示,应移出包含的View
				mTabspace.removeAllViews();
				mTabspace.setVisibility(View.GONE);

				initHideWebViewspace();

				updateBottomTabsNum();

				mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
				mUrlEditText.setText("");
				mUrlEditText.addTextChangedListener(mUrlTextWatcher);
				urlText.setText("");

				tabs_gallery.setVisibility(View.GONE);
				tabs_gallery_Flag = false;
			}
		});
		tabs_new_tab = (TextView) tabs_gallery.findViewById(R.id.tabs_new_tab);

		tabs_new_tab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabs_gallery.setVisibility(View.GONE);
				tabs_gallery_Flag = false;
				if (tabs_num > 9) {
					Toast.makeText(Launcher.this, "窗口数目已达到上限", 1000).show();
				} else {
					Toast.makeText(Launcher.this, "新标签页添加", 1000).show();
					newTab();
				}
			}
		});

		mRecentsPanelView = (RecentsPanelView) tabs_gallery
				.findViewById(R.id.myRecentsPanelView);

		ImageView mytabgallery_back = (ImageView) findViewById(R.id.mytabgallery_back);
		mytabgallery_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tabs_gallery.setVisibility(View.GONE);
				tabs_gallery_Flag = false;
			}
		});
	}

	private void initSnapShotWebView() {
		snapshotwebview = (WebView) findViewById(R.id.snapshotwebview);
		snapshotwebview.setDrawingCacheEnabled(true);
		snapshotwebview.getDrawingCache();
	}

	public static void loadSnapShot(String url) {
		// 添加书签，判断url地址可用性
		// 访问URL地址，获取快照
		snapshotwebview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				catchSnapshot(view, view.getOriginalUrl());
				super.onPageFinished(view, url);
			}

		});
		snapshotwebview.loadUrl(url);
	}

	public static void loadSnapShot(String url, final int bookmarkid) {
		// 添加书签，判断url地址可用性
		// 访问URL地址，获取快照
		snapshotwebview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				Context context = LauncherApplication.getInstance();
				catchSnapshot(view, url);
				// 更新bookmark
				final ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.URL, url);
				values.put(BookmarksUtil.BookmarkColumns_SNAPSHOT,
						Tools.generateMD5(url));
				UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
				values.put(BookmarksUtil.BookmarkColumns_UUID, uuid + "");
				BookmarksUtil.update(context, values, bookmarkid);

				super.onPageFinished(view, url);
			}

		});
		snapshotwebview.loadUrl(url);
	}

	public static void loadSnapShot(String url, final int launcherid,
			final int bookmarkid) {
		// 添加书签，判断url地址可用性
		// 访问URL地址，获取快照
		snapshotwebview.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				Context context = LauncherApplication.getInstance();
				catchSnapshot(view, url);
				// 更新bookmark
				final ContentValues values = new ContentValues();
				values.put(Browser.BookmarkColumns.URL, url);
				values.put(BookmarksUtil.BookmarkColumns_SNAPSHOT,
						Tools.generateMD5(url));
				UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
				values.put(BookmarksUtil.BookmarkColumns_UUID, uuid + "");
				BookmarksUtil.update(context, values, bookmarkid);
				// 更新launcher
				ShortcutInfo shortcutInfo = new ShortcutInfo();
				shortcutInfo.id = launcherid;
				shortcutInfo.url = url;
				shortcutInfo.iconResource = Tools.generateMD5(url);
				LauncherModel.updateShortcutInDatabase(context, shortcutInfo);

				super.onPageFinished(view, url);
			}

		});
		snapshotwebview.loadUrl(url);
	}

	/**
	 * 全屏下 工具栏与标题栏显示/隐藏
	 */

	public static void initBubbleView() {
		if (bottom_bar.getVisibility() == View.VISIBLE) {
			bottom_bar.setVisibility(View.GONE);
			top_bar_main.setVisibility(View.GONE);
		} else {
			bottom_bar.setVisibility(View.VISIBLE);
			top_bar_main.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 初始化网页查找按钮
	 */
	private void initFindBar() {
		mFindBar = (LinearLayout) findViewById(R.id.findControls);
		mFindBar.setVisibility(View.GONE);

		mFindPreviousButton = (ImageView) findViewById(R.id.find_previous);
		mFindPreviousButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WebViewspace mWebViewspace = (WebViewspace) mTabspace
						.getChildAt(mCurrentIndex);
				View childView = mWebViewspace.getChildAt(0);
				CustomWebView mCustomWebView = (CustomWebView) childView
						.findViewById(R.id.webview);
				mCustomWebView.findNext(false);
				hideKeyboardFromFindDialog();
			}
		});

		mFindNextButton = (ImageView) findViewById(R.id.find_next);
		mFindNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WebViewspace mWebViewspace = (WebViewspace) mTabspace
						.getChildAt(mCurrentIndex);
				View childView = mWebViewspace.getChildAt(0);
				CustomWebView mCustomWebView = (CustomWebView) childView
						.findViewById(R.id.webview);
				mCustomWebView.findNext(true);
				hideKeyboardFromFindDialog();
			}
		});

		mFindCloseButton = (Button) findViewById(R.id.find_close);
		mFindCloseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeFindDialog();
			}
		});

		mFindText = (EditText) findViewById(R.id.find_value);
		mFindText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!StringUtil.isNull(s.toString()))
					doFind();
				else {
					mFindPreviousButton.setEnabled(false);
					mFindNextButton.setEnabled(false);
					mFindPreviousButton
							.setImageResource(R.drawable.ic_btn_find_prev_notwork);
					mFindNextButton
							.setImageResource(R.drawable.ic_btn_find_next_notwork);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 初始化顶部Bar
	 */
	private void initTopBar() {
		// 初始化 地址栏和搜索栏
		top_bar_main = (LinearLayout) findViewById(R.id.top_bar_main);
		top_bar_main_layout = (LinearLayout) top_bar_main
				.findViewById(R.id.top_bar_main_layout);
		mProgressBar = (ProgressBar) top_bar_main
				.findViewById(R.id.WebViewProgress);
		mProgressBar.setMax(100);
		top_folder_view = (TextView) findViewById(R.id.top_folder_view);

		addToLauncher = (Button) findViewById(R.id.add_to_launcher);


		urlText = (EditText) top_bar_main.findViewById(R.id.url_edittext);
		urlText.setFocusable(false);
		urlText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				top_bar_main.setVisibility(View.GONE);
				mHomespace.setVisibility(View.GONE);
				mUrlEditText.requestFocus();
				mUrlEditText.setSelectAllOnFocus(true);
				top_bar_input.setVisibility(View.VISIBLE);
				top_bar_input_Flag = true;
				key_input_layout.setVisibility(View.VISIBLE);
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(mUrlEditText,
						InputMethodManager.SHOW_FORCED);

				if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
					bottom_bar.setVisibility(View.VISIBLE);
					top_bar_main.setVisibility(View.VISIBLE);
					ShowOrHideFloatWindow(FloatWindowService.OPERATION_HIDE,
							FloatWindowService.TYPE_TOOLMENU);

				}

				if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
					ShowOrHideFloatWindow(FloatWindowService.OPERATION_HIDE,
							FloatWindowService.TYPE_TURNPAGE);
				}
				new Thread(new GetKey_Thread()).start();
			}

		});

		addbookmark = (ImageView) top_bar_main.findViewById(R.id.addbookmark);
		addbookmark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					showAddBookmark();
				} else {
					if (mWorkspace.getCurrentScreen() == 0) {
						showAddBookmark();
					} else {
						Intent i = new Intent(Launcher.this,
								BookmarksHistoryActivity.class);
						startActivityForResult(i,
								RESULT_OPEN_NEW_BOOKMARKS_HISTORY);
					}

				}

			}
		});

		ImageView title_er_wei_ma = (ImageView) top_bar_main
				.findViewById(R.id.title_er_wei_ma);
		title_er_wei_ma.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				top_bar_inpu_ErWeiMa = top_bar_input;
				top_bar_mai_ErWeiMa = top_bar_main;
				mHomespac_ErWeiMa = mHomespace;

				Intent intent = new Intent(Launcher.this, CaptureActivity.class);
				intent.putExtra("String", "dingbusousuo");
				startActivity(intent);
			}
		});
	}

	/**
	 * 初始化顶部BarBrowser
	 */
	private void initTopBarBrowser() {
		top_bar_browser = (LinearLayout) findViewById(R.id.top_bar_browser);
		top_bar_browser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Dummy event to steel it from the WebView, in case of clicking
				// between the buttons.
			}
		});
	}

	/**
	 * 初始化底部Bar
	 */
	private void initBottomBar() {
		if (popmenu != null && popmenu.isShowing()) {
			popmenu.dismiss();
		}
		// 初始化底部菜单
		LinearLayout bottom_one = (LinearLayout) top_bar_main
				.findViewById(R.id.bottom_one);
		LinearLayout bottom_two = (LinearLayout) top_bar_main
				.findViewById(R.id.bottom_two);
		bottom_one.setVisibility(View.GONE);
		bottom_two.setVisibility(View.GONE);

		bottom_bar = (LinearLayout) findViewById(R.id.bottom);
		bottom_bar.setVisibility(View.VISIBLE);

		bottom_folder_view = (TextView) bottom_bar
				.findViewById(R.id.bottom_folder_view);

		bottom_bar_back = (ImageView) bottom_bar.findViewById(R.id.bottom_back);
		bottom_bar_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					navigatePrevious(mCustomWebView);
				} else if (0 == mHomespace.getCurrentScreen()) {
					if (One_webView != null && One_webView.canGoBack()) {
						One_webView.goBack();
					}
				}
			}
		});

		bottom_bar_forward = (ImageView) bottom_bar
				.findViewById(R.id.bottom_forward);
		bottom_bar_forward.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					navigateNext(mCustomWebView);
				} else if (0 == mHomespace.getCurrentScreen()) {
					if (One_webView != null && One_webView.canGoForward()) {
						One_webView.goForward();
					}
				}
			}
		});
		bottom_bar_menu = (ImageView) bottom_bar.findViewById(R.id.bottom_menu);
		bottom_bar_menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (popmenu != null && popmenu.isShowing()) {
					popmenu.dismiss();
				} else {
					showPopMenuWindow();
				}
			}
		});

		bottom_bar_homepage = (ImageView) bottom_bar
				.findViewById(R.id.bottom_homepage);
		bottom_bar_homepage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
					mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
					mUrlEditText.setText("");
					mUrlEditText.addTextChangedListener(mUrlTextWatcher);
					urlText.setText("");
				}

				if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
					bottom_bar.setVisibility(View.VISIBLE);
					top_bar_main.setVisibility(View.VISIBLE);
					ShowOrHideFloatWindow(FloatWindowService.OPERATION_HIDE,
							FloatWindowService.TYPE_TOOLMENU);
				}
				if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
					ShowOrHideFloatWindow(FloatWindowService.OPERATION_HIDE,
							FloatWindowService.TYPE_TURNPAGE);
				}

				onPageStarted("");
				setcurrentPoint(1);
				CheckIsBookmark("");
				mProgressBar.setVisibility(View.GONE);
				mWorkspace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE_MIDDLE);
			}
		});

		bottom_refresh = (ImageView) bottom_bar
				.findViewById(R.id.bottom_refresh);
		bottom_refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					mCustomWebView.reload();
				} else if (0 == mHomespace.getCurrentScreen()) {
					if (One_webView != null) {
						One_webView.reload();
						 Tools.startSavePage(ConstantsUrl.ONE_WEBVIEW_URL,
						 "one_webview", Launcher.this);
					}
				}

			}
		});

		bottom_bar_tabs = (ImageView) bottom_bar.findViewById(R.id.bottom_tabs);
		bottom_bar_tabs.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// 存储当前页面缩略图,是否使用视情况决定
				HashMap<String, Object> bitmaphashmap = new HashMap<String, Object>();

				Bitmap bitmap = getViewBitmap(mHomespace);

				bitmaphashmap.put("bitmap", bitmap);

				HashMap<String, Object> hashmap = new HashMap<String, Object>();

				// 存储当前tab显示状态 workspace false tabspace true
				if (mTabspace.getChildVisibility(mCurrentIndex)) {
					hashmap.put("isWebview", true);
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					bitmaphashmap.put("title", mCustomWebView.getTitle());

				} else {
					hashmap.put("isWebview", false);
				}

				if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {
					hashmap.put("isHome", mWorkspace.getCurrentScreen());
					bitmaphashmap.put("title", "主页");
				} else {
					hashmap.put("isHome", CURRENT_SCREEN_WORKSPACE_NOT);
				}

				if (mWebViewBitmaps.size() > mCurrentIndex) {
					mWebViewBitmaps.set(mCurrentIndex, bitmaphashmap);
				} else {
					mWebViewBitmaps.add(mCurrentIndex, bitmaphashmap);
				}

				if (mWebViewStates.size() > mCurrentIndex) {
					mWebViewStates.set(mCurrentIndex, hashmap);
				} else {
					mWebViewStates.add(mCurrentIndex, hashmap);
				}

				if (mWebViewBitmaps.size() > 0) {
					mRecentsPanelView.show(true, true, bindMyTabGallery());
				}
				tabs_gallery.setVisibility(View.VISIBLE);
				tabs_gallery_Flag = true;

			}
		});

		bottom_tabs_num = (TextView) bottom_bar
				.findViewById(R.id.bottom_tabs_num);
		if (tabs_num == 0) {
			tabs_num = 1;
		}
		String text = String.valueOf(tabs_num);
		bottom_tabs_num.setText(text);
	}

	/**
	 * 初始化底部Bar
	 */
	private void initBottomBarLand() {
		// 初始化底部菜单
		if (popmenu != null && popmenu.isShowing()) {
			popmenu.dismiss();
		}
		bottom_bar = (LinearLayout) findViewById(R.id.top_bar_main);

		LinearLayout bottom_one = (LinearLayout) bottom_bar
				.findViewById(R.id.bottom_one);
		LinearLayout bottom_two = (LinearLayout) bottom_bar
				.findViewById(R.id.bottom_two);
		bottom_one.setVisibility(View.VISIBLE);
		bottom_two.setVisibility(View.VISIBLE);

		LinearLayout bottom_change = (LinearLayout) findViewById(R.id.bottom);
		bottom_change.setVisibility(View.GONE);

		bottom_bar_back = (ImageView) bottom_bar.findViewById(R.id.bottom_back);
		bottom_bar_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					navigatePrevious(mCustomWebView);
				} else if (0 == mHomespace.getCurrentScreen()) {
					if (One_webView != null && One_webView.canGoBack()) {
						One_webView.goBack();
					}
				}
			}
		});

		bottom_bar_forward = (ImageView) bottom_bar
				.findViewById(R.id.bottom_forward);
		bottom_bar_forward.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					navigateNext(mCustomWebView);
				} else if (0 == mHomespace.getCurrentScreen()) {
					if (One_webView != null && One_webView.canGoForward()) {
						One_webView.goForward();
					}
				}
			}
		});
		bottom_bar_menu = (ImageView) bottom_bar.findViewById(R.id.bottom_menu);
		bottom_bar_menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (popmenu != null && popmenu.isShowing()) {
					popmenu.dismiss();
				} else {
					showPopMenuWindow();
				}
			}
		});

		bottom_bar_homepage = (ImageView) bottom_bar
				.findViewById(R.id.bottom_homepage);
		bottom_bar_homepage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
					mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
					mUrlEditText.setText("");
					mUrlEditText.addTextChangedListener(mUrlTextWatcher);
					urlText.setText("");

					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.VISIBLE);
						top_bar_main.setVisibility(View.VISIBLE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TOOLMENU);

					}
					if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TURNPAGE);
					}
				}

				mWorkspace.moveToDefaultScreen(CURRENT_SCREEN_WORKSPACE_MIDDLE);
			}
		});

		bottom_refresh = (ImageView) bottom_bar
				.findViewById(R.id.bottom_refresh);
		bottom_refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					mCustomWebView.reload();
				}

			}
		});

		bottom_bar_tabs = (ImageView) bottom_bar.findViewById(R.id.bottom_tabs);
		bottom_bar_tabs.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				// 存储当前页面缩略图,是否使用视情况决定
				HashMap<String, Object> bitmaphashmap = new HashMap<String, Object>();

				Bitmap bitmap = getViewBitmap(mHomespace);

				bitmaphashmap.put("bitmap", bitmap);

				HashMap<String, Object> hashmap = new HashMap<String, Object>();

				// 存储当前tab显示状态 workspace false tabspace true
				if (mTabspace.getChildVisibility(mCurrentIndex)) {
					hashmap.put("isWebview", true);
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					bitmaphashmap.put("title", mCustomWebView.getTitle());
				} else {
					hashmap.put("isWebview", false);
				}

				if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {
					hashmap.put("isHome", mWorkspace.getCurrentScreen());
					bitmaphashmap.put("title", "主页");
				} else {
					hashmap.put("isHome", CURRENT_SCREEN_WORKSPACE_NOT);
				}

				if (mWebViewBitmaps.size() > mCurrentIndex) {
					mWebViewBitmaps.set(mCurrentIndex, bitmaphashmap);
				} else {
					mWebViewBitmaps.add(mCurrentIndex, bitmaphashmap);
				}

				if (mWebViewStates.size() > mCurrentIndex) {
					mWebViewStates.set(mCurrentIndex, hashmap);
				} else {
					mWebViewStates.add(mCurrentIndex, hashmap);
				}

				if (mWebViewBitmaps.size() > 0) {
					mRecentsPanelView.show(true, true, bindMyTabGallery());
				}
				tabs_gallery.setVisibility(View.VISIBLE);
				tabs_gallery_Flag = true;
			}
		});

		bottom_tabs_num = (TextView) bottom_bar
				.findViewById(R.id.bottom_tabs_num);
		if (tabs_num == 0) {
			tabs_num = 1;
		}
		String text = String.valueOf(tabs_num);
		bottom_tabs_num.setText(text);
	}

	ArrayList<TaskDescription> mRecentTaskDescriptions = new ArrayList<TaskDescription>();

	private ArrayList<TaskDescription> bindMyTabGallery() {
		mRecentTaskDescriptions.clear();
		for (int position = 0; position < mWebViewBitmaps.size(); position++) {
			TaskDescription item = new TaskDescription();
			item.setPosition(position);
			item.setLabel((String) mWebViewBitmaps.get(
					position % mWebViewBitmaps.size()).get("title"));
			item.setThumbnail((Bitmap) mWebViewBitmaps.get(
					position % mWebViewBitmaps.size()).get("bitmap"));

			mRecentTaskDescriptions.add(position, item);
		}
		return mRecentTaskDescriptions;
	}

	public void updateBottomTabsNum() {
		String text = "1";
		if (mTabspace.getChildCount() == 0) {
			tabs_num = 1;
		} else {
			tabs_num = mTabspace.getChildCount();
		}
		if (tabs_num > 9) {
			Toast.makeText(Launcher.this, "来的小伙伴太多了，有点挤", 1000).show();
			text = "n";
		} else {
			text = String.valueOf(tabs_num);
		}

		bottom_tabs_num.setText(text);
	}

	public static void updateRecentsBottomTabsNum() {
		String text = "1";
		if (mTabspace.getChildCount() == 0) {
			tabs_num = 1;
		} else {
			tabs_num = mTabspace.getChildCount();
		}
		if (tabs_num > 9) {
			text = "n";
		} else {
			text = String.valueOf(tabs_num);
		}

		bottom_tabs_num.setText(text);
	}

	public void addTabAndThumbnail(String url) {

		// 存储当前页面缩略图,是否使用视情况决定
		HashMap<String, Object> bitmaphashmap = new HashMap<String, Object>();

		Bitmap bitmap = getViewBitmap(mHomespace);

		bitmaphashmap.put("bitmap", bitmap);

		HashMap<String, Object> hashmap = new HashMap<String, Object>();

		// 存储当前tab显示状态 workspace false tabspace true
		if (mTabspace.getChildVisibility(mCurrentIndex)) {
			hashmap.put("isWebview", true);
			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			// mCustomWebView.getTitle()
			bitmaphashmap.put("title", mCustomWebView.getTitle());
		} else {
			hashmap.put("isWebview", false);
		}

		if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {
			hashmap.put("isHome", mWorkspace.getCurrentScreen());
			bitmaphashmap.put("title", "主页");
		} else {
			hashmap.put("isHome", CURRENT_SCREEN_WORKSPACE_NOT);
		}

		if (mWebViewBitmaps.size() > mCurrentIndex) {
			mWebViewBitmaps.set(mCurrentIndex, bitmaphashmap);
		} else {
			mWebViewBitmaps.add(mCurrentIndex, bitmaphashmap);
		}

		if (mWebViewStates.size() > mCurrentIndex) {
			mWebViewStates.set(mCurrentIndex, hashmap);
		} else {
			mWebViewStates.add(mCurrentIndex, hashmap);
		}

		// 设置Tabspace索引值

		mCurrentIndex = mTabspace.getChildCount();

		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
		mUrlEditText.setText("");
		mUrlEditText.addTextChangedListener(mUrlTextWatcher);
		urlText.setText("");

		initShowWebViewspace(url);

		updateBottomTabsNum();
		mHomespace.setCurrentScreen(CURRENT_SCREEN_TABSPACE);

	}

	private void newTab() {
		// 设置Tabspace索引值

		mCurrentIndex = mTabspace.getChildCount();
		tabCurrentItem = mTabspace.getChildCount();

		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
		mUrlEditText.setText("");
		mUrlEditText.addTextChangedListener(mUrlTextWatcher);
		urlText.setText("");

		initHideWebViewspace();

		updateBottomTabsNum();
		mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
		mWorkspace.moveToDefaultScreen(CURRENT_SCREEN_WORKSPACE_MIDDLE);
	}

	private void newTabAndThumbnail() {
		// 存储当前页面缩略图,是否使用视情况决定
		HashMap<String, Object> bitmaphashmap = new HashMap<String, Object>();

		Bitmap bitmap = getViewBitmap(mHomespace);

		bitmaphashmap.put("bitmap", bitmap);

		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		// 存储当前tab显示状态 workspace false tabspace true
		if (mTabspace.getChildVisibility(mCurrentIndex)) {
			hashmap.put("isWebview", true);
			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			bitmaphashmap.put("title", mCustomWebView.getTitle());
		} else {
			hashmap.put("isWebview", false);
		}

		if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {
			hashmap.put("isHome", mWorkspace.getCurrentScreen());
			bitmaphashmap.put("title", "主页");
		} else {
			hashmap.put("isHome", CURRENT_SCREEN_WORKSPACE_NOT);
		}

		if (mWebViewBitmaps.size() > mCurrentIndex) {
			mWebViewBitmaps.set(mCurrentIndex, bitmaphashmap);
		} else {
			mWebViewBitmaps.add(mCurrentIndex, bitmaphashmap);
		}

		if (mWebViewStates.size() > mCurrentIndex) {
			mWebViewStates.set(mCurrentIndex, hashmap);
		} else {
			mWebViewStates.add(mCurrentIndex, hashmap);
		}

		// 设置Tabspace索引值
		mCurrentIndex = mTabspace.getChildCount();

		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
		mUrlEditText.setText("");
		mUrlEditText.addTextChangedListener(mUrlTextWatcher);
		urlText.setText("");

		initHideWebViewspace();

		updateBottomTabsNum();
		mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
	}

	private Bitmap getViewBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = null;
		try {
			if (null != view.getDrawingCache()) {
				mWidth = mHomespace.getWidth();
				mHeight = mHomespace.getHeight();
				bitmap = Bitmap.createScaledBitmap(view.getDrawingCache(),
						mWidth, mHeight, false);
			} else {
				bitmap = ((BitmapDrawable) (getResources()
						.getDrawable(R.drawable.welcome))).getBitmap();
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			view.setDrawingCacheEnabled(false);
			view.destroyDrawingCache();
		}

		return bitmap;
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			menuLayout.setCurrentItem(index);
		}
	};

	/**
	 * 实现页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		public void onPageSelected(int arg0) {
			setCurPage(arg0);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 获取Android状态栏的高度
	 * 
	 * @return
	 */
	private int getStatusBarHeight() {
		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
			return 0;
		}
		return rect.top;
	}

	private void showPopMenuWindow() {
		popmenu = null;
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.menu_main, null);
		RelativeLayout layout_menu = (RelativeLayout) view
				.findViewById(R.id.layout_menu);

		/**
		 * 1.解决再次点击MENU键无反应问题 2.sub_view是PopupWindow的子View
		 */
		layout_menu.setFocusableInTouchMode(true);
		layout_menu.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// TODO Auto-generated method stub
				if ((keyCode == KeyEvent.KEYCODE_MENU)) {
					if (!menuShowed && popmenu != null && popmenu.isShowing()) {
						popmenu.dismiss();
					} else {
						menuShowed = false;
					}

					return true;
				}
				return false;
			}
		});

		initPopmenu(view);

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		// 创建一个PopuWidow对象
		int rotation = this.getWindowManager().getDefaultDisplay()
				.getRotation();

		// 手机处于正常状态 或者 旋转180度
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			popmenu = new PopupWindow(view, display.getWidth(),
					top_bar_main.getHeight() * 3);
		} else {
			popmenu = new PopupWindow(view, display.getHeight(),
					top_bar_main.getHeight() * 3);
		}
		// 使其聚集
		popmenu.setFocusable(true);
		// 设置允许在外点击消失
		popmenu.setOutsideTouchable(true);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popmenu.setBackgroundDrawable(new BitmapDrawable());

		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			// 设置layout在PopupWindow中显示的位置
			popmenu.showAtLocation(Launcher.this.findViewById(R.id.bottom),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
					bottom_bar.getHeight());
		} else {
			// 设置layout在PopupWindow中显示的位置
			popmenu.showAtLocation(
					Launcher.this.findViewById(R.id.top_bar_main),
					Gravity.RIGHT | Gravity.TOP, 0, top_bar_main.getHeight()
							+ getStatusBarHeight());
		}

	}

	/**
	 * 初始化弹窗菜单
	 */
	private void initPopmenu(View view) {
		menuLayout = (ViewPager) view.findViewById(R.id.vPager);
		page_1 = (View) view.findViewById(R.id.page_1);
		page_2 = (View) view.findViewById(R.id.page_2);

		setCurPage(0);

		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		viewpage1 = mInflater.inflate(R.layout.gridview_pop, null);
		viewpage2 = mInflater.inflate(R.layout.gridview_pop, null);
		listViews.add(viewpage1);
		listViews.add(viewpage2);

		menuLayout.setAdapter(new MyPagerAdapter(listViews));
		menuLayout.setCurrentItem(0);
		menuLayout.setOnPageChangeListener(new MyOnPageChangeListener());
		// mPager.setOnTouchListener(new OnTouchListener() {
		// public boolean onTouch(View v, MotionEvent event) {
		// return true;// 禁止滑动
		// }
		//
		// });

		gridview1 = (GridView) viewpage1.findViewById(R.id.grid_page_a);
		gridview2 = (GridView) viewpage2.findViewById(R.id.grid_page_a);
		initPopGridView();
	}

	private void initPopGridView() {

		initPopData();
		gridviewPop1 = new PopmenuDateAdapter(Launcher.this, lstDate1);
		gridviewPop2 = new PopmenuDateAdapter(Launcher.this, lstDate2);

		gridview1.setAdapter(gridviewPop1);
		gridview2.setAdapter(gridviewPop2);

		gridview1.setOnItemClickListener(gridListener);
		gridview2.setOnItemClickListener(gridListener);

	}

	private void initPopData() {

		lstDate1 = null;
		lstDate2 = null;
		// 1
		lstDate1 = new ArrayList<GridButtonInfo>();
		content1 = new String[] {
				(false == (sp.getBoolean(
						Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) ? "全屏"
						: "退出全屏"),
				"书签/历史",
				"加书签",
				(true == (sp
						.getBoolean(Constants.PREFERENCES_NIGHT_MODE, false)) ? "白天"
						: "夜间"), "分享", "下载", "更新", "退出" };
		int[] imageDrawables1 = {
				(false == (sp.getBoolean(
						Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) ? R.drawable.icon_fullscreen_normal
						: R.drawable.icon_fullscreen_exit),
				R.drawable.icon_menu_bookmark_normal,
				R.drawable.icon_add_bookmark_normal,

				(true == (sp
						.getBoolean(Constants.PREFERENCES_NIGHT_MODE, false)) ? R.drawable.icon_daymode_normal
						: R.drawable.icon_nightmode_normal),
				R.drawable.icon_share_normal, R.drawable.icon_download_normal,
				R.drawable.icon_refresh_page_normal,
				R.drawable.icon_exit_normal };

		for (int i = 0; i < content1.length; i++) {
			GridButtonInfo buttonInfo = new GridButtonInfo();
			buttonInfo.setButtonTitle(content1[i]);
			buttonInfo.setButtonImage(imageDrawables1[i]);
			lstDate1.add(buttonInfo);
		}

		// 2
		lstDate2 = new ArrayList<GridButtonInfo>();
		content2 = new String[] {
				"设置",
				"屏幕",
				"翻页模式",
				"复制链接",
				(false == (sp.getBoolean(Constants.PREFERENCES_INCOGNITO_MODE,
						false)) ? "无痕" : "退出无痕"),
				(true == (sp.getBoolean(
						Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true)) ? "无图"
						: "有图"), "页内查找" };

		int[] imageDrawables2 = {
				R.drawable.icon_settings_normal,
				R.drawable.icon_pingmu_normal,
				((CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen() || mWorkspace
						.getCurrentScreen() == 0) ? R.drawable.icon_page_scroll_normal
						: R.drawable.icon_page_scroll),
				R.drawable.icon_copy_normal,
				(false == (sp.getBoolean(Constants.PREFERENCES_INCOGNITO_MODE,
						false)) ? R.drawable.icon_privacy
						: R.drawable.icon_privacy_normal),

				(true == (sp.getBoolean(
						Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true)) ? R.drawable.icon_picture_normal
						: R.drawable.icon_picture),

				((CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) ? R.drawable.icon_page_search_normal
						: R.drawable.icon_page_search) };

		for (int i = 0; i < content2.length; i++) {
			GridButtonInfo buttonInfo = new GridButtonInfo();
			buttonInfo.setButtonTitle(content2[i]);
			buttonInfo.setButtonImage(imageDrawables2[i]);
			lstDate2.add(buttonInfo);
		}

	}

	/**
	 * 显示搜索引擎弹框
	 */
	private void showSearWindow() {
		SearWindow = new DialogSearchEngineUtil.Builder(Launcher.this).create();
		SearWindow.setCanceledOnTouchOutside(true);// 点击区域外关闭
		SearWindow.show();

	}

	/**
	 * 地址输入栏显示效果布局 包含自动提示 用户输入的地址将返回到浏览器地址栏中
	 */
	private void initAddress() {

		top_bar_input = (RelativeLayout) findViewById(R.id.top_bar_input);
		// 选择搜索引擎
		search_engine = (ImageView) top_bar_input
				.findViewById(R.id.search_engine);

		keywordsFlow = (KeywordsFlow) top_bar_input
				.findViewById(R.id.frameLayout1);

		search_engine_layout = (LinearLayout) top_bar_input
				.findViewById(R.id.search_engine_layout);
		search_engine_layout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showSearWindow();
			}
		});
		mUrlEditText = (EditText) top_bar_input.findViewById(R.id.UrlText);
		key_listview = (ListView) top_bar_input.findViewById(R.id.key_listview);
		key_input_layout = (KeyboardLayout) top_bar_input
				.findViewById(R.id.key_input_layout);
		TextView input_http = (TextView) top_bar_input
				.findViewById(R.id.input_http);
		TextView input_www = (TextView) top_bar_input
				.findViewById(R.id.input_www);
		TextView input_com = (TextView) top_bar_input
				.findViewById(R.id.input_com);
		TextView input_cn = (TextView) top_bar_input
				.findViewById(R.id.input_cn);

		Top_sao_yi_sao = (Button) top_bar_input
				.findViewById(R.id.Top_sao_yi_sao);
		Top_sao_yi_sao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				top_bar_inpu_ErWeiMa = top_bar_input;
				top_bar_mai_ErWeiMa = top_bar_main;
				mHomespac_ErWeiMa = mHomespace;

				Intent intent = new Intent(Launcher.this, CaptureActivity.class);
				intent.putExtra("String", "dingbusousuo");
				startActivity(intent);
			}
		});

		key_input_layout.setOnkbdStateListener(new onKybdsChangeListener() {

			@Override
			public void onKeyBoardStateChange(int state) {
				switch (state) {
				case KeyboardLayout.KEYBOARD_STATE_HIDE:
					/*
					 * getWindow() .setSoftInputMode(
					 * WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					 */
					key_input_layout.setVisibility(View.INVISIBLE);
					break;
				case KeyboardLayout.KEYBOARD_STATE_SHOW:
					/*
					 * getWindow() .setSoftInputMode(
					 * WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
					 * WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
					 */
					key_input_layout.setVisibility(View.VISIBLE);
					break;
				}
			}
		});

		input_http.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mUrlEditText.setText(mUrlEditText.getText().toString()
						+ "http://");
				mUrlEditText.setSelection(mUrlEditText.getText().length());
			}
		});
		input_www.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mUrlEditText
						.setText(mUrlEditText.getText().toString() + "www.");
				mUrlEditText.setSelection(mUrlEditText.getText().length());
			}
		});
		input_com.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mUrlEditText
						.setText(mUrlEditText.getText().toString() + ".com");
				mUrlEditText.setSelection(mUrlEditText.getText().length());
			}
		});
		input_cn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mUrlEditText.setText(mUrlEditText.getText().toString() + ".cn");
				mUrlEditText.setSelection(mUrlEditText.getText().length());
			}
		});

		mUrlEditText.setSelectAllOnFocus(true);
		mUrlEditText.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					addTab(mUrlEditText.getText().toString());
					top_bar_input.setVisibility(View.GONE);
					top_bar_main.setVisibility(View.VISIBLE);
					mHomespace.setVisibility(View.VISIBLE);
					top_bar_input_Flag = false;
					return true;
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					// key_input_layout.setVisibility(View.VISIBLE);
					getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				}

				return false;
			}

		});

		mUrlTextWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (StringUtil.isNull(arg0.toString())) {
					isCancel = true;
					mGoButton.setBackgroundResource(R.drawable.ic_btn_cancel);
				} else {
					isCancel = false;
					mGoButton.setBackgroundResource(R.drawable.ic_btn_go);
				}

				input_cursor = BookmarksProviderWrapper
						.getUrlSuggestions_history(getContentResolver(),
								mUrlEditText.getText().toString(),
								Launcher.this);
				String[] from = new String[] {
						UrlSuggestionCursorAdapter.URL_SUGGESTION_TITLE,
						UrlSuggestionCursorAdapter.URL_SUGGESTION_URL };
				int[] to = new int[] { R.id.AutocompleteTitle,
						R.id.AutocompleteUrl };
				adapter = new UrlSuggestionCursorAdapter(Launcher.this,
						R.layout.url_autocomplete_line, input_cursor, from, to);
				key_listview.setAdapter(adapter);
				Tools.setListViewHeightBasedOnChildren(key_listview);// 设置listview高度

				if (adapter.getCount() != 0) {
					key_listview.setVisibility(View.VISIBLE);
					keywordsFlow.setVisibility(View.GONE);
				} else {
					key_listview.setVisibility(View.GONE);
					keywordsFlow.setVisibility(View.VISIBLE);
				}

			}
		};

		mUrlEditText.addTextChangedListener(mUrlTextWatcher);

		mUrlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// Select all when focus gained.
				if (hasFocus) {
					mUrlEditText.setSelection(0, mUrlEditText.getText()
							.length());
				}
			}
		});

		key_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				input_cursor.moveToPosition(arg2);
				mUrlEditText.setText(input_cursor.getString(input_cursor
						.getColumnIndex(UrlSuggestionCursorAdapter.URL_SUGGESTION_URL)));
				mUrlEditText.setSelection(mUrlEditText.getText().length());
				// navigateToUrl();
				addTab(mUrlEditText.getText().toString());
				top_bar_input.setVisibility(View.GONE);
				top_bar_main.setVisibility(View.VISIBLE);
				mHomespace.setVisibility(View.VISIBLE);
				top_bar_input_Flag = false;
			}
		});

		mGoButton = (Button) top_bar_input.findViewById(R.id.GoBtn);
		mGoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				if (isCancel) {
					top_bar_main.setVisibility(View.VISIBLE);
					mHomespace.setVisibility(View.VISIBLE);
					top_bar_input.setVisibility(View.GONE);
					top_bar_input_Flag = false;
					getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
											| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (inputMethodManager.isActive()) {
						// 关闭输入法
						inputMethodManager.toggleSoftInput(
								InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
					}

				} else {
					addTab(mUrlEditText.getText().toString());
					// navigateToUrl();
					top_bar_input.setVisibility(View.GONE);
					top_bar_main.setVisibility(View.VISIBLE);
					mHomespace.setVisibility(View.VISIBLE);
					top_bar_input_Flag = false;
				}

			}
		});
	}

	/**
	 * 初始化书签发送区域
	 */
	private void initZone() {
		mZone = (Zone) findViewById(R.id.top_zone);
	}

	/**
	 * 初始化书签发送区域
	 */
	private void initSendZone() {
		mSendZone = (SendZone) findViewById(R.id.send_zone);
		mSendZone.setText("发送到桌面");
	}

	/**
	 * 初始化书签删除区域
	 */
	private void initDeleteZone() {
		mDeleteZone = (DeleteZone) findViewById(R.id.delete_zone);
		mDeleteZone.setText("删除");
	}

	private void initHomespace() {
		mHomespace = (Homespace) findViewById(R.id.homespace);
		mHomespace.setOnTouchListener(this);
		mHomespace.SetOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int view) {
				// 处理导航和浏览页面的切换事件
				if (CURRENT_SCREEN_WORKSPACE == view) {
					// 设置导航页面
					mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
					mUrlEditText.setText("");
					mUrlEditText.addTextChangedListener(mUrlTextWatcher);
					urlText.setText("");
					top_bar_main.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
					onPageStarted("");
					CheckIsBookmark("");
					bottom_bar.setVisibility(View.VISIBLE);

					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.VISIBLE);
						top_bar_main.setVisibility(View.VISIBLE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TOOLMENU);

					}

					if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TURNPAGE);
					}

				} else {
					// 设置浏览页面
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View childView = mWebViewspace.getChildAt(0);
					if (childView != null) {
						CustomWebView mCustomWebView = (CustomWebView) childView
								.findViewById(R.id.webview);
						if (mCustomWebView != null) {
							urlText.setText(mCustomWebView.getTitle());
							mUrlEditText
									.removeTextChangedListener(mUrlTextWatcher);
							mUrlEditText.setText(mCustomWebView.getUrl());
							mUrlEditText
									.addTextChangedListener(mUrlTextWatcher);
							bottom_refresh.setEnabled(true);
							bottom_refresh
									.setImageResource(R.drawable.bottom_bar_refresh);
							CheckIsBookmark(mCustomWebView.getUrl());

							if (sp.getBoolean(
									Constants.PREFERENCES_SHOW_FULL_SCREEN,
									false)) {
								bottom_bar.setVisibility(View.GONE);
								top_bar_main.setVisibility(View.GONE);
								ShowOrHideFloatWindow(
										FloatWindowService.OPERATION_SHOW,
										FloatWindowService.TYPE_TOOLMENU);

							}

							if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
								ShowOrHideFloatWindow(
										FloatWindowService.OPERATION_SHOW,
										FloatWindowService.TYPE_TURNPAGE);
							}
						}
					}
				}
			}
		});
		// DragLayer
		DragController dragController = mDragController;
		DragLayer dragLayer = (DragLayer) findViewById(R.id.temp_laucher);
		dragLayer.setDragController(dragController);

		// workspace
		mWorkspace = (Workspace) dragLayer.findViewById(R.id.workspace);

		final Workspace workspace = mWorkspace;
		workspace.setHapticFeedbackEnabled(false);

		// 初始化 indicator
		final LinearLayout indicatorLayout = (LinearLayout) dragLayer
				.findViewById(R.id.indicator);
		mainpage_1 = (ImageView) indicatorLayout.findViewById(R.id.page_1);
		mainpage_2 = (ImageView) indicatorLayout.findViewById(R.id.page_2);
		mainpage_3 = (ImageView) indicatorLayout.findViewById(R.id.page_3);

		setcurrentPoint(1);
		onPageStarted("");
		mWorkspace.SetOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int view) {
				// 处理向左滑动时，显示WebView的判断
				// 当当前页面为Launcher最右侧页面，则判断是否有WebView需要显示
				if (view == 0) {

					TongJi.AddAnalyticsData(TongJi.m_zuopinggouwu);
					
					if (One_webView == null) {

						LayoutInflater inflate = LayoutInflater
								.from(Launcher.this);
						View view_one = inflate.inflate(R.layout.webview, null);

						One_webView = (CustomWebView) view_one
								.findViewById(R.id.webview);

						String loadurl = ConstantsUrl.ONE_WEBVIEW_URL;
						workspace.addInLeftScreen(view_one, 0, loadurl);
						initOneWebView();
						One_webView.setWebViewClient(new CustomWebViewClient(
								Launcher.this, view_one));
						One_webView.setFocusable(true);
						One_webView.requestFocusFromTouch();
						One_webView.setOnTouchListener(new OnTouchListener() {

							@Override
							public boolean onTouch(View v, MotionEvent event) {
								if (sp.getBoolean(
										Constants.PREFERENCES_SHOW_FULL_SCREEN,
										false)) {
									bottom_bar.setVisibility(View.GONE);
									top_bar_main.setVisibility(View.GONE);
									ShowOrHideFloatWindow(
											FloatWindowService.OPERATION_SHOW,
											FloatWindowService.TYPE_TOOLMENU);

								}
								switch (event.getAction()) {
								case MotionEvent.ACTION_DOWN:
								case MotionEvent.ACTION_UP:
									if (!v.hasFocus()) {
										v.requestFocus();
									}
									break;
								}
								return false;
							}
						});
					}

					mUrlEditText.setText(One_webView.getUrl());
					bottom_refresh.setEnabled(true);
					bottom_refresh
							.setImageResource(R.drawable.bottom_bar_refresh);
					urlText.setText(One_webView.getTitle());
					CheckIsBookmark(One_webView.getUrl());

					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.GONE);
						top_bar_main.setVisibility(View.GONE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_SHOW,
								FloatWindowService.TYPE_TOOLMENU);

					}

					if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_SHOW,
								FloatWindowService.TYPE_TURNPAGE);
					}
				} else {

					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.VISIBLE);
						top_bar_main.setVisibility(View.VISIBLE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TOOLMENU);

					}

					if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_HIDE,
								FloatWindowService.TYPE_TURNPAGE);
					}

					onPageStarted("");
					CheckIsBookmark("");
					mProgressBar.setVisibility(View.GONE);
					indicatorLayout.setVisibility(View.VISIBLE);
				}
				setcurrentPoint(view);
			}
		});

		workspace.setOnLongClickListener(this);
		workspace.setDragController(dragController);
		workspace.setLauncher(this);

		for (int i = 0; i < workspace.getChildCount(); i++) {
			View child = workspace.getChildAt(i);
			if (child instanceof CellLayout) {
				((CellLayout) child).setDragController(dragController);
				((CellLayout) child).setLauncher(this);
			}
		}

		mHomespace.setWorkspace(workspace);

		// 初始化打开文件夹
		mFolder = (Folder) dragLayer.findViewById(R.id.folder);

		// Zone
		Zone zone = mZone;
		zone.setHandle(findViewById(R.id.top_bar_main));

		// SendZone
		SendZone sendZone = mSendZone;
		sendZone.setLauncher(this);
		sendZone.setWorkspace(workspace);

		// DeleteZone
		DeleteZone deleteZone = mDeleteZone;
		deleteZone.setLauncher(this);

		zone.setmSendZone(sendZone);
		zone.setmDeleteZone(deleteZone);
		zone.setmLauncher(this);

		// DragController setting
		dragController.setDragListener(zone);
		dragController.setScrollView(dragLayer);
		dragController.setMoveTarget(workspace);

		for (int i = 0; i < workspace.getChildCount(); i++) {
			View child = workspace.getChildAt(i);
			if (child instanceof CellLayout) {
				dragController.addDropTarget((CellLayout) child);
			}
		}

		dragController.addDropTarget(zone);
		dragController.addDropTarget(sendZone);
		dragController.addDropTarget(deleteZone);

		mTabspace = (Tabspace) findViewById(R.id.tabspace);
		mHomespace.setTabspace(mTabspace);
		mTabspace.setVisibility(View.GONE);

		initHideWebViewspace();
	}

	private void initShowWebViewspace(String url) {

		WebViewspace mWebViewspace = (WebViewspace) mInflater.inflate(
				R.layout.webviewspace, mTabspace, false);
		mWebViewspace.setOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int view) {
				mWebViewCurrentIndex = view;
				// Log.i(TAG, "mWebViewCurrentIndex= " + mWebViewCurrentIndex);
				Toast.makeText(Launcher.this,
						"mWebViewCurrentIndex= " + mWebViewCurrentIndex, 1000)
						.show();
			}
		});

		synchronized (mTabspace) {
			mTabspace.addInSpace(mWebViewspace, mCurrentIndex);
			mHomespace.setWebViewspace(mWebViewspace);
			mTabspace.setVisibility(View.VISIBLE);
		}
		createCurrentWebView(url, mWebViewspace);
	}

	private void initHideWebViewspace() {
		WebViewspace mWebViewspace = (WebViewspace) mInflater.inflate(
				R.layout.webviewspace, mTabspace, false);
		mWebViewspace.setOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int view) {
				mWebViewCurrentIndex = view;
				// Log.i(TAG, "mWebViewCurrentIndex= " + mWebViewCurrentIndex);
				Toast.makeText(Launcher.this,
						"mWebViewCurrentIndex= " + mWebViewCurrentIndex, 1000)
						.show();
			}
		});

		synchronized (mTabspace) {
			mTabspace.addAndHideInSpace(mWebViewspace, mCurrentIndex);
			mTabspace.setVisibility(View.GONE);
			mHomespace.setWebViewspace(mWebViewspace);
		}
	}

	public void createCurrentWebView(String url, WebViewspace mWebViewspace) {

		mWebViewspace.removeAllViews();

		// RelativeLayout view = (RelativeLayout) mInflater.inflate(
		// R.layout.webview, mWebViewspace, false);

		LayoutInflater inflate = LayoutInflater.from(this);
		ADview = inflate.inflate(R.layout.webview, null);

		final CustomWebView mCreateCurrentWebView = (CustomWebView) ADview
				.findViewById(R.id.webview);

		ADshowflag = 0;
		mCreateCurrentWebView.setWebViewClient(new CustomWebViewClient(this,
				ADview));

		mCreateCurrentWebView.setFocusable(true);
		mCreateCurrentWebView.requestFocusFromTouch();

		mCreateCurrentWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				PointerXY.x = (int) event.getX();
				PointerXY.y = (int) event.getY();

				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()
						|| mWorkspace.getCurrentScreen() == 0) {
					if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN,
							false)) {
						bottom_bar.setVisibility(View.GONE);
						top_bar_main.setVisibility(View.GONE);
						ShowOrHideFloatWindow(
								FloatWindowService.OPERATION_SHOW,
								FloatWindowService.TYPE_TOOLMENU);

					}
				}

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
					}
					break;
				}

				return false;
			}

		});
		mCreateCurrentWebView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return ContextMenuPopWindow(v);
			}

		});

		mCreateCurrentWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				doDownloadStart(url, userAgent, contentDisposition, mimetype,
						contentLength);

				if (mCreateCurrentWebView.getOpenWindownFlag() == 0) {
					if (mCreateCurrentWebView.getTitle() == null
							&& mTabspace.getChildCount() == 1) {

						mTabspace.removeViewAt(mTabspace.getChildCount() - 1);
						mTabspace.setVisibility(View.GONE);
						initHideWebViewspace();
						mCurrentIndex = 0;

						updateBottomTabsNum();
						mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);

					} else if (mCreateCurrentWebView.getTitle() == null
							&& mTabspace.getChildCount() != 1) {
						mTabspace.setVisibility(View.GONE);
						mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);

					}
				}

			}

		});

		mCreateCurrentWebView.setInitialScale(5);

		final Activity activity = this;
		mCreateCurrentWebView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				Launcher.this.startActivityForResult(Intent.createChooser(i,
						Launcher.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				Launcher.this.startActivityForResult(Intent.createChooser(i,
						Launcher.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@Override
			public Bitmap getDefaultVideoPoster() {
				if (mDefaultVideoPoster == null) {
					mDefaultVideoPoster = BitmapFactory.decodeResource(
							Launcher.this.getResources(),
							R.drawable.default_video_poster);
				}

				return mDefaultVideoPoster;
			}

			@Override
			public View getVideoLoadingProgressView() {
				if (mVideoProgressView == null) {
					LayoutInflater inflater = LayoutInflater
							.from(Launcher.this);
					mVideoProgressView = inflater.inflate(
							R.layout.video_loading_progress, null);
				}

				return mVideoProgressView;
			}

			public void onShowCustomView(View view,
					WebChromeClient.CustomViewCallback callback) {
				showCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				hideCustomView();
			}

			@Override
			public void onProgressChanged(final WebView view, int newProgress) {
				
				if(newProgress<30){
					addToLauncher.setBackgroundResource(R.drawable.ic_btn_cancel);
					addToLauncher.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							view.stopLoading();
							addToLauncher.setBackgroundResource(R.drawable.ic_btn_account);
							addToLauncher.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) { }
							});
						}
					});
				}
				
			
				if(newProgress==100){
					addToLauncher.setBackgroundResource(R.drawable.ic_btn_account);
					addToLauncher.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) { }
					});
				}

				
				((CustomWebView) view).setProgress(newProgress);
				mProgressBar.setProgress(mCreateCurrentWebView.getProgress());
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				new Thread(new FaviconUpdaterRunnable(Launcher.this, view
						.getUrl(), view.getOriginalUrl(), icon)).start();

				super.onReceivedIcon(view, icon);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				setTitle(title);

				startHistoryUpdaterRunnable(title,
						mCreateCurrentWebView.getUrl(),
						mCreateCurrentWebView.getOriginalUrl(),
						mCreateCurrentWebView.getFavicon());

				super.onReceivedTitle(view, title);
			}

			@Override
			public void onCloseWindow(WebView window) {
				Message msg = Message.obtain();
				msg.what = 3;
				msg.arg1 = window.getId();
				mWindowHandler.sendMessage(msg);
				super.onCloseWindow(window);
			}

			@Override
			public boolean onCreateWindow(WebView view, boolean isDialog,
					boolean isUserGesture, final Message resultMsg) {

				if (isUserGesture) {
					// addTab("");
					addTabAndThumbnail("");
					WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
					transport.setWebView(getCurrentTab());
					resultMsg.sendToTarget();
					mWindowHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							getCurrentTab().setOpenWindownFlag(1);
							getCurrentTab().loadUrl(mUrlText);
						}
					}, 500);
				}
				return true;
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				// 确定弹框
				dialogUtil = new DialogUtil.Builder(Launcher.this)
						.setTitleText("温馨提醒").setText(message)
						.setPositiveButton("确定", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.confirm();
							}
						}).create();
				dialogUtil.show();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				// 确定取消弹框
				dialogUtil = new DialogUtil.Builder(Launcher.this)
						.setTitleText("温馨提醒").setText(message)
						.setPositiveButton("确定", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.confirm();
							}
						}).setNegativeButton("取消", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.cancel();
							}
						}).create();
				dialogUtil.show();
				return true;
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {

				dialogeditUtil = new DialogEditUtil.Builder(Launcher.this)
						.setTitleText(message)
						.setPositiveButton(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String value = dialogeditUtil.GetEditText();
								if (StringUtil.isNull(value)) {
									Toast.makeText(Launcher.this, "请输入内容",
											Toast.LENGTH_SHORT).show();
									return;
								}
								dialogeditUtil.dismiss();
								result.confirm(value);
							}
						}).setNegativeButton(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogeditUtil.dismiss();
							}
						}).create();
				dialogeditUtil.show();

				return true;

			}

		});
		synchronized (mWebViewspace) {
			mWebViewspace.addView(ADview, mWebViewCurrentIndex);
			mWebViewspace.requestLayout();
			mTabspace.requestLayout();
			mWebViewspace.setCurrentScreen(mWebViewCurrentIndex);
		}

		Intent i = getIntent();
		if (i.getData() != null && url.contains("file://")) {
			if (i.getType().equals("text/plain")) {
				mCreateCurrentWebView.getSettings().setDefaultTextEncodingName(
						"utf-8");
			} else if (i.getType().equals("text/html")) {
				mCreateCurrentWebView.getSettings().setDefaultTextEncodingName(
						"gbk");
			}

		} else {
			mCreateCurrentWebView.getSettings().setDefaultTextEncodingName(
					"Latin-1");
		}

		mCreateCurrentWebView.loadUrl(url);

		updateUI(mCreateCurrentWebView);
	}

	public void createMutilCurrentWebView(String url, WebViewspace mWebViewspace) {
		// 如果当前webview不是最后一个,
		// 则移除mWebViewCurrentIndex之后的webview,并添加当前webview
		if (mWebViewCurrentIndex < mWebViewspace.getChildCount() - 1) {
			mWebViewCurrentIndex++;
			// 移除mWebViewCurrentIndex之后的webview
			mWebViewspace.removeViews(mWebViewCurrentIndex,
					mWebViewspace.getChildCount() - mWebViewCurrentIndex);
			//
			for (int i = mWebViewCurrentIndex; i < mWebViewspace
					.getChildCount() - mWebViewCurrentIndex; i++) {
				beforeUrl[i] = "";
			}

		} else {
			mWebViewCurrentIndex = mWebViewspace.getChildCount();
		}

		beforeUrl[mWebViewCurrentIndex] = url;

		RelativeLayout view = (RelativeLayout) mInflater.inflate(
				R.layout.webview, mWebViewspace, false);
		final CustomWebView mCreateCurrentWebView = (CustomWebView) view
				.findViewById(R.id.webview);
		mCreateCurrentWebView.setWebViewClient(new CustomWebViewClient(this,
				null));
		mCreateCurrentWebView.setOnTouchListener(this);

		mCreateCurrentWebView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return ContextMenuPopWindow(v);
			}

		});

		mCreateCurrentWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				doDownloadStart(url, userAgent, contentDisposition, mimetype,
						contentLength);
			}

		});

		final Activity activity = this;
		mCreateCurrentWebView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				Launcher.this.startActivityForResult(Intent.createChooser(i,
						Launcher.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@SuppressWarnings("unused")
			// This is an undocumented method, it _is_ used, whatever Eclipse
			// may think :)
			// Used to show a file chooser dialog.
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				Launcher.this.startActivityForResult(Intent.createChooser(i,
						Launcher.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@Override
			public Bitmap getDefaultVideoPoster() {
				if (mDefaultVideoPoster == null) {
					mDefaultVideoPoster = BitmapFactory.decodeResource(
							Launcher.this.getResources(),
							R.drawable.default_video_poster);
				}

				return mDefaultVideoPoster;
			}

			@Override
			public View getVideoLoadingProgressView() {
				if (mVideoProgressView == null) {
					LayoutInflater inflater = LayoutInflater
							.from(Launcher.this);
					mVideoProgressView = inflater.inflate(
							R.layout.video_loading_progress, null);
				}

				return mVideoProgressView;
			}

			public void onShowCustomView(View view,
					WebChromeClient.CustomViewCallback callback) {
				showCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				hideCustomView();
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				((CustomWebView) view).setProgress(newProgress);
				mProgressBar.setProgress(mCreateCurrentWebView.getProgress());
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				new Thread(new FaviconUpdaterRunnable(Launcher.this, view
						.getUrl(), view.getOriginalUrl(), icon)).start();

				super.onReceivedIcon(view, icon);
			}

			@Override
			public void onCloseWindow(WebView window) {
				Message msg = Message.obtain();
				msg.what = 3;
				msg.arg1 = window.getId();
				mWindowHandler.sendMessage(msg);
				super.onCloseWindow(window);
			}

			@Override
			public boolean onCreateWindow(WebView view, boolean isDialog,
					boolean isUserGesture, final Message resultMsg) {

				if (isUserGesture) {
					addTab("");
					WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
					transport.setWebView(getCurrentTab());
					resultMsg.sendToTarget();
					mWindowHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							getCurrentTab().loadUrl(mUrlText);
						}
					}, 500);
				}
				return true;
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				setTitle(title);

				startHistoryUpdaterRunnable(title,
						mCreateCurrentWebView.getUrl(),
						mCreateCurrentWebView.getOriginalUrl(),
						mCreateCurrentWebView.getFavicon());

				super.onReceivedTitle(view, title);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				// 确定弹框
				dialogUtil = new DialogUtil.Builder(Launcher.this)
						.setTitleText("温馨提醒").setText(message)
						.setPositiveButton("确定", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.confirm();
							}
						}).create();
				dialogUtil.show();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				// 确定取消弹框
				dialogUtil = new DialogUtil.Builder(Launcher.this)
						.setTitleText("温馨提醒").setText(message)
						.setPositiveButton("确定", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.confirm();
							}
						}).setNegativeButton("取消", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.cancel();
							}
						}).create();
				dialogUtil.show();
				return true;
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {

				dialogeditUtil = new DialogEditUtil.Builder(Launcher.this)
						.setTitleText(message)
						.setPositiveButton(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String value = dialogeditUtil.GetEditText();
								if (StringUtil.isNull(value)) {
									Toast.makeText(Launcher.this, "请输入内容",
											Toast.LENGTH_SHORT).show();
									return;
								}
								dialogeditUtil.dismiss();
								result.confirm(value);
							}
						}).setNegativeButton(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogeditUtil.dismiss();
							}
						}).create();
				dialogeditUtil.show();

				return true;

			}

		});
		synchronized (mWebViewspace) {
			// mWebViews.add(mWebViewCurrentIndex, mCurrentWebView);
			mWebViewspace.addView(view, mWebViewCurrentIndex);
			// mWebViewspace.addView(view);
			mWebViewspace.requestLayout();
			mTabspace.requestLayout();
			mWebViewspace.setCurrentScreen(mWebViewCurrentIndex);
		}
		// add to mCurrentWebViews
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		hashmap.put("url", url);
		hashmap.put("webview", mCreateCurrentWebView);
		mCurrentWebViews.add(hashmap);
		// Log.i(TAG, "mCurrentWebViews size= " + mCurrentWebViews.size());

		mCreateCurrentWebView.loadUrl(url);
		// mCurrentWebView = mCreateCurrentWebView;
		updateUI(mCreateCurrentWebView);
	}

	private void navigateToUrl() {
		String url = mUrlEditText.getText().toString();
		addTab(url);
		navigateToUrl(url);
	}

	public void navigateToUrl(String url) {
		// Needed to hide toolbars properly.
		WebViewspace mWebViewspace = (WebViewspace) mTabspace
				.getChildAt(mCurrentIndex);
		View childView = mWebViewspace.getChildAt(0);
		CustomWebView mCustomWebView = (CustomWebView) childView
				.findViewById(R.id.webview);
		if ((url != null) && (url.length() > 0)) {

			if (UrlUtils.isUrl(url)) {
				url = UrlUtils.checkUrl(url);
			} else {
				url = UrlUtils.getSearchUrl(this, url);
			}

			hideKeyboard(true);

			mCustomWebView.loadUrl(url);
		}
	}

	private void hideKeyboard(boolean isHide) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mUrlEditText.getWindowToken(), 0);
	}

	// Browser Begin
	/**
	 * Create main UI.
	 */
	private void buildComponents() {

		mGestureDetector = new GestureDetector(this, new GestureListener());

		mUrlBarVisible = true;

		mCurrentWebViews = new ArrayList<HashMap<String, Object>>();

		mWebViews = new ArrayList<Object>();

		mWebViewBitmaps = new ArrayList<HashMap<String, Object>>();

		mWebViewStates = new ArrayList<HashMap<String, Object>>();

		Controller.getInstance().setWebViewList(mWebViews);

		initAllView();
		// 初始化顶部
		initTopBar();

		int rotation = this.getWindowManager().getDefaultDisplay()
				.getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			// 初始化底部
			initBottomBar();
		} else {
			initBottomBarLand();
		}

		// 初始化地址栏输入
		initAddress();

		// 初始化网页内容查找按钮
		initFindBar();

		initZone();

		initSendZone();
		// 初始化书签删除按钮
		initDeleteZone();
		// 初始化TopBar Browser
		initTopBarBrowser();
		// 初始化TabsGallery
		initTabsGallery();

		initSnapShotWebView();

		initHomespace();
		// initWebViewspace();

		// 初始化设置为默认浏览器提醒UI
		initsetDefaultBrowser();

	}

	private void updateSwitchTabsMethod() {
		String method = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(Constants.PREFERENCES_GENERAL_SWITCH_TABS_METHOD,
						"fling");

		if (method.equals("buttons")) {
			mSwitchTabsMethod = SwitchTabsMethod.BUTTONS;
		} else if (method.equals("fling")) {
			mSwitchTabsMethod = SwitchTabsMethod.FLING;
		} else if (method.equals("both")) {
			mSwitchTabsMethod = SwitchTabsMethod.BOTH;
		} else {
			mSwitchTabsMethod = SwitchTabsMethod.BUTTONS;
		}
	}

	private void updateBookmarksDatabaseSource() {
		String source = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(Constants.PREFERENCE_BOOKMARKS_DATABASE, "STOCK");

		if (source.equals("STOCK")) {
			BookmarksProviderWrapper.setBookmarksSource(BookmarksSource.STOCK);
		} else if (source.equals("INTERNAL")) {
			BookmarksProviderWrapper
					.setBookmarksSource(BookmarksSource.INTERNAL);
		}
	}

	private void registerPreferenceChangeListener() {
		mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				if (key.equals(Constants.PREFERENCE_BOOKMARKS_DATABASE)) {
					updateBookmarksDatabaseSource();
				}
			}
		};

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(
						mPreferenceChangeListener);
	}

	/**
	 * 打开网址
	 */
	public void addTab(String url) {
		if (mFindDialogVisible) {
			closeFindDialog();
		}

		if (sp.getBoolean(Constants.Turn_Page_Kye, false)) {
			ShowOrHideFloatWindow(FloatWindowService.OPERATION_SHOW,
					FloatWindowService.TYPE_TURNPAGE);
		}

		if (sp.getBoolean(Constants.PREFERENCES_SHOW_FULL_SCREEN, false)) {
			bottom_bar.setVisibility(View.GONE);
			top_bar_main.setVisibility(View.GONE);
			top_bar_input.setVisibility(View.GONE);
			ShowOrHideFloatWindow(FloatWindowService.OPERATION_SHOW,
					FloatWindowService.TYPE_TOOLMENU);

		}

		if ((url != null) && (url.length() > 0)) {
			if (UrlUtils.isUrl(url)) {
				url = UrlUtils.checkUrl(url);
			} else {
				url = UrlUtils.getSearchUrl(this, url);
			}

			hideKeyboard(true);
		}

		// 判断Tab是否为打开的
		if (mCurrentIndex <= mTabspace.getChildCount() - 1) {
			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			createCurrentWebView(url, mWebViewspace);
			mTabspace.setVisibility(View.VISIBLE);
			mTabspace.setChildVisibility(mCurrentIndex);
			mHomespace.setCurrentScreen(CURRENT_SCREEN_TABSPACE);
			return;
		}

		initShowWebViewspace(url);

		// updateUI();

		// navigateToUrl(url);

		mHomespace.setCurrentScreen(CURRENT_SCREEN_TABSPACE);

	}

	/**
	 * Initialize the Web icons database.
	 */
	private void initializeWebIconDatabase() {

		final WebIconDatabase db = WebIconDatabase.getInstance();
		db.open(getDir("icons", 0).getPath());
	}

	/**
	 * Navigate to the user home page.
	 */
	private void navigateToHome() {
		navigateToUrl(Controller
				.getInstance()
				.getPreferences()
				.getString(Constants.PREFERENCES_GENERAL_HOME_PAGE,
						Constants.URL_ABOUT_START));
	}

	/**
	 * Thread to delay the show of the find dialog. This seems to be necessary
	 * when shown from a QuickAction. If not, the keyboard does not show. 50ms
	 * seems to be enough on a Nexus One and on the (rather) slow emulator.
	 * Dirty hack :(
	 */
	private void startShowFindDialogRunnable() {
		new Thread(new Runnable() {

			private Handler mHandler = new Handler() {
				public void handleMessage(Message msg) {
					showFindDialog();
				}
			};

			@Override
			public void run() {
				try {
					Thread.sleep(50);
					mHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					mHandler.sendEmptyMessage(0);
				}

			}
		}).start();
	}

	/**
	 * Select Text in the webview and automatically sends the selected text to
	 * the clipboard.
	 */
	public void swithToSelectAndCopyTextMode() {
		try {
			KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(mCurrentWebView);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Gesture listener implementation.
	 */
	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			mCurrentWebView.zoomIn();
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	}

	/**
	 * Update the "Go" button image.
	 */
	private void updateGoButton(CustomWebView mCurrentWebView) {
		if (mCurrentWebView == null)
			return;
		if (mCurrentWebView.isLoading()) {
			isCancel = true;
			mGoButton.setBackgroundResource(R.drawable.ic_btn_cancel);
			mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(null, null,
					mCircularProgress, null);
			((AnimationDrawable) mCircularProgress).start();
		} else {
			isCancel = false;
			if (!mCurrentWebView.isSameUrl(mUrlEditText.getText().toString())) {
				mGoButton.setBackgroundResource(R.drawable.ic_btn_go);
			} else {
				mGoButton.setBackgroundResource(R.drawable.ic_btn_refresh);
			}

			mUrlEditText.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null, null);
			((AnimationDrawable) mCircularProgress).stop();
		}

	}

	/**
	 * Navigate to the previous page in history.
	 */
	private void navigatePrevious() {
		// Needed to hide toolbars properly.
		mUrlEditText.clearFocus();

		hideKeyboard(true);
		mCurrentWebView.goBack();
	}

	private void navigatePrevious(CustomWebView mCurrentWebView) {
		// Needed to hide toolbars properly.
		mUrlEditText.clearFocus();

		hideKeyboard(true);
		if (mCurrentWebView != null) {
			mCurrentWebView.goBack();
		}
	}

	/**
	 * Navigate to the next page in history.
	 */
	private void navigateNext() {
		// Needed to hide toolbars properly.
		mUrlEditText.clearFocus();

		hideKeyboard(true);
		if (mCurrentWebView != null)
			mCurrentWebView.goForward();
	}

	private void navigateNext(CustomWebView mCurrentWebView) {
		// Needed to hide toolbars properly.
		mUrlEditText.clearFocus();

		hideKeyboard(true);
		if (mCurrentWebView != null)
			mCurrentWebView.goForward();
	}

	private void hideKeyboardFromFindDialog() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mFindText.getWindowToken(), 0);
	}

	private void closeFindDialog() {
		WebViewspace mWebViewspace = (WebViewspace) mTabspace
				.getChildAt(mCurrentIndex);
		View childView = mWebViewspace.getChildAt(0);
		CustomWebView mCustomWebView = (CustomWebView) childView
				.findViewById(R.id.webview);

		hideKeyboardFromFindDialog();
		mCustomWebView.doNotifyFindDialogDismissed();
		setFindBarVisibility(false);
	}

	private void doFind() {
		WebViewspace mWebViewspace = (WebViewspace) mTabspace
				.getChildAt(mCurrentIndex);
		View childView = mWebViewspace.getChildAt(0);
		CustomWebView mCustomWebView = null;
		if (childView != null) {
			mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
		}
		if (mCustomWebView != null) {
			CharSequence find = mFindText.getText();
			if (find.length() == 0) {
				mFindPreviousButton.setEnabled(false);
				mFindNextButton.setEnabled(false);
				mFindPreviousButton
						.setImageResource(R.drawable.ic_btn_find_prev_notwork);
				mFindNextButton
						.setImageResource(R.drawable.ic_btn_find_next_notwork);

				mCustomWebView.clearMatches();
			} else {
				int found = mCustomWebView.findAll(find.toString());

				if (found < 2) {
					mFindPreviousButton.setEnabled(false);
					mFindNextButton.setEnabled(false);
					mFindPreviousButton
							.setImageResource(R.drawable.ic_btn_find_prev_notwork);
					mFindNextButton
							.setImageResource(R.drawable.ic_btn_find_next_notwork);
				} else {
					mFindPreviousButton.setEnabled(true);
					mFindNextButton.setEnabled(true);
					mFindPreviousButton
							.setImageResource(R.drawable.ic_btn_find_prev);
					mFindNextButton
							.setImageResource(R.drawable.ic_btn_find_next);
				}
			}
		}
	}

	/**
	 * Update the UI: Url edit text, previous/next button state,...
	 */
	private void updateUI(CustomWebView mCurrentWebView) {
		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
		mUrlEditText.setText(mCurrentWebView.getUrl());
		mUrlEditText.addTextChangedListener(mUrlTextWatcher);

		bottom_bar_back.setEnabled(mCurrentWebView.canGoBack());
		bottom_bar_forward.setEnabled(mCurrentWebView.canGoForward());
		if (mCurrentWebView.canGoForward()) {
			bottom_bar_forward.setImageResource(R.drawable.bottom_bar_forward);
		} else {
			bottom_bar_forward
					.setImageResource(R.drawable.bottom_bar_forward_cannot);
		}

		if (mCurrentWebView.canGoBack()) {
			bottom_bar_back.setImageResource(R.drawable.bottom_bar_back);
		} else {
			bottom_bar_back.setImageResource(R.drawable.bottom_bar_back_cannot);
		}

		// if (mCurrentWebView.getUrl() != null)
		// mRemoveTabButton.setEnabled((mViewFlipper.getChildCount() > 1 ||
		// !mCurrentWebView.getUrl().equals(Constants.URL_ABOUT_START)));
		// else
		// mRemoveTabButton.setEnabled(mViewFlipper.getChildCount() > 1);

		mProgressBar.setProgress(mCurrentWebView.getProgress());

		updateGoButton(mCurrentWebView);

		updateTitle(mCurrentWebView);

	}

	private void showFindDialog() {

		WebViewspace mWebViewspace = (WebViewspace) mTabspace
				.getChildAt(mCurrentIndex);
		View childView = mWebViewspace.getChildAt(0);
		CustomWebView mCustomWebView = (CustomWebView) childView
				.findViewById(R.id.webview);

		setFindBarVisibility(true);
		mCustomWebView.doSetFindIsUp(true);
		CharSequence text = mFindText.getText();
		if (text.length() > 0) {
			mFindText.setSelection(0, text.length());
			doFind();
		} else {
			mFindPreviousButton.setEnabled(false);
			mFindNextButton.setEnabled(false);
			mFindPreviousButton
					.setImageResource(R.drawable.ic_btn_find_prev_notwork);
			mFindNextButton
					.setImageResource(R.drawable.ic_btn_find_next_notwork);
		}

		mFindText.requestFocus();
		showKeyboardForFindDialog();
	}

	/**
	 * Initiate a download. Check the SD card and start the download runnable.
	 * 
	 * @param url
	 *            The url to download.
	 * @param userAgent
	 *            The user agent.
	 * @param contentDisposition
	 *            The content disposition.
	 * @param mimetype
	 *            The mime type.
	 * @param contentLength
	 *            The content length.
	 */
	private void doDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {

		if (ApplicationUtils.checkCardState(this, true)) {

			new DownloadAppIn(url, Launcher.this);
			Toast.makeText(this, getString(R.string.Main_DownloadStartedMsg),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showCustomView(View view,
			WebChromeClient.CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (mCustomView != null) {
			callback.onCustomViewHidden();
			return;
		}
		mFullscreenFlag = true;
		Launcher.this.getWindow().getDecorView();

		FrameLayout decor = (FrameLayout) getWindow().getDecorView();

		decor.setBackgroundColor(Launcher.this.getResources().getColor(
				android.R.color.black));
		mFullscreenContainer = new FullscreenHolder(Launcher.this);
		mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		mCustomView = view;
		setStatusBarVisibility(false);
		mCustomViewCallback = callback;
	}

	private void hideCustomView() {
		if (mCustomView == null)
			return;
		mFullscreenFlag = false;
		setStatusBarVisibility(true);
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		decor.setBackgroundColor(Launcher.this.getResources().getColor(
				android.R.color.black));
		decor.removeView(mFullscreenContainer);
		mFullscreenContainer = null;
		mCustomView = null;
		mCustomViewCallback.onCustomViewHidden();
	}

	public void onPageFinished(final String url,
			final CustomWebView mCurrentWebView) {
		//mCurrentWebView.loadAdSweep();
		mProgressBar.setVisibility(View.GONE);
		updateUI(mCurrentWebView);
		CheckIsBookmark(url);
		WebIconDatabase.getInstance().retainIconForPageUrl(
				mCurrentWebView.getUrl());

		// 尝试过添加5秒延时,依然无法获取加载图片的网页的快照
		catchSnapshot(mCurrentWebView, url);
	}

	private static void catchSnapshot(WebView view, String url) {

		String snapMD5 = Tools.generateMD5(url);

		Picture picture = view.capturePicture();
		int width = picture.getWidth();
		int height = picture.getHeight();

		if (width > 0 && height > 0) {
			Bitmap bmp = Bitmap.createBitmap(width / 2, width / 2,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);

			picture.draw(canvas);

			int size = Utilities
					.getIconResourcesSize(R.drawable.ic_launcher_folder);
			Bitmap mBitmap = Bitmap.createScaledBitmap(bmp, size, size, true);

			if (Tools.sdCardExist()) {
				Tools.storeInSD(Tools.toRoundCorner(mBitmap, 10), snapMD5
						+ ".png");
			}
			// 针对手动添加的书签，对url地址进行再加工
			Context context = LauncherApplication.getInstance();
			new Thread(new SnapshotUpdaterRunnable(context, view.getUrl(),
					view.getOriginalUrl(), snapMD5)).start();
		}

	}

	public void onPageStarted(String url) {

		if (mFindDialogVisible) {
			closeFindDialog();
		}
		if (StringUtil.isNull(url)) {
			mProgressBar.setVisibility(View.GONE);
			bottom_refresh.setEnabled(false);
			bottom_refresh
					.setImageResource(R.drawable.bottom_bar_refresh_cannot);
		} else {
			mProgressBar.setVisibility(View.VISIBLE);
			bottom_refresh.setEnabled(true);
			bottom_refresh.setImageResource(R.drawable.bottom_bar_refresh);
		}

		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
		mUrlEditText.setText(url);
		mUrlEditText.addTextChangedListener(mUrlTextWatcher);
		mUrlText = url;
		bottom_bar_back.setEnabled(false);
		bottom_bar_forward.setEnabled(false);

		bottom_bar_forward
				.setImageResource(R.drawable.bottom_bar_forward_cannot);
		bottom_bar_back.setImageResource(R.drawable.bottom_bar_back_cannot);

	}

	public void onMailTo(String url) {
		Intent sendMail = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(sendMail);
	}
	
	public void onPhoneTo(String url) {
		Intent sendCall = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(sendCall);
	}

	public void setHttpAuthUsernamePassword(String host, String realm,
			String username, String password) {
		mCurrentWebView.setHttpAuthUsernamePassword(host, realm, username,
				password);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// return super.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	private void setFindBarVisibility(boolean visible) {
		if (visible) {
			mFindBar.startAnimation(AnimationManager.getInstance()
					.getTopBarShowAnimation());
			mFindBar.setVisibility(View.VISIBLE);
			mFindDialogVisible = true;
		} else {
			mFindBar.startAnimation(AnimationManager.getInstance()
					.getTopBarHideAnimation());
			mFindBar.setVisibility(View.GONE);
			mFindDialogVisible = false;
		}
	}

	/**
	 * Start a runnable to update history.
	 * 
	 * @param title
	 *            The page title.
	 * @param url
	 *            The page url.
	 */
	private void startHistoryUpdaterRunnable(String title, String url,
			String originalUrl, Bitmap favicon) {

		if ((url != null) && (url.length() > 0)) {
			if (false == sp.getBoolean(Constants.PREFERENCES_INCOGNITO_MODE,
					false)) {

				Date datenow = new Date();
				long nowTime = datenow.getTime();
				
				
				if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {
					if (mWorkspace.getCurrentScreen() == 0) { 
							return ;
					}

				}

				if (!HistoryUtil.isName(Launcher.this, url)) { 
					HistoryUtil.saveName(Launcher.this, title, url, nowTime,
							favicon);
				} else {
					HistoryUtil.updateHistory(Launcher.this, title, url,
							nowTime);
				}
			}

		}
	}

	/**
	 * Update the application title.
	 */
	private void updateTitle(CustomWebView mCurrentWebView) {
		String value = mCurrentWebView.getTitle();

		if ((value != null) && (value.length() > 0)) {
			this.setTitle(value);
			urlText.setText(value);
		} else {
			clearTitle();
		}
	}

	/**
	 * Set the application title to default.
	 */
	private void clearTitle() {
		this.setTitle(getResources().getString(R.string.app_name));
	}

	private void showKeyboardForFindDialog() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mFindText, InputMethodManager.SHOW_IMPLICIT);
	}

	private void setStatusBarVisibility(boolean visible) {
		int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(
					android.R.color.black));
		}

		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}

	}

	/**
	 * Get a Drawable of the current favicon, with its size normalized relative
	 * to current screen density.
	 * 
	 * @return The normalized favicon.
	 */
	private BitmapDrawable getNormalizedFavicon() {

		BitmapDrawable favIcon = new BitmapDrawable(getResources(),
				mCurrentWebView.getFavicon());

		if (mCurrentWebView.getFavicon() != null) {
			int imageButtonSize = ApplicationUtils.getImageButtonSize(this);
			int favIconSize = ApplicationUtils.getFaviconSize(this);

			Bitmap bm = Bitmap.createBitmap(imageButtonSize, imageButtonSize,
					Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bm);

			favIcon.setBounds((imageButtonSize / 2) - (favIconSize / 2),
					(imageButtonSize / 2) - (favIconSize / 2),
					(imageButtonSize / 2) + (favIconSize / 2),
					(imageButtonSize / 2) + (favIconSize / 2));
			favIcon.draw(canvas);

			favIcon = new BitmapDrawable(getResources(), bm);
		}

		return favIcon;
	}

	private BitmapDrawable getNormalizedFavicon(CustomWebView mCurrentWebView) {

		BitmapDrawable favIcon = new BitmapDrawable(getResources(),
				mCurrentWebView.getFavicon());

		if (mCurrentWebView.getFavicon() != null) {
			int imageButtonSize = ApplicationUtils.getImageButtonSize(this);
			int favIconSize = ApplicationUtils.getFaviconSize(this);

			Bitmap bm = Bitmap.createBitmap(imageButtonSize, imageButtonSize,
					Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bm);

			favIcon.setBounds((imageButtonSize / 2) - (favIconSize / 2),
					(imageButtonSize / 2) - (favIconSize / 2),
					(imageButtonSize / 2) + (favIconSize / 2),
					(imageButtonSize / 2) + (favIconSize / 2));
			favIcon.draw(canvas);

			favIcon = new BitmapDrawable(getResources(), bm);
		}

		return favIcon;
	}

	@Override
	public void onDownloadEvent(String event, Object data) {
		if (event.equals(EventConstants.EVT_DOWNLOAD_ON_FINISHED)) {

			DownloadItem item = (DownloadItem) data;
			if (item.getErrorMessage() == null) {
				Toast.makeText(this,
						getString(R.string.Main_DownloadFinishedMsg),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, item.getErrorMessage(), Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * 首先，我们画的界面的大小可能在不同屏幕下是不同的，则需要获得屏幕大小
	 */

	/**
	 * @return屏幕的宽度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		return screenWidth;

	}

	/**
	 * 
	 * @return屏幕的高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		int screenHeight = dm.heightPixels;
		return screenHeight;

	}

	// Browser End

	@Override
	public boolean isAllAppsVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 */
	private void setScreenMode(int value) {
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE, value);
	}

	/**
	 * 设置当前屏幕亮度值 0--255，并使之生效
	 */
	private void setScreenBrightness(float value) {
		Window mWindow = getWindow();
		WindowManager.LayoutParams mParams = mWindow.getAttributes();
		float f = value / 255.0F;
		mParams.screenBrightness = f;
		mWindow.setAttributes(mParams);

		// 保存设置的屏幕亮度值
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, (int) value);
	}

	/**
	 * 手动检测更新
	 */
	public void ManuallyCheckVersion() {
		final String versionName = Tools.getVersion(Launcher.this);
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg != null) {
					VersionInfo versionInfo;
					switch (msg.what) {
					case 0:
						versionInfo = (VersionInfo) msg.obj;
						String newVersion = versionInfo.getVersion();
						if (!StringUtil.isNull(newVersion)
								&& !versionName.equals(newVersion)) {
							Intent notificationIntent = new Intent(
									Launcher.this, NewVersionActivity.class);
							notificationIntent.putExtra(Constants.VERSION_INFO,
									versionInfo);
							startActivity(notificationIntent);

						} else if (versionName.equals(newVersion)) {
							Toast.makeText(Launcher.this, "您的已经是最新版本",
									Toast.LENGTH_SHORT).show();
						}
						break;
					}
				}
			}
		};

		Thread thread = new Thread(VersionManagerUtilManually.getInstance(
				Launcher.this, handler));
		thread.start();
	}

	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow,
			String[] arr, String[] bitmapurl) {

		keywordsFlow.setMAX(arr.length);
		for (int i = 0; i < arr.length; i++) {
			String tmp = arr[i];
			keywordsFlow.feedKeyword(tmp, bitmapurl);
		}
	}

	class GetKey_Thread implements Runnable {
		public void run() {
			try {
				String poststring = Tools.getPoststring(Launcher.this);

				String json = URLUtil.getInstance().getJson(
						ConstantsUrl.GETKEY_WORD_SEARCH, poststring);

				if (StringUtil.isNull(json)) {
					Message msg = new Message();
					msg.what = 0;
					key_handler.sendMessage(msg);
				}

				JSONObject jsonObject = new JSONObject(json);

				if ("fail".equals(jsonObject.optString("flag"))) {
					Message msg = new Message();
					msg.what = 0;
					key_handler.sendMessage(msg);

				} else {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = jsonObject.optString("keyWrodList");
					key_handler.sendMessage(msg);

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	};

	Handler key_handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
				case 0:

					break;

				case 1:
					try {
						JSONArray jsonObject = new JSONArray((String) msg.obj);
						String[] keywords = new String[jsonObject.length()];
						final String[] URLS = new String[jsonObject.length()];
						final String[] bitmapURLS = new String[jsonObject
								.length()];
						for (int i = 0; i < jsonObject.length(); i++) {
							keywords[i] = (String) jsonObject.getJSONObject(i)
									.get("keyr");
							URLS[i] = (String) jsonObject.getJSONObject(i).get(
									"url");
							bitmapURLS[i] = (String) jsonObject
									.getJSONObject(i).get("pic");

						}

						keywordsFlow.setDuration(800l);
						// 添加
						feedKeywordsFlow(keywordsFlow, keywords, bitmapURLS);
						keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);

						LinearLayout[] foTextView = keywordsFlow.getTextView();
						for (int i = 0; i < jsonObject.length(); i++) {
							final int j = i;
							if (!bitmapURLS[i].equals("")) {
								final ImageView keyword_iamge = (ImageView) foTextView[i]
										.findViewById(R.id.keyword_image);
								keyword_iamge.setVisibility(View.VISIBLE);

								Handler handler = new Handler() {
									@Override
									public void handleMessage(Message msg) {
										if (msg != null) {
											AdInfo info = null;
											switch (msg.what) {
											case 1:
												info = (AdInfo) msg.obj;
												if (info != null) {
													Bitmap bitmap = info
															.getBitmap();
													if (bitmap != null) {
														keyword_iamge
																.setImageBitmap(bitmap);
													}
												}
												break;
											}
										}
									}
								};

								Thread thread = new Thread(AdUtil.getInstance(
										Launcher.this, handler, bitmapURLS[i]));
								thread.start();
							}
							foTextView[i]
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											addTab(/* "http://" + */URLS[j]);
											top_bar_input
													.setVisibility(View.GONE);
											top_bar_main
													.setVisibility(View.VISIBLE);
											mHomespace
													.setVisibility(View.VISIBLE);
										}
									});

						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;

				}
			}
		}
	};
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
				case 0:
					// fail
					break;
				case 1:

					break;
				case 2:

					break;

				case 3:

					break;

				}
			}
		}
	};
	
	class GetSalenet_Thread implements Runnable {
		public void run() {
			try {
				String poststring = Tools.getPoststring(Launcher.this);
				String json = URLUtil.getInstance().getJson(
						ConstantsUrl.getSALENET, poststring);

				Log.v("LS","每次进的统计 json=="+json);
				
				if (StringUtil.isNull(json)) {
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				} else {
					JSONObject jsonObject = new JSONObject(json);
					if ("success".equals(jsonObject.optString("status"))) {
						Message msg = new Message();
						msg.obj = jsonObject.optString("ret");
						msg.what = 3;
						handler.sendMessage(msg);

					} else {
						Message msg = new Message();
						msg.obj = jsonObject.optString("ret");
						msg.what = 2;
						handler.sendMessage(msg);

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	};

	private void checkDefaultBrowser() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		Log.d("H",
				"onCreate str=" + str + " last="
						+ sp.getString(Constants.lastUpdateTime, "2014-01-01"));
		try {
			if (Tools.ConverToDate(
					(sp.getString(Constants.lastUpdateTime, "2014-01-01")),
					"yyyy-MM-dd").before(Tools.ConverToDate(str, "yyyy-MM-dd"))) {
				Intent i = (new Intent(Intent.ACTION_VIEW,
						Uri.parse(SettingActivity.setDefaultFail)));
				PackageManager pm = getPackageManager();
				ResolveInfo mInfo = pm.resolveActivity(i, 0);

				try {
					PackageInfo info = getPackageManager().getPackageInfo(
							getPackageName(), 0);
					Log.d("H", "onCreate " + info.packageName + "  package:"
							+ mInfo.activityInfo.packageName);

					if (!mInfo.activityInfo.packageName
							.equals(info.packageName)) {

						sp.edit().putString(Constants.lastUpdateTime, str)
								.commit();
						set_default_browser.setVisibility(View.VISIBLE);

					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class PointerXY {
		public static int x;
		public static int y;

		public static int getX() {
			return x;
		}

		public static int getY() {
			return y;
		}

	}

	private boolean ContextMenuPopWindow(View v) {

		final HitTestResult result = ((WebView) v).getHitTestResult();
		if (null == result)
			return false;

		int type = result.getType();
		if (type == WebView.HitTestResult.UNKNOWN_TYPE)
			return false;

		if (type == WebView.HitTestResult.EDIT_TEXT_TYPE) {
			// let TextView handles context menu
			return true;
		}

		final ContextMenuPopWindow contextMenuPopWindow = new ContextMenuPopWindow(
				Launcher.this);

		contextMenuPopWindow.showAtLocation(v, Gravity.TOP | Gravity.LEFT,
				PointerXY.getX(), PointerXY.getY() + 10);
		ListView pop_listview = (ListView) contextMenuPopWindow
				.getView(R.id.pop_listview);

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		Map<String, String> map = new HashMap<String, String>();
		map.put("text", "打开链接");
		list.add(map);

		map = new HashMap<String, String>();
		map.put("text", "分享链接");
		list.add(map);

		map = new HashMap<String, String>();
		map.put("text", "复制链接");
		list.add(map);

		SimpleAdapter adapter = new SimpleAdapter(Launcher.this, list,
				R.layout.webview_longclick_window_item,
				new String[] { "text" }, new int[] { R.id.item });
		pop_listview.setAdapter(adapter);
		pop_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				HashMap<String, String> map = (HashMap<String, String>) listView
						.getItemAtPosition(position);
				String text = map.get("text");
				if ("打开链接".equals(text)) {
					addTabAndThumbnail(result.getExtra());
				} else if ("分享链接".equals(text)) {
					// 分享链接
				} else if ("复制链接".equals(text)) {
					ApplicationUtils.copyTextToClipboard(Launcher.this,
							result.getExtra(),
							getString(R.string.Commons_UrlCopyToastMessage));
				} else if ("保存图片".equals(text)) {
					// 保存图片
					new DownloadAppIn(result.getExtra(), Launcher.this);

				} else if ("查看图片".equals(text)) {
					WebViewspace mmWebViewspace = (WebViewspace) mTabspace
							.getChildAt(mCurrentIndex);
					View mchildView = mmWebViewspace.getChildAt(0);
					CustomWebView mmCustomWebView = (CustomWebView) mchildView
							.findViewById(R.id.webview);
					mmCustomWebView.loadUrl(result.getExtra());
					// addTab(result.getExtra());
				} else if ("分享图片".equals(text)) {
					// 分享图片
				}
				contextMenuPopWindow.dismiss();
			}

		});
		switch (type) {
		case WebView.HitTestResult.PHONE_TYPE:
			// 处理拨号
			break;
		case WebView.HitTestResult.EMAIL_TYPE:
			// 处理Email
			break;
		case WebView.HitTestResult.GEO_TYPE:
			// TODO
			break;
		case WebView.HitTestResult.SRC_ANCHOR_TYPE:
			// 超链接
			break;
		case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE:
		case WebView.HitTestResult.IMAGE_TYPE:
			map = new HashMap<String, String>();
			map.put("text", "保存图片");
			list.add(map);

			map = new HashMap<String, String>();
			map.put("text", "查看图片");
			list.add(map);

			map = new HashMap<String, String>();
			map.put("text", "分享图片");
			list.add(map);
			break;
		default:
			break;
		}
		return true;

	}

	/**
	 * Initialize a newly created initOneWebView.
	 */
	private void initOneWebView() {
		One_webView.setOnTouchListener(this);

		One_webView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return ContextMenuPopWindow(v);
			}

		});

		One_webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				doDownloadStart(url, userAgent, contentDisposition, mimetype,
						contentLength);
			}

		});

		final Activity activity = this;
		One_webView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				Launcher.this.startActivityForResult(Intent.createChooser(i,
						Launcher.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				Launcher.this.startActivityForResult(Intent.createChooser(i,
						Launcher.this
								.getString(R.string.Main_FileChooserPrompt)),
						OPEN_FILE_CHOOSER_ACTIVITY);
			}

			@Override
			public Bitmap getDefaultVideoPoster() {
				if (mDefaultVideoPoster == null) {
					mDefaultVideoPoster = BitmapFactory.decodeResource(
							Launcher.this.getResources(),
							R.drawable.default_video_poster);
				}

				return mDefaultVideoPoster;
			}

			@Override
			public View getVideoLoadingProgressView() {
				if (mVideoProgressView == null) {
					LayoutInflater inflater = LayoutInflater
							.from(Launcher.this);
					mVideoProgressView = inflater.inflate(
							R.layout.video_loading_progress, null);
				}
				return mVideoProgressView;
			}

			public void onShowCustomView(View view,
					WebChromeClient.CustomViewCallback callback) {
				showCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				hideCustomView();
			}

			@Override
			public void onProgressChanged(final WebView view, int newProgress) {
				if(newProgress<30){
					addToLauncher.setBackgroundResource(R.drawable.ic_btn_cancel);
					addToLauncher.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							view.stopLoading();
							addToLauncher.setBackgroundResource(R.drawable.ic_btn_account);
							addToLauncher.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) { }
							});
						}
					});
				}
				
			
				if(newProgress==100){
					addToLauncher.setBackgroundResource(R.drawable.ic_btn_account);
					addToLauncher.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) { }
					});
				}
				
				((CustomWebView) view).setProgress(newProgress);
				mProgressBar.setProgress(One_webView.getProgress());
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				new Thread(new FaviconUpdaterRunnable(Launcher.this, view
						.getUrl(), view.getOriginalUrl(), icon)).start();

				super.onReceivedIcon(view, icon);
			}

			@Override
			public void onCloseWindow(WebView window) {
				Message msg = Message.obtain();
				msg.what = 3;
				msg.arg1 = window.getId();
				mWindowHandler.sendMessage(msg);
				super.onCloseWindow(window);
			}

			@Override
			public boolean onCreateWindow(final WebView view, boolean isDialog,
					boolean isUserGesture, final Message resultMsg) {

				if (isUserGesture) {

					final WebView interim = new WebView(Launcher.this);

					interim.setWebViewClient(new WebViewClient() {
						public boolean shouldOverrideUrlLoading(WebView view,
								String url) {
							view.loadUrl(url);
							return true;
						}
					});

					WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
					transport.setWebView(interim);
					resultMsg.sendToTarget();

					mWindowHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							view.loadUrl(interim.getUrl());
						}
					}, 1000);

				}
				return false;
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				setTitle(title);

				startHistoryUpdaterRunnable(title, One_webView.getUrl(),
						One_webView.getOriginalUrl(), One_webView.getFavicon());

				super.onReceivedTitle(view, title);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final JsResult result) {
				// 确定弹框
				dialogUtil = new DialogUtil.Builder(Launcher.this)
						.setTitleText("温馨提醒").setText(message)
						.setPositiveButton("确定", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.confirm();
							}
						}).create();
				dialogUtil.show();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				// 确定取消弹框
				dialogUtil = new DialogUtil.Builder(Launcher.this)
						.setTitleText("温馨提醒").setText(message)
						.setPositiveButton("确定", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.confirm();
							}
						}).setNegativeButton("取消", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
								result.cancel();
							}
						}).create();
				dialogUtil.show();
				return true;
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {

				dialogeditUtil = new DialogEditUtil.Builder(Launcher.this)
						.setTitleText(message)
						.setPositiveButton(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								String value = dialogeditUtil.GetEditText();
								if (StringUtil.isNull(value)) {
									Toast.makeText(Launcher.this, "请输入内容",
											Toast.LENGTH_SHORT).show();
									return;
								}
								dialogeditUtil.dismiss();
								result.confirm(value);
							}
						}).setNegativeButton(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialogeditUtil.dismiss();
							}
						}).create();
				dialogeditUtil.show();

				return true;

			}

		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// setContentView(R.layout.launcher_main);
		int rotation = this.getWindowManager().getDefaultDisplay()
				.getRotation();

		tabs_gallery.setVisibility(View.GONE);

		// TODO 横竖屏切换重新加载布局
		tabs_gallery = (RelativeLayout) findViewById(R.id.tabs_gallery);
		tabs_gallery.removeView(mRecentsPanelView);
		mRecentsPanelView = (RecentsPanelView) LayoutInflater.from(this)
				.inflate(R.layout.status_bar_recent_panel, null);

		mRecentsPanelView.setPadding(0, 0, 0, 70);// TODO
		tabs_gallery.addView(mRecentsPanelView);
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			initBottomBar();
		} else {
			initBottomBarLand();
		}

		mWorkspace.requestLayout();
		mHomespace.requestLayout();

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		Log.i("Launcher", "Launcher Height->>>" + display.getHeight());
		Log.i("Launcher", "Launcher Width->>>" + display.getWidth());
		// CURRENT_SCREEN_WORKSPACE = mWorkspace.getCurrentScreen();
		Log.i("CurrentScreen", "CurrentScreen ->>>" + CURRENT_SCREEN_WORKSPACE);
		mWorkspace.moveToDefaultScreen(mWorkspace.getCurrentScreen());
		mHomespace.moveToDefaultScreen(mHomespace.getCurrentScreen());
		super.onConfigurationChanged(newConfig);
	}

	private boolean CheckIsBookmark(String url) {
		if (StringUtil.isNull(url)) {
			addbookmark.setImageResource(R.drawable.hai_detail_2);
			return false;
		}
		UUID uuid = UUID.nameUUIDFromBytes(url.getBytes());
		if (BookmarksUtil.findUUID_isBookmark(Launcher.this, uuid + "")) {
			addbookmark.setImageResource(R.drawable.hai_detail_1);
			return true;
		} else {
			addbookmark.setImageResource(R.drawable.hai_detail_2);
			return false;
		}
	}

	/**
	 * 显示添加书签列表
	 */
	private void showAddBookmark() {
		boolean isBookmark;
		if (mWorkspace.getCurrentScreen() == 0) {
			isBookmark = CheckIsBookmark(One_webView.getUrl().toString());
		} else {
			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			isBookmark = CheckIsBookmark(mCustomWebView.getUrl().toString());
		}

		final boolean isbookmark = isBookmark;
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.top_bar_addbookmark_pop,
				null);
		GridView search_engine_pop_gridview = (GridView) view
				.findViewById(R.id.search_engine_pop_gridview);
		search_engine_pop_gridview.setNumColumns(1);
		String[] itemSub = { "添加书签", "发送至桌面", "进入书签/历史" };
		if (isBookmark) {
			itemSub[0] = "删除书签";
		} else {
			itemSub[0] = "添加书签";
		}

		final int[] imageSub = { R.drawable.search_baidu,
				R.drawable.search_guge, R.drawable.earch_dangdang };
		ArrayList<HashMap<String, Object>> Itemload = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < imageSub.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("ItemImage", imageSub[i]);
			map.put("ItemText", itemSub[i]);
			Itemload.add(map);
		}

		search_engine_pop_gridview
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						if (arg2 == 0) {
							if (isbookmark) {
								if (mWorkspace.getCurrentScreen() == 0) {
									BookmarksUtil.deleteStockBookmarkByUUID(
											Launcher.this,
											UUID.nameUUIDFromBytes(One_webView
													.getUrl().toString()
													.getBytes())
													+ "");
								} else {
									WebViewspace mWebViewspace = (WebViewspace) mTabspace
											.getChildAt(mCurrentIndex);
									View childView = mWebViewspace
											.getChildAt(0);
									CustomWebView mCustomWebView = (CustomWebView) childView
											.findViewById(R.id.webview);
									BookmarksUtil.deleteStockBookmarkByUUID(
											Launcher.this,
											UUID.nameUUIDFromBytes(mCustomWebView
													.getUrl().toString()
													.getBytes())
													+ "");
								}
								addbookmark
										.setImageResource(R.drawable.hai_detail_2);
								Toast.makeText(
										Launcher.this,
										R.string.HistoryListActivity_BookmarkRemoved,
										Toast.LENGTH_SHORT).show();

							} else {

								if (mWorkspace.getCurrentScreen() == 0) {
									if (UrlUtils.CheckUrl(One_webView.getUrl()
											.toString())) {
										String snapshot = Tools
												.generateMD5(One_webView
														.getUrl());
										BookmarksUtil.setAsBookmark(
												Launcher.this,
												One_webView.getId(),
												One_webView.getTitle()
														.toString(),
												One_webView.getUrl().toString(),
												true, One_webView.getFavicon(),
												snapshot);
										Toast.makeText(Launcher.this, "已添加书签",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(Launcher.this,
												"当前网址不符合规范", Toast.LENGTH_SHORT)
												.show();
									}
								} else {
									WebViewspace mWebViewspace = (WebViewspace) mTabspace
											.getChildAt(mCurrentIndex);
									View childView = mWebViewspace
											.getChildAt(0);
									CustomWebView mCustomWebView = (CustomWebView) childView
											.findViewById(R.id.webview);
									if (UrlUtils.CheckUrl(mCustomWebView
											.getUrl().toString())) {
										String snapshot = Tools
												.generateMD5(mCustomWebView
														.getUrl().toString());
										BookmarksUtil.setAsBookmark(
												Launcher.this, mCustomWebView
														.getId(),
												mCustomWebView.getTitle()
														.toString(),
												mCustomWebView.getUrl()
														.toString(), true,
												mCustomWebView.getFavicon(),
												snapshot);
										Toast.makeText(Launcher.this, "已添加书签",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(Launcher.this,
												"当前网址不符合规范", Toast.LENGTH_SHORT)
												.show();
									}
								}
								addbookmark
										.setImageResource(R.drawable.hai_detail_1);
							}

						} else if (arg2 == 1) {
							if (mWorkspace.getCurrentScreen() == 0) {
								Tools.shortcutCreate(Launcher.this, One_webView
										.getTitle().toString(), One_webView
										.getFavicon(), One_webView.getUrl()
										.toString());
								Toast.makeText(Launcher.this, "已发送到桌面",
										Toast.LENGTH_SHORT).show();
							} else {
								WebViewspace mWebViewspace = (WebViewspace) mTabspace
										.getChildAt(mCurrentIndex);
								View childView = mWebViewspace.getChildAt(0);
								CustomWebView mCustomWebView = (CustomWebView) childView
										.findViewById(R.id.webview);
								Tools.shortcutCreate(Launcher.this,
										mCustomWebView.getTitle().toString(),
										mCustomWebView.getFavicon(),
										mCustomWebView.getUrl().toString());
								Toast.makeText(Launcher.this, "已发送到桌面",
										Toast.LENGTH_SHORT).show();
							}

						} else if (arg3 == 2) {
							Intent i = new Intent(Launcher.this,
									BookmarksHistoryActivity.class);
							startActivityForResult(i,
									RESULT_OPEN_NEW_BOOKMARKS_HISTORY);
						}

						ShowBookmark.dismiss();
					}
				});

		SimpleAdapter SA = new SimpleAdapter(this, Itemload,
				R.layout.gridview_addbookmark_item,
				new String[] { "ItemText" }, new int[] { R.id.ItemText });

		search_engine_pop_gridview.setAdapter(SA);

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		// 创建一个PopuWidow对象
		ShowBookmark = new PopupWindow(view, 220, top_bar_main.getHeight() * 2);

		// 使其聚集
		ShowBookmark.setFocusable(true);

		// 设置允许在外点击消失
		ShowBookmark.setOutsideTouchable(true);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		ShowBookmark.setBackgroundDrawable(new BitmapDrawable());

		// 设置layout在PopupWindow中显示的位置
		ShowBookmark.showAsDropDown(addbookmark, -5, 10);

	}

	private void ShowOrHideFloatWindow(int show, String toolmenu) {

		Intent intent = new Intent(Launcher.this, FloatWindowService.class);
		intent.putExtra(FloatWindowService.OPERATION, show);
		intent.putExtra(FloatWindowService.TYPE, toolmenu);
		startService(intent);
	}

	static class WindowHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				getCurrentTab().loadUrl(mUrlEditText.getText().toString());
				break;
			}
			case 2: {
				// deleteTab(msg.arg1);
				break;
			}
			case 3: {
				getCurrentTab().invalidate();
				break;
			}
			}
			super.handleMessage(msg);
		}

	}

	private static CustomWebView getCurrentTab() {
		WebViewspace mWebViewspace = (WebViewspace) mTabspace
				.getChildAt(mCurrentIndex);
		View childView = mWebViewspace.getChildAt(0);
		CustomWebView mCurrentWebView = null;
		if (childView != null) {
			mCurrentWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
		}
		return mCurrentWebView;
	}

	public long getData() {
		// TrafficStats类根据应用的UID获取到流量的相关数据
		try {
			PackageManager packageManager = getPackageManager();
			ApplicationInfo ai = packageManager.getApplicationInfo(
					"com.qing.browser", PackageManager.GET_ACTIVITIES);

			long recv = TrafficStats.getUidRxBytes(ai.uid);
			long sent = TrafficStats.getUidTxBytes(ai.uid);

			if (recv < 0 || sent < 0) {
				return 0;
			}
			return recv + sent;

		} catch (NameNotFoundException e) {
			Log.e("H", "关机监听  " + e.toString());
			return 0;
		}
	}

	public void saveLiuLiangData() {

		int NetworkStatus = sp.getInt(LiuLiangTongji.NetworkStatus,
				LiuLiangTongji.NetworkStatus_Not);
		switch (NetworkStatus) {
		case ConnectivityManager.TYPE_MOBILE:
			// 上次是数据连接

			Log.d("H",
					"退出 本次 3G使用流量 w="
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp)
							+ " getData()="
							+ LiuLiangTongji.getDataStr(getData()));

			if (0 != LiuLiangTongji.temp) {
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}

			Log.d("H",
					"退出 本次 3G使用流量"
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp));
			sp.edit()
					.putLong(
							LiuLiangTongji.G_Today,
							sp.getLong(LiuLiangTongji.G_Today, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.G_Month,
							sp.getLong(LiuLiangTongji.G_Month, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.G_Total,
							sp.getLong(LiuLiangTongji.G_Total, 0)
									+ LiuLiangTongji.temp).commit();
			LiuLiangTongji.temp = getData();
			break;

		case ConnectivityManager.TYPE_WIFI:
			// 上次是wifi连接

			Log.d("H",
					"退出 本次 WIFI使用流量 w="
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp)
							+ " getData()="
							+ LiuLiangTongji.getDataStr(getData()));
			if (0 != LiuLiangTongji.temp) {
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"退出 本次 WIFI使用流量"
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp));
			sp.edit()
					.putLong(
							LiuLiangTongji.W_Today,
							sp.getLong(LiuLiangTongji.W_Today, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.W_Month,
							sp.getLong(LiuLiangTongji.W_Month, 0)
									+ LiuLiangTongji.temp).commit();
			sp.edit()
					.putLong(
							LiuLiangTongji.W_Total,
							sp.getLong(LiuLiangTongji.W_Total, 0)
									+ LiuLiangTongji.temp).commit();
			LiuLiangTongji.temp = getData();
			break;

		default:
			// 上次未连接
			LiuLiangTongji.temp = getData();

		}
	}

	private void add_AD_Tab() {
		HashMap<String, Object> bitmaphashmap = new HashMap<String, Object>();

		Bitmap bitmap = getViewBitmap(mHomespace);

		bitmaphashmap.put("bitmap", bitmap);

		HashMap<String, Object> hashmap = new HashMap<String, Object>();

		// 存储当前tab显示状态 workspace false tabspace true

		if (mTabspace.getChildVisibility(mCurrentIndex)) {
			hashmap.put("isWebview", true);
			WebViewspace mWebViewspace = (WebViewspace) mTabspace
					.getChildAt(mCurrentIndex);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			// mCustomWebView.getTitle()
			bitmaphashmap.put("title", mCustomWebView.getTitle());
		} else {
			hashmap.put("isWebview", false);
		}

		if (CURRENT_SCREEN_WORKSPACE == mHomespace.getCurrentScreen()) {
			hashmap.put("isHome", mWorkspace.getCurrentScreen());
			bitmaphashmap.put("title", "主页");
		} else {
			hashmap.put("isHome", CURRENT_SCREEN_WORKSPACE_NOT);
		}

		if (mWebViewBitmaps.size() > mCurrentIndex) {
			mWebViewBitmaps.set(mCurrentIndex, bitmaphashmap);
		} else {
			mWebViewBitmaps.add(mCurrentIndex, bitmaphashmap);
		}

		if (mWebViewStates.size() > mCurrentIndex) {
			mWebViewStates.set(mCurrentIndex, hashmap);
		} else {
			mWebViewStates.add(mCurrentIndex, hashmap);
		}

		// 设置Tabspace索引值

		mCurrentIndex = mTabspace.getChildCount();
		add_ad_tab_index = mTabspace.getChildCount();
		mUrlEditText.removeTextChangedListener(mUrlTextWatcher);
		mUrlEditText.setText("");
		mUrlEditText.addTextChangedListener(mUrlTextWatcher);
		urlText.setText("");

		initAD_Show_WebViewspace();

		updateBottomTabsNum();
		// mHomespace.setCurrentScreen(CURRENT_SCREEN_WORKSPACE);
	}

	private void initAD_Show_WebViewspace() {
		WebViewspace mWebViewspace = (WebViewspace) mInflater.inflate(
				R.layout.webviewspace, mTabspace, false);
		mWebViewspace.setOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int view) {
				mWebViewCurrentIndex = view;
			}
		});

		synchronized (mTabspace) {
			// mTabspace.addAndHideInSpace(mWebViewspace, mCurrentIndex);
			mTabspace.addView(mWebViewspace, mCurrentIndex);
			// mTabspace.setVisibility(View.GONE);
			// mHomespace.setWebViewspace(mWebViewspace);
		}
	}

	// 创建Handler对象
	Handler AD_TAB_handler = new Handler();
	// 新建一个线程对象
	Runnable updateThread = new Runnable() {
		// 将要执行的操作写在线程对象的run方法当中
		@Override
		public void run() {
			waitingTime--;

			if (waitingTime == 0) {
				AD_TAB_handler.removeCallbacks(updateThread);
				waitingTime = 3;
				if (CURRENT_SCREEN_TABSPACE == mHomespace.getCurrentScreen()) {
					add_AD_Tab();
					WebViewspace mWebViewspace = (WebViewspace) mTabspace
							.getChildAt(add_ad_tab_index);
					createCurrentWebView(LINKURL, mWebViewspace);
				}
			} else {
				AD_TAB_handler.postDelayed(updateThread, 1000);
			}
		}
	};

	public Handler AD_handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case Constants.AD_HENGTIAO_FLAG:
				// 横条底部广告
				if (ADview != null) {
					JSONObject jsonObj;
					String picURL = "";
					String APKURL = "";
					String comtext = "";
					try {
						jsonObj = new JSONObject((String) msg.obj);
						picURL = jsonObj.optString("pic");
						APKURL = jsonObj.optString("url");
						comtext = jsonObj.optString("comtext");
					} catch (JSONException e) {
						e.printStackTrace();
						return;
					}

					final Button ad_close = (Button) ADview
							.findViewById(R.id.ad_close);
					final RelativeLayout bottom_ad = (RelativeLayout) ADview
							.findViewById(R.id.bottom_ad);
					final ImageView imageView1 = (ImageView) ADview
							.findViewById(R.id.imageView1);
					final TextView textView2 = (TextView) ADview
							.findViewById(R.id.textView2);
					final TextView textView1 = (TextView) ADview
							.findViewById(R.id.textView1);
					textView2.setText(comtext);
					bottom_ad.setVisibility(View.VISIBLE);
					final String APKurl = APKURL;
					bottom_ad.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							openADTab(APKurl);
							bottom_ad.setVisibility(View.GONE);
						}
					});
					textView1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							openADTab(APKurl);
							bottom_ad.setVisibility(View.GONE);
						}
					});
					ad_close.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							bottom_ad.setVisibility(View.GONE);
						}
					});
					final String picName;
					picName = picURL.substring(picURL.lastIndexOf("/") + 1);
					if (Tools.sdCardExist()) {
						if (getADImage(picName) != null) {
							FileInputStream fis;
							try {
								fis = new FileInputStream(getADImage(picName));
								Bitmap bitmap = BitmapFactory.decodeStream(fis);
								if (bitmap != null) {
									imageView1.setImageBitmap(bitmap);
									return;
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}

						}
					}

					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							if (msg != null) {
								AdInfo info = null;
								switch (msg.what) {
								case 1:
									info = (AdInfo) msg.obj;
									if (info != null) {
										Bitmap bitmap = info.getBitmap();
										if (bitmap != null) {
											imageView1.setImageBitmap(bitmap);
											try {
												saveFile(bitmap, picName);
											} catch (IOException e) {
												e.printStackTrace();
											}
										} else {
										}
									}
									break;
								}
							}
						}
					};

					Thread thread = new Thread(AdUtil.getInstance(
							Launcher.this, handler, picURL));
					thread.start();

				}
				break;
			case Constants.AD_CHAPING_FLAG:
				// 插屏广告

				JSONObject jsonObj;
				String picURL = "";
				String APKURL = "";
				String comtext = "";
				if (ADview == null)
					return;
				try {
					jsonObj = new JSONObject((String) msg.obj);
					picURL = jsonObj.optString("pic");
					APKURL = jsonObj.optString("url");
					comtext = jsonObj.optString("comtext");
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
				final String APKurl = APKURL;

				final LinearLayout ADBG = (LinearLayout) ADview
						.findViewById(R.id.chapinbg);

				final ImageView middle_image = (ImageView) ADview
						.findViewById(R.id.middle_image);
				final Button middle_close = (Button) ADview
						.findViewById(R.id.middle_close);
				final Button ad_dakai = (Button) ADview
						.findViewById(R.id.ad_dakai);
				middle_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ADBG.setVisibility(View.GONE);
					}
				});

				ad_dakai.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openADTab(APKurl);
						ADBG.setVisibility(View.GONE);
					}
				});

				ADBG.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openADTab(APKurl);
						ADBG.setVisibility(View.GONE);
					}
				});

				final String picName;
				picName = picURL.substring(picURL.lastIndexOf("/") + 1);
				if (Tools.sdCardExist()) {
					if (getADImage(picName) != null) {
						FileInputStream fis;
						try {
							fis = new FileInputStream(getADImage(picName));
							Bitmap bitmap = BitmapFactory.decodeStream(fis);
							if (bitmap != null) {
								ADBG.setVisibility(View.VISIBLE);
								middle_image.setImageBitmap(bitmap);
								return;
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

					}
				}

				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if (msg != null) {
							AdInfo info = null;
							switch (msg.what) {
							case 1:
								info = (AdInfo) msg.obj;
								if (info != null) {
									Bitmap bitmap = info.getBitmap();
									if (bitmap != null) {
										ADBG.setVisibility(View.VISIBLE);
										middle_image.setImageBitmap(bitmap);
										try {
											saveFile(bitmap, picName);
										} catch (IOException e) {
											e.printStackTrace();
										}
									} else {

									}
								}
								break;
							}
						}
					}
				};

				Thread thread = new Thread(AdUtil.getInstance(Launcher.this,
						handler, picURL));
				thread.start();

				break;
			case Constants.AD_XINZENG_FLAG:
				// 增屏广告

				break;

			case 0:
				// 获取错误
				break;

			}

		}

	};
	Handler ADD_TAB_handler = new Handler();

	private void openADTab(final String url) {
		addTabAndThumbnail("");

		ADD_TAB_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getCurrentTab().setOpenWindownFlag(1);
				getCurrentTab().loadUrl(url);
			}
		}, 500);

	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		if (sdDir == null)
			return null;
		return sdDir.toString();
	}

	/**
	 * 保存广告图片
	 */
	public boolean saveFile(Bitmap bm, String fileName) throws IOException {

		if (getSDPath() == null)
			return false;

		File dirFile1 = new File(getSDPath() + "/Qing/");
		if (!dirFile1.exists()) {
			dirFile1.mkdir();
		}

		String path = getSDPath() + "/Qing/adicon/";
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(path + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
		bos.flush();
		bos.close();
		return true;
	}

	/** 判断SD卡中有没有广告资源图片 */
	private String getADImage(String imagename) {

		File dirFile1 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/");
		if (!dirFile1.exists()) {
			dirFile1.mkdir();
		}
		File dirFile2 = new File(ErWeiMaChaKanActivity.getSDPath()
				+ "/Qing/adicon/");
		if (!dirFile2.exists()) {
			dirFile2.mkdir();
		}

		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/Qing/adicon";

		File mFile = new File(imagePath);
		File[] files = mFile.listFiles();

		/* 将所有文件存入ArrayList中 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.getName().equals(imagename))
				return file.getPath();
		}
		return null;
	}

	public void setAlarm() {

		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
		PendingIntent setPendIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// 1分钟后发送广播，然后每隔1小时秒重复发广播
		int triggerAtTime = (int) (System.currentTimeMillis() + 60 * 1000);
		int interval = 60 * 60 * 1000;

		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, interval, setPendIntent);

	}

	private void checkSnapShotClean() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		try {
			if (Tools.ConverToDate(
					(sp.getString(Constants.snaplastUpdateTime, "2014-01-01")),
					"yyyy-MM-dd").before(Tools.ConverToDate(str, "yyyy-MM-dd"))) {
				new Thread(new SnapShotClean_Thread()).start();
				// 保存日期
				sp.edit().putString(Constants.snaplastUpdateTime, str).commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class SnapShotClean_Thread implements Runnable {
		public void run() {
			List<String> filesList = IOUtils.getSnapshotFileList();
			// 读取数据库中数据
			List<String> launcherList = getSnapshotDbList(Launcher.this);

			List<String> bookmarkList = getBookmarkDbList(Launcher.this);
			List<String> historyList = getHistoryDbList(Launcher.this);

			for (int i = 0; i < filesList.size(); i++) {
				Log.i("filesList", "filesList[" + i + "]=" + filesList.get(i));
				if ((!launcherList.contains(filesList.get(i)))
						&& (!bookmarkList.contains(filesList.get(i)))
						&& (!historyList.contains(filesList.get(i)))) {
					File folder = IOUtils.getIconFolder();
					File file = new File(folder.getAbsoluteFile() + "/"
							+ filesList.get(i));
					Log.i("File", "File------>" + file.getAbsolutePath());
					file.delete();
				}
			}
		}

	};

	private List<String> getSnapshotDbList(Context context) {
		final ContentResolver contentResolver = context.getContentResolver();

		final Cursor c = contentResolver.query(
				LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION, null,
				null, null, null);
		List<String> result = new ArrayList<String>();
		while (c.moveToNext()) {
			try {
				final int iconResourceIndex = c
						.getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE);
				String fileName = c.getString(iconResourceIndex) + ".png";
				result.add(fileName);
			} catch (Exception e) {

			}
		}
		c.close();
		return result;
	}

	private List<String> getBookmarkDbList(Context context) {
		final ContentResolver contentResolver = context.getContentResolver();

		final Cursor c = contentResolver.query(BookmarksProvider.CONTENT_URI,
				null, null, null, null);
		List<String> result = new ArrayList<String>();
		while (c.moveToNext()) {
			try {
				final int snapshotIndex = c
						.getColumnIndexOrThrow(BookmarksUtil.BookmarkColumns_SNAPSHOT);
				String fileName = c.getString(snapshotIndex) + ".png";
				result.add(fileName);
			} catch (Exception e) {

			}
		}
		c.close();
		return result;
	}

	private List<String> getHistoryDbList(Context context) {
		final ContentResolver contentResolver = context.getContentResolver();

		final Cursor c = contentResolver.query(HistoryProvider.CONTENT_URI,
				null, null, null, null);
		List<String> result = new ArrayList<String>();
		while (c.moveToNext()) {
			try {
				final int snapshotIndex = c
						.getColumnIndexOrThrow(HistoryUtil.HistoryColumns_SNAPSHOT);
				String fileName = c.getString(snapshotIndex) + ".png";
				result.add(fileName);
			} catch (Exception e) {

			}
		}
		c.close();
		return result;
	}

}
