package com.qing.browser.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.ui.MyPagerAdapter;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.utils.Constants;

public class BookmarksHistoryActivity extends BaseActivity {
	private int currentView = 0;// 当前视图
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private View viewpage1, viewpage2;
	private TextView tv_bookmark, tv_history;
	private LinearLayout tv_bookmark_layout, tv_history_layout;
	private BookmarksListActivity bookmarkslist;
	private HistoryListActivity historylist;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bookmarks_history_activity);
		ImageView btn_back = (ImageView) findViewById(R.id.item_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent result = new Intent();
				result.putExtra(Constants.EXTRA_ID_URL, "");
				result.putExtra(Constants.EXTRA_ID_NEW_TAB, false);
				setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, result);
				finish();
			}
		});

		InitViewPager();
	}

	/**
	 * 更新当前页码
	 */
	public void setCurPage(int page) {

		if (page == currentView)
			return;
		switch (page) {
		case 0:
			tv_bookmark.setTextColor(Color.parseColor("#FFFFFF"));
			tv_bookmark_layout
					.setBackgroundResource(R.drawable.title_select_bg);

			tv_history.setTextColor(Color.parseColor("#666666"));
			tv_history_layout.setBackgroundResource(0);
			break;
		case 1:

			tv_bookmark.setTextColor(Color.parseColor("#666666"));
			tv_bookmark_layout.setBackgroundResource(0);

			tv_history.setTextColor(Color.parseColor("#FFFFFF"));
			tv_history_layout.setBackgroundResource(R.drawable.title_select_bg);

			break;
		}

		currentView = page;

	}

	/**
	 * 初始化页卡内容ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		tv_bookmark = (TextView) findViewById(R.id.tv_bookmark);
		tv_history = (TextView) findViewById(R.id.tv_history);

		tv_bookmark_layout = (LinearLayout) findViewById(R.id.tv_bookmark_layout);
		tv_history_layout = (LinearLayout) findViewById(R.id.tv_history_layout);

		tv_bookmark.setOnClickListener(new MyOnClickListener(0));
		tv_history.setOnClickListener(new MyOnClickListener(1));

		setCurPage(0);
		tv_bookmark.setTextColor(Color.parseColor("#FFFFFF"));
		tv_bookmark_layout.setBackgroundResource(R.drawable.title_select_bg);

		listViews = new ArrayList<View>();

		bookmarkslist = new BookmarksListActivity();
		historylist = new HistoryListActivity();

		LayoutInflater mInflater = getLayoutInflater();
		viewpage1 = mInflater.inflate(R.layout.bookmarks_list_activity, null);
		viewpage2 = mInflater.inflate(R.layout.history_list_activity, null);
		listViews.add(viewpage1);
		listViews.add(viewpage2);

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// mPager.setOnTouchListener(new OnTouchListener() {
		// public boolean onTouch(View v, MotionEvent event) {
		// return true;// 禁止滑动
		// }
		//
		// });

		bookmarkslist.iList(BookmarksHistoryActivity.this, viewpage1);

	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 实现页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int arg0) {

			setCurPage(arg0);

			if (arg0 == 0) {
				bookmarkslist.iList(BookmarksHistoryActivity.this, viewpage1);
			} else if (arg0 == 1) {
				historylist.iList(BookmarksHistoryActivity.this, viewpage2);
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode != RESULT_CANCELED) {
			if (requestCode == EditBookmarkActivity.EditBookmark_OK) {
				if (intent != null) {
					bookmarkslist.fillData();
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent result = new Intent();
			result.putExtra(Constants.EXTRA_ID_URL, "");
			result.putExtra(Constants.EXTRA_ID_NEW_TAB, false);
			setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, result);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
