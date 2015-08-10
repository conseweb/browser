package com.qing.browser.download;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.Tools;

/**
 * The adapter for the download UI list.
 */
public class DownloadListAdapter extends BaseAdapter {

	private Context mContext;
	private List<DownloadItem> mDownloads;

	private Map<String, TextView> mTitleMap;
	private Map<String, ProgressBar> mBarMap;
	private Map<String, Button> mButtonMap;
	private Map<String, TextView> mSizeMap;
	private Map<String, TextView> mSpeedMap;
	private DownloadManager downloadManager;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 * @param downloads
	 *            The download list.
	 */
	public DownloadListAdapter(Context context, List<DownloadItem> downloads,
			DownloadManager downloadmanager) {
		mContext = context;
		mDownloads = downloads;
		mTitleMap = new Hashtable<String, TextView>();
		mBarMap = new Hashtable<String, ProgressBar>();
		mButtonMap = new Hashtable<String, Button>();
		mSizeMap = new Hashtable<String, TextView>();
		mSpeedMap = new Hashtable<String, TextView>();
		this.downloadManager = downloadmanager;

	}

	/**
	 * Get a map of download item related to the UI text component representing
	 * the download title.
	 * 
	 * @return A Map<DownloadItem, TextView>.
	 */
	public Map<String, TextView> getTitleMap() {
		return mTitleMap;
	}

	/**
	 * Get a map of download item related to the UI progress bar component.
	 * 
	 * @return A Map<DownloadItem, ProgressBar>.
	 */
	public Map<String, ProgressBar> getBarMap() {
		return mBarMap;
	}

	public Map<String, TextView> getSizeMap() {
		return mSizeMap;
	}

	public Map<String, TextView> getSpeedMap() {
		return mSpeedMap;
	}

	/**
	 * Get a map of download item related to the UI cancel button component.
	 * 
	 * @return A Map<DownloadItem, ImageButton>.
	 */
	public Map<String, Button> getButtonMap() {
		return mButtonMap;
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

	// 改变下载按钮的样式
	private void changeBtnStyle(ImageView btn, boolean enable) {
		if (enable) {
			btn.setImageResource(R.drawable.download_ing);
		} else {
			btn.setImageResource(R.drawable.download_stop);
		}
		btn.setEnabled(enable);
	}

	public static class ViewHolder {
		public ImageView downimage;
		public ProgressBar progressBar;
		public TextView fileNameView;
		public TextView urlView;
		public TextView speed;
		public Button stopButton;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.download_row, null);
			holder.downimage = (ImageView) convertView
					.findViewById(R.id.DownloadRowimage);
			holder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.DownloadRow_ProgressBar);
			holder.fileNameView = (TextView) convertView
					.findViewById(R.id.DownloadRow_FileName);
			holder.urlView = (TextView) convertView
					.findViewById(R.id.DownloadRow_Url);
			holder.speed = (TextView) convertView
					.findViewById(R.id.DownloadRow_speed);
			holder.stopButton = (Button) convertView
					.findViewById(R.id.DownloadRow_StopBtn);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DownloadItem item = mDownloads.get(position);

		holder.progressBar.setIndeterminate(false);
		holder.progressBar.setMax(100);
		holder.progressBar.setProgress(item.getCompeleteSize() * 100 / item.getEndPos());

		long curTime = System.currentTimeMillis();
		int usedTime = (int) ((curTime - DownloadManager.startTime) / 1000);
		if (usedTime == 0)
			usedTime = 1;
		int downloadSpeed = (item.getProgress() / usedTime) / 1024;
		holder.speed.setText(downloadSpeed + "KB/S");

		String itemName = item.getFileName().replace(
				Constants.DownLoadFileName, "");
		holder.fileNameView.setText(itemName);
		
		switch (item.getDownloadState()) {
		case DownloadManager.DOWNLOAD_STATE_NORMAL:
			holder.stopButton.setText("下载");
			this.changeBtnStyle(holder.downimage, true);
			break;
		case DownloadManager.DOWNLOAD_STATE_DOWNLOADING:
			holder.stopButton.setText("暂停");
			this.changeBtnStyle(holder.downimage, true);
			break;
		case DownloadManager.DOWNLOAD_STATE_WAITING:
			holder.stopButton.setText("等待");
			this.changeBtnStyle(holder.downimage, true);
			break;
		case DownloadManager.DOWNLOAD_STATE_PAUSE:
			holder.stopButton.setText("继续");
			this.changeBtnStyle(holder.downimage, false);
			break;
		}

		holder.urlView.setText(Tools.getAppSize(item.getCompeleteSize()) + "/"
				+ Tools.getAppSize(item.getEndPos()));

		holder.stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				switch (item.getDownloadState()) {
				case DownloadManager.DOWNLOAD_STATE_NORMAL:
				case DownloadManager.DOWNLOAD_STATE_DOWNLOADING:
				case DownloadManager.DOWNLOAD_STATE_WAITING:
					changeBtnStyle(holder.downimage, false);
					holder.stopButton.setText("继续");
					holder.progressBar.setProgress(item.getProgress());
					downloadManager.stopDownloadItem(item.getFileName());
					break;

				case DownloadManager.DOWNLOAD_STATE_PAUSE:
					if (Tools.isConnectInternet(mContext)) {
						
						changeBtnStyle(holder.downimage, true);
						holder.stopButton.setText("等待");
						holder.progressBar.setProgress(item.getProgress());
						downloadManager.startDownload(item, mContext);
					} else {
						Toast.makeText(mContext, "请检查网络设置", Toast.LENGTH_SHORT)
								.show();
					}
					break;
				}

			}

		});

		mTitleMap.put(item.getUrl(), holder.fileNameView);
		mBarMap.put(item.getUrl(), holder.progressBar);
		mButtonMap.put(item.getUrl(), holder.stopButton);
		mSizeMap.put(item.getUrl(), holder.urlView);
		mSpeedMap.put(item.getUrl(), holder.speed);
		return convertView;
	}

}
