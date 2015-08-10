package com.qing.browser.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.utils.Tools;

/**
 * About dialog activity.
 */
public class AboutActivity extends BaseActivity {
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);

		TextView item_title = (TextView) findViewById(R.id.item_title);
		item_title.setText("¹ØÓÚ");

		ImageView item_back = (ImageView)findViewById(R.id.item_back);
		item_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		
	}
  

	
}
