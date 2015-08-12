package com.qing.browser.ui;

import com.qing.browser.R;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;


public class PullToRefreshWebView extends PullToRefreshBase<WebView> {

	private final OnRefreshListener defaultOnRefreshListener = getOnRefreshListener();

	public PullToRefreshWebView(Context context) {
		super(context);
		setOnRefreshListener(defaultOnRefreshListener);
	}

	public PullToRefreshWebView(Context context, int mode) {
		super(context, mode);
		setOnRefreshListener(defaultOnRefreshListener);
	}

	public PullToRefreshWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnRefreshListener(defaultOnRefreshListener);
	}

	@Override
	protected WebView createRefreshableView(Context context, AttributeSet attrs) {
		WebView webView = new WebView(context, attrs);
		webView.setId(R.id.webview);
		return webView;
	}

	@Override
	protected boolean isReadyForPullDown() {
		return refreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		return refreshableView.getScrollY() >= (refreshableView
		        .getContentHeight() - refreshableView.getHeight());
	}

}
