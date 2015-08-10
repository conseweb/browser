package com.qing.browser.model.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;

/**
 * Cursor adapter for bookmarks.
 */
public class BookmarksCursorAdapter extends BaseAdapter {
	public Context mContext;
	private int mFaviconSize;
	ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
	public BookmarksCursorAdapter(Context mContext, ArrayList<HashMap<String, Object>> mData, int faviconSize){
		this.mData = mData;
		this.mContext = mContext;
		mFaviconSize = faviconSize;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.bookmark_row,
					null);
		}
		
		TextView BookmarkRow_Title = (TextView) convertView.findViewById(R.id.BookmarkRow_Title);
		BookmarkRow_Title.setText(mData.get(position).get( Browser.BookmarkColumns.TITLE).toString());
		
		TextView BookmarkRow_Url = (TextView) convertView.findViewById(R.id.BookmarkRow_Url);
		BookmarkRow_Url.setText(mData.get(position).get( Browser.BookmarkColumns.URL).toString());
		
		ImageView thumbnailView = (ImageView) convertView.findViewById(R.id.BookmarkRow_Thumbnail);
		
		byte[] favicon = (byte[])mData.get(position).get(Browser.BookmarkColumns.FAVICON);
		if (favicon != null) {
			BitmapDrawable icon = new BitmapDrawable(BitmapFactory.decodeByteArray(favicon, 0, favicon.length));
			
			Bitmap bm = Bitmap.createBitmap(mFaviconSize, mFaviconSize, Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bm);
			
			icon.setBounds(0, 0, mFaviconSize, mFaviconSize);
			icon.draw(canvas);
			
			thumbnailView.setImageBitmap(bm);
		} else {
			thumbnailView.setImageResource(R.drawable.fav_icn_unknown);
		}
		
		return convertView;
	}
	
	 

}
