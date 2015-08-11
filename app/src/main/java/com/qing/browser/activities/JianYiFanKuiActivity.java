package com.qing.browser.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.net.URLUtil;
import com.qing.browser.utils.ConstantsUrl;
import com.qing.browser.utils.DialogUtil;
import com.qing.browser.utils.Loading_Dialog;
import com.qing.browser.utils.StringUtil;
import com.qing.browser.utils.Tools;

public class JianYiFanKuiActivity extends BaseActivity {
	private TextView item_title;
	private EditText edit_content, edit_email;
	private Button btn_ok;
	private ImageView btn_back;
	Loading_Dialog LoadDialog = null;
	private String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	private DialogUtil dialogUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jian_yi_fan_kui);
		item_title = (TextView) findViewById(R.id.item_title);
		item_title.setText("建议反馈");
		btn_back = (ImageView) findViewById(R.id.item_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		edit_content = (EditText) findViewById(R.id.edit_content);
		edit_email = (EditText) findViewById(R.id.edit_email);
		btn_ok = (Button) findViewById(R.id.btn_ok);

		btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (StringUtil.isNull(edit_content.getText().toString())) {
					Toast.makeText(JianYiFanKuiActivity.this, "请输入反馈内容",
							Toast.LENGTH_SHORT).show();
					edit_content.requestFocus();
					edit_content.setSelectAllOnFocus(true);
					return;
				}

				String email = edit_email.getText().toString();
				if (!StringUtil.isNull(email) && !email.matches(regex)) {
					Toast.makeText(JianYiFanKuiActivity.this, "请输入正确的邮箱",
							Toast.LENGTH_SHORT).show();
					edit_email.requestFocus();
					edit_email.setSelectAllOnFocus(true);
					return;
				}

				if (Tools.isConnectInternet(JianYiFanKuiActivity.this)) {
					LoadDialog = new Loading_Dialog(JianYiFanKuiActivity.this);
					LoadDialog.Loading_SetText("正在加载数据...");
					LoadDialog.Loading_ZhuanDong();
					new Thread(new GetList_Thread()).start();
				} else {
					Toast.makeText(JianYiFanKuiActivity.this, "请检查网络设置",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	class GetList_Thread implements Runnable {
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);

		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg != null) {
				if (LoadDialog != null)
					LoadDialog.Loading_colse();
				switch (msg.what) {
				case 1:
					dialogUtil = new DialogUtil.Builder(
							JianYiFanKuiActivity.this)
							.setTitleText("温馨提示")
							.setText("提交成功")
							.setPositiveButton("确定",
									new View.OnClickListener() {
										public void onClick(View v) {
											dialogUtil.dismiss();
											finish();
										}
									}).create();
					dialogUtil.show();

					break;

				}
			}
		}
	};

}
