package com.universe.galaxy.version;

import java.text.DecimalFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.qing.browser.R;
import com.universe.galaxy.download.Download;
import com.universe.galaxy.util.Constants;
import com.universe.galaxy.util.StringUtil;
import com.universe.galaxy.util.TongJi;
import com.universe.galaxy.util.Tools;

public class NewVersionActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		final VersionInfo versionInfo = (VersionInfo) intent
				.getSerializableExtra(Constants.VERSION_INFO);
		if (versionInfo != null && !StringUtil.isNull(versionInfo.getVersion())) {
			TongJi.AddAnalyticsData(TongJi.n_gengxin);
			setContentView(R.layout.new_version);
			TextView dialog_title = (TextView)findViewById(R.id.dialog_title);
			dialog_title.setText("检查新版本");
			
			TextView vtv = (TextView) findViewById(R.id.gengxin_banben);
			vtv.setText("版本：" + versionInfo.getVersion());
			TextView stv = (TextView) findViewById(R.id.gengxin_daxiao);
			float size = Float.valueOf(versionInfo.getSize()) / 1024 / 1024;
			DecimalFormat fnum = new DecimalFormat("##0.00");
			String dd = fnum.format(size);
			stv.setText("大小： " + fnum.format(size) + " M");
			TextView xtv = (TextView) findViewById(R.id.gengxin_neirong);
			Log.i("CNCOMAN", versionInfo.getText());
			String contentString = "";
			String[] strarray = versionInfo.getText().split("#");
			for (int i = 0; i < strarray.length; i++) {
				Log.i("CNCOMAN", strarray[i]);
				contentString = contentString + strarray[i] + "\r\n";
			}
			xtv.setText(contentString);
			final String version = versionInfo.getVersion();
			final String path = versionInfo.getPath();
			Button confirmBtn = (Button) findViewById(R.id.gengxinqueding);
			Button cancelBtn = (Button) findViewById(R.id.gengxinquxiao);
			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String SVTIME = versionInfo.getVtime();
					Log.v("L", "VTIME=" + SVTIME);
					if (SVTIME != null && !SVTIME.equals("")) {
						int VTIME = Integer.parseInt(SVTIME);
						Date datenow = new Date();
						long nowTime = datenow.getTime();
						SharedPreferences sp = getSharedPreferences(
								Constants.PREFERENCES_NAME,
								Context.MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putInt(Constants.SHENG_JI_JIAN_GE, VTIME);
						editor.putLong(Constants.SHENG_JI_TIME, nowTime);
						editor.commit();
					}
					finish();
				}
			});
			confirmBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Tools.isConnectInternet(NewVersionActivity.this)) {
						if (Tools.sdCardExist()) {
							Download.DownLoadFile(path, "Qing_v" + version
									+ ".apk");
						} else {
						}
					} else {
					}
					finish();
				}
			});
		} else {
			finish();
		}
	}
}
