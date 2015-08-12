package com.qing.browser.ui.menu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;

public class DateAdapter extends BaseAdapter {

	private Context context;
	/** 列表. */
	private List<GridButtonInfo> lstDate;
	private TextView buttonTitle;
	private ImageView buttonImage;

	// 每页显示的Item个数
	public static final int SIZE = 8;

	public DateAdapter(Context mContext, List<GridButtonInfo> list, int page) {
		this.context = mContext;
		lstDate = new ArrayList<GridButtonInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < list.size()) && (i < iEnd)) {
			lstDate.add(list.get(i));
			i++;
		}
	}

	@Override
	public int getCount() {
		return lstDate.size();
	}

	@Override
	public Object getItem(int position) {
		return lstDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridButtonInfo buttonInfo = lstDate.get(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.menu_item,
				null);

		buttonTitle = (TextView) convertView.findViewById(R.id.buttonTitle);
		buttonImage = (ImageView) convertView.findViewById(R.id.buttonImage);

		buttonTitle.setText(buttonInfo.getButtonTitle());
		buttonImage.setImageResource(buttonInfo.getButtonImage());
		return convertView;
	}
}