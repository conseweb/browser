package com.qing.browser.ui.launcher;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qing.browser.R;

public class GridViewAdapter extends BaseAdapter {
	private ArrayList<ItemInfo> mshortcuts = new ArrayList();
	private LayoutInflater mInflater;
	private Context mContext;

	public GridViewAdapter(Context context, ArrayList<ItemInfo> shortcuts) {
		mshortcuts = shortcuts;
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mshortcuts.size();
	}

	public Object getItem(int position) {
		return mshortcuts.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ShortcutInfo info = (ShortcutInfo) getItem(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.application_boxed, parent,
					false);
		}

		final TextView textView = (TextView) convertView;
		textView.setCompoundDrawablesWithIntrinsicBounds(null,
				new FastBitmapDrawable(info.getIcon()), null, null);
		textView.setText(info.title);
		return convertView;
	}
}
