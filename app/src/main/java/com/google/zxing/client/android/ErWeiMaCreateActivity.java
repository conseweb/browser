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
		titlename.setText("���ɶ�ά��");

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
					titlename.setText("�ı���ά��");
					qrStrEditText.setHint("����������");
					qrStrEditText.getLayoutParams().height =120;
				} else if (bundle.getString("String").equals("duanxinbutton")) {
					titlename.setText("���Ŷ�ά��");
					qrStrEditText.setHint("���ź���");
					qrStrEditText2.setVisibility(View.VISIBLE);
					qrStrEditText2.setHint("��������");
					qrStrEditText2.getLayoutParams().height =120;
					publicString1 = "SMSTO:";
				} else if (bundle.getString("String").equals("URLbutton")) {
					titlename.setText("��ַ��ά��");
					qrStrEditText.setHint("��������ַ");
					qrStrEditText.getLayoutParams().height =120;
					publicString1 = "http://";
				} else if (bundle.getString("String").equals("telbutton")) {
					titlename.setText("�绰��ά��");
					qrStrEditText.setHint("������绰����");
					qrStrEditText.getLayoutParams().height =120;
					publicString1 = "TEL:";
				} else if (bundle.getString("String").equals("mingpianbutton")) {
					titlename.setText("��Ƭ��ά��");
					publicString1 = "BEGIN:VCARD%0AVERSION:3.0%0AN:";
					qrStrEditText.setHint("����");
					qrStrEditText.getLayoutParams().height =55;
					
					qrStrEditText2.setVisibility(View.VISIBLE);
					qrStrEditText2.setHint("��������");
					qrStrEditText2.getLayoutParams().height =55;
					
					lianxidianhua.setVisibility(View.VISIBLE);
					lianxidianhua.setHint("��ϵ����");
					lianxidianhua.getLayoutParams().height =55;
					
					lianxidizhi.setVisibility(View.VISIBLE);
					lianxidizhi.setHint("��ϵ��ַ");
					//lianxidizhi.getLayoutParams().height =32;
					
					gongsimingzi.setVisibility(View.VISIBLE);
					gongsimingzi.setHint("��˾����");
					//gongsimingzi.getLayoutParams().height =32;
					
					gongsizhiwei.setVisibility(View.VISIBLE);
					gongsizhiwei.setHint("��˾ְλ");
					gongsizhiwei.getLayoutParams().height =55;
					
					zhuyedizhi.setVisibility(View.VISIBLE);
					zhuyedizhi.setHint("��ҳ��ַ");
					//zhuyedizhi.getLayoutParams().height =32;
					
					qrStrEditText3.setVisibility(View.VISIBLE);
					qrStrEditText3.setHint("��ע");
					publicString1 = "BEGIN:VCARD";
					
				} else if (bundle.getString("String").equals("youjianbutton")) {
					titlename.setText("�ʼ���ά��");
					qrStrEditText.setHint("�ռ���");
					qrStrEditText2.setVisibility(View.VISIBLE);
					qrStrEditText2.setHint("����");
					qrStrEditText3.setVisibility(View.VISIBLE);
					qrStrEditText3.setHint("�ʼ�����");
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
									Toast.makeText(ErWeiMaCreateActivity.this,"����������", 1000).show();
								}
							} else if (bundle.getString("String").equals("duanxinbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									if (!StringUtil.isNull(qrStrEditText2.getText().toString())) {
										contentString = publicString1+ qrStrEditText.getText().toString()
												+ ":"+ qrStrEditText2.getText().toString();
									} else {
										Toast.makeText(ErWeiMaCreateActivity.this,"�������������", 1000).show();
									}
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"��������ź���", 1000).show();
								}
							} else if (bundle.getString("String").equals("URLbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									contentString = publicString1+ qrStrEditText.getText().toString();
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"��������ַ", 1000).show();
								}
							} else if (bundle.getString("String").equals(
									"telbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText()
										.toString())) {
									contentString = publicString1+ qrStrEditText.getText().toString();
								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"���������", 1000).show();
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
									Toast.makeText(ErWeiMaCreateActivity.this,"��������Ƭ����", 1000).show();
								}
							} else if (bundle.getString("String").equals("youjianbutton")) {
								if (!StringUtil.isNull(qrStrEditText.getText().toString())) {
									if (!StringUtil.isNull(qrStrEditText3.getText().toString())) {
										contentString = publicString1+ qrStrEditText.getText().toString()
												+ ";SUB:"+ qrStrEditText2.getText().toString()
												+ ";BODY:"+ qrStrEditText3.getText().toString() + ";;";
									} else {
										Toast.makeText(ErWeiMaCreateActivity.this,"��������������", 1000).show();
									}

								} else {
									Toast.makeText(ErWeiMaCreateActivity.this,"�����������ռ���", 1000).show();
								}
							}
						}
					}

					if (!contentString.equals("")) {
						// �����ַ������ɶ�ά��ͼƬ����ʾ�ڽ����ϣ��ڶ�������ΪͼƬ�Ĵ�С��350*350��
						qrCodeBitmap = EncodingHandler.createQRCode(
								contentString, 350);

						Intent intent = new Intent(ErWeiMaCreateActivity.this,
								ErWeiMaChaKanActivity.class);
						startActivity(intent);

					} else {
						Toast.makeText(ErWeiMaCreateActivity.this,"����������", 1000).show();
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
