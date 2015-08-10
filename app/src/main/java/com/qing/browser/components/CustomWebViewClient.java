/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2012 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.qing.browser.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.qing.browser.R;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.utils.AdShowUtil;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.UrlUtils;

/**
 * Convenient extension of WebViewClient.
 */
public class CustomWebViewClient extends WebViewClient {

	private final String TAG = "CustomWebViewClient";

	private Launcher mLauncher;
	private View RelativeLayoutWebView;
	SharedPreferences sp;
	private boolean AdErrorflag = true; 

	public CustomWebViewClient(Launcher launcher,View RelativeLayoutWebView) {
		super();
		mLauncher = launcher;
		this.RelativeLayoutWebView = RelativeLayoutWebView;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		((CustomWebView) view).notifyPageFinished();
		mLauncher.onPageFinished(url, (CustomWebView) view);
		// mLauncher.onPageFinished(url);
		sp = mLauncher.getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		//显示广告
		if(mLauncher.ADshowflag==0&&AdErrorflag){
			mLauncher.ADshowflag =1;
			int AD_HENGTIAO_SHOUCI = sp.getInt(Constants.AD_HENGTIAO_SHOUCI, 0);
			int AD_CHAPING_SHOUCI = sp.getInt(Constants.AD_CHAPING_SHOUCI, 0);
			int AD_XINZENG_SHOUCI = sp.getInt(Constants.AD_XINZENG_SHOUCI, 0);
			
			
	
			Editor editor = sp.edit();
			editor.putInt(Constants.AD_HENGTIAO_SHOUCI, (AD_HENGTIAO_SHOUCI+1));
			editor.putInt(Constants.AD_CHAPING_SHOUCI, (AD_CHAPING_SHOUCI+1));
			editor.putInt(Constants.AD_XINZENG_SHOUCI, (AD_XINZENG_SHOUCI+1));
			editor.commit();
			
			if((AD_HENGTIAO_SHOUCI+1)==3||(AD_HENGTIAO_SHOUCI+1)==11||(AD_HENGTIAO_SHOUCI+1)==18){
				//横条 开始3次  间隔8次  上限3个
				Thread thread = new Thread(AdShowUtil.getInstance(mLauncher,
						mLauncher.AD_handler,Constants.AD_HENGTIAO_FLAG));
				thread.start();
			}
			
			if((AD_CHAPING_SHOUCI+1)==5||(AD_CHAPING_SHOUCI+1)==13){
				//插屏 开始5次 间隔8次 上限2个。
				Thread thread = new Thread(AdShowUtil.getInstance(mLauncher,
						mLauncher.AD_handler,Constants.AD_CHAPING_FLAG));
				thread.start();
			}
			
			/*if((AD_XINZENG_SHOUCI+1)==7||(AD_XINZENG_SHOUCI+1)==19){
				//新增 开始7次 间隔12次 上限2个。
				Message msg = new Message();
				msg.obj = "";
				msg.what = 103;
				mLauncher.AD_handler.sendMessage(msg);
			}*/
		}
		//广告结束
		
		super.onPageFinished(view, url);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {

		((CustomWebView) view).notifyPageStarted();

		// if (!url.equals(Launcher.beforeUrl[Launcher.mWebViewCurrentIndex])) {
		// Log.i(TAG, "view.getUrl()!=url");
		// mLauncher.createCurrentWebView(url);
		// return;
		// }

		mLauncher.onPageStarted(url);

		super.onPageStarted(view, url, favicon);
	}
	
	@Override
	public void onReceivedError(final WebView view, int errorCode,
			String description, String failingUrl) {
		
		AdErrorflag = false;
		
		if(errorCode == -10){
			return;				//不受支持的URI方案
		}
		if(RelativeLayoutWebView!=null){
			 final CustomWebView WebView = (CustomWebView) RelativeLayoutWebView
					.findViewById(R.id.webview);
			 
			 final LinearLayout failweblinearlayout = (LinearLayout) RelativeLayoutWebView
						.findViewById(R.id.failweblinearlayout);
			 
			 ImageButton shuju = (ImageButton) RelativeLayoutWebView.findViewById(R.id.shuju);
			 ImageButton wifi = (ImageButton) RelativeLayoutWebView.findViewById(R.id.wifi);
			 ImageButton shuaxin = (ImageButton) RelativeLayoutWebView.findViewById(R.id.shuaxin);
			 ImageButton daohang = (ImageButton) RelativeLayoutWebView.findViewById(R.id.daohang);
			 
			 shuju.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
					mLauncher.startActivity(intent);
				}
			 });
			 
