package com.qing.browser.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class IconInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String iconUrl;

	private Bitmap bitmap = null;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setDrawable() {
		URL url = null;
		URLConnection conn = null;
		try {
			Log.v("CNCOMAN", "setDrawable");
			url = new URL(iconUrl);
			conn = url.openConnection();
			Bitmap b = BitmapFactory.decodeStream(new FlushedInputStream(conn
					.getInputStream()));
			this.bitmap = b;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			url = null;
			conn = null;
		}
	}

	public IconInfo() {

	}

	public IconInfo(String iconUrl) {
		this.iconUrl = iconUrl;
		this.setDrawable();
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

}
