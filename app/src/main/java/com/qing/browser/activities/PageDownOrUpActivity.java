package com.qing.browser.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.utils.Constants;

public class PageDownOrUpActivity extends Dialog {

	private final Context mContext;
	private Button btn_ok;
	private Button btn_cancel;
	private CharSequence dialogText;
	private CharSequence dialogTitle;
	private CharSequence positiveText;
	private CharSequence negativeText;
	private int theme;
	private View.OnClickListener positiveClickListener;
	private View.OnClickListener negativeClickListener;
	private CheckBox checkBox_key, checkBox_volumeKey;
	private SharedPreferences sp;
	
	
	public PageDownOrUpActivity(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public PageDownOrUpActivity(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.mContext = context;
		this.theme = theme;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_pagedownorup);
		findView();
		setListener();
	}

	private void setListener() {
		// 取消
		if (negativeClickListener != null) {
			btn_cancel.setOnClickListener(negativeClickListener);
		} else {
			btn_cancel.setOnClickListener(dismissListener);
		}
		if (positiveClickListener != null) {
			btn_ok.setOnClickListener(positiveClickListener);
		} else {
			btn_ok.setOnClickListener(dismissListener);
		}

	}

	private final View.OnClickListener dismissListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	private void findView() {
		TextView dialog_title = (TextView)findViewById(R.id.dialog_title);
		dialog_title.setText("选择翻页模式");
		
		btn_ok = (Button) findViewById(R.id.dialog_ok);
		if (positiveText != null) {
			btn_ok.setVisibility(View.VISIBLE);
			btn_ok.setText(positiveText);
		}
		btn_cancel = (Button) findViewById(R.id.dialog_cancel);
		if (negativeText != null) {
			btn_cancel.setVisibility(View.VISIBLE);
			btn_cancel.setText(negativeText);
		}
		
		checkBox_key = (CheckBox) findViewById(R.id.checkBox_key);
		checkBox_volumeKey = (CheckBox) findViewById(R.id.checkBox_volumeKey);

		sp = mContext.getSharedPreferences(Constants.PREFERENCES_NAME,Context.MODE_PRIVATE);
		checkBox_key.setChecked(sp.getBoolean(Constants.Turn_Page_Kye, false));
		checkBox_key.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				checkBox_key.setChecked(isChecked);

			}
		});

		checkBox_volumeKey.setChecked(sp.getBoolean(Constants.Turn_Page_VolumeKey, false));
		checkBox_volumeKey.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			        public void onCheckedChanged(CompoundButton arg0,
			                boolean isChecked) {
				        checkBox_volumeKey.setChecked(isChecked);

			        }
		        });
		
	}

	private void setText(CharSequence text) {
		dialogText = text;
	}

	private void setTitleText(CharSequence title) {
		dialogTitle = title;
	}

	private void setPositiveButton(CharSequence mPositiveButtonText,
			View.OnClickListener onClickListener) {
		positiveText = mPositiveButtonText;
		positiveClickListener = onClickListener;// can't use btn_sure here
												// because it's on defined yet
	}

	private void setNegativeButton(CharSequence mNegativeButtonText,
			View.OnClickListener onClickListener) {
		negativeText = mNegativeButtonText;
		negativeClickListener = onClickListener;// can't use btn_sure here
												// because it's on defined yet
	}
	
	public boolean checkBox_volumeKeyisCheck() {
		return checkBox_volumeKey.isChecked();
	}
	
	public boolean checkBox_keyisCheck() {
		return checkBox_key.isChecked();
		 
	}

	public static class Builder {
		private final DatePickParams P;

		public Builder(Context context) {
			P = new DatePickParams(context);
		}

		public Builder setTitleText(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		public Builder setText(CharSequence text) {
			P.mText = text;
			return this;
		}

		public Builder setIcon(int iconId) {
			P.mIconId = iconId;
			return this;
		}

		public Builder setPositiveButton(CharSequence text,
				final View.OnClickListener listener) {
			P.mPositiveButtonText = text;
			P.mPositiveButtonListener = listener;
			return this;
		}

		public Builder setNegativeButton(CharSequence text,
				final View.OnClickListener listener) {
			P.mNegativeButtonText = text;
			P.mNegativeButtonListener = listener;
			return this;
		}

		public PageDownOrUpActivity create() {
			final PageDownOrUpActivity dialog = new PageDownOrUpActivity(P.mContext,
					R.style.waitdailog);
			P.apply(dialog);
			return dialog;
		}
	}

	public static class DatePickParams {
		public int mIconId;
		public View.OnClickListener mPositiveButtonListener;
		public CharSequence mPositiveButtonText;
		public CharSequence mTitle;
		public CharSequence mText;
		public final Context mContext;

		private CharSequence mNegativeButtonText;
		private View.OnClickListener mNegativeButtonListener;

		public DatePickParams(Context context) {
			mContext = context;
		}

		public void apply(PageDownOrUpActivity dialog) {

			if (mTitle != null) {
				dialog.setTitleText(mTitle);
			}
			if (mText != null) {
				dialog.setText(mText);
			}

			if (mPositiveButtonText != null) {
				dialog.setPositiveButton(mPositiveButtonText,
						mPositiveButtonListener);
			}
			if (mNegativeButtonText != null) {
				dialog.setNegativeButton(mNegativeButtonText,
						mNegativeButtonListener);
			}
		}
	}
	

  



}

 
