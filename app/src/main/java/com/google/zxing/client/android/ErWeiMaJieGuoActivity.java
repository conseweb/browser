package com.google.zxing.client.android;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.common.HybridBinarizer;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.utils.Constants;

public final class ErWeiMaJieGuoActivity extends BaseActivity implements OnItemSelectedListener {
	private static Result rawResult; 
	private static Bitmap barcode;
	private ImageView barcodeImageView = null;
	private boolean copyToClipboard;
	private Bundle bundle = null;
	private Gallery mGallery = null;
	private String tupianname = "";
	private String[] list;
	private List<String> imagePathList;
	private TextView textView = null;
	private ImageView item_back = null;
	public static ErWeiMaJieGuoActivity jieguoAcvivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_jieguo);
		
		barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true);
		
		jieguoAcvivity = this;
		
		bundle = getIntent().getExtras();
		if (bundle != null) {
			if (this.getIntent().hasExtra("String")) {
				if(bundle.getString("String").equals("tupianliulan")){
					
					mGallery = (Gallery) findViewById(R.id.mygallery);
					mGallery.setVisibility(View.VISIBLE);
					/* 新增几ImageAdapter并设定给Gallery对象 */
					mGallery.setAdapter(new ImageAdapter(ErWeiMaJieGuoActivity.this, getImagePathFromSD()));

					mGallery.setOnItemSelectedListener(this);

					/* 设定一个itemclickListener事件 */
					mGallery.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View v,
								int position, long id) {
									//点击了Gallery上的图片  下面这个
						}
					});
					
					imagePathList = getImagePathFromSD();
					list = imagePathList.toArray(new String[imagePathList.size()]);
					

				    
				    if(imagePathList.isEmpty()){
				    	setContentView(R.layout.er_wei_ma_li_shi_cha_kan_shi_bai);
				        textView =(TextView)findViewById(R.id.item_title);
				    	textView.setText("二维码历史");
				    	item_back = (ImageView)findViewById(R.id.item_back);
				    	item_back.setOnClickListener(new OnClickListener(){
				    		public void onClick(View v) {
				    			finish();	
				    		}});
				    	Button loading_Button = (Button)findViewById(R.id.loading_Button);
				    	loading_Button.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								finish();
								Intent intent = new Intent(ErWeiMaJieGuoActivity.this,
										ErWeiMaEntryActivity.class);
								startActivity(intent);	
							}
						});
				    }
				    return;
				}
				
			}
		}
		
		
		showErWeiMa();
		
		
	}
	
	private void showErWeiMa(){
		if (barcode == null) {
		      barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
		          R.drawable.launcher_icon));
		    } else {
		      barcodeImageView.setImageBitmap(barcode);
		    }

		    TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
		    formatTextView.setText(rawResult.getBarcodeFormat().toString());
		    

		    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
		    TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
		    typeTextView.setText(resultHandler.getType().toString());

		    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		    String formattedTime = formatter.format(new Date(rawResult.getTimestamp()));
		    TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
		    timeTextView.setText(formattedTime);


		    TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
		    View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
		    metaTextView.setVisibility(View.GONE);
		    metaTextViewLabel.setVisibility(View.GONE);
		    Map<ResultMetadataType,Object> metadata =
		        (Map<ResultMetadataType,Object>) rawResult.getResultMetadata();
		    if (metadata != null) {
		      StringBuilder metadataText = new StringBuilder(20);
		      for (Map.Entry<ResultMetadataType,Object> entry : metadata.entrySet()) {
		        if (CaptureActivity.DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
		          metadataText.append(entry.getValue()).append('\n');
		        }
		      }
		      if (metadataText.length() > 0) {
		        metadataText.setLength(metadataText.length() - 1);
		        metaTextView.setText(metadataText);
		        metaTextView.setVisibility(View.VISIBLE);
		        metaTextViewLabel.setVisibility(View.VISIBLE);
		      }
		    }

		    Button button_cancer = (Button) findViewById(R.id.button_cancer);
		    button_cancer.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		    
		    TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
		    CharSequence displayContents = resultHandler.getDisplayContents();
		    contentsTextView.setText(displayContents);
		    // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
		    //int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
		   // contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

		    int buttonCount = resultHandler.getButtonCount();
		    ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
		    buttonView.requestFocus();
		    for (int x = 0; x < ResultHandler.MAX_BUTTON_COUNT; x++) {
		      TextView button = (TextView) buttonView.getChildAt(x);
		      if (x < buttonCount) {
		        button.setVisibility(View.VISIBLE);
		        button.setText(resultHandler.getButtonText(x));
		        button.setOnClickListener(new ResultButtonListener(resultHandler, x));
		      } else {
		        button.setVisibility(View.GONE);
		      }
		    }

		    if (copyToClipboard) {
		      ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		      clipboard.setText(displayContents);
		    }
	}

	public static void SetResult(Result rawResult1){
		rawResult = rawResult1;
	}
	
	static public void Setbarcode(Bitmap barcode1){
		barcode = barcode1;
	}
	
	
	/** 从SD卡中获取资源图片的路径 */
	private List<String> getImagePathFromSD() {
		/* 设定目前所在路径 */
		List<String> it = new ArrayList<String>();

		File dirFile1 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/");
		if (!dirFile1.exists()) {
			dirFile1.mkdir();
		}
		File dirFile2 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/erweima/");
		if (!dirFile2.exists()) {
			dirFile2.mkdir();
		}
		
		// 根据自己的需求读取SDCard中的资源图片的路径
		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/Qing/erweima";

		File mFile = new File(imagePath);
		File[] files = mFile.listFiles();

		/* 将所有文件存入ArrayList中 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (checkIsImageFile(file.getPath()))
				it.add(file.getPath());
		}
		return it;
	}

	/** 判断是否相应的图片格式 */
	private boolean checkIsImageFile(String fName) {
		boolean isImageFormat;

		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 按扩展名的类型决定MimeType */
		if (end.equals("png") || end.equals("jpeg")) {
			isImageFormat = true;
		} else {
			isImageFormat = false;
		}
		return isImageFormat;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String photoURL = list[position];		
		Log.v("L","photoURl=="+photoURL);

		tupianname = photoURL.toString().replace(Environment.getExternalStorageDirectory().toString()
				+ "/Qing/erweima/", "");

		if(photoURL!=null)
		{
			try {
				Log.v("L","nameUrl="+photoURL);
				FileInputStream fis = new FileInputStream(photoURL);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				
				if (null != bitmap) {
					BinaryBitmap localBinaryBitmap = new BinaryBitmap(
							new HybridBinarizer(new RGBLuminanceSource(
									bitmap)));
					try {	
						Result rawResult1 = new MultiFormatReader()
								.decode(localBinaryBitmap);
						rawResult = rawResult1;
						barcode = bitmap;
						showErWeiMa();
					} catch (com.google.zxing.NotFoundException e) {
						fis.close();
						Toast.makeText(ErWeiMaJieGuoActivity.this,"图片未扫描成功",1000).show();
						finish();
						e.printStackTrace();
					}
				}
				fis.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
	
	/* 改写BaseAdapter自定义一ImageAdapter class */
	public class ImageAdapter extends BaseAdapter {
		/* 声明变量 */
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> lis;

		/* ImageAdapter的构造符 */
		public ImageAdapter(Context c, List<String> li) {
			mContext = c;
			lis = li;
			/*
			 * 使用res/values/attrs.xml中的<declare-styleable>定义 的Gallery属性.
			 */
			TypedArray mTypeArray = obtainStyledAttributes(R.styleable.Gallery);

			/* 取得Gallery属性的Index id */
			mGalleryItemBackground = mTypeArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			/* 让对象的styleable属性能够反复使用 */
			mTypeArray.recycle();
		}

		/* 重写的方法getCount,传回图片数目 */
		public int getCount() {
			return lis.size();
		}

		/* 重写的方法getItem,传回position */
		public Object getItem(int position) {
			return position;
		}

		/* 重写的方法getItemId,传并position */
		public long getItemId(int position) {
			return position;
		}

		/* 重写方法getView,传并几View对象 */
		public View getView(int position, View convertView, ViewGroup parent) {
			/* 产生ImageView对象 */
			ImageView i = new ImageView(mContext);
			/* 设定图片给imageView对象 */
			Bitmap bm = BitmapFactory.decodeFile(lis.get(position).toString());
			
			i.setImageBitmap(bm);
			/*
			 * 重新设定图片的宽高 i.setScaleType(ImageView.ScaleType.FIT_XY);
			 * 重新设定Layout的宽高 i.setLayoutParams(new Gallery.LayoutParams(136,
			 * 88));
			 */

			i.setAdjustViewBounds(true); // 允许调整边框
			// 设定底部画廊，自适应大小
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// 设置画廊背景

			/* 设定Gallery背景图 */
			i.setBackgroundResource(mGalleryItemBackground);
			/* 传回imageView对象 */
			return i;
		}
	}
	
}
