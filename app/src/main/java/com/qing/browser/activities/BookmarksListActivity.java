package com.qing.browser.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.model.adapters.BookmarksCursorAdapter;
import com.qing.browser.model.items.BookmarkItem;
import com.qing.browser.providers.BookmarksUtil;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.utils.ApplicationUtils;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.DialogListUtil;
import com.qing.browser.utils.Loading_Dialog;
/**
 * Bookmarks list activity.
 */
public class BookmarksListActivity {
	Loading_Dialog LoadDialog = null;
	ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
	private BookmarksCursorAdapter mCursorAdapter;
	private ListView mList;
	private Activity context;
	DialogListUtil builder;
	final String[] mItems = { "新窗口打开", "编辑书签", "复制url","删除书签" };
	private TextView btn_tongbu, btn_guangli;
	private TextView nodata_btn_tongbu, nodata_btn_guangli;
	private boolean updatabookmark = false;
	private LinearLayout data_layout, no_data_layout;
	
	public void iList(final Activity activity, View page) {
		this.context = activity;
		data_layout = (LinearLayout)page.findViewById(R.id.data_layout);
		no_data_layout = (LinearLayout)page.findViewById(R.id.no_data_layout);
		mList = (ListView) page.findViewById(R.id.BookmarksListActivity_List);
		btn_tongbu = (TextView) page.findViewById(R.id.btn_tongbu);
		btn_guangli = (TextView) page.findViewById(R.id.btn_guangli);
		
		nodata_btn_tongbu = (TextView) page.findViewById(R.id.nodata_btn_tongbu);
		nodata_btn_guangli = (TextView) page.findViewById(R.id.nodata_btn_guangli);
		
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				Intent result = new Intent();
				result.putExtra(Constants.EXTRA_ID_NEW_TAB, false);
				int pos = Integer.parseInt(mData.get(position).get(Browser.BookmarkColumns._ID).toString());
				
				BookmarkItem item = BookmarksUtil.getStockBookmarkById(activity,pos);
				if (item != null) {
					result.putExtra(Constants.EXTRA_ID_URL, item.getUrl());
				} else {
					result.putExtra(
							Constants.EXTRA_ID_URL,
							PreferenceManager.getDefaultSharedPreferences(
									context).getString(
									Constants.PREFERENCES_GENERAL_HOME_PAGE,
									Constants.URL_ABOUT_START));
				}

				if (context.getParent() != null) {
					context.getParent().setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, result);
				} else {
					context.setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, result);
				}

				context.finish();
			}
		});

		mList.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final long pos = Integer.parseInt(mData.get(position).get(Browser.BookmarkColumns._ID).toString());
				
				final BookmarkItem bookmarkItem = BookmarksUtil.getStockBookmarkById(activity,pos);
				if (null == bookmarkItem) {
					return false;
				}
				
				builder = new DialogListUtil.Builder(context)
						.setTitleText(bookmarkItem.getTitle()).setItems(mItems)
						.setlistListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								// 点击后弹出窗口选择了第几项
								LongClick(arg2, pos, bookmarkItem);
								builder.dismiss();
							}
						}).create();
				builder.show();
				 
				return false;

			}
		});

		btn_tongbu.setOnClickListener(clicklistener);
		btn_guangli.setOnClickListener(clicklistener);
		nodata_btn_guangli.setOnClickListener(clicklistener);
		nodata_btn_tongbu.setOnClickListener(clicklistener);
		fillData();
	}
	
	private OnClickListener clicklistener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			 switch(v.getId()){
			 case R.id.btn_tongbu:
			 case R.id.nodata_btn_tongbu:
				 Toast.makeText(context, "服务器代码尚未同步", Toast.LENGTH_SHORT)
					.show();
			 break;
			 case R.id.nodata_btn_guangli:
			 case R.id.btn_guangli:
					Intent i = new Intent(context, EditBookmarkActivity.class);
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, "");
					i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, "");
					context.startActivityForResult(i, EditBookmarkActivity.EditBookmark_OK);
				
				 break;
			 
			 
			 }
		}
	};

	private void LongClick(int position, long id, BookmarkItem bookmarkItem) {
		Intent i;
		switch (position) {
		case 0:
			i = new Intent();
			i.putExtra(Constants.EXTRA_ID_NEW_TAB, true);

			if (bookmarkItem != null) {
				i.putExtra(Constants.EXTRA_ID_URL, bookmarkItem.getUrl());
			} else {
				i.putExtra(
						Constants.EXTRA_ID_URL,
						PreferenceManager
								.getDefaultSharedPreferences(context)
								.getString(
										Constants.PREFERENCES_GENERAL_HOME_PAGE,
										Constants.URL_ABOUT_START));
			}

			if (context.getParent() != null) {
				context.getParent().setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, i);
			} else {
				context.setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, i);
			}

			context.finish();
			break;

		case 1:
			if (bookmarkItem != null) {
				i = new Intent(context, EditBookmarkActivity.class);
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, id);
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_UUID,
						bookmarkItem.getmUUID());
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE,
						bookmarkItem.getTitle());
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL,
						bookmarkItem.getUrl());

				context.startActivityForResult(i, EditBookmarkActivity.EditBookmark_OK);
			}
			break;

		case 2:
			if (bookmarkItem != null) {
				ApplicationUtils
						.copyTextToClipboard(
								context,
								bookmarkItem.getUrl(),
								context.getString(R.string.Commons_UrlCopyToastMessage));
			}
			break;

		case 3:
			// mDbAdapter.deleteBookmark(info.id);
			BookmarksUtil.deleteStockBookmark(context,id);
			fillData();
			break;
		}
	}

	/**
	 * Fill the bookmark to the list UI.
	 */
	public void fillData() {
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1)"
				+ " group by (" + BookmarksUtil.BookmarkColumns_UUID
				+ ") having count((" + BookmarksUtil.BookmarkColumns_UUID
				+ ")>=1 ";

		String orderClause = Browser.BookmarkColumns.VISITS + " DESC, "
				+ Browser.BookmarkColumns._ID + " COLLATE NOCASE";

		mData = BookmarksUtil.queryForBookmarkList(context,
				BookmarksUtil.sHistoryBookmarksProjection, whereClause,
				null, orderClause);

		 
		if(mData == null || mData.size() ==0){
			data_layout.setVisibility(View.GONE);
			no_data_layout.setVisibility(View.VISIBLE);
		}else{
			data_layout.setVisibility(View.VISIBLE);
			no_data_layout.setVisibility(View.GONE);
		}
		
		mCursorAdapter = new BookmarksCursorAdapter(context, mData,
				ApplicationUtils.getFaviconSizeForBookmarks(context));

		mList.setAdapter(mCursorAdapter);

		setAnimation();
	}

	/**
	 * Set the list loading animation.
	 */
	private void setAnimation() {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);

		mList.setLayoutAnimation(controller);
	}

	
}
