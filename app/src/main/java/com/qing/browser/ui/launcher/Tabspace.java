package com.qing.browser.ui.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class Tabspace extends ViewGroup {

	private static final String TAG = "Launcher.Tabspace";

	public Tabspace(Context context) {
		super(context);
	}

	public Tabspace(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Tabspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		/*final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}*/

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				child.layout(0, 0, child.getMeasuredWidth(),
						child.getMeasuredHeight());
			}
		}
	}

	void addInSpace(View view, int index) {
		this.addView(view);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (i == index) {
				child.setVisibility(View.VISIBLE);
			} else {
				child.setVisibility(View.GONE);
			}
		}
	}

	void addAndHideInSpace(View view) {
		view.setVisibility(View.GONE);
		this.addView(view);
	}

	void addAndHideInSpace(View view, int index) {
		view.setVisibility(View.GONE);
		this.addView(view, index);
	}

	void addAndShowInSpace(View view, int index) {
		this.addView(view);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (i == index) {
				child.setVisibility(View.VISIBLE);
			} else {
				child.setVisibility(View.GONE);
			}
		}
	}

	void HideAllView() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			child.setVisibility(View.GONE);
		}
	}

	public void setChildVisibility(int index) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (i == index) {
				child.setVisibility(View.VISIBLE);
			} else {
				child.setVisibility(View.GONE);
			}
		}
	}

	boolean getChildVisibility(int index) {
		final View child = getChildAt(index);
		if (View.GONE == child.getVisibility()) {
			return false;
		} else {
			return true;
		}

	}
}
