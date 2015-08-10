package com.google.zxing.client.android;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.google.zxing.client.android.result.ResultHandler;
import com.qing.browser.R;
import com.qing.browser.activities.BaseActivity;

public class ErWeiMaEntryActivity extends BaseActivity {

	private Button item_back = null;
	String[] item = { "普通文本", "短信", "网址", "号码", "名片", "邮件" };

	final int[] imageSub = { R.drawable.er_wei_ma_wenben,
			R.drawable.er_wei_ma_duanxin, R.drawable.er_wei_ma_wangzhi,
			R.drawable.er_wei_ma_haoma, R.drawable.er_wei_ma_mingpian,
			R.drawable.er_wei_ma_youjian };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.er_wei_ma_entry);
		item_back = (Button) findViewById(R.id.dialog_cancel);
		item_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		GridView lv = (GridView) findViewById(R.id.entry_list);
		lv.setNumColumns(3);
		ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < item.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", imageSub[i]);
			map.put("ItemText", item[i]);
			Item.add(map);
		}

		SimpleAdapter SA = new SimpleAdapter(this, Item,
				R.layout.faxian_gridview_item, new String[] { "ItemImage",
						"ItemText" },
				new int[] { R.id.ItemImage, R.id.ItemText });
		lv.setAdapter(SA);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ErWeiMaEntryActivity.this,
						ErWeiMaCreateActivity.class);
				if (position == 0) {
					intent.putExtra("String", "wenbenbutton");
				} else if (position == 1) {
					intent.putExtra("String", "duanxinbutton");
				} else if (position == 2) {
					intent.putExtra("String", "URLbutton");
				} else if (position == 3) {
					intent.putExtra("String", "telbutton");
				} else if (position == 4) {
					intent.putExtra("String", "mingpianbutton");
				} else if (position == 5) {
					intent.putExtra("String", "youjianbutton");
				}
				startActivity(intent);
				finish();
			}
		});

		ResultHandler.ErWeiMaEntryActivity = this;
	}

}
