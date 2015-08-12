package com.google.zxing.client.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.encode.EncodingHandler;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.common.HybridBinarizer;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.utils.Tools;

public class ErWeiMaLiShilistActivity extends BaseActivity {

	private TextView textView = null;
	private ImageView item_back = null;
	private static Bitmap qrCodeBitmap = null;
	private TextView title_search = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_li_shi_list);
		textView = (TextView) findViewById(R.id.item_title);
		title_search = (TextView) findViewById(R.id.title_search);
		title_search.setText("清除记录");
		title_search.setVisibility(View.VISIBLE);
		textView.setText("二维码历史");
		item_back = (ImageView) findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		title_search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Tools.sdCardExist()) {
					File file = null;
					try {
						file = new File(ErWeiMaChaKanActivity.getSDPath()
								+ "/Qing/erweima/erweimajilu.txt");
						if(file.delete()){
							Toast.makeText(ErWeiMaLiShilistActivity.this,"清除记录成功!", 1000).show();
							finish();
						}else{
							Toast.makeText(ErWeiMaLiShilistActivity.this,"清除记录失败", 1000).show();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		ListView lv = (ListView) findViewById(R.id.list);

		final ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();
		JSONArray jsonArray = null;
		try {
			if (readErWeiMaLiShi() != null) {
				jsonArray = new JSONArray("[" + readErWeiMaLiShi() + "]");
				Log.v("L", "jsonArray=" + jsonArray);
			} else {
				setContentView(R.layout.er_wei_ma_li_shi_cha_kan_shi_bai);
		        textView =(TextView)findViewById(R.id.item_title);
		    	textView.setText("二维码历史");
		    	item_back = (ImageView)findViewById(R.id.item_back);
		    	item_back.setOnClickListener(new OnClickListener(){
		    		public void onClick(View v) {
		    			finish();	
		    		}});
		    	Button loading_Button = (Button)findViewById(R.id.loading_Button);
		    	TextView loading_text = (TextView)findViewById(R.id.loading_text);
		    	loading_text.setText("您还没有扫描历史记录哦！");
		    	loading_Button.setText("立即扫描");
		    	loading_Button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
						Intent intent = new Intent(ErWeiMaLiShilistActivity.this,
								CaptureActivity.class);
						startActivity(intent);	
					}
				});
			}

			if (jsonArray != null) {
				if (jsonArray.length() != 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						HashMap map = new HashMap<String, Object>();
						try {
							JSONObject jsonObj = jsonArray.getJSONObject(i);
							if (!jsonObj.isNull("neirong")) {
								map.put("name", jsonObj.getString("neirong"));
							}
							if (!jsonObj.isNull("time")) {
								map.put("time", "日期:"+jsonObj.getString("time"));
							}

						} catch (JSONException e) {
							Log.v("L", "handler error");
							e.printStackTrace();
						}

						Log.v("L", "map=" + map);
						if (i != 0) {
							al.add(map);
						}
					}
				}
			}
		} catch (JSONException e1) {
			Log.v("L", "原来是出错了");
			e1.printStackTrace();
		}

		SimpleAdapter sap = new SimpleAdapter(this, al,
				R.layout.private_contact_item,
				new String[] { "name", "time" }, new int[] { R.id.text_name,
						R.id.text_number });
		lv.setAdapter(sap);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				try {
					qrCodeBitmap = EncodingHandler.createQRCode(al.get((int) id).toString().substring(
							al.get((int) id).toString().indexOf("name=") +5, 
							al.get((int) id).toString().indexOf("}")), 350);
				} catch (WriterException e) {
					e.printStackTrace();
				}
				

			    if(qrCodeBitmap!=null){
			    	textView.setText("扫描历史");
					BinaryBitmap localBinaryBitmap = new BinaryBitmap(
							new HybridBinarizer(new RGBLuminanceSource(qrCodeBitmap)));
					try {	
						Result rawResult = new MultiFormatReader()
								.decode(localBinaryBitmap);
						
						ErWeiMaJieGuoActivity.SetResult(rawResult);
					    ErWeiMaJieGuoActivity.Setbarcode(qrCodeBitmap);
					    Intent intent = new Intent(ErWeiMaLiShilistActivity.this,
								ErWeiMaJieGuoActivity.class);
						startActivity(intent);	
						
					} catch (com.google.zxing.NotFoundException e) {
						Toast.makeText(ErWeiMaLiShilistActivity.this,"很抱歉，读取历史出错了~",1000).show();
						e.printStackTrace();
					}
				}
			

			}
		});
		
		ResultHandler.ErWeiMaLiShilistActivity = this;
	}

	/**
	 * 读取二维码的历史记录
	 * 
	 * @return
	 */
	private String readErWeiMaLiShi() {
		String str = null;
		if (Tools.sdCardExist()) {
			File file = null;
			try {
				file = new File(ErWeiMaChaKanActivity.getSDPath()
						+ "/Qing/erweima/erweimajilu.txt");
				if (file.exists()) {
					FileInputStream inStream = new FileInputStream(file);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					byte[] buffer = new byte[32];
					int length = -1;
					while ((length = inStream.read(buffer)) != -1) {
						stream.write(buffer, 0, length);
					}
					str = stream.toString();
					stream.close();
					inStream.close();
					Log.v("L", "字符串是这个===" + str);
					return str;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {

			}
		}
		return str;
	}
}
