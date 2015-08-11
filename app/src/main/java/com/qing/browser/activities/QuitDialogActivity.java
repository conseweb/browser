package com.qing.browser.activities;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.utils.Constants;

public class QuitDialogActivity extends Dialog {
	private final Context mContext;
	private int theme;
	private Button mOkButton;
	private Button mCancelButton;
	private Button mCleanButton;
	private SharedPreferences sp;
	private View.OnClickListener positiveClickListener;
	private View.OnClickListener negativeClickListener;
	private View.OnClickListener cleanButtonClickListener;
	
	public QuitDialogActivity(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public QuitDialogActivity(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.mContext = context;
		this.theme = theme;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_quit_page);
		TextView dialog_title = (TextView)findViewById(R.id.dialog_title);
		dialog_title.setText("退出");
        mOkButton = (Button) findViewById(R.id.dialog_ok);
        mCancelButton = (Button) findViewById(R.id.dialog_cancel);
		mCleanButton = (Button)findViewById(R.id.dialog_ok_clean);

		sp = mContext.getSharedPreferences(Constants.PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		
		setListener();
        
	}
	
	private void setListener() {
		 
		if (negativeClickListener != null) {
			mCancelButton.setOnClickListener(negativeClickListener);
		} else {
			mCancelButton.setOnClickListener(dismissListener);
		}

		if (positiveClickListener != null) {
			mOkButton.setOnClickListener(positiveClickListener);
		} else {
			mOkButton.setOnClickListener(dismissListener);
		}
		
		if (cleanButtonClickListener != null) {
			mCleanButton.setOnClickListener(cleanButtonClickListener);
		}else{
			mCleanButton.setOnClickListener(dismissListener);
		}
	}

	private final View.OnClickListener dismissListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	}; 
  
	private void setCleanButton(View.OnClickListener onClickListener){
		cleanButtonClickListener = onClickListener;
	}
	 

	private void setPositiveButton(View.OnClickListener onClickListener) {
		positiveClickListener = onClickListener;
	}
	
	private void setNegativeButton(View.OnClickListener onClickListener) {
		negativeClickListener = onClickListener;
	}
 

	public static class Builder {
		private final DatePickParams P;

		public Builder(Context context) {
			P = new DatePickParams(context);
		}
 

		public Builder setIcon(int iconId) {
			P.mIconId = iconId;
			return this;
		}

		public Builder setPositiveButton(final View.OnClickListener listener) {
			P.mPositiveButtonListener = listener;
			return this;
		}
		public Builder setNegativeButton(final View.OnClickListener listener) {
			P.mNegativeButtonListener = listener;
			return this;
		}
		
		public Builder setCleanButton(final View.OnClickListener listener) {
			P.mCleanOnClickListener = listener;
			return this;
		}


		public QuitDialogActivity create() {
			final QuitDialogActivity dialog = new QuitDialogActivity(P.mContext,
			        R.style.waitdailog);
			P.apply(dialog);
			return dialog;
		}
	}

	public static class DatePickParams {
		public int mIconId;
		public View.OnClickListener mPositiveButtonListener;
		private View.OnClickListener mNegativeButtonListener;
		private View.OnClickListener mCleanOnClickListener;
		public final Context mContext;

		public DatePickParams(Context context) {
			mContext = context;
		}

		public void apply(QuitDialogActivity dialog) {
			dialog.setPositiveButton(mPositiveButtonListener);
			dialog.setNegativeButton(mNegativeButtonListener);
			dialog.setCleanButton(mCleanOnClickListener);
		}
	}

}
