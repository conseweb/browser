package com.qing.browser.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.download.Dao;
import com.qing.browser.download.DownloadItem;
import com.qing.browser.download.DownloadListAdapter;
import com.qing.browser.download.DownloadManager;
import com.qing.browser.download.IDownloadEventsListener;
import com.qing.browser.events.EventConstants;
import com.qing.browser.events.EventController;
import com.qing.browser.pic.ImageLoaderTest;
import com.qing.browser.ui.MyPagerAdapter;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.DialogUtil;
import com.qing.browser.utils.IOUtils;
import com.qing.browser.utils.Tools;

/**
 * Download list activity.
 */
public class DownloadsListActivity extends BaseActivity implements
		IDownloadEventsListener {
	private DownloadListAdapter mAdapter;
	private DownFinishListAdapter downfinshlistadapter;
	private int currentView = 0;// 当前视图
	private ListView mListloading;
	private ListView mListFinsh;
	private List<Map<String, Object>> downfinish;
	private List<DownloadItem> downloads;
	private DialogUtil dialogUtil;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private View viewpage1, viewpage2;
	private TextView tv_bookmark, tv_history;
	private LinearLayout tv_bookmark_layout, tv_history_layout;
	private LinearLayout data_layout, no_data_layout;
	private DownloadManager downloadManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloads_list_activity);
		ImageView btn_back = (ImageView) findViewById(R.id.item_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		this.downloadManager = DownloadManager.getInstance();
		EventController.getInstance().addDownloadListener(this);

		InitViewPager();
	}

	/**
	 * 初始化页卡内容ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		tv_bookmark = (TextView) findViewById(R.id.tv_bookmark);
		tv_bookmark.setText("正在下载");

		tv_history = (TextView) findViewById(R.id.tv_history);
		tv_history.setText("已下载");

		tv_bookmark_layout = (LinearLayout) findViewById(R.id.tv_bookmark_layout);
		tv_history_layout = (LinearLayout) findViewById(R.id.tv_history_layout);

		tv_bookmark.setOnClickListener(new MyOnClickListener(0));
		tv_history.setOnClickListener(new MyOnClickListener(1));

		setCurPage(0);
		tv_bookmark.setTextColor(Color.parseColor("#FFFFFF"));
		tv_bookmark_layout.setBackgroundResource(R.drawable.title_select_bg);

		listViews = new ArrayList<View>();

		LayoutInflater mInflater = getLayoutInflater();
		viewpage1 = mInflater.inflate(R.layout.downloads_page, null);
		viewpage2 = mInflater.inflate(R.layout.downloads_page, null);
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
		initPage(viewpage1);
	}

	private void initPage(View view) {
		mListloading = (ListView) view.findViewById(R.id.list);
		data_layout = (LinearLayout) view.findViewById(R.id.data_layout);
		no_data_layout = (LinearLayout) view.findViewById(R.id.no_data_layout);

		TextView btn_clear = (TextView) view.findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Dao dao = new Dao(DownloadsListActivity.this);
				dao.clean();
				dao.closeDb();
				fillData();
				downloadManager.stopAllDownloadTask();
			}
		});

		fillData();
	}

	private void initPage2(View view) {

		data_layout = (LinearLayout) view.findViewById(R.id.data_layout);
		no_data_layout = (LinearLayout) view.findViewById(R.id.no_data_layout);
		mListFinsh = (ListView) view.findViewById(R.id.list);
		mListFinsh.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String filepath = downfinish.get(position).get("path")
						.toString();
				File file = new File(filepath);
				if (file.exists()) {

					MimeTypeMap mime = MimeTypeMap.getSingleton();
					String ext = file.getName()
							.substring(file.getName().lastIndexOf(".") + 1)
							.toLowerCase();
					String type = mime.getMimeTypeFromExtension(ext);

					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setDataAndType(Uri.fromFile(file), type);

					try {

						startActivity(i);

					} catch (ActivityNotFoundException e) {
						
					}
				}
			}
		});

		TextView btn_clear = (TextView) view.findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogUtil = new DialogUtil.Builder(DownloadsListActivity.this)
						.setTitleText("温馨提示").setText("删除全部已下载文件？")
						.setPositiveButton("确定", new View.OnClickListener() {
							public void onClick(View v) {
								cleardownloadfile();
								downfinish.clear();
								downfinshlistadapter.notifyDataSetChanged();
								data_layout.setVisibility(View.GONE);
								no_data_layout.setVisibility(View.VISIBLE);
								dialogUtil.dismiss();
							}
						}).setNegativeButton("取消", new View.OnClickListener() {
							public void onClick(View v) {
								dialogUtil.dismiss();
							}
						}).create();
				dialogUtil.show();
			}
		});

		fillDataFinish();

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
				initPage(viewpage1);
			} else if (arg0 == 1) {
				initPage2(viewpage2);
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
	protected void onDestroy() {
		EventController.getInstance().removeDownloadListener(this);
		super.onDestroy();
	}

	/**
	 * Fill the downloading list.
	 */
	private void fillData() {
		Dao dao = new Dao(DownloadsListActivity.this);
		downloads = dao.getInfos(this);
		dao.closeDb();

		if (downloads == null || downloads.size() == 0) {
			data_layout.setVisibility(View.GONE);
			no_data_layout.setVisibility(View.VISIBLE);
		} else {
			data_layout.setVisibility(View.VISIBLE);
			no_data_layout.setVisibility(View.GONE);
			mAdapter = new DownloadListAdapter(this, downloads, downloadManager);
			mListloading.setAdapter(mAdapter);
		}

	}

	/**
	 * Get pictures under directory of strPath
	 * 
	 * @param strPath
	 * @return list
	 */
	public List<Map<String, Object>> getdownloadfile() {
		File downloadFolder = IOUtils.getDownloadFolder();
		File[] files = downloadFolder.listFiles();

		if (files == null) {
			return null;
		}
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f2.lastModified()).compareTo(
						f1.lastModified());
			}
		});

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < files.length; i++) {
			final File f = files[i];
			if (f.isFile()) {
				long size = 0;
				try {
					size = Tools.getFileSize(f);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("获取文件大小", "获取失败!");
				}

				// TODO 未下载完成 不加入已下载列表
				if (f.getName().toString().indexOf(Constants.DownLoadFileName) == -1) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name", f.getName());
					map.put("path", f.getPath());
					map.put("size", Tools.getAppSize(size));
					list.add(map);
				}

			}
		}

		return list;
	}

	public void cleardownloadfile() {
		File downloadFolder = IOUtils.getDownloadFolder();
		File[] files = downloadFolder.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			final File f = files[i];
			if (f.isFile()) {
				// TODO 未下载完成 不删除
				if (f.getName().toString().indexOf(Constants.DownLoadFileName) == -1) {
					f.delete();
				}
			}
		}

	}

	/**
	 * Fill the download list.
	 */
	private void fillDataFinish() {
		downfinish = getdownloadfile();

		if (downfinish == null || downfinish.size() == 0) {
			data_layout.setVisibility(View.GONE);
			no_data_layout.setVisibility(View.VISIBLE);
		} else {
			data_layout.setVisibility(View.VISIBLE);
			no_data_layout.setVisibility(View.GONE);
			downfinshlistadapter = new DownFinishListAdapter(this, downfinish);
			mListFinsh.setAdapter(downfinshlistadapter);
		}

	}

	// TODO
	@Override
	public void onDownloadEvent(String event, Object data) {
		if (event.equals(EventConstants.EVT_DOWNLOAD_ON_START)) {
			fillData();
		} else if (event.equals(EventConstants.EVT_DOWNLOAD_ON_PROGRESS)) {
			if (data != null) {
				DownloadItem item = (DownloadItem) data;

				ProgressBar bar = mAdapter.getBarMap().get(item.getUrl());
				if (bar != null) {
					bar.setMax(100);
					bar.setProgress(item.getProgress());
				}
				TextView mSize = mAdapter.getSizeMap().get(item.getUrl());
				if (mSize != null) {
					String str = Tools.getAppSize(item.getCompeleteSize())
							+ "/" + Tools.getAppSize(item.getEndPos());
					mSize.setText(str);
				}

				TextView mSpeed = mAdapter.getSpeedMap().get(item.getUrl());
				if (mSpeed != null) {
					long curTime = System.currentTimeMillis();
					int usedTime = (int) ((curTime - DownloadManager.startTime) / 1000);
					if (usedTime == 0)
						usedTime = 1;
					int downloadSpeed = (item.getCompeleteSize() / usedTime) / 1024;
					mSpeed.setText(downloadSpeed + "KB/S");

				}
				Button mStop = mAdapter.getButtonMap().get(item.getUrl());
				if (mStop != null) {
					mStop.setText("暂停");
				}

			}
		} else if (event.equals(EventConstants.EVT_DOWNLOAD_ON_FINISHED)) {
			if (data != null) {
				DownloadItem item = (DownloadItem) data;
				Log.i("H", "DownloadsListActivity " + item.getFileName()
						+ "下载完成");
				TextView title = mAdapter.getTitleMap().get(item.getUrl());
				if (title != null) {
					title.setText(item.getFileName());
				}

				ProgressBar bar = mAdapter.getBarMap().get(item.getUrl());
				if (bar != null) {
					bar.setProgress(bar.getMax());
				}

				Button button = mAdapter.getButtonMap().get(item.getUrl());
				if (button != null) {
					button.setText("完成");
					button.setEnabled(false);
				}

				downloads.clear();
				fillData();

			}
		} else if (event.equals(EventConstants.EVT_DOWNLOAD_ON_PAUSE)) {
			DownloadItem item = (DownloadItem) data;
			TextView title = mAdapter.getTitleMap().get(item.getUrl());
			if (title != null) {
				title.setText(item.getFileName().replace(
						Constants.DownLoadFileName, ""));

			}

			ProgressBar bar = mAdapter.getBarMap().get(item.getUrl());
			if (bar != null) {
				bar.setMax(100);
				bar.setProgress(item.getProgress());
			}

			Button button = mAdapter.getButtonMap().get(item.getUrl());
			if (button != null) {
				button.setText("继续");
				button.setEnabled(true);
			}
			
			TextView mSpeed = mAdapter.getSpeedMap().get(item.getUrl());
			if (mSpeed != null) {
				mSpeed.setText("0KB/S");

			}
			
			downloads.clear();
			fillData();
		}

	}

	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				Log.e("ApkIconLoader", e.toString());
			}
		}
		return null;
	}

	public class DownFinishListAdapter extends BaseAdapter {

		private Context mContext;
		private List<Map<String, Object>> mDownloads;

		public DownFinishListAdapter(Context context,
				List<Map<String, Object>> downloads) {
			mContext = context;
			mDownloads = downloads;
		}

		@Override
		public int getCount() {
			return mDownloads.size();
		}

		@Override
		public Object getItem(int position) {
			return mDownloads.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.download_finish_item,
						null);
			}
			final ImageView download_image = (ImageView) convertView
					.findViewById(R.id.download_image);
			final TextView fileNameView = (TextView) convertView
					.findViewById(R.id.DownloadRow_FileName);
			final TextView urlView = (TextView) convertView
					.findViewById(R.id.DownloadRow_Url);
			final Button stopButton = (Button) convertView
					.findViewById(R.id.DownloadRow_StopBtn);

			fileNameView.setText(mDownloads.get(position).get("name")
					.toString());
			urlView.setText(mDownloads.get(position).get("size").toString());
			Drawable img = getApkIcon(DownloadsListActivity.this, mDownloads
					.get(position).get("path").toString());
			if (null == img) {
				download_image.setImageBitmap(ImageLoaderTest
						.getSmallBitmap(mDownloads.get(position).get("path")
								.toString()));
			} else {
				download_image.setImageDrawable(img);
			}

			stopButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String filepath = downfinish.get(position).get("path")
							.toString();
					File file = new File(filepath);
					int idx = file.getPath().lastIndexOf(".");
					if (idx <= 0) {
						return;
					}
					String suffix = file.getPath().substring(idx);
					if (suffix.toLowerCase().equals(".apk")
							|| suffix.toLowerCase().equals(".exe"))// TODO 安装文件
					{

						MimeTypeMap mime = MimeTypeMap.getSingleton();
						String ext = file.getName()
								.substring(file.getName().lastIndexOf(".") + 1)
								.toLowerCase();
						String type = mime.getMimeTypeFromExtension(ext);

						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setDataAndType(Uri.fromFile(file), type);

						try {
							startActivity(i);
						} catch (ActivityNotFoundException e) {
						}
					}
				}
			});

			return convertView;
		}

	}
}
