package com.google.zxing.client.android;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.result.ResultHandler;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;
import com.qing.browser.utils.Tools;

public class ErWeiMaListActivity extends BaseActivity {

	private Button item_back = null;
	private ErWeiMaAdapter mAdapter;
	String[] item = { "生成二维码","扫描历史", "生成历史"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_list);

		item_back = (Button) findViewById(R.id.dialog_cancel);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		ListView lv = (ListView) findViewById(R.id.entry_list);
		
		ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < item.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemText", item[i]);
			Item.add(map);
		}
		
		mAdapter = new ErWeiMaAdapter(Item, ErWeiMaListActivity.this);
		lv.setAdapter(mAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0){
					Intent intent = new Intent(ErWeiMaListActivity.this,
							ErWeiMaEntryActivity.class);
					startActivity(intent);
				}else if(position == 1){
					Intent intent = new Intent(ErWeiMaListActivity.this,
							ErWeiMaLiShilistActivity.class);
					startActivity(intent);
				}else if(position == 2){
					if (Tools.sdCardExist()) {
						Intent intent = new Intent(ErWeiMaListActivity.this,
								ErWeiMaJieGuoActivity.class);
						intent.putExtra("String", "tupianliulan");
						startActivity(intent);
					} else {
						Toast.makeText(ErWeiMaListActivity.this,
								"SD卡已卸载或不存在.", 1000).show();
					}
				}

			}
		});
		ResultHandler.ErWeiMaListActivity = this;
	}
	
	private class ErWeiMaAdapter extends BaseAdapter {
		private ArrayList<HashMap<String, Object>> list;
		private LayoutInflater inflater = null;
		private Context context;

		public ErWeiMaAdapter(ArrayList<HashMap<String, Object>> list,Context context) {
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
			
			convertView = inflater.inflate(R.layout.er_wei_ma_item,null);
			
			TextView erweima_name = (TextView) convertView.findViewById(R.id.erweima_name);
			
			erweima_name.setText(list.get(position).get("ItemText").toString());
				
			return convertView;
		}

	}
	

}
