package com.qing.browser.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;

public class DialogImageUtil extends Dialog {

	private final Context mContext;
	private TextView dialogTitleView;
	private ImageView dialog_image;
	private Button btn_ok;
	private Button btn_cancel;
	private int dialogresId;
	private CharSequence dialogTitle;
	private CharSequence positiveText;
	private CharSequence negativeText;
	private int theme;
	private View.OnClickListener positiveClickListener;
	private View.OnClickListener negativeClickListener;

	public DialogImageUtil(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public DialogImageUtil(Context context, int theme) {
		// TODO Auto-generated constructor stub
		super(context, theme);
		this.mContext = context;
		this.theme = theme;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_image_page);
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
		dialogTitleView = (TextView) findViewById(R.id.dialog_title);
		if (dialogTitle != null) {
			dialogTitleView.setVisibility(View.VISIBLE);
			dialogTitleView.setText(dialogTitle);
		}else {
			dialogTitleView.setVisibility(View.VISIBLE);
			dialogTitleView.setText("温馨提示");
		}
		dialog_image = (ImageView) findViewById(R.id.dialog_image);
		dialog_image.setImageResource(dialogresId);
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
	}

	private void setImageResource(int resId) { 
		dialogresId = resId;
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

	public static class Builder {
		private final DatePickParams P;

		public Builder(Context context) {
			P = new DatePickParams(context);
		}

		public Builder setTitleText(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		public Builder setImageResource(int resId) {
			P.resId = resId;
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

		public DialogImageUtil create() {
			final DialogImageUtil dialog = new DialogImageUtil(P.mContext,
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
		public int resId = 0;
		public final Context mContext;

		private CharSequence mNegativeButtonText;
		private View.OnClickListener mNegativeButtonListener;

		public DatePickParams(Context context) {
			mContext = context;
		}

		public void apply(DialogImageUtil dialog) {

			if (mTitle != null) {
				dialog.setTitleText(mTitle);
			}
			if (resId != 0) {
				dialog.setImageResource(resId);
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
