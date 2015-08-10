package com.qing.browser.ui.runnables;

import android.content.Context;

import com.qing.browser.providers.BookmarksProviderWrapper;

/**
 * Runnable to update database favicon.
 */
public class SnapshotUpdaterRunnable implements Runnable {
	
	private Context mContext;
	private String mUrl;
	private String mOriginalUrl;
	private String mSnapMD5;

	/**
	 * Constructor.
	 * @param activity The parent activity.
	 * @param url The page url.
	 * @param originalUrl The page original url.
	 * @param favicon The favicon.
	 */
	public SnapshotUpdaterRunnable(Context context, String url, String originalUrl, String snapMD5) {
		mContext = context;
		mUrl = url;
		mOriginalUrl = originalUrl;
		mSnapMD5 = snapMD5;
	}
	
	@Override
	public void run() {
		BookmarksProviderWrapper.updateSnapshot(mContext, mUrl, mOriginalUrl, mSnapMD5);
	}

}
