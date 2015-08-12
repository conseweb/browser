package com.qing.browser.activities;

import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.utils.Constants;

public class ScreenSwitchDialogActivity extends BaseActivity {

	private Button mCancelButton;
	private RadioGroup radiogroup;
	private RadioButton hengping;
	private RadioButton shuping;
	private RadioButton xitong;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_screen_switch_page);
		TextView dialog_title = (TextView) findViewById(R.id.dialog_title);
		dialog_title.setText("选择屏幕");

		mCancelButton = (Button) findViewById(R.id.dialog_cancel);

		radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
		hengping= (RadioButton) findViewById(R.id.hengping);
		shuping= (RadioButton) findViewById(R.id.shuping);
		xitong= (RadioButton) findViewById(R.id.xitong);
		
		if (0 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			hengping.setChecked(true);
		} else if (1 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			shuping.setChecked(true);
		} else if (2 == sp.getInt(Constants.PREFERENCES_SCREEN_ORIENTATION, 0)) {
			xitong.setChecked(true);
		}
		
		
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Editor editor = sp.edit();
				switch (checkedId) {
				case R.id.hengping:
					editor.putInt(Constants.PREFERENCES_SCREEN_ORIENTATION,
								0);
					editor.commit();
					Toast.makeText(ScreenSwitchDialogActivity.this, "横屏",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case R.id.shuping:
					editor.putInt(Constants.PREFERENCES_SCREEN_ORIENTATION,
								1);
					editor.commit(); 
					Toast.makeText(ScreenSwitchDialogActivity.this, "竖屏",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case R.id.xitong:
					editor.putInt(Constants.PREFERENCES_SCREEN_ORIENTATION,
							2);
					editor.commit(); 
					Toast.makeText(ScreenSwitchDialogActivity.this, "跟随系统",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
			}
		});

		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

}
