package com.qing.browser.user;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebView.HitTestResult;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.DialogUtil;
import com.qing.browser.utils.StringUtil;
import com.qing.browser.utils.Tools;
import com.universe.galaxy.download.Download;

public class PublicWebViewActivity extends BaseActivity {

	private WebView webView = null;
	private static String menu_name = null;
	private ImageView webimage = null;
	private AnimationDrawable anim = null;
	private DialogUtil dialogUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
				Window.PROGRESS_VISIBILITY_ON);
		RefreshScreen();
	}

	// public void onProgressChanged(WebView view, int progress) {
	// PublicWebViewActivity.this.setProgress(progress * 100);
	// super.onProgressChanged(view, progress);
	// }
	// }

	public void onPhoneTo(String url) {
		Intent sendCall = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(sendCall);
	}
	
	private class HelloWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.v("L", "url=" + url);
			if ((view.getHitTestResult() != null && view.getHitTestResult()
					.getType() == HitTestResult.PHONE_TYPE)
					|| (url.startsWith("tel:"))) {
				onPhoneTo(url);
				return true;

			} 
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			NetWorkFailScreen(view);
			super.onReceivedError(null, errorCode, description, failingUrl);
		}

	}

	public class WebViewDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			if (Tools.sdCardExist()) {
				Download.DownLoadFile(url);
			} else {
				Toast.makeText(PublicWebViewActivity.this, "SD卡未就绪！",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	// 加载页面
	WebChromeClient webchromeclient = new WebChromeClient() {
		public void onProgressChanged(WebView view, int progress) {
			PublicWebViewActivity.this.setProgress(progress);
			if (100 == progress) {
				if (anim != null) {
					anim.stop();
				}
				if (webView != null) {
					webView.setVisibility(View.VISIBLE);
					webimage.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			// 确定取消弹框
			dialogUtil = new DialogUtil.Builder(PublicWebViewActivity.this)
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
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {

			// 确定弹框
			dialogUtil = new DialogUtil.Builder(PublicWebViewActivity.this)
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
	};

	private void RefreshScreen() {
		Intent intent = getIntent();
		String url = intent.getStringExtra(Constants.WEB_VIEW_URL);
		int isLogin = intent.getIntExtra(Constants.CookieType, 0);
		Log.i("DATA", "DATA" + url);
		menu_name = intent.getStringExtra(Constants.MENU_NAME);
		setContentView(R.layout.public_web_view);

		RelativeLayout layout_title_bar = (RelativeLayout) findViewById(R.id.layout_title_bar);
		if (StringUtil.isNull(menu_name)) {
			layout_title_bar.setVisibility(View.GONE);
		} else {
			layout_title_bar.setVisibility(View.VISIBLE);
		}

		TextView texttitle = (TextView) findViewById(R.id.item_title);
		ImageView item_back = (ImageView) findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		texttitle.setText(menu_name);
		webimage = (ImageView) findViewById(R.id.loading_process_dialog_progressBar);
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webset = webView.getSettings();
		webset.setJavaScriptEnabled(true);
		webset.setBuiltInZoomControls(true);
		
		//支持通过 JS 打开新窗口 
		webset.setJavaScriptCanOpenWindowsAutomatically(true);
		
		int FontSize = sp.getInt(Constants.FontSize, 1);
		if (FontSize == 0) {
			webset.setTextSize(WebSettings.TextSize.SMALLER);
		} else if (FontSize == 1) {
			webset.setTextSize(WebSettings.TextSize.NORMAL);
		} else if (FontSize == 2) {
			webset.setTextSize(WebSettings.TextSize.LARGER);
		} else if (FontSize == 3) {
			webset.setTextSize(WebSettings.TextSize.LARGEST);
		}

		LauncherApplication mCookie = new LauncherApplication();

		if (isLogin == Constants.CookieTypeLogin) {
			mCookie.CookieforWebview(PublicWebViewActivity.this, url,
					LauncherApplication.getLoginCookie());
		} else if (isLogin == Constants.CookieTypeShop) {
			mCookie.CookieforWebview(PublicWebViewActivity.this, url,
					LauncherApplication.getShopCookie());
		}

		webView.setWebViewClient(new HelloWebViewClient());
		webView.setDownloadListener(new WebViewDownloadListener());
		webView.loadUrl(url);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebChromeClient(webchromeclient);
		// webView.addJavascriptInterface(new JsWebView(
		// PublicWebViewActivity.this, webView), "android");
		webView.setFocusable(true);
		webView.requestFocusFromTouch();
		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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

	private void NetWorkFailScreen(final WebView view) {
		setContentView(R.layout.public_failweb_view);
		TextView texttitle = (TextView) findViewById(R.id.item_title);
		ImageView item_back = (ImageView) findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		texttitle.setText(menu_name);

		RelativeLayout layout_title_bar = (RelativeLayout) findViewById(R.id.layout_title_bar);
		if (StringUtil.isNull(menu_name)) {
			layout_title_bar.setVisibility(View.GONE);
		} else {
			layout_title_bar.setVisibility(View.VISIBLE);
		}

		ImageButton shuju = (ImageButton) findViewById(R.id.shuju);
		ImageButton wifi = (ImageButton) findViewById(R.id.wifi);
		ImageButton shuaxin = (ImageButton) findViewById(R.id.shuaxin);
		ImageButton daohang = (ImageButton) findViewById(R.id.daohang);

		shuju.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
				startActivity(intent);
			}
		});

		wifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});

		shuaxin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.reload();
				RefreshScreen();
				if (webimage != null) {
					Log.v("L", "转动函数");
					Object ob = webimage.getBackground();
					anim = (AnimationDrawable) ob;
					anim.start();
				}
			}
		});

		daohang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (webView != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
				webView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (webimage != null) {
			Log.v("L", "转动函数");
			Object ob = webimage.getBackground();
			anim = (AnimationDrawable) ob;
			anim.start();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onDestroy() {
		if (webView != null) {
			webView.setVisibility(View.GONE);
			webView.clearCache(true);
			webView.destroy();
			webView = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
