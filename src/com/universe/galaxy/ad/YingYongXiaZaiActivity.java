package com.universe.galaxy.ad;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.universe.galaxy.download.Download;
import com.universe.galaxy.util.Tools;

public class YingYongXiaZaiActivity extends Activity {

	private WebView webView = null;
	// private ImageView webimage = null;
	private AnimationDrawable anim = null;
	private LinearLayout linear_webview;
	private LinearLayout linear_loading;
	private LinearLayout linear_button;
	private TextView TextView_name;
	private ImageView ImageView_Image;
	private Button guanbi;
	private Button xiazai;
	private Button button_bangdan;
	private String apkurl;
	private String bangdan;
	ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RefreshScreen();
	}

	private class HelloWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			view.getSettings().setJavaScriptEnabled(true);
			view.requestFocus();
			view.requestFocusFromTouch();
			super.onPageFinished(view, url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains("tel:") || url.contains("mailto:")) {
				return true;
			}
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(null, errorCode, description, failingUrl);
		}

	}

	public class WebViewDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			if (Tools.sdCardExist()) {
				Download.DownLoadFile(url);
				finish();
			} else {
				Toast.makeText(YingYongXiaZaiActivity.this, "SD卡未就绪",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	// 加载页面
	WebChromeClient webchromeclient = new WebChromeClient() {
		public void onProgressChanged(WebView view, int progress) {
			YingYongXiaZaiActivity.this.setProgress(progress);
			progressBar.setProgress(progress);
			if (100 == progress) {
				if (anim != null) {
					anim.stop();
				}
				if (webView != null) {
					linear_webview.setVisibility(View.VISIBLE);
					webView.setVisibility(View.VISIBLE);
					// webimage.setVisibility(View.GONE);
					//progressBar.setVisibility(View.GONE);
					linear_loading.setVisibility(View.GONE);
				}
			}
		}
	};

	private void RefreshScreen() {
		Intent intent = getIntent();

		String zuhe = (String) intent.getSerializableExtra("name");
		String Image = (String) intent.getSerializableExtra("tupian");
		apkurl = (String) intent.getSerializableExtra("apkurl");
		bangdan = (String) intent.getSerializableExtra("bangdan");

		String url = "";
		String name = "";

		try {
			JSONArray json = new JSONArray(zuhe);
			if (json != null && json.length() != 0) {
				name = json.getJSONObject(0).getString("name");
				url = json.getJSONObject(0).getString("url");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setContentView(R.layout.ying_yong_xia_zai);
		TextView_name = (TextView) findViewById(R.id.name);
		ImageView_Image = (ImageView) findViewById(R.id.image);
		guanbi = (Button) findViewById(R.id.guanbi);
		xiazai = (Button) findViewById(R.id.xiazai);
		button_bangdan = (Button) findViewById(R.id.bangdan);
		linear_button = (LinearLayout) findViewById(R.id.linear_button);
		button_bangdan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bangdan.equals("")) {
					Toast.makeText(YingYongXiaZaiActivity.this, "暂不提供服务",
							Toast.LENGTH_SHORT).show();
				} else {
					Uri uri = Uri.parse(bangdan);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}

			}
		});
		xiazai.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Download.DownLoadFile(apkurl);
				finish();
				return;
			}
		});
		guanbi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				return;
			}
		});
		;
		if (apkurl.equals("")) {
			linear_button.setVisibility(View.GONE);
		}

		TextView_name.setText(name);
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
								ImageView_Image.setImageBitmap(bitmap);
							} else {
							}
						}
						break;
					}
				}
			}
		};

		Thread thread = new Thread(AdUtil.getInstance(
				YingYongXiaZaiActivity.this, handler, Image));
		thread.start();
		LinearLayout linear = (LinearLayout) findViewById(R.id.dialog);
		linear_webview = (LinearLayout) findViewById(R.id.linear_webview);
		linear_loading = (LinearLayout) findViewById(R.id.linear_loading);

		progressBar = new ProgressBar(this, null,
				android.R.attr.progressBarStyleHorizontal);
		linear_loading.addView(progressBar);
		// webimage = (ImageView)
		// findViewById(R.id.loading_process_dialog_progressBar);
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webset = webView.getSettings();
		webset.setJavaScriptEnabled(true);
		webset.setBuiltInZoomControls(true);

		webView.setWebViewClient(new HelloWebViewClient());
		webView.setDownloadListener(new WebViewDownloadListener());
		webView.loadUrl(url);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebChromeClient(webchromeclient);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (webView != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				// Intent intent = new Intent(YingYongXiaZaiActivity.this,
				// MainActivity.class);
				// startActivity(intent);
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// if (webimage != null) {
		// Log.v("L", "转动函数");
		// Object ob = webimage.getBackground();
		// anim = (AnimationDrawable) ob;
		// anim.start();
		// }
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onDestroy() {
		if (webView != null) {
			webView.setVisibility(View.GONE);
			linear_webview.setVisibility(View.GONE);
			webView.clearCache(true);
			webView.destroy();
			Log.i("AAA", "销毁webView");
			webView = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
