package com.qing.browser.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.qing.browser.R;
import com.qing.browser.net.URLUtil;
import com.qing.browser.providers.BookmarksProviderWrapper;
import com.qing.browser.providers.BookmarksUtil;
import com.qing.browser.ui.MyPagerAdapter;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.launcher.LauncherModel;
import com.qing.browser.ui.launcher.LauncherSettings;
import com.qing.browser.ui.launcher.ShortcutInfo;
import com.qing.browser.ui.launcher.Utilities;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.ConstantsUrl;
import com.qing.browser.utils.StringUtil;
import com.qing.browser.utils.Tools;
import com.universe.galaxy.util.TongJi;

public class AddShortcutActivity extends BaseActivity {

	private int currentView = 0;// 当前视图
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private View viewpage1, viewpage2, viewpage3;
	private TextView tv_bookmark, tv_history, tv_faxian;
	private LinearLayout tv_bookmark_layout, tv_history_layout,
			tv_faxian_layout;
	private ListView mList;
	private SharedPreferences sp;
	private MyAddShortcurAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();

	static DisplayImageOptions options;
	protected static ImageLoader imageLoader = ImageLoader.getInstance();
	private TextView tv_addbookmark, nodata_btn_guangli, nodata_tv;
	private LinearLayout data_layout, no_data_layout;

