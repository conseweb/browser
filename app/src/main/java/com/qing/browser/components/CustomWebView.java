package com.qing.browser.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

import com.qing.browser.controllers.Controller;
import com.qing.browser.utils.ApplicationUtils;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.Tools;

/**
 * A convenient extension of WebView.
 */
public class CustomWebView extends WebView {

	private Context mContext;

	private int mProgress = 100;

	private boolean mIsLoading = false;

	private boolean mLoaded = false;

	// 识别在切换出去前 当前Tab是显示的是否是WebView页面
	private boolean mCurrent = false;

	private String mLoadedUrl;

	private static boolean mBoMethodsLoaded = false;

	private static Method mOnPauseMethod = null;
	private static Method mOnResumeMethod = null;
	private static Method mSetFindIsUp = null;
	private static Method mNotifyFindDialogDismissed = null;
	private SharedPreferences sp;
	private int openwindownflag = 0;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 */
	public CustomWebView(Context context) {
		super(context);

		mContext = context;

		initializeOptions();
		loadMethods();
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 * @param attrs
	 *            The attribute set.
	 */
	public CustomWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		initializeOptions();
		loadMethods();
	}

	/**
	 * Initialize the WebView with the options set by the user through
	 * preferences.
	 */
	public void initializeOptions() {
		WebSettings settings = getSettings();

		// User settings
		sp = getContext().getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		if (sp.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_WIFI_IMAGES,
				false)) {
			if (1 == Tools.getConnectionMethod(mContext)) {
				settings.setLoadsImagesAutomatically(sp.getBoolean(
						Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true));
			} else {
				settings.setLoadsImagesAutomatically(false);
			}
		} else {
			settings.setLoadsImagesAutomatically(sp.getBoolean(
					Constants.PREFERENCES_BROWSER_ENABLE_IMAGES, true));
		}

