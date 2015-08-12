package com.qing.browser.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.tongji.LiuLiangTongji;
import com.qing.browser.user.PublicWebViewActivity;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.DialogImageUtil;
import com.qing.browser.utils.DialogListUtil;

public class SettingActivity extends BaseActivity {
	private LinearLayout seting_clean, seting_textSize, seting_jianyifankui,
			seting_bangzhu, seting_guanyu, seting_liuliang,seting_xinshouzhinan;
	private CheckBox checkBox_wifi_img, checkBox_wifi_download,
			checkBox_cookies, checkBox_remData;
	private LinearLayout checkBox_seting_default;
	private ImageView seting_default_select;
	private TextView textSize;
	DialogImageUtil dialogimageutil;
	public static String setDefaultFail = "http://www.baidu.com/search/error.html";
	ResolveInfo mInfo;
	PackageInfo info;
	PackageManager pm;
	public static boolean setDefault = false;
	public boolean cleanDefault = false;
	private String[] itemTimes = { "小号字体", "中号字体", "大号字体", "超大号字体" };
	private int FontSize = 0;
	private final static String helpUrl = "http://www.baidu.com";
	DialogListUtil builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		TextView item_title = (TextView) findViewById(R.id.item_title);
		item_title.setText("设置");
		ImageView btn_back = (ImageView) findViewById(R.id.item_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		seting_clean = (LinearLayout) findViewById(R.id.seting_clean);
		checkBox_wifi_img = (CheckBox) findViewById(R.id.checkBox_wifi_img);
		checkBox_wifi_download = (CheckBox) findViewById(R.id.checkBox_wifi_download);
		seting_default_select = (ImageView) findViewById(R.id.seting_default_select);
		checkBox_seting_default = (LinearLayout) findViewById(R.id.checkBox_seting_default);
		seting_textSize = (LinearLayout) findViewById(R.id.seting_textSize);
		textSize = (TextView) findViewById(R.id.textSize);
		seting_liuliang = (LinearLayout)findViewById(R.id.seting_liuliang);

		FontSize = sp.getInt(Constants.FontSize, 1);
		textSize.setText(itemTimes[FontSize]);

		checkBox_cookies = (CheckBox) findViewById(R.id.checkBox_cookies);
		checkBox_remData = (CheckBox) findViewById(R.id.checkBox_remData);

		seting_jianyifankui = (LinearLayout) findViewById(R.id.seting_jianyifankui);
		seting_xinshouzhinan = (LinearLayout) findViewById(R.id.seting_xinshouzhinan);
		seting_bangzhu = (LinearLayout) findViewById(R.id.seting_bangzhu);
		seting_guanyu = (LinearLayout) findViewById(R.id.seting_guanyu);

		seting_clean.setOnClickListener(listener);
		seting_textSize.setOnClickListener(listener);
		seting_jianyifankui.setOnClickListener(listener);
		seting_xinshouzhinan.setOnClickListener(listener);
		seting_bangzhu.setOnClickListener(listener);
		seting_guanyu.setOnClickListener(listener);
		seting_liuliang.setOnClickListener(listener);

		Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(setDefaultFail)));
		pm = getPackageManager();
		mInfo = pm.resolveActivity(i, 0);
		try {
			info = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (mInfo.activityInfo.packageName.equals(info.packageName)) {
			seting_default_select
					.setImageResource(R.drawable.checkbox_selected);
		} else {
			seting_default_select
					.setImageResource(R.drawable.checkbox_unselect);
		}

		checkBox_seting_default.setOnClickListener(listener);

		checkBox_wifi_img.setChecked(sp.getBoolean(
				Constants.PREFERENCES_BROWSER_ENABLE_WIFI_IMAGES, false));

		checkBox_wifi_download.setChecked(sp.getBoolean(
				Constants.PREFERENCES_BROWSER_DOWNLOAD_NOT_WIFI_REMIND, true));

		checkBox_wifi_img
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 仅在wifi下显示图片
						sp.edit()
								.putBoolean(
										Constants.PREFERENCES_BROWSER_ENABLE_WIFI_IMAGES,
										isChecked).commit();
					}
				});
		checkBox_wifi_download
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 非wifi环境下载文件提醒
						sp.edit()
								.putBoolean(
										Constants.PREFERENCES_BROWSER_DOWNLOAD_NOT_WIFI_REMIND,
										isChecked).commit();
					}
				});

		checkBox_cookies.setChecked(sp.getBoolean(
				Constants.PREFERENCES_BROWSER_ENABLE_COOKIES, true));
		checkBox_remData.setChecked(sp.getBoolean(
				Constants.PREFERENCES_BROWSER_ENABLE_FORM_DATA, true));
		checkBox_cookies
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 接收cookies
						sp.edit()
								.putBoolean(
										Constants.PREFERENCES_BROWSER_ENABLE_COOKIES,
										isChecked).commit();

					}
				});
		checkBox_remData
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 记住表单数据
						sp.edit()
								.putBoolean(
										Constants.PREFERENCES_BROWSER_ENABLE_FORM_DATA,
										isChecked).commit();
					}
				});
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.seting_clean:
				startActivity(new Intent(SettingActivity.this,
						KongJianGuanLiActivity.class));

				break;

			case R.id.seting_textSize:
				// 设置字体大小
				builder = new DialogListUtil.Builder(SettingActivity.this)
						.setTitleText("设置字体").setItems(itemTimes)
						.setlistListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								FontSize = arg2;
								textSize.setText(itemTimes[FontSize]);
								sp.edit().putInt(Constants.FontSize, FontSize)
										.commit();
								builder.dismiss();
							}
						}).create();
				builder.show();

				break;

			case R.id.seting_jianyifankui:
				// 建议反馈
				startActivity(new Intent(SettingActivity.this,
						JianYiFanKuiActivity.class));
				break;
			case R.id.seting_xinshouzhinan:
				// 新手指南
				startActivity(new Intent(SettingActivity.this,
						WelcomePageActivity.class));
				break;
			case R.id.seting_bangzhu:
				// 帮助
				Intent intent = new Intent(SettingActivity.this,
						PublicWebViewActivity.class);
				intent.putExtra(Constants.WEB_VIEW_URL, helpUrl);
				intent.putExtra(Constants.MENU_NAME, "帮助");
				startActivity(intent);

				break;

			case R.id.seting_guanyu:
				// 关于我们
				startActivity(new Intent(SettingActivity.this,
						AboutActivity.class));
				break;
			case R.id.checkBox_seting_default:

				// 设置为默认浏览器
				Intent i = (new Intent(Intent.ACTION_VIEW,
						Uri.parse(setDefaultFail)));
				pm = getPackageManager();
				mInfo = pm.resolveActivity(i, 0);
				try {
					info = getPackageManager().getPackageInfo(getPackageName(),
							0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

				if (!mInfo.activityInfo.packageName.equals(info.packageName)) {
					// 设置为默认
					setDefault();
				} else {
					// 取消默认设置
					seting_default_select
							.setImageResource(R.drawable.checkbox_unselect);
					PackageManager packageManager = getPackageManager();
					packageManager
							.clearPackagePreferredActivities(info.packageName);
				}

				break;
				
			case R.id.seting_liuliang:
				
				//TODO 查看流量时先保存数据
				saveLiuLiangData();
				startActivity(new Intent(SettingActivity.this,
						LiuLiangTongji.class));
				break;
			}

		}
	};

	@Override
	protected void onRestart() {
		super.onRestart();
		Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(setDefaultFail)));
		mInfo = pm.resolveActivity(i, 0);

		try {
			info = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.d("H", "onRestart " + info.packageName + "  package:"
				+ mInfo.activityInfo.packageName + " setDefault=" + setDefault);
		if (setDefault) {
			setDefault = false;
			if (mInfo.activityInfo.packageName.equals(info.packageName)) {
				Toast.makeText(SettingActivity.this, "设置成功", Toast.LENGTH_SHORT)
						.show();

				seting_default_select
						.setImageResource(R.drawable.checkbox_selected);
			} else {
				Toast.makeText(SettingActivity.this, "设置失败", Toast.LENGTH_SHORT)
						.show();

				seting_default_select
						.setImageResource(R.drawable.checkbox_unselect);
			}

		}

		if (cleanDefault) {
			cleanDefault = false;
			setDefault();
		}
	}

	public void setDefault() {
		Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(setDefaultFail)));
		mInfo = pm.resolveActivity(i, 0);

		if ("android".equals(mInfo.activityInfo.packageName)) {
			// 没有设置过，直接设置
			Log.i("H", "没有设置过，直接设置  " + info.packageName + "  package:"
					+ mInfo.activityInfo.packageName);
			dialogimageutil = new DialogImageUtil.Builder(SettingActivity.this)
					.setTitleText("如何设置默认")
					.setImageResource(R.drawable.setdefault)
					.setPositiveButton("去设置", new View.OnClickListener() {
						public void onClick(View v) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse(setDefaultFail)));
							setDefault = true;
							dialogimageutil.dismiss();
						}
					}).create();
			dialogimageutil.show();
		} else {
			// 已经设置过，先清除设置
			Log.i("H", "已经设置过，先清除设置 " + info.packageName + "  package:"
					+ mInfo.activityInfo.packageName);
			dialogimageutil = new DialogImageUtil.Builder(SettingActivity.this)
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
			Log.e("H", "查看流量  " + e.toString());
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
					"查看 本次 3G使用流量 w="
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp)
							+ " getData()="
							+ LiuLiangTongji.getDataStr(getData()));

			if(0 != LiuLiangTongji.temp){
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			
			Log.d("H",
					"查看 本次 3G使用流量"
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
					"查看 本次 WIFI使用流量 w="
							+ LiuLiangTongji.getDataStr(LiuLiangTongji.temp)
							+ " getData()="
							+ LiuLiangTongji.getDataStr(getData()));
			if(0 != LiuLiangTongji.temp){
				LiuLiangTongji.temp = getData() - LiuLiangTongji.temp;
			}
			Log.d("H",
					"查看 本次 WIFI使用流量"
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
}