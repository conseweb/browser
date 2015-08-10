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
					/* ������ImageAdapter���趨��Gallery���� */
					mGallery.setAdapter(new ImageAdapter(ErWeiMaJieGuoActivity.this, getImagePathFromSD()));

					mGallery.setOnItemSelectedListener(this);

					/* �趨һ��itemclickListener�¼� */
					mGallery.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View v,
								int position, long id) {
									//�����Gallery�ϵ�ͼƬ  �������
						}
					});
					
					imagePathList = getImagePathFromSD();
					list = imagePathList.toArray(new String[imagePathList.size()]);
					

				    
				    if(imagePathList.isEmpty()){
				    	setContentView(R.layout.er_wei_ma_li_shi_cha_kan_shi_bai);
				        textView =(TextView)findViewById(R.id.item_title);
				    	textView.setText("��ά����ʷ");
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
	
	
	/** ��SD���л�ȡ��ԴͼƬ��·�� */
	private List<String> getImagePathFromSD() {
		/* �趨Ŀǰ����·�� */
		List<String> it = new ArrayList<String>();

		File dirFile1 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/");
		if (!dirFile1.exists()) {
			dirFile1.mkdir();
		}
		File dirFile2 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/erweima/");
		if (!dirFile2.exists()) {
			dirFile2.mkdir();
		}
		
		// �����Լ��������ȡSDCard�е���ԴͼƬ��·��
		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/Qing/erweima";

		File mFile = new File(imagePath);
		File[] files = mFile.listFiles();

		/* �������ļ�����ArrayList�� */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (checkIsImageFile(file.getPath()))
				it.add(file.getPath());
		}
		return it;
	}

	/** �ж��Ƿ���Ӧ��ͼƬ��ʽ */
	private boolean checkIsImageFile(String fName) {
		boolean isImageFormat;

		/* ȡ����չ�� */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* ����չ�������;���MimeType */
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
						Toast.makeText(ErWeiMaJieGuoActivity.this,"ͼƬδɨ��ɹ�",1000).show();
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
	
	/* ��дBaseAdapter�Զ���һImageAdapter class */
	public class ImageAdapter extends BaseAdapter {
		/* �������� */
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> lis;

		/* ImageAdapter�Ĺ���� */
		public ImageAdapter(Context c, List<String> li) {
			mContext = c;
			lis = li;
			/*
			 * ʹ��res/values/attrs.xml�е�<declare-styleable>���� ��Gallery����.
			 */
			TypedArray mTypeArray = obtainStyledAttributes(R.styleable.Gallery);

			/* ȡ��Gallery���Ե�Index id */
			mGalleryItemBackground = mTypeArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			/* �ö����styleable�����ܹ�����ʹ�� */
			mTypeArray.recycle();
		}

		/* ��д�ķ���getCount,����ͼƬ��Ŀ */
		public int getCount() {
			return lis.size();
		}

		/* ��д�ķ���getItem,����position */
		public Object getItem(int position) {
			return position;
		}

		/* ��д�ķ���getItemId,����position */
		public long getItemId(int position) {
			return position;
		}

		/* ��д����getView,������View���� */
		public View getView(int position, View convertView, ViewGroup parent) {
			/* ����ImageView���� */
			ImageView i = new ImageView(mContext);
			/* �趨ͼƬ��imageView���� */
			Bitmap bm = BitmapFactory.decodeFile(lis.get(position).toString());
			
			i.setImageBitmap(bm);
			/*
			 * �����趨ͼƬ�Ŀ�� i.setScaleType(ImageView.ScaleType.FIT_XY);
			 * �����趨Layout�Ŀ�� i.setLayoutParams(new Gallery.LayoutParams(136,
			 * 88));
			 */

			i.setAdjustViewBounds(true); // ��������߿�
			// �趨�ײ����ȣ�����Ӧ��С
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// ���û��ȱ���

			/* �趨Gallery����ͼ */
			i.setBackgroundResource(mGalleryItemBackground);
			/* ����imageView���� */
			return i;
		}
	}
	
}
