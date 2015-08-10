package com.qing.browser.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.components.CustomWebView;
import com.qing.browser.controllers.Controller;
import com.qing.browser.model.adapters.HistoryExpandableListAdapter;
import com.qing.browser.model.items.HistoryItem;
import com.qing.browser.providers.BookmarksProviderWrapper;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.launcher.LauncherApplication;
import com.qing.browser.utils.ApplicationUtils;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.DialogListUtil;
import com.qing.browser.utils.DialogUtil;

/**
 * history list activity.
 */
public class HistoryListActivity {
	private Activity context; 
	ExpandableListView expandablelistview;
	private HistoryExpandableListAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	DialogListUtil builder;
	final String[] mItems = { "�´��ڴ�", "����url", "ɾ��" };
	private int sign = -1;// �����б��չ��
	private DialogUtil dialogUtil;
	private LinearLayout no_data_layout, data_layout;

	ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
	public void iList(Activity activity, View page) {
		
		this.context = activity;
		
		TextView btn_clean = (TextView) page.findViewById(R.id.btn_clean);
		btn_clean.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				clearHistory();
			}
		});
		
		data_layout = (LinearLayout)page.findViewById(R.id.data_layout);
		no_data_layout = (LinearLayout)page.findViewById(R.id.no_data_layout);
		 
		
		expandablelistview = (ExpandableListView) page.findViewById(R.id.expandablelistview);
		expandablelistview.setGroupIndicator(null);// ȥ����ͷͼ��
 
		// ֻչ��һ��group��ʵ�ַ���
		expandablelistview.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
			        int groupPosition, long id) { 
				if (sign == -1) {
					// չ����ѡ��group
					expandablelistview.expandGroup(groupPosition);
					// ���ñ�ѡ�е�group���ڶ���
					expandablelistview.setSelectedGroup(groupPosition);
					sign = groupPosition;
				} else if (sign == groupPosition) {
					expandablelistview.collapseGroup(sign);
					sign = -1;
				} else {
					expandablelistview.collapseGroup(sign);
					// չ����ѡ��group
					expandablelistview.expandGroup(groupPosition);
					// ���ñ�ѡ�е�group���ڶ���
					expandablelistview.setSelectedGroup(groupPosition);
					sign = groupPosition;
				}
				return true;
			}
		});
		
		expandablelistview.setOnChildClickListener(new OnChildClickListener() { 
			public boolean onChildClick(ExpandableListView parent, View v,
			        final int groupPosition, final int childPosition, long id) {
				HistoryItem item = (HistoryItem) mAdapter.getChild(groupPosition, childPosition);
				builder = new DialogListUtil.Builder(context)
						.setTitleText(item.getTitle()).setItems(mItems)
						.setlistListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								// ����󵯳�����ѡ���˵ڼ���
								ExpandableClick(groupPosition, childPosition,
										arg2);
								builder.dismiss();
						}
					}).create();
				builder.show();
				
				return false;
			}
		});
		
		

		fillData();
		expandablelistview.expandGroup(0);
	}

	private void ExpandableClick(int groupPosition, int childPosition, int which){
		HistoryItem item = (HistoryItem) mAdapter.getChild(groupPosition, childPosition);

		switch (which) {
		case 0:
			doNavigateToUrl(item.getUrl(), true);
			break;
		case 1:
			ApplicationUtils.copyTextToClipboard(context, item.getUrl(),
					context.getString(R.string.Commons_UrlCopyToastMessage));
			break;
		case 2:
			BookmarksProviderWrapper.deleteHistoryRecord(item.getUrl(),context);
			fillData();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Fill the history list.
	 */
	private void fillData() {
		
		mData = BookmarksProviderWrapper.getStockHistory(context);
		
		if(mData == null || mData.size() ==0){
			data_layout.setVisibility(View.GONE);
			no_data_layout.setVisibility(View.VISIBLE);
		}else{
			data_layout.setVisibility(View.VISIBLE);
			no_data_layout.setVisibility(View.GONE);
		}
		
		mAdapter = new HistoryExpandableListAdapter(context, mData,
				ApplicationUtils.getFaviconSizeForBookmarks(context)); 
		expandablelistview.setAdapter(mAdapter);
		
	}


 

	/**
	 * Load the given url.
	 * 
	 * @param url
	 *            The url.
	 * @param newTab
	 *            If True, will open a new tab. If False, the current tab is
	 *            used.
	 */
	private void doNavigateToUrl(String url, boolean newTab) {
		Intent result = new Intent();
		result.putExtra(Constants.EXTRA_ID_NEW_TAB, newTab);
		result.putExtra(Constants.EXTRA_ID_URL, url);

		if (context.getParent() != null) {
			context.getParent().setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, result);
		} else {
			context.setResult(Launcher.RESULT_OPEN_NEW_BOOKMARKS_HISTORY, result);
		}
		context.finish();
	}

	/**
	 * Clear history.
	 */
	private void doClearHistory() {
		mProgressDialog = ProgressDialog.show(context, context.getResources()
				.getString(R.string.Commons_PleaseWait), context.getResources()
				.getString(R.string.Commons_ClearingHistory));
		
		BookmarksProviderWrapper.clearHistory(context);
		new HistoryClearer();
	}

	/**
	 * Display confirmation and clear history.
	 */
	private void clearHistory() {		
		dialogUtil = new DialogUtil.Builder(context)
				.setTitleText("��ܰ��ʾ")
				.setText("ȷ��Ҫɾ����ʷ��¼��")
				.setPositiveButton("ȷ��",
						new View.OnClickListener() {
							public void onClick(View v) {
								dialogUtil.dismiss();
								doClearHistory();
							}
						})
				.setNegativeButton("ȡ��",new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialogUtil.dismiss();
					}
				})
				.create();
		dialogUtil.show();
	}

	/**
	 * Runnable to clear history.
	 */
	private class HistoryClearer implements Runnable {

		/**
		 * Constructor.
		 */
		public HistoryClearer() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			
			for (Object object : Controller.getInstance().getWebViewList()) {
				if (object instanceof CustomWebView) {
					CustomWebView webView = (CustomWebView) object;
					webView.clearHistory();
				}
			}

			handler.sendEmptyMessage(0);
		}

		private Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mProgressDialog.dismiss();
				fillData();
			}
		};
	}

}
