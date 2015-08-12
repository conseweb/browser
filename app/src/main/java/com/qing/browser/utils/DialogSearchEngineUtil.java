package com.qing.browser.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.ui.launcher.Launcher;

public class DialogSearchEngineUtil extends Dialog {

	private Launcher mLauncher;
	private Button btn_cancel;
	private GridView dragGridView;

	String[] itemSub = { "百度", "谷歌", "当当", "搜狗", "淘宝", "新浪", "易查",
			"亚马逊", "宜搜" };
	final int[] imageSub = { R.drawable.search_baidu,
			R.drawable.search_guge, R.drawable.earch_dangdang,
			R.drawable.search_sougou, R.drawable.search_taobao,
			R.drawable.search_xinlang, R.drawable.earch_yicha_6,
			R.drawable.search_yamaxun, R.drawable.search_yisou };

	final String[] vlue = { Constants.URL_SEARCH_BAIDU,
			Constants.URL_SEARCH_GOOGLE, Constants.URL_SEARCH_DANGDANG,
			Constants.URL_SEARCH_SOUGOU, Constants.URL_SEARCH_TAOBAO,
			Constants.URL_SEARCH_XINLANG, Constants.URL_SEARCH_YICHA,
			Constants.URL_SEARCH_YAMAXUN, Constants.URL_SEARCH_YISOU };
	ArrayList<HashMap<String, Object>> Itemload = new ArrayList<HashMap<String, Object>>();
	
	public DialogSearchEngineUtil(Launcher context) {
		super(context);
		this.mLauncher = context;
	}

	public DialogSearchEngineUtil(Launcher context, int theme) {
		super(context, theme);
		this.mLauncher = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.faxian_activity);

		TextView title = (TextView) findViewById(R.id.grid_pop_title);
		title.setText("选择搜索引擎");
		
		btn_cancel = (Button) findViewById(R.id.faxian_close);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		dragGridView = (GridView) findViewById(R.id.dragGridView);
		dragGridView.setOnItemClickListener(new GridViewItemClickListener());
		
		for (int i = 0; i < imageSub.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageSub[i]);
			map.put("ItemText", itemSub[i]);
			Itemload.add(map);
		}
		
		SimpleAdapter SA = new SimpleAdapter(mLauncher, Itemload,
				R.layout.faxian_gridview_item, new String[] { "ItemImage",
						"ItemText" }, new int[] { R.id.ItemImage,
						R.id.ItemText });

		dragGridView.setAdapter(SA);
	}

	public static class Builder {
		private final DatePickParams P;

		public Builder(Launcher context) {
			P = new DatePickParams(context);
		}

		public DialogSearchEngineUtil create() {
			final DialogSearchEngineUtil dialog = new DialogSearchEngineUtil(P.mContext,
					R.style.waitdailog);
			P.apply(dialog);
			return dialog;
		}
	}

	public static class DatePickParams {
		public final Launcher mContext;

		public DatePickParams(Launcher context) {
			mContext = context;
		}

		public void apply(DialogSearchEngineUtil dialog) {

		}
	}



	class GridViewItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Editor editor = mLauncher.sp.edit();
			editor.putString(Constants.PREFERENCES_GENERAL_SEARCH_URL, vlue[arg2]);
			editor.commit();

			mLauncher.search_engine.setImageResource(imageSub[arg2]);
			mLauncher.mUrlEditText.requestFocus();
			mLauncher.mUrlEditText.setSelectAllOnFocus(true);
			dismiss();
		}
	}


}
