package com.qing.browser.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.qing.browser.R;

public class ImageNumberView extends ImageView {
	public String number;
	private PaintFlagsDrawFilter pfd;
	Resources res = getResources();

	public ImageNumberView(Context context) {
		super(context);
	}

	public ImageNumberView(Context context, AttributeSet set) {
		super(context, set);
	}

	// 画图类
	// public void onDraw(Canvas canvas)
	// {
	//
	// super.onDraw(canvas);
	// Paint mPaint = new Paint();
	// pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG |
	// Paint.FILTER_BITMAP_FLAG);
	// canvas.setDrawFilter(pfd);
	//
	// String familyName = "Arial Rounded MT Bold";
	// Typeface font = Typeface.create(familyName, Typeface.BOLD);
	// mPaint.setColor(Color.BLACK);
	// mPaint.setTypeface(font);
	// mPaint.setTextSize(20);
	// if (number != null && !number.equals("0"))
	// {
	// if (number != null && number.length() == 1)
	// {
	// Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.num_bg3);
	//
	// canvas.drawBitmap(bmp, this.getWidth()-bmp.getWidth(),
	// this.getHeight()-bmp.getHeight(), mPaint);
	// canvas.drawText(number, this.getWidth()-bmp.getWidth()/2-5,
	// this.getHeight()-bmp.getHeight()/2+6, mPaint);
	// } else if (number != null && number.length() == 2)
	// {
	// Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.num_bg2);
	// canvas.drawBitmap(bmp, 17, 12, mPaint);
	// canvas.drawText(number, 24, 21, mPaint);
	// } else if (number != null && number.length() == 3)
	// {
	// Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.num_bg3);
	// canvas.drawBitmap(bmp, 12, 12, mPaint);
	// canvas.drawText(number, 19, 21, mPaint);
	// }
	// }
	//
	// }

	/**
	 * 在给定的图片的右上角加上联系人数量。数量用红色表示
	 * 
	 * @param icon
	 *            给定的图片
	 * @return 带联系人数量的图片
	 */
	public Bitmap generatorContactCountIcon(Bitmap icon, int number) {
		// 初始化画布
		// int
		// iconSize=(int)getResources().getDimension(android.R.dimen.app_icon_size);
		// Log.d(TAG, "the icon size is "+iconSize);

		// Bitmap contactIcon=Bitmap.createBitmap(this.getWidth(),
		// this.getHeight(), Config.ARGB_8888);
		Bitmap contactIcon = Bitmap.createBitmap(icon.getWidth(),
				icon.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(contactIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		canvas.drawBitmap(icon, src, dst, iconPaint);

		// 在图片上创建一个覆盖的联系人个数
		int contacyCount = number;
		Bitmap bmp = BitmapFactory.decodeResource(res,
				R.drawable.hotseat_browser_bg);
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		// countPaint.setColor(Color.parseColor("#f63003"));
		countPaint.setColor(Color.WHITE);
		countPaint.setTextSize(35);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		countPaint.setAntiAlias(true);
		// countPaint.setShadowLayer (5, 3, 3, 0xFF000000);//加阴影
		canvas.drawBitmap(bmp, 135, 1, countPaint);
		canvas.drawText(String.valueOf(contacyCount), 143, 35, countPaint);
		return contactIcon;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}