		settings.setUseWideViewPort(Controller
				.getInstance()
				.getPreferences()
				.getBoolean(Constants.PREFERENCES_BROWSER_USE_WIDE_VIEWPORT,
						true));
		settings.setLoadWithOverviewMode(Controller
				.getInstance()
				.getPreferences()
				.getBoolean(Constants.PREFERENCES_BROWSER_LOAD_WITH_OVERVIEW,
						false));
		settings.setSaveFormData(Controller
				.getInstance()
				.getPreferences()
				.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_FORM_DATA,
						true));

		// 设置是否保存密码
		settings.setSavePassword(Controller
				.getInstance()
				.getPreferences()
				.getBoolean(Constants.PREFERENCES_BROWSER_ENABLE_PASSWORDS,
						true));
		// 设置默认缩放方式尺寸
		settings.setDefaultZoom(ZoomDensity.valueOf(Controller
				.getInstance()
				.getPreferences()
				.getString(Constants.PREFERENCES_DEFAULT_ZOOM_LEVEL,
						ZoomDensity.MEDIUM.toString())));
		settings.setUserAgentString(Controller
				.getInstance()
				.getPreferences()
				.getString(Constants.PREFERENCES_BROWSER_USER_AGENT,
						Constants.USER_AGENT_DEFAULT));

		int FontSize = sp.getInt(Constants.FontSize, 1);
		if (FontSize == 0) {
			settings.setTextSize(WebSettings.TextSize.SMALLER);
		} else if (FontSize == 1) {
			settings.setTextSize(WebSettings.TextSize.NORMAL);
		} else if (FontSize == 2) {
			settings.setTextSize(WebSettings.TextSize.LARGER);
		} else if (FontSize == 3) {
			settings.setTextSize(WebSettings.TextSize.LARGEST);
		}

		/*
		 * if (sp.getBoolean(Constants.PREFERENCES_INCOGNITO_MODE, false)) {
		 * CookieManager .getInstance() .setAcceptCookie( Controller
		 * .getInstance() .getPreferences() .getBoolean(
		 * Constants.PREFERENCES_BROWSER_ENABLE_COOKIES, true)); }
		 */
		CookieManager.getInstance().setAcceptCookie(true);
		if (Build.VERSION.SDK_INT <= 7) {
			settings.setPluginsEnabled(Controller
					.getInstance()
					.getPreferences()
					.getBoolean(
							Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS_ECLAIR,
							true));
		} else {
			settings.setPluginState(PluginState.valueOf(Controller
					.getInstance()
					.getPreferences()
					.getString(Constants.PREFERENCES_BROWSER_ENABLE_PLUGINS,
							PluginState.ON_DEMAND.toString())));
		}

		settings.setSupportZoom(true);

		// Technical settings
		settings.setSupportMultipleWindows(true);
		setLongClickable(true);
		setScrollbarFadingEnabled(true);
		setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		setDrawingCacheEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setJavaScriptEnabled(true);
		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();

		// Enable / disable zoom support in case of multiple pointer, e.g.
		// enable zoom when we have two down pointers, disable with one pointer
		// or when pointer up.
		// We do this to prevent the display of zoom controls, which are not
		// useful and override over the right bubble.
		if ((action == MotionEvent.ACTION_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_1_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_2_DOWN)
				|| (action == MotionEvent.ACTION_POINTER_3_DOWN)) {
			if (ev.getPointerCount() > 1) {
				this.getSettings().setBuiltInZoomControls(true);
				this.getSettings().setSupportZoom(true);
			} else {
				this.getSettings().setBuiltInZoomControls(false);
				this.getSettings().setSupportZoom(false);
			}
		} else if ((action == MotionEvent.ACTION_UP)
				|| (action == MotionEvent.ACTION_POINTER_UP)
				|| (action == MotionEvent.ACTION_POINTER_1_UP)
				|| (action == MotionEvent.ACTION_POINTER_2_UP)
				|| (action == MotionEvent.ACTION_POINTER_3_UP)) {
			this.getSettings().setBuiltInZoomControls(false);
			this.getSettings().setSupportZoom(false);
		}

		return super.onTouchEvent(ev);
	}

	@Override
	public void loadUrl(String url) {
		mLoadedUrl = url;
		super.loadUrl(url);
	}

	/**
	 * Set the current loading progress of this view.
	 * 
	 * @param progress
	 *            The current loading progress.
	 */
	public void setProgress(int progress) {
		mProgress = progress;
	}

	/**
	 * Get the current loading progress of the view.
	 * 
	 * @return The current loading progress of the view.
	 */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * Triggered when a new page loading is requested.
	 */
	public void notifyPageStarted() {
		mIsLoading = true;
	}

	/**
	 * Triggered when the page has finished loading.
	 */
	public void notifyPageFinished() {
		mProgress = 100;
		mIsLoading = false;
	}

	/**
	 * Check if the view is currently loading.
	 * 
	 * @return True if the view is currently loading.
	 */
	public boolean isLoading() {
		return mIsLoading;
	}

	public boolean getCurrent() {
		return mCurrent;
	}

	public void setCurrent(boolean mCurrent) {
		this.mCurrent = mCurrent;
	}

	public boolean getLoaded() {
		return mLoaded;
	}

	public void setLoaded(boolean mLoaded) {
		this.mLoaded = mLoaded;
	}

	/**
	 * Get the loaded url, e.g. the one asked by the user, without redirections.
	 * 
	 * @return The loaded url.
	 */
	public String getLoadedUrl() {
		return mLoadedUrl;
	}

	/**
	 * Reset the loaded url.
	 */
	public void resetLoadedUrl() {
		mLoadedUrl = null;
	}

	public boolean isSameUrl(String url) {
		if (url != null) {
			return url.equalsIgnoreCase(this.getUrl());
		}

		return false;
	}

	/**
	 * Perform an 'onPause' on this WebView through reflexion.
	 */
	public void doOnPause() {
		if (mOnPauseMethod != null) {
			try {

				mOnPauseMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnPause(): " + e.getMessage());
			}
		}
	}

	/**
	 * Perform an 'onResume' on this WebView through reflexion.
	 */
	public void doOnResume() {
		if (mOnResumeMethod != null) {
			try {

				mOnResumeMethod.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doOnResume(): " + e.getMessage());
			}
		}
	}

	public void doSetFindIsUp(boolean value) {
		if (mSetFindIsUp != null) {
			try {

				mSetFindIsUp.invoke(this, value);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView", "doSetFindIsUp(): " + e.getMessage());
			}
		}
	}

	public void doNotifyFindDialogDismissed() {
		if (mNotifyFindDialogDismissed != null) {
			try {

				mNotifyFindDialogDismissed.invoke(this);

			} catch (IllegalArgumentException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			} catch (InvocationTargetException e) {
				Log.e("CustomWebView",
						"doNotifyFindDialogDismissed(): " + e.getMessage());
			}
		}
	}

	/**
	 * Load static reflected methods.
	 */
	private void loadMethods() {

		if (!mBoMethodsLoaded) {

			try {

				mOnPauseMethod = WebView.class.getMethod("onPause");
				mOnResumeMethod = WebView.class.getMethod("onResume");

			} catch (SecurityException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			} catch (NoSuchMethodException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mOnPauseMethod = null;
				mOnResumeMethod = null;
			}

			try {

				mSetFindIsUp = WebView.class.getMethod("setFindIsUp",
						Boolean.TYPE);
				mNotifyFindDialogDismissed = WebView.class
						.getMethod("notifyFindDialogDismissed");

			} catch (SecurityException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mSetFindIsUp = null;
				mNotifyFindDialogDismissed = null;
			} catch (NoSuchMethodException e) {
				Log.e("CustomWebView", "loadMethods(): " + e.getMessage());
				mSetFindIsUp = null;
				mNotifyFindDialogDismissed = null;
			}

			mBoMethodsLoaded = true;
		}
	}

	/**
	 * Inject the AdSweep javascript.
	 */
	public void loadAdSweep() {
		super.loadUrl(ApplicationUtils.getAdSweepString(mContext));
	}

	public void setOpenWindownFlag(int flag) {
		this.openwindownflag = flag;
	}

	public int getOpenWindownFlag() {
		return this.openwindownflag;
	}
}
