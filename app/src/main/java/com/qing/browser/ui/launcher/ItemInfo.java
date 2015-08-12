package com.qing.browser.ui.launcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Log;

public class ItemInfo {

	static final int NO_ID = -1;

	public long id = NO_ID;

	public long shortid = NO_ID;

	public CharSequence title;

	public long container = NO_ID;

	public int screen = -1;

	public int itemIndex = -1;
	
	public int itemType;
	
	public int iconType;
	
	public int userType;

	boolean isGesture = false;

	ItemInfo() {
	}

	/**
	 * Write the fields of this item to the DB
	 * 
	 * @param values
	 */
	void onAddToDatabase(ContentValues values) {
		values.put(LauncherSettings.Favorites.ITEM_TYPE, itemType);
		if (!isGesture) {
			values.put(LauncherSettings.Favorites.CONTAINER, container);
			values.put(LauncherSettings.Favorites.SCREEN, screen);
			values.put(LauncherSettings.Favorites.ITEM_INDEX, itemIndex);
		}
	}

	static byte[] flattenBitmap(Bitmap bitmap) {
		// Try go guesstimate how much space the icon will take when serialized
		// to avoid unnecessary allocations/copies during the write.
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			Log.w("Favorite", "Could not write icon");
			return null;
		}
	}

	static void writeBitmap(ContentValues values, Bitmap bitmap) {
		if (bitmap != null) {
			byte[] data = flattenBitmap(bitmap);
			values.put(LauncherSettings.Favorites.ICON, data);
		}
	}

	void unbind() {
	}

	@Override
	public String toString() {
		return "Item(id=" + this.id + " type=" + this.itemType + ")";
	}
}
