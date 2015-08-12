package com.universe.galaxy.ad;

import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class AdInfo implements Serializable {

	private String icon;
	private Bitmap bitmap = null;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap() {
		URL url = null;
		URLConnection conn = null;
		try {
			url = new URL(icon);
			Log.i("CNCOMAN", icon);
			conn = url.openConnection();
			this.bitmap = new BitmapDrawable(conn.getInputStream()).getBitmap();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			url = null;
			conn = null;
		}
	}

	public AdInfo() {

	}

	public AdInfo(String icon) {
		this.icon = icon;
		this.setBitmap();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
