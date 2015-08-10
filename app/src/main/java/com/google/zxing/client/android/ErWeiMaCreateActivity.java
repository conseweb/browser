package com.google.zxing.client.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.client.android.encode.EncodingHandler;
import com.google.zxing.client.android.result.ResultHandler;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.utils.StringUtil;

public class ErWeiMaCreateActivity extends BaseActivity {

	private EditText qrStrEditText;
	private EditText qrStrEditText2;
	private EditText qrStrEditText3;
	private EditText lianxidianhua;
	private EditText lianxidizhi;
	private EditText gongsimingzi;
	private EditText gongsizhiwei;
	private EditText zhuyedizhi;
	
	private TextView titlename = null;
	private TextView imbutton = null;
	private String publicString1 = "";
	static Bitmap qrCodeBitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_create);

		titlename = (TextView) findViewById(R.id.item_title);
		titlename.setText("生成二维码");

		ImageView item_back = (ImageView) findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		final Bundle bundle = getIntent().getExtras();

		qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
		qrStrEditText2 = (EditText) this.findViewById(R.id.et_qr_string2);
		qrStrEditText3 = (EditText) this.findViewById(R.id.et_qr_string3);
		lianxidianhua = (EditText) this.findViewById(R.id.lianxidianhua);
		lianxidizhi = (EditText) this.findViewById(R.id.lianxidizhi);
		gongsimingzi = (EditText) this.findViewById(R.id.gongsimingzi);
		gongsizhiwei = (EditText) this.findViewById(R.id.gongsizhiwei);
		zhuyedizhi = (EditText) this.findViewById(R.id.zhuyedizhi);

		if (bundle != null) {
			if (this.getIntent().hasExtra("String")) {
				if (bundle.getString("String").equals("wenbenbutton")) {
					titlename.setText("文本二维码");
					qrStrEditText.setHint("请输入内容");
					qrStrEditText.getLayoutParams().height =120;
				} else if (bundle.getString("String").equals("duanxinbutton")) {
					titlename.setText("短信二维码");
					qrStrEditText.setHint("短信号码");
					qrStrEditText2.setVisibility(View.VISIBLE);
					qrStrEditText2.setHint("短信内容");
					qrStrEditText2.getLayoutParams().height =120;
					publicString1 = "SMSTO:";
				} else if (bundle.getString("String").equals("URLbutton")) {
					titlename.setText("网址二维码");
					qrStrEditText.setHint("请输入网址");
					qrStrEditText.getLayoutParams().height =120;
					publicString1 = "http://";
				} else if (bundle.getString("String").equals("telbutton")) {
					titlename.setText("电话二维码");
					qrStrEditText.setHint("请输入电话号码");
					qrStrEditText.getLayoutParams().height =120;
					publicString1 = "TEL:";
				} else if (bundle.getString("String").equals("mingpianbutton")) {
					titlename.setText("名片二维码");
					publicString1 = "BEGIN:VCARD%0AVERSION:3.0%0AN:";
					qrStrEditText.setHint("姓名");
					qrStrEditText.getLayoutParams().height =55;
					
					qrStrEditText2.setVisibility(View.VISIBLE);
					qrStrEditText2.setHint("电子邮箱");
					qrStrEditText2.getLayoutParams().height =55;
					
					lianxidianhua.setVisibility(View.VISIBLE);
					lianxidianhua.setHint("联系号码");
					lianxidianhua.getLayoutParams().height =55;
					
					lianxidizhi.setVisibility(View.VISIBLE);
					lianxidizhi.setHint("联系地址");
					//lianxidizhi.getLayoutParams().height =32;
					
					gongsimingzi.setVisibility(View.VISIBLE);
					gongsimingzi.setHint("公司名字");
					//gongsimingzi.getLayoutParams().height =32;
					
					gongsizhiwei.setVisibility(View.VISIBLE);
					gongsizhiwei.setHint("公司职位");
					gongsizhiwei.getLayoutParams().height =55;
					
					zhuyedizhi.setVisibility(View.VISIBLE);
					zhuyedizhi.setHint("主页地址");
					//zhuyedizhi.getLayoutParams().height =32;
					
					qrStrEditText3.setVisibility(View.VISIBLE);
					qrStrEditText3.setHint("备注");
					publicString1 = "BEGIN:VCARD";
					
				} else if (bundle.getString("String").equals("youjianbutton")) {
					titlename.setText("邮件二维码");
					qrStrEditText.setHint("收件人");
					qrStrEditText2.setVisibility(View.VISIBLE);
					qrStrEditText2.setHint("标题");
					qrStrEditText3.setVisibility(View.VISIBLE);
					qrStrEditText3.setHint("邮件内容");
					qrStrEditText3.getLayoutParams().height =120;
					publicString1 = "MATMSG:TO:";
				}
			}
		}

		Button generateQRCodeButton = (Button) this
				.findViewById(R.id.btn_add_qrcode);
		generateQRCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String contentString = "";

					if (bundle != null) {
						if (ErWeiMaCreateActivity.this.getIntent().hasExtra("String")) {
							if (bundle.getString("String").equals("wenbenbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									contentString = publicString1+ qrStrEditText.getText().toString();
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"请输入内容", 1000).show();
								}
							} else if (bundle.getString("String").equals("duanxinbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									if (!StringUtil.isNull(qrStrEditText2.getText().toString())) {
										contentString = publicString1+ qrStrEditText.getText().toString()
												+ ":"+ qrStrEditText2.getText().toString();
									} else {
										Toast.makeText(ErWeiMaCreateActivity.this,"请输入短信内容", 1000).show();
									}
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"请输入短信号码", 1000).show();
								}
							} else if (bundle.getString("String").equals("URLbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									contentString = publicString1+ qrStrEditText.getText().toString();
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"请输入网址", 1000).show();
								}
							} else if (bundle.getString("String").equals(
									"telbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText()
										.toString())) {
									contentString = publicString1+ qrStrEditText.getText().toString();
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"请输入号码", 1000).show();
								}
							} else if (bundle.getString("String").equals(
									"mingpianbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
										contentString = publicString1+ '\n'+"VERSION:3.0"+'\n'+"N:"
										+qrStrEditText.getText().toString()+'\n'+"EMAIL:"+qrStrEditText2.getText().toString()
										+'\n'+"TEL:"+lianxidianhua.getText().toString()+'\n'+"ADR:"+
										lianxidizhi.getText().toString()+'\n'+"ORG:"+gongsimingzi.getText().toString()
										+'\n'+"TITLE:"+gongsizhiwei.getText().toString()+'\n'+"URL:"+
										zhuyedizhi.getText().toString()+'\n'+"NOTE:"+qrStrEditText3.getText().toString()
										+'\n'+"END:VCARD"
										;
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"请输入名片名字", 1000).show();
								}
							} else if (bundle.getString("String").equals("youjianbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									if (!StringUtil.isNull(qrStrEditText3.getText().toString())) {
										contentString = publicString1+ qrStrEditText.getText().toString()
												+ ";SUB:"+ qrStrEditText2.getText().toString()
												+ ";BODY:"+ qrStrEditText3.getText().toString() + ";;";
									} else {
										Toast.makeText(ErWeiMaCreateActivity.this,"请输入邮箱内容", 1000).show();
									}

								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"请输入邮箱收件人", 1000).show();
								}
							}
						}
					}

					if (!contentString.equals("")) {
						// 根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
						qrCodeBitmap = EncodingHandler.createQRCode(
								contentString, 350);

						Intent intent = new Intent(ErWeiMaCreateActivity.this,
								ErWeiMaChaKanActivity.class);
						startActivity(intent);

					} else {
						Toast.makeText(ErWeiMaCreateActivity.this,"请输入内容", 1000).show();
					}

				} catch (WriterException e) {
					e.printStackTrace();
				}
			}
		});
		ResultHandler.ErWeiMaCreateActivity = this;
	}

	public static Bitmap erWeiMaMap() {
		if (qrCodeBitmap != null) {
			return qrCodeBitmap;
		} else {
			return null;
		}
	}

}
