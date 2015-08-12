package com.qing.browser.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.qing.browser.R;

public class DialogListUtil extends Dialog {

	private final Context mContext;
	private ListView dialog_list;
	private TextView dialogTitleView;
	private CharSequence dialogTitle;
	private String[] mItems = null;
	private int theme;
	private AdapterView.OnItemClickListener listItemListener;

	public DialogListUtil(Context context) {
		super(context);
		this.mContext = context;
	}

	public DialogListUtil(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		this.theme = theme;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_list_page);
		findView();
		setListener();
	}

	private void setListener() {
		if (listItemListener != null) {
			dialog_list.setOnItemClickListener(listItemListener);
		}

	}

	private void findView() {
		dialogTitleView = (TextView) findViewById(R.id.dialog_title);
		if (dialogTitle != null) {
			dialogTitleView.setVisibility(View.VISIBLE);
			dialogTitleView.setText(dialogTitle);
		} else {
			dialogTitleView.setVisibility(View.VISIBLE);
			dialogTitleView.setText("温馨提示");
		}
		dialog_list = (ListView) findViewById(R.id.dialog_list);
		if(mItems != null){
			ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < mItems.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("title", mItems[i]);
				al.add(map);
			}

			String[] from = { "title" };
			int[] to = { R.id.title };

			SimpleAdapter adapter = new SimpleAdapter(mContext, al,
					R.layout.list_item, from, to);

			dialog_list.setAdapter(adapter);
		}
	
	}

	private void setTitleText(CharSequence title) {
		dialogTitle = title;
	}

	private void setItems(String[] mItemstr) {
		mItems = mItemstr;
	}

	private void setlistListener(AdapterView.OnItemClickListener listitemListener) {
		listItemListener = listitemListener;
	}

	public static class Builder {
		private final DatePickParams P;

		public Builder(Context context) {
			P = new DatePickParams(context);
		}

		public Builder setTitleText(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		public Builder setItems(String[] mItems) {
			P.mItems = mItems;
			return this;
		}

		public Builder setlistListener(final AdapterView.OnItemClickListener listener) {
			P.listitemListener = listener;
			return this;
		}

		public DialogListUtil create() {
			final DialogListUtil dialog = new DialogListUtil(P.mContext,
					R.style.waitdailog);
			P.apply(dialog);
			return dialog;
		}
	}

	public static class DatePickParams {
		public String[] mItems;
		public AdapterView.OnItemClickListener listitemListener;
		public CharSequence mPositiveButtonText;
		public CharSequence mTitle;
		public final Context mContext;

		public DatePickParams(Context context) {
			mContext = context;
		}

		public void apply(DialogListUtil dialog) {
			if (mTitle != null) {
				dialog.setTitleText(mTitle);
			}
			
			dialog.setItems(mItems);
			dialog.setlistListener(listitemListener);
		}
	}

}