			 wifi.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View v) {
						mLauncher.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				});
			 
			 shuaxin.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View v) {
						 WebView.setVisibility(View.VISIBLE);
						 failweblinearlayout.setVisibility(View.GONE);
						 AdErrorflag = true;
						 view.reload();
					}
				});
			 
			 daohang.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View v) {
						
					}
				});
			 
			 WebView.setVisibility(View.GONE);
			 failweblinearlayout.setVisibility(View.VISIBLE);
		}
		
		//super.onReceivedError(view, errorCode, description, failingUrl);
	}

	@Override
	public void onReceivedSslError(WebView view, final SslErrorHandler handler,
			SslError error) {
		handler.proceed();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.startsWith(Constants.URL_ACTION_SEARCH)) {
			String searchTerm = url.replace(Constants.URL_ACTION_SEARCH, "");

			SharedPreferences sp = mLauncher.getSharedPreferences(Constants.PREFERENCES_NAME, mLauncher.MODE_PRIVATE);
			String searchUrl = sp.getString(Constants.PREFERENCES_GENERAL_SEARCH_URL, Constants.URL_SEARCH_GOOGLE);
			 
			String newUrl = String.format(searchUrl, searchTerm);

			view.loadUrl(newUrl);
			return true;

		} else if (view.getHitTestResult() != null
				&& view.getHitTestResult().getType() == HitTestResult.EMAIL_TYPE) {
			mLauncher.onMailTo(url);
			return true;

		} else if ((view.getHitTestResult() != null && view.getHitTestResult()
				.getType() == HitTestResult.PHONE_TYPE)
				|| (url.startsWith("tel:"))) {
			mLauncher.onPhoneTo(url);
			return true;

		}
		
		return super.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			final HttpAuthHandler handler, final String host, final String realm) {
		String username = null;
		String password = null;

		boolean reuseHttpAuthUsernamePassword = handler
				.useHttpAuthUsernamePassword();

		if (reuseHttpAuthUsernamePassword && view != null) {
			String[] credentials = view
					.getHttpAuthUsernamePassword(host, realm);
			if (credentials != null && credentials.length == 2) {
				username = credentials[0];
				password = credentials[1];
			}
		}

		if (username != null && password != null) {
			handler.proceed(username, password);
		} else {
			LayoutInflater factory = LayoutInflater.from(mLauncher);
			final View v = factory.inflate(R.layout.http_authentication_dialog,
					null);

			if (username != null) {
				((EditText) v.findViewById(R.id.username_edit))
						.setText(username);
			}
			if (password != null) {
				((EditText) v.findViewById(R.id.password_edit))
						.setText(password);
			}

			AlertDialog dialog = new AlertDialog.Builder(mLauncher)
					.setTitle(
							String.format(
									mLauncher
											.getString(R.string.HttpAuthenticationDialog_DialogTitle),
									host, realm))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setView(v)
					.setPositiveButton(R.string.Commons_Proceed,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String nm = ((EditText) v
											.findViewById(R.id.username_edit))
											.getText().toString();
									String pw = ((EditText) v
											.findViewById(R.id.password_edit))
											.getText().toString();
									mLauncher.setHttpAuthUsernamePassword(host,
											realm, nm, pw);
									handler.proceed(nm, pw);
								}
							})
					.setNegativeButton(R.string.Commons_Cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									handler.cancel();
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									handler.cancel();
								}
							}).create();

			dialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			dialog.show();

			v.findViewById(R.id.username_edit).requestFocus();
		}
	}
	

}
