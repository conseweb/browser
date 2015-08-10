package com.qing.browser.activities;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;

public class Middle_AD_Show extends Dialog {
	private final Context mContext;
	private int theme;
	private Button CLOSEButton;
	private Button OPENButton;
	private ImageView dialog_title;
	private View.OnClickListener CLOSEClickListener = null;
	private View.OnClickListener OPENClickListener = null;
	
	public Middle_AD_Show(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public Middle_AD_Show(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.mContext = context;
		this.theme = theme;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_middle_ad_show);
		CLOSEButton = (Button)findViewById(R.id.ad_close);
		OPENButton = (Button)findViewById(R.id.ad_dakai);
		dialog_title = (ImageView)findViewById(R.id.dialog_title);
		setListener();
        
	}
	
	private void setListener() {
		if (CLOSEClickListener != null) {
			CLOSEButton.setOnClickListener(CLOSEClickListener);
		}else{
			CLOSEButton.setOnClickListener(dismissListener);
		}
		
		if (OPENClickListener != null) {
			OPENButton.setOnClickListener(OPENClickListener);
		}else{
			OPENButton.setText("Á¢¼´¹Ø±Õ");
			OPENButton.setOnClickListener(dismissListener);
		}
	}
	
	private final View.OnClickListener dismissListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	}; 
  
	 

	private void setCLOSEButton(View.OnClickListener onClickListener) {
		CLOSEClickListener = onClickListener;
	}
	
	public void setBitMap(Bitmap bitmap) {
		dialog_title.setImageBitmap(bitmap);
	}
	
	private void setOPENButton(View.OnClickListener onClickListener) {
		OPENClickListener = onClickListener;
	}
	


	public static class Builder {
		private final DatePickParams P;

		public Builder(Context context) {
			P = new DatePickParams(context);
		}
		
		public Builder setCLOSEButton(final View.OnClickListener listener) {
			P.mCLOSEButtonListener = listener;
			return this;
		}

		public Builder setOPENButton(final View.OnClickListener listener) {
			P.mOPENButtonListener = listener;
			return this;
		}


		public Middle_AD_Show create() {
			final Middle_AD_Show dialog = new Middle_AD_Show(P.mContext,
					R.style.ADDialogStyle);
			P.apply(dialog);
			return dialog;
		}
	}

	public static class DatePickParams {
		public View.OnClickListener mCLOSEButtonListener;
		public final Context mContext;

		private View.OnClickListener mOPENButtonListener;

		public DatePickParams(Context context) {
			mContext = context;
		}

		public void apply(Middle_AD_Show dialog) {
				dialog.setCLOSEButton(mCLOSEButtonListener);
			
				dialog.setOPENButton(mOPENButtonListener);
		}
	}

}
