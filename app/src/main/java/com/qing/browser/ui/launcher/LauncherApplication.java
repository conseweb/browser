package com.qing.browser.ui.launcher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.qing.browser.R;
import com.qing.browser.utils.CrashHandler;
import com.qing.browser.utils.Tools;

public class LauncherApplication extends Application {
	public LauncherModel mModel;
	private static Cookie mCookie;
	private static CookieStore shopcookiestore;
	private static CookieStore logincookiestore;
	private static LauncherApplication mInstance = null;

	public static final int CITY_LIST_SCUESS = 100;
	private HashMap<String, Integer> mWeatherIcon;// 天气图标
	public static int mNetWorkState;
	private NotificationManager mNotificationManager;

	private long exitTime = System.currentTimeMillis();

	public static CookieStore getShopCookie() {
		return shopcookiestore;
	}

	public static void setShopCookie(CookieStore cks) {
		shopcookiestore = cks;
		Log.d("H", "setShopCookie");
	}
	

	public static CookieStore getLoginCookie() {
		return logincookiestore;
	}

	public static void setLoginCookie(CookieStore logincks) {
		logincookiestore = logincks;
		Log.d("H", "setLoginCookie");
	}

	public boolean CookieforWebview(Context context, String url, CookieStore cookiestore) {
		boolean flag = false;
		if (cookiestore != null) {
			List<Cookie> webcookies = cookiestore.getCookies();
			for (int i = webcookies.size(); i > 0; i--) {
				Cookie cookie = webcookies.get(i - 1);
				if (cookie.getName().equalsIgnoreCase("jsessionid")) {
					mCookie = cookie;
					Log.i("H", "CookieforWebview mCookie=" + mCookie);
				}
			}
		} else {
			Log.e("H", "CookieforWebview  cookiestore is null! please check!");
			return flag;
		}
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.getCookie(url);
		Cookie sessionCookie = mCookie;
		if (sessionCookie != null) {

			String cookieString = sessionCookie.getName() + "="
					+ sessionCookie.getValue() + "; domain="
					+ sessionCookie.getDomain();
			Log.i("H", "CookieforWebview  cookieString=" + cookieString);
			cookieManager.setAcceptCookie(true);
			cookieManager.setCookie(url, cookieString);
			flag = true;
		}
		CookieSyncManager.getInstance().sync();
		return flag;
	}

	public static LauncherApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		mInstance = this;
		mModel = new LauncherModel(this);

		// Register for changes to the favorites
		ContentResolver resolver = getContentResolver();
		resolver.registerContentObserver(
				LauncherSettings.Favorites.CONTENT_URI, true,
				mFavoritesObserver);

		CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
		crashHandler.init(this);

		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.discCacheFileCount(100)
				// Set max cache file count in SD card
				.discCacheSize(50 * 1024 * 1024)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() // Not
																				// necessary
																				// in
																				// common
				.build();

		// Initialize ImageLoader with configuration
		ImageLoader.getInstance().init(config);

		initData();
	}

	/**
	 * There's no guarantee that this function is ever called.
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
		ContentResolver resolver = getContentResolver();
		resolver.unregisterContentObserver(mFavoritesObserver);

	}

	/**
	 * Receives notifications whenever the user favorites have changed.
	 */
	private final ContentObserver mFavoritesObserver = new ContentObserver(
			new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			// 启动的时候只加载一次
			
			if(LauncherModel.is_runing_Loader!=true){
				CellLayout.update_screen = Launcher.mLauncher_ErWeiMa.getCurrentWorkspaceScreen();
				mModel.startLoader(LauncherApplication.this, false);
			}
			
			if ((System.currentTimeMillis() - exitTime) > 500) {
				exitTime = System.currentTimeMillis();
			}

		}
	};

	LauncherModel setLauncher(Launcher launcher) {
		mModel.initialize(launcher);
		return mModel;
	}

	LauncherModel getModel() {
		return mModel;
	}

	public void notifyFavoritesChanged() {
		mModel.startLoader(LauncherApplication.this, false);
	}



	public void initData() {
		mNetWorkState = Tools.getConnectionMethod(this);
	}


	public NotificationManager getNotificationManager() {
		return mNotificationManager;
	}

	private List<Activity> activityList = new LinkedList<Activity>();
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}


}
