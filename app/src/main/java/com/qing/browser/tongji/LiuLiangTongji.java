package com.qing.browser.tongji;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;

public class LiuLiangTongji extends BaseActivity {

	private TextView item_title;
	private ImageView item_back;
	private TextView today_3g, today_wifi, month_3g, month_wifi, total_3g,
			total_wifi;
	private Button btn_clean_data;
	public static long temp = 0;
	public static final String G_Today = "G_Today";
	public static final String G_Month = "G_Month";
	public static final String G_Total = "G_Total";

	public static final String W_Today = "W_Today";
	public static final String W_Month = "W_Month";
	public static final String W_Total = "W_Total";
	public static final String NetworkStatus = "NetworkStatus";
	public static final int NetworkStatus_Not = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liuliangtongji);

		item_title = (TextView) findViewById(R.id.item_title);
		item_title.setText("流量统计");

		item_back = (ImageView) findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		today_3g = (TextView) findViewById(R.id.today_3g);
		today_wifi = (TextView) findViewById(R.id.today_wifi);

		month_3g = (TextView) findViewById(R.id.month_3g);
		month_wifi = (TextView) findViewById(R.id.month_wifi);

		total_3g = (TextView) findViewById(R.id.total_3g);
		total_wifi = (TextView) findViewById(R.id.total_wifi);

		Log.d("H", "2G3G流量统计：今日："
				+ getDataStr(sp.getLong(G_Today, 0)).toString() + " 本月："
				+ getDataStr(sp.getLong(G_Total, 0)).toString() + " 总计："
				+ getDataStr(sp.getLong(G_Month, 0)).toString());

		Log.d("H", "WIFI流量统计：今日："
				+ getDataStr(sp.getLong(W_Today, 0)).toString() + " 本月："
				+ getDataStr(sp.getLong(W_Month, 0)).toString() + " 总计："
				+ getDataStr(sp.getLong(W_Total, 0)).toString());

		today_3g.setText(getDataStr(sp.getLong(G_Today, 0)).toString());
		total_3g.setText(getDataStr(sp.getLong(G_Total, 0)).toString());
		month_3g.setText(getDataStr(sp.getLong(G_Month, 0)).toString());

		today_wifi.setText(getDataStr(sp.getLong(W_Today, 0)).toString());
		month_wifi.setText(getDataStr(sp.getLong(W_Month, 0)).toString());
		total_wifi.setText(getDataStr(sp.getLong(W_Total, 0)).toString());
 
		String total_str = null;
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					"com.qing.browser", PackageManager.GET_ACTIVITIES);

			long recv = TrafficStats.getUidRxBytes(ai.uid);
			long sent = TrafficStats.getUidTxBytes(ai.uid);

			if (recv < 0 || sent < 0) {
				total_str = "总计：-";
			}else{ 
				total_str = "总计：" + getDataStr(recv + sent).toString();
			} 
			
		} catch (NameNotFoundException e) {
			Log.e("H", "流量统计 " + e.toString());
			total_str = "总计：-";
		}

		Log.d("H", "流量统计 " + total_str); 
		
		btn_clean_data = (Button) findViewById(R.id.btn_clean_data);
		btn_clean_data.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				today_3g.setText("0B");
				month_3g.setText("0B");
				total_3g.setText("0B");

				today_wifi.setText("0B");
				month_wifi.setText("0B");
				total_wifi.setText("0B");

				sp.edit().putLong(G_Today, 0).commit();
				sp.edit().putLong(G_Month, 0).commit();
				sp.edit().putLong(G_Total, 0).commit();

				sp.edit().putLong(W_Today, 0).commit();
				sp.edit().putLong(W_Month, 0).commit();
				sp.edit().putLong(W_Total, 0).commit();

			}
		});

	}

	public static String getDataStr(long data) {

		double d = data;
		if (d >= 1024) {
			d = d / 1024;
			if (d >= 1024) {
				d = d / 1024; 
				return String.format("%.2f", d) + "MB";
			} else {
				return String.format("%.2f", d) + "KB";
			}
		} else {
			return String.format("%.2f", d) + "B";
		}

	}

}
