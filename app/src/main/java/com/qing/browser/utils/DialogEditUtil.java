package com.qing.browser.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qing.browser.R;

public class DialogEditUtil extends Dialog {

	private final Context mContext;
	private EditText dialogTextView;
	private TextView dialogTitleView;
	private Button btn_cancel;
	private Button btn_sure;
	private CharSequence dialogTitle;
	private int theme;
	private View.OnClickListener positiveClickListener;
	private View.OnClickListener negativeClickListener;

	public DialogEditUtil(Context context) {
		super(context);
		this.mContext = context;
	}

	public DialogEditUtil(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		this.theme = theme;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_edit_page);
		findView();
		setListener();
	}

	private void setListener() {
		if (negativeClickListener != null) {
			btn_cancel.setOnClickListener(negativeClickListener);
		} else {
			btn_cancel.setOnClickListener(dismissListener);
		}
		if (positiveClickListener != null) {
			btn_sure.setOnClickListener(positiveClickListener);
		} else {
			btn_sure.setOnClickListener(dismissListener);
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
			dialogTitleView.setText("Œ¬‹∞Ã· æ");
		}
		dialogTextView = (EditText) findViewById(R.id.dialog_edit);
		btn_sure = (Button) findViewById(R.id.dialog_ok);
		btn_cancel = (Button) findViewById(R.id.dialog_cancel);
	}

	public String GetEditText() {
		if (dialogTextView != null) {
			return dialogTextView.getText().toString();
		}
		return "";
	}
	private void setTitleText(CharSequence title) {
		dialogTitle = title;
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

		public Builder setTitleText(CharSequence title) {
			P.mTitle = title;
			return this;
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

		public DialogEditUtil create() {
			final DialogEditUtil dialog = new DialogEditUtil(P.mContext,
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
		public final Context mContext;

		private View.OnClickListener mNegativeButtonListener;

		public DatePickParams(Context context) {
			mContext = context;
		}

		public void apply(DialogEditUtil dialog) {
			if (mTitle != null) {
				dialog.setTitleText(mTitle);
			}
			dialog.setPositiveButton(mPositiveButtonListener);
			dialog.setNegativeButton(mNegativeButtonListener);
		}
	}

}
