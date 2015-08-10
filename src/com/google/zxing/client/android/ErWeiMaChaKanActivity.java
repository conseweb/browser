package com.google.zxing.client.android;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.result.ResultHandler;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.utils.StringUtil;

public class ErWeiMaChaKanActivity extends BaseActivity {

	private TextView textView = null;
	private ImageView item_back = null;
	private ImageView iv_qr_image = null;
	private Button baochunbutton = null;
	private Button chakanbutton = null;
	private EditText tupianname_edit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_cha_kan);

		textView = (TextView) findViewById(R.id.item_title);
		textView.setText("二维码图片");
		item_back = (ImageView) findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		iv_qr_image = (ImageView) findViewById(R.id.iv_qr_image);
		baochunbutton = (Button) findViewById(R.id.baochunbutton);
		chakanbutton = (Button) findViewById(R.id.chakanbutton);
		tupianname_edit = (EditText) findViewById(R.id.tupianname_edit);

		chakanbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(getSDPath()!=null){
					Intent intent = new Intent(ErWeiMaChaKanActivity.this,
							ErWeiMaJieGuoActivity.class);
					intent.putExtra("String", "tupianliulan");
					startActivity(intent);
				}else{
					Toast.makeText(ErWeiMaChaKanActivity.this, "SD卡已卸载或不存在", 1000).show();
				}
			}
		});
		
		if (ErWeiMaCreateActivity.erWeiMaMap() != null) {

			iv_qr_image.setImageBitmap(ErWeiMaCreateActivity.erWeiMaMap());

			baochunbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!StringUtil
							.isNull(tupianname_edit.getText().toString())) {
						try {
							if(
							saveFile(ErWeiMaCreateActivity.erWeiMaMap(),
									tupianname_edit.getText().toString()
											+ ".png")){

								Toast.makeText(ErWeiMaChaKanActivity.this, "/sdcard/Qing/erweima/"
										+ tupianname_edit.getText()
										.toString() + ".png"
								+ "已经生成", 1000).show();
							}else {
								Toast.makeText(ErWeiMaChaKanActivity.this, "SD卡已卸载或不存在", 1000).show();
							}
						} catch (IOException e) {
							Log.v("L", "出错咯~~");
							e.printStackTrace();
						}
					} else {
						Toast.makeText(ErWeiMaChaKanActivity.this, "请输入图片的名字", 1000).show();
					}
				}
			});

		}
		
		ResultHandler.ErWeiMaChaKanActivity = this;

	}

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		if(sdDir ==null)
			return null;
		return sdDir.toString();
	}

	/**
	 * 保存文件
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public boolean saveFile(Bitmap bm, String fileName) throws IOException {
		
		if(getSDPath()==null)
			return false;
		
		File dirFile1 = new File(getSDPath() + "/Qing/");
		if (!dirFile1.exists()) {
			dirFile1.mkdir();
		}
		
		String path = getSDPath() + "/Qing/erweima/";
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(path + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
		bos.flush();
		bos.close();
		return true;
	}

}
