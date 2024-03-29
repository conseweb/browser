package com.qing.browser.ui.runnables;

import android.app.Activity;
import android.graphics.Bitmap;

import com.qing.browser.providers.BookmarksProviderWrapper;

/**
 * Runnable to update database favicon.
 */
public class FaviconUpdaterRunnable implements Runnable {
	
	private Activity mActivity;
	private String mUrl;
	private String mOriginalUrl;
	private Bitmap mFavIcon;

	/**
	 * Constructor.
	 * @param activity The parent activity.
	 * @param url The page url.
	 * @param originalUrl The page original url.
	 * @param favicon The favicon.
	 */
	public FaviconUpdaterRunnable(Activity activity, String url, String originalUrl, Bitmap favicon) {
		mActivity = activity;
		mUrl = url;
		mOriginalUrl = originalUrl;
		mFavIcon = favicon;
	}
	
	@Override
	public void run() {
		BookmarksProviderWrapper.updateFavicon(mActivity, mUrl, mOriginalUrl, mFavIcon);
	}

}
