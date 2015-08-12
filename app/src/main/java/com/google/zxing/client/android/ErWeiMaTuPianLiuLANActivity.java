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
 * ImageSwitcher和Gallery如何展示SD卡中的资源图片
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
		textView.setText("历史图片浏览");
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

		/* 设定Switcher */
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		/* 设定载入Switcher的模式 */
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		/* 设定输出Switcher的模式 */
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		mSwitcher.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//点击了ImageSwitch上的图片

			}

		});

		mGallery = (Gallery) findViewById(R.id.mygallery);
		/* 新增几ImageAdapter并设定给Gallery对象 */
		mGallery.setAdapter(new ImageAdapter(this, getImagePathFromSD()));

		mGallery.setOnItemSelectedListener(this);

		/* 设定一个itemclickListener事件 */
		mGallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
						//点击了Gallery上的图片  下面这个
			}
		});

	}

	/** 从SD卡中获取资源图片的路径 */
	private List<String> getImagePathFromSD() {
		/* 设定目前所在路径 */
		List<String> it = new ArrayList<String>();

		// 根据自己的需求读取SDCard中的资源图片的路径
		String imagePath = Environment.getExternalStorageDirectory().toString()
				+ "/Qing/erweima";

		Log.v("L", "imagePath" + imagePath);

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
						Toast.makeText(ErWeiMaTuPianLiuLANActivity.this,"图片未扫描成功", 1000).show();
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
