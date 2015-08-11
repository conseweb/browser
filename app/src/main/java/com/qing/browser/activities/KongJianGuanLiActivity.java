package com.qing.browser.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.providers.BookmarksProviderWrapper;
import com.qing.browser.utils.DialogUtil;


public class KongJianGuanLiActivity extends BaseActivity {

	private MyGuanliAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> Itemload;
	
	String[] gongneng = { "清除缓存", "清除历史记录", "清除所有Cookie数据", "清除表单","清除密码"};
	
	CheckBox[] checkbox = new CheckBox[gongneng.length]; 
	
	private Button xiaochu;
	private DialogUtil dialogUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kong_jian_guan_li_page);
		
		TextView item_title = (TextView) findViewById(R.id.item_title);
		item_title.setText("清除数据");
		ImageView btn_back = (ImageView) findViewById(R.id.item_back);
		btn_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		ListView listview = (ListView)findViewById(R.id.shortList);
		
		Itemload = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < gongneng.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemText", gongneng[i]);
			Itemload.add(map);
		}
		
		mAdapter = new MyGuanliAdapter(Itemload, this);
		listview.setAdapter(mAdapter);
		
		xiaochu = (Button)findViewById(R.id.xiaochu);
		
		xiaochu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogUtil = new DialogUtil.Builder(
						KongJianGuanLiActivity.this)
						.setTitleText("温馨提示")
						.setText("确定删除所选数据吗？")
						.setPositiveButton("确定",
								new View.OnClickListener() {
									public void onClick(View v) {
										
										if(checkbox[0].isChecked()){
											//清除缓存
											clearCacheFolder(KongJianGuanLiActivity.this.getCacheDir(), System.currentTimeMillis());
										}
										if(checkbox[1].isChecked()){
											//清除历史记录
											BookmarksProviderWrapper.clearHistory(KongJianGuanLiActivity.this);
										}
										if(checkbox[2].isChecked()){
											//清除所有cookies
											new CookiesClearer();
										}
										if(checkbox[3].isChecked()){
											//清除表单
										}
										
										if(checkbox[4].isChecked()){
											//清除密码
										}
										
										dialogUtil.dismiss();
										dialogUtil = new DialogUtil.Builder(
												KongJianGuanLiActivity.this)
												.setTitleText("温馨提示")
												.setText("删除成功！")
												.setPositiveButton("确定",
														new View.OnClickListener() {
															public void onClick(View v) {
																finish();
															}
														})
												.create();
										dialogUtil.show();
									}
								})
						.setNegativeButton("返回", new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								dialogUtil.dismiss();
							}
						})		
						.create();
				dialogUtil.show();
			}
		});
		
	}

	
	
	private class MyGuanliAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> list;
		private LayoutInflater inflater = null;
		private Context context;
		ViewHolder holder;

		public MyGuanliAdapter(ArrayList<HashMap<String, Object>> list,Context context) {
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
			
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.kong_jian_guan_li_list_item,null);
				
				holder.guanli = (LinearLayout) convertView
						.findViewById(R.id.guanli);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.checkbox_select = (CheckBox) convertView
						.findViewById(R.id.checkbox_select);
				
				holder.tv_title.setText(list.get(position).get("ItemText").toString());
					
				checkbox[position]=holder.checkbox_select;
				
				holder.guanli.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(checkbox[position].isChecked()){
							checkbox[position].setChecked(false);
						}else{
							checkbox[position].setChecked(true);
						}
						
					}
				});

			return convertView;
		}

	}

	class ViewHolder {
		public TextView tv_title;
		public LinearLayout guanli;
		public CheckBox checkbox_select;

	}
	
	
	/**
	 * 清除Cookies值
	 */
	class CookiesClearer implements Runnable {
		public CookiesClearer() {
			new Thread(this).start();
		}
		@Override
		public void run() {
			CookieSyncManager.createInstance(KongJianGuanLiActivity.this);
			CookieManager.getInstance().removeAllCookie();
		}

	}
	
	/*
	 * 清除缓存
	 */
	private int clearCacheFolder(File dir, long numDays) {        
        int deletedFiles = 0;       
        if (dir!= null && dir.isDirectory()) {           
            try {              
                for (File child:dir.listFiles()) {  
                    if (child.isDirectory()) {            
                        deletedFiles += clearCacheFolder(child, numDays);        
                    }  
                    if (child.lastModified() < numDays) {   
                        if (child.delete()) {                 
                            deletedFiles++;         
                        }  
                    } 
                }           
            } catch(Exception e) {     
                e.printStackTrace();  
            }   
        }     
        return deletedFiles;   
    }
	
}
