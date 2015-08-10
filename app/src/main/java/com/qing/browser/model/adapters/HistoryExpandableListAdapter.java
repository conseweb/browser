package com.qing.browser.model.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DateSorter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.model.items.HistoryItem;
import com.qing.browser.providers.BookmarksUtil;
import com.qing.browser.utils.Tools;

public class HistoryExpandableListAdapter extends BaseExpandableListAdapter {

	private LayoutInflater mInflater = null;

	private int[] mItemMap;
	private int mNumberOfBins;
	private DateSorter mDateSorter;

	private Context mContext;
	
	private int mFaviconSize;

	private ArrayList<HashMap<String, Object>> mData;
	
	
	public HistoryExpandableListAdapter(Context context, ArrayList<HashMap<String, Object>> mdata, int faviconSize) {
		mContext = context;
		mData = mdata;
		mFaviconSize = faviconSize;
		mDateSorter = new DateSorter(mContext);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		buildMap();
	}
	
	/**
	 * Split the data in the cursor into several "bins": today, yesterday, last 7 days, last month, older.
	 */
	private void buildMap() {
		int[] array = new int[DateSorter.DAY_COUNT];
        for (int j = 0; j < DateSorter.DAY_COUNT; j++) {
            array[j] = 0;
        }
        
        mNumberOfBins = 0;
        int dateIndex = -1;
        if (mData.size() > 0) {
        	for(int i = 0; i< mData.size(); i++) {
        		 
                long date = Long.parseLong(mData.get(i).get(Browser.BookmarkColumns.DATE).toString());
                int index = mDateSorter.getIndex(date);
                if (index > dateIndex) {
                    mNumberOfBins++;
                    if (index == DateSorter.DAY_COUNT - 1) {
                        array[index] = mData.size() - i;
                        break;
                    }
                    dateIndex = index;
                }
                array[dateIndex]++;
            }
        }
        mItemMap = array;
	}
	

	
	/**
     * Translates from a group position in the ExpandableList to a bin.  This is
     * necessary because some groups have no history items, so we do not include
     * those in the ExpandableList.
     * @param groupPosition Position in the ExpandableList's set of groups
     * @return The corresponding bin that holds that group.
     */
    private int groupPositionToBin(int groupPosition) {
        if (groupPosition < 0 || groupPosition >= DateSorter.DAY_COUNT) {
            throw new AssertionError("group position out of range");
        }
        if (DateSorter.DAY_COUNT == mNumberOfBins || 0 == mNumberOfBins) {
            // In the first case, we have exactly the same number of bins
            // as our maximum possible, so there is no need to do a
            // conversion
            // The second statement is in case this method gets called when
            // the array is empty, in which case the provided groupPosition
            // will do fine.
            return groupPosition;
        }
        int arrayPosition = -1;
        while (groupPosition > -1) {
            arrayPosition++;
            if (mItemMap[arrayPosition] != 0) {
                groupPosition--;
            }
        }
        return arrayPosition;
    }

	 
	@Override
	public Object getChild(int groupPosition, int childPosition) {

		groupPosition = groupPositionToBin(groupPosition);
        int index = childPosition;
        for (int i = 0; i < groupPosition; i++) {
            index += mItemMap[i];
        } 
		int id = Integer.parseInt(mData.get(index).get(Browser.BookmarkColumns._ID).toString());
		String title = mData.get(index).get(Browser.BookmarkColumns.TITLE).toString();
		String url = mData.get(index).get(Browser.BookmarkColumns.URL).toString();
		byte[] faviconData = (byte[]) mData.get(index).get(Browser.BookmarkColumns.FAVICON);

		return new HistoryItem(id, title, url, false, faviconData);

	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.history_row, null, false);

        
		TextView titleView = (TextView) view.findViewById(R.id.HistoryRow_Title);
		
		final HistoryItem item = (HistoryItem) getChild(groupPosition, childPosition);
		titleView.setText(item.getTitle());
		
		TextView urlView = (TextView) view.findViewById(R.id.HistoryRow_Url);		 					
		urlView.setText(item.getUrl());
		
		final CheckBox bookmarkStar = (CheckBox) view.findViewById(R.id.HistoryRow_BookmarkStar);
		String uuid = UUID.nameUUIDFromBytes(item.getUrl().getBytes()) +"";
		if(BookmarksUtil.findUUID_isBookmark(mContext, uuid)){
			bookmarkStar.setChecked(true);
		}else{
			bookmarkStar.setChecked(false);
		}
		bookmarkStar.setTag(item.getId());
		bookmarkStar.setOnCheckedChangeListener(null);
		bookmarkStar.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					String snapshot=Tools.generateMD5(item.getUrl());
					BookmarksUtil.setAsBookmark(mContext,item.getId(), item.getTitle().toString(),
							 item.getUrl().toString(), isChecked,item.getFavicon(),snapshot);
					Toast.makeText(mContext, R.string.HistoryListActivity_BookmarkAdded, Toast.LENGTH_SHORT).show();
				} else {
					BookmarksUtil.deleteStockBookmarkByUUID(mContext,UUID.nameUUIDFromBytes(item.getUrl().getBytes())+"");
					Toast.makeText(mContext, R.string.HistoryListActivity_BookmarkRemoved, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		ImageView faviconView = (ImageView) view.findViewById(R.id.HistoryRow_Thumbnail);
		Bitmap favicon = item.getFavicon();
		if (favicon != null) {
			BitmapDrawable icon = new BitmapDrawable(favicon);
			
			Bitmap bm = Bitmap.createBitmap(mFaviconSize, mFaviconSize, Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(bm);
			
			icon.setBounds(0, 0, mFaviconSize, mFaviconSize);
			icon.draw(canvas);
			
			faviconView.setImageBitmap(bm);
		} else {
			faviconView.setImageResource(R.drawable.fav_icn_unknown);
		}
        
        return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mItemMap[groupPositionToBin(groupPosition)];
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		int binIndex = groupPositionToBin(groupPosition);
		
		switch (binIndex) {
		case 0: return "今日";
		case 1: return "昨日";
		case 2: return "上周";
		case 3: return "上个月";
		default: return "更早之前";
		}
	}

	@Override
	public int getGroupCount() {
		return mNumberOfBins;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {        
        View view = mInflater.inflate(R.layout.history_group_item_layout, null);
		TextView groupNameTextView = (TextView) view.findViewById(R.id.group_name);
		groupNameTextView.setText(getGroup(groupPosition).toString());
		ImageView group_image=(ImageView) view.findViewById(R.id.group_image);
		  
        if(isExpanded){
        	group_image.setBackgroundResource(R.drawable.arrow_down);
        }else{
        	group_image.setBackgroundResource(R.drawable.arrow_up);
        }
        
        return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