	public static int IMAGE_TYPE_FAXIAN = 0;
	public static int IMAGE_TYPE_BOOKMARK = 1;
	public static int IMAGE_TYPE_HISTORY = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_shortcut_activity);
		ImageView btn_back = (ImageView) findViewById(R.id.item_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		sp = getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		InitViewPager();

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.hotseat_browser_bg)
				.showImageForEmptyUri(R.drawable.hotseat_browser_bg)
				.showImageOnFail(R.drawable.hotseat_browser_bg)
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		TongJi.AddAnalyticsData(TongJi.m_tianjiashuqian);
	}

	/**
	 * 初始化页卡内容ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		tv_faxian = (TextView) findViewById(R.id.tv_faxian);
		tv_bookmark = (TextView) findViewById(R.id.tv_bookmark);
		tv_history = (TextView) findViewById(R.id.tv_history);

		tv_faxian_layout = (LinearLayout) findViewById(R.id.tv_faxian_layout);
		tv_bookmark_layout = (LinearLayout) findViewById(R.id.tv_bookmark_layout);
		tv_history_layout = (LinearLayout) findViewById(R.id.tv_history_layout);

		tv_faxian.setOnClickListener(new MyOnClickListener(0));
		tv_bookmark.setOnClickListener(new MyOnClickListener(1));
		tv_history.setOnClickListener(new MyOnClickListener(2));

		setCurPage(0);
		tv_faxian.setTextColor(Color.parseColor("#FFFFFF"));
		tv_faxian_layout.setBackgroundResource(R.drawable.title_select_bg);

		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		viewpage1 = mInflater.inflate(R.layout.add_shortcut_page, null);
		viewpage2 = mInflater.inflate(R.layout.add_shortcut_page, null);
		viewpage3 = mInflater.inflate(R.layout.add_shortcut_page, null);
		listViews.add(viewpage1);
		listViews.add(viewpage2);
		listViews.add(viewpage3);

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		initPage(viewpage1);
		
		TongJi.AddAnalyticsData(TongJi.s_remen);

	}

	/**
	 * 更新当前页码
	 */
	public void setCurPage(int page) {

		if (page == currentView)
			return;
		switch (page) {
		case 0:
			tv_faxian.setTextColor(Color.parseColor("#FFFFFF"));
			tv_faxian_layout.setBackgroundResource(R.drawable.title_select_bg);

			tv_bookmark.setTextColor(Color.parseColor("#666666"));
			tv_bookmark_layout.setBackgroundResource(0);

			tv_history.setTextColor(Color.parseColor("#666666"));
			tv_history_layout.setBackgroundResource(0);
			break;
		case 1:

			tv_faxian.setTextColor(Color.parseColor("#666666"));
			tv_faxian_layout.setBackgroundResource(0);

			tv_bookmark.setTextColor(Color.parseColor("#FFFFFF"));
			tv_bookmark_layout
					.setBackgroundResource(R.drawable.title_select_bg);

			tv_history.setTextColor(Color.parseColor("#666666"));
			tv_history_layout.setBackgroundResource(0);
			break;

		case 2:
			tv_faxian.setTextColor(Color.parseColor("#666666"));
			tv_faxian_layout.setBackgroundResource(0);

			tv_bookmark.setTextColor(Color.parseColor("#666666"));
			tv_bookmark_layout.setBackgroundResource(0);

			tv_history.setTextColor(Color.parseColor("#FFFFFF"));
			tv_history_layout.setBackgroundResource(R.drawable.title_select_bg);

			break;
		}

		currentView = page;

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
				TongJi.AddAnalyticsData(TongJi.s_remen);
				initPage(viewpage1);
			} else if (arg0 == 1) {
				TongJi.AddAnalyticsData(TongJi.s_zidingyi);
				initPage(viewpage2);
			} else if (arg0 == 2) {
				TongJi.AddAnalyticsData(TongJi.s_lishi);
				initPage(viewpage3);
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void fillBookmarkData() {
		mData.clear();
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		String orderClause = Browser.BookmarkColumns.VISITS + " DESC, "
				+ Browser.BookmarkColumns.TITLE + " COLLATE NOCASE";

		mData = BookmarksUtil.queryForaddShortcut(AddShortcutActivity.this,
				BookmarksUtil.sHistoryBookmarksProjection, whereClause, null,
				orderClause);

	}

	private OnClickListener clicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.nodata_btn_guangli:
			case R.id.tv_addbookmark:
			case R.id.btn_guangli:
				Intent i = new Intent(AddShortcutActivity.this,
						EditBookmarkActivity.class);
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_ID, (long) -1);
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_TITLE, "");
				i.putExtra(Constants.EXTRA_ID_BOOKMARK_URL, "");
				startActivityForResult(i, EditBookmarkActivity.EditBookmark_OK);

				break;

			}
		}
	};

	public void noDataView(View page){
		if (mData == null || mData.size() == 0) {
			data_layout.setVisibility(View.GONE);
			no_data_layout.setVisibility(View.VISIBLE);
			if (currentView == 1) {
				page.findViewById(R.id.nodata_addbookmark_layout)
						.setVisibility(View.VISIBLE);
			} else {
				page.findViewById(R.id.nodata_addbookmark_layout)
						.setVisibility(View.GONE);
			}
		} else {
			data_layout.setVisibility(View.VISIBLE);
			no_data_layout.setVisibility(View.GONE);
			
		}
	}
	
	public void initPage(View page) {
		mList = (ListView) page.findViewById(R.id.shortList);

		data_layout = (LinearLayout) page.findViewById(R.id.data_layout);
		no_data_layout = (LinearLayout) page.findViewById(R.id.no_data_layout);
		nodata_tv = (TextView)page.findViewById(R.id.nodata_tv);
		mData.clear();
		if (currentView == 0) {
			// 发现数据
			nodata_tv.setText("没有发现数据");
			Message msg = new Message();
			msg.obj = "";
			msg.what = 1;
			handler.sendMessage(msg);
			if (Tools.isConnectInternet(AddShortcutActivity.this)) {
				new Thread(new GetBK_Thread()).start();

			}
		} else if (currentView == 1) {
			// 书签数据

			page.findViewById(R.id.addbookmark_layout).setVisibility(
					View.VISIBLE);

			tv_addbookmark = (TextView) page.findViewById(R.id.tv_addbookmark);
			nodata_btn_guangli = (TextView) page
					.findViewById(R.id.nodata_btn_guangli);
			nodata_tv.setText("没有可用的书签");
			tv_addbookmark.setOnClickListener(clicklistener);
			nodata_btn_guangli.setOnClickListener(clicklistener);

			fillBookmarkData();
			noDataView(page);
		} else if (currentView == 2) {
			// 历史数据
			nodata_tv.setText("您没有留下任何历史记录");
			mData = BookmarksProviderWrapper
					.getStockHistoryForAddShortcut(AddShortcutActivity.this);
			noDataView(page);
		}

		
		mAdapter = new MyAddShortcurAdapter(mData, this);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				String url = mData.get(position).get("ItemUrl").toString();
				long _id = LauncherModel.QueryDuplicate(
						AddShortcutActivity.this, url, true);
				ViewHolder holder = (ViewHolder) v.getTag();
				holder.checkbox_select.toggle();
				if (-1 != _id) {
					LauncherModel.deleteItemFromDatabase(
							AddShortcutActivity.this, _id);
					holder.checkbox_select.setSelected(false);
					Toast.makeText(AddShortcutActivity.this, "桌面快捷方式已删除",
							Toast.LENGTH_SHORT).show();
				} else {
					int count = LauncherModel.QueryCount(
							AddShortcutActivity.this, 1, true);
					ShortcutInfo info = new ShortcutInfo();
					info.title = mData.get(position).get("ItemText").toString();
					info.url = url;
					info.customIcon = true;
					info.itemIndex = count;
					info.screen = 1;
					info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
					int itemTpye = Integer.valueOf(mData.get(position)
							.get("ItemImageType").toString());
					if (itemTpye == IMAGE_TYPE_FAXIAN) {
						info.shortid = Long.valueOf(mData.get(position)
								.get("id").toString());
						info.iconType = LauncherSettings.Favorites.ICON_TYPE_RESOURCE_UNIVERSAL;
						String imageUrl = mData.get(position).get("ItemImage")
								.toString();
						if (!("".equals(imageUrl))) {
							info.iconResource = Tools.generateMD5(imageUrl);
						}
					} else if (itemTpye == IMAGE_TYPE_BOOKMARK) {
						info.iconType = LauncherSettings.Favorites.ICON_TYPE_BITMAP;
						if (mData.get(position).get("ItemSnap") != null) {
							info.iconType = LauncherSettings.Favorites.ICON_TYPE_RESOURCE_SNAPSHOT;
							info.iconResource = mData.get(position)
									.get("ItemSnap").toString();
						} else if (mData.get(position).get("ItemImage") != null) {
							Bitmap bmpout = BitmapFactory.decodeByteArray(
									(byte[]) mData.get(position).get(
											"ItemImage"),
									0,
									((byte[]) mData.get(position).get(
											"ItemImage")).length);
							info.mIcon = bmpout;
						} else {
							info.mIcon = Utilities
									.drawableToBitmap(R.drawable.hotseat_browser_bg);
						}
					} else if (itemTpye == IMAGE_TYPE_HISTORY) {
						info.iconType = LauncherSettings.Favorites.ICON_TYPE_BITMAP;

						if (mData.get(position).get("ItemSnap") != null) {
							info.iconType = LauncherSettings.Favorites.ICON_TYPE_RESOURCE_SNAPSHOT;
							info.iconResource = mData.get(position)
									.get("ItemSnap").toString();
						} else if (mData.get(position).get("ItemImage") != null) {
							Bitmap bmpout = BitmapFactory.decodeByteArray(
									(byte[]) mData.get(position).get(
											"ItemImage"),
									0,
									((byte[]) mData.get(position).get(
											"ItemImage")).length);
							info.mIcon = bmpout;
						} else {
							info.mIcon = Utilities
									.drawableToBitmap(R.drawable.hotseat_browser_bg);
						}
					}

					LauncherModel.addItemToDatabase(AddShortcutActivity.this,
							info, true);

					holder.checkbox_select.setSelected(true);
					Toast.makeText(AddShortcutActivity.this, "桌面快捷方式已创建",
							Toast.LENGTH_SHORT).show();
				}

				mAdapter.notifyDataSetChanged();
			}
		});

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
				case 0:
					mData.clear();
					sp.edit()
							.putString("BookmarkInfoJSonList",
									msg.obj.toString()).commit();
					try {
						JSONObject o = (JSONObject) msg.obj;
						JSONArray jsonArray = o.getJSONArray("bm");
						for (int i = 0; i < jsonArray.length(); i++) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							JSONObject jsonObj = jsonArray.getJSONObject(i);
							map.put("id", jsonObj.getString("id"));
							map.put("ItemImageType", IMAGE_TYPE_FAXIAN);
							map.put("ItemImage", jsonObj.getString("pic"));
							map.put("ItemText", jsonObj.getString("name"));
							map.put("ItemUrl", jsonObj.getString("url"));
							mData.add(map);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					mAdapter.notifyDataSetChanged();
					
					if (mData == null || mData.size() == 0) {
						data_layout.setVisibility(View.GONE);
						no_data_layout.setVisibility(View.VISIBLE);
						 
					} else {
						data_layout.setVisibility(View.VISIBLE);
						no_data_layout.setVisibility(View.GONE);
						
					}
					
					break;

				case 1:
					mData.clear();
					String json = sp.getString("BookmarkInfoJSonList",
							"");
					if (json == null)
						return;
					try {
						JSONObject o = new JSONObject(json);
						JSONArray jsonArray = o.getJSONArray("bm");
						for (int i = 0; i < jsonArray.length(); i++) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							JSONObject jsonObj = jsonArray.getJSONObject(i);
							map.put("id", jsonObj.getString("id"));
							map.put("ItemImageType", IMAGE_TYPE_FAXIAN);
							map.put("ItemImage", jsonObj.getString("pic"));
							map.put("ItemText", jsonObj.getString("name"));
							map.put("ItemUrl", jsonObj.getString("url"));
							mData.add(map);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					mAdapter.notifyDataSetChanged();
					break;

				}
			}
		}
	};

	class GetBK_Thread implements Runnable {
		public void run() {
			try {
				String poststring = Tools
						.getPoststring(AddShortcutActivity.this)
						+ Tools.getFaxianBookmarkString(AddShortcutActivity.this);

				String json = URLUtil.getInstance().getJson(
						ConstantsUrl.BOOKMARK_ACTION_PROTOCOL, poststring);
				if (StringUtil.isNull(json)) {
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				} else {
					JSONObject jsonObject = new JSONObject(json);
					if (Launcher.STATUS_NULL.equals(jsonObject
							.optString(Launcher.STATUS_KEY))) {
						// 无更新
						Message msg = new Message();
						msg.obj = Launcher.STATUS_NULL;
						msg.what = 1;
						handler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.obj = jsonObject;
						msg.what = 0;
						handler.sendMessage(msg);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	};

	public static class MyAddShortcurAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> list;
		private LayoutInflater inflater = null;
		private Context context;

		public MyAddShortcurAdapter(ArrayList<HashMap<String, Object>> list,
				Context context) {
			this.list = list;
			inflater = LayoutInflater.from(context);
			this.context = context;
		}

		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.add_shortcut_list_item,
						null);
				holder.Thumbnail = (ImageView) convertView
						.findViewById(R.id.Thumbnail);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tv_url = (TextView) convertView
						.findViewById(R.id.tv_url);
				holder.checkbox_select = (CheckBox) convertView
						.findViewById(R.id.checkbox_select);

				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			int itemTpye = Integer.valueOf(list.get(position)
					.get("ItemImageType").toString());
			if (itemTpye == IMAGE_TYPE_FAXIAN) {
				imageLoader.displayImage(list.get(position).get("ItemImage")
						.toString(), holder.Thumbnail, options);
			} else if (itemTpye == IMAGE_TYPE_BOOKMARK) {
				if (list.get(position).get("ItemImage") != null) {
					Bitmap bmpout = BitmapFactory
							.decodeByteArray(
									(byte[]) list.get(position)
											.get("ItemImage"), // LS
									0,
									((byte[]) list.get(position).get(
											"ItemImage")).length);
					holder.Thumbnail.setImageBitmap(bmpout);
				} else {
					holder.Thumbnail
							.setImageResource(R.drawable.fav_icn_default);
				}
			} else if (itemTpye == IMAGE_TYPE_HISTORY) {
				Bitmap bmpout = BitmapFactory.decodeByteArray(
						(byte[]) list.get(position).get("ItemImage"), // LS
						0,
						((byte[]) list.get(position).get("ItemImage")).length);
				holder.Thumbnail.setImageBitmap(bmpout);
			}

			holder.tv_title.setText(list.get(position).get("ItemText")
					.toString());
			holder.tv_url.setText(list.get(position).get("ItemUrl").toString());
			if (-1 != LauncherModel.QueryDuplicate(context, list.get(position)
					.get("ItemUrl").toString(), true)) {
				holder.checkbox_select.setChecked(true);
			} else {
				holder.checkbox_select.setChecked(false);
			}

			return convertView;
		}

	}

	public static class ViewHolder {
		public ImageView Thumbnail;
		public TextView tv_title;
		public TextView tv_url;
		public CheckBox checkbox_select;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			if (requestCode == EditBookmarkActivity.EditBookmark_OK) {
				if (data != null) {
					fillBookmarkData();
					if (mData == null || mData.size() == 0) {
						data_layout.setVisibility(View.GONE);
						no_data_layout.setVisibility(View.VISIBLE);
						if (currentView == 1) {
							viewpage2.findViewById(R.id.nodata_addbookmark_layout)
									.setVisibility(View.VISIBLE);
						} else {
							viewpage2.findViewById(R.id.nodata_addbookmark_layout)
									.setVisibility(View.GONE);
						}
					} else {
						data_layout.setVisibility(View.VISIBLE);
						no_data_layout.setVisibility(View.GONE);
						
					}
					mAdapter = new MyAddShortcurAdapter(mData, this);
					mList.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					
				}
			}
		}
	}

}
