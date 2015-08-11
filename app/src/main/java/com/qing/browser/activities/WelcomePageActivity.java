package com.qing.browser.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.qing.browser.R;

public class WelcomePageActivity extends Activity {
	private List<View> show = new ArrayList<View>();
	private ImageView curDot;
	private int[] pics = { R.drawable.frist, R.drawable.second,
	        R.drawable.thrid, R.drawable.four, R.drawable.five };
	private ImageView open;
	private int offset;
	private int curPos = 0;// 记录当前的位置
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_show);

		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(WelcomePageActivity.this);
			iv.setImageResource(pics[i]);
			ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(
			        ViewGroup.LayoutParams.MATCH_PARENT,
			        ViewGroup.LayoutParams.MATCH_PARENT);
			iv.setLayoutParams(vl);
			iv.setScaleType(ScaleType.FIT_XY);
			show.add(iv);

		}
		curDot = (ImageView) findViewById(R.id.cur_dot);
		open = (ImageView) findViewById(R.id.open);
		open.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});
		curDot.getViewTreeObserver().addOnPreDrawListener(
		        new OnPreDrawListener() {

			        public boolean onPreDraw() {
				        offset = curDot.getWidth();
				        return true;
			        }
		        });
		GuidePagerAdapter adapter = new GuidePagerAdapter(show);
		ViewPager pager = (ViewPager) findViewById(R.id.contentPager);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				// 如果图片为最后一张就应该弹出进入 窗口了 
				moveCursorTo(arg0);
				if (arg0 == (pics.length - 1)) {
					handler.sendEmptyMessageDelayed(0, 500);
				} else {
					handler.sendEmptyMessageDelayed(1, 200);
				}
				curPos = arg0;
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 移动指针到相邻的位置
	 * 
	 * @param position
	 *            指针的索引值
	 * */
	private void moveCursorTo(int position) {
		TranslateAnimation anim = new TranslateAnimation(offset * curPos,
		        offset * position, 0, 0);
		anim.setDuration(300);
		anim.setFillAfter(true);
		curDot.startAnimation(anim);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				open.setVisibility(View.VISIBLE);
				break;
			case 1:
				open.setVisibility(View.GONE);
				break;
			}

		}

	};
	public class GuidePagerAdapter extends PagerAdapter{


		private List<View> views;
		
		public GuidePagerAdapter(List<View> views){
			this.views=views;
		}
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			
			((ViewPager)arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager)arg0).addView(views.get(arg1));
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0==(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}
	}
 
}
