package com.google.zxing.client.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;

/**
 * ImageSwitcher��Gallery���չʾSD���е���ԴͼƬ
 */

public class ErWeiMaTuPianLiuLANActivity extends BaseActivity implements
		OnItemSelectedListener, ViewFactory {

	private List<String> imagePathList;
	private String[] list;
	private ImageSwitcher mSwitcher;
	private Gallery mGallery;

	private TextView textView = null;
	private ImageView item_back = null;
	private TextView tupianname = null;
	public  static String nameUrl = null;
	private TextView neirong = null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_tu_pian_liu_lan);

		textView = (TextView) findViewById(R.id.item_title);
		textView.setText("��ʷͼƬ���");
		item_back = (ImageView) findViewById(R.id.item_back);
		tupianname = (TextView) findViewById(R.id.tupianname);
		neirong = (TextView) findViewById(R.id.neirong);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		imagePathList = getImagePathFromSD();
		list = imagePathList.toArray(new String[imagePathList.size()]);

		/* �趨Switcher */
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		/* �趨����Switcher��ģʽ */
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		/* �趨���Switcher��ģʽ */
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		mSwitcher.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//�����ImageSwitch�ϵ�ͼƬ

			}

		});

		mGallery = (Gallery) findViewById(R.id.mygallery);
		/* ������ImageAdapter���趨��Gallery���� */
		mGallery.setAdapter(new ImageAdapter(this, getImagePathFromSD()));

		mGallery.setOnItemSelectedListener(this);

		/* �趨һ��itemclickListener�¼� */
		mGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
						//�����Gallery�ϵ�ͼƬ  �������
			}
		});

	}

	/** ��SD���л�ȡ��ԴͼƬ��·�� */
	private List<String> getImagePathFromSD() {
		/* �趨Ŀǰ����·�� */
		List<String> it = new ArrayList<String>();

		// �����Լ��������ȡSDCard�е���ԴͼƬ��·��
		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/Qing/erweima";

		Log.v("L", "imagePath" + imagePath);

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

	@Override
	public View makeView() {
		ImageView iv = new ImageView(this);
		iv.setBackgroundColor(0xFF000000);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iv.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return iv;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String photoURL = list[position];
		
		Log.v("L","photoURl=="+photoURL);
		
		nameUrl = photoURL;//(imagePathList.get((int) id));
		tupianname.setText(nameUrl.toString().replace(Environment.getExternalStorageDirectory().toString()
				+ "/Qing/erweima", ""));

		if(nameUrl!=null)
		{
			try {
				Log.v("L","nameUrl="+nameUrl);
				FileInputStream fis = new FileInputStream(nameUrl);
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				
				if (null != bitmap) {
					BinaryBitmap localBinaryBitmap = new BinaryBitmap(
							new HybridBinarizer(new RGBLuminanceSource(
									bitmap)));
					try {	
						Result rawResult = new MultiFormatReader()
								.decode(localBinaryBitmap);
						
						Log.v("L","rawResult="+rawResult.getText());
						
						neirong.setText(rawResult.getText());
				
					} catch (com.google.zxing.NotFoundException e) {
						fis.close();
						Toast.makeText(ErWeiMaTuPianLiuLANActivity.this,"ͼƬδɨ��ɹ�", 1000).show();
						finish();
						e.printStackTrace();
					}
				}
				fis.close();
			} catch (FileNotFoundException e1) {
				Log.v("L","FileNotFoundException=");
				e1.printStackTrace();
			} catch (IOException e) {
				Log.v("L","IOException=");
				e.printStackTrace();
			}
		}
		mSwitcher.setImageURI(Uri.parse(photoURL));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
}
