package com.qing.browser.ui.recent;

import android.graphics.Bitmap;

public final class TaskDescription {
	private Bitmap mThumbnail;
	private CharSequence mLabel;
	private int position;

	public TaskDescription() {

	}

	public CharSequence getLabel() {
		return mLabel;
	}

	public void setLabel(CharSequence label) {
		mLabel = label;
	}

	public void setThumbnail(Bitmap thumbnail) {
		mThumbnail = thumbnail;
	}

	public Bitmap getThumbnail() {
		return mThumbnail;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String toString() {
		return "item " + getPosition() + " " + getLabel() + " "
				+ getThumbnail().getWidth();
	}
}
