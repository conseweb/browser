package com.qing.browser.ui.launcher;

import java.util.ArrayList;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Log;

import com.qing.browser.R;

public class ShortcutInfo extends ItemInfo {

	public String url;

	/**
	 * Indicates whether the icon comes from an application's resource (if
	 * false) or from a custom Bitmap (if true.)
	 */
	public boolean customIcon;

	/**
	 * Indicates whether we're using the default fallback icon instead of
	 * something from the app.
	 */
	public boolean usingFallbackIcon;

	/**
	 * Indicates whether the shortcut is on external storage and may go away at
	 * any time.
	 */
	boolean onExternalStorage;

	public String iconResource;

	public String iconUrl;

	/**
	 * The application icon.
	 */
	// private Bitmap mIcon;
	public Bitmap mIcon;

	public ShortcutInfo() {
		itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
	}

	public void setIcon(Bitmap b) {
		mIcon = b;
	}

	public Bitmap getIcon() {
		if (mIcon == null) {
			mIcon = Utilities.drawableToBitmap(R.drawable.hotseat_browser_bg);
		}
		return mIcon;
	}

	
	protected ShortcutIcon mFolderIcon = null;  
 
	void setShortcutIcon(ShortcutIcon icon) {
		mFolderIcon = icon;
	} 
	
	
	@Override
	void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);

		String titleStr = title != null ? title.toString() : null;
		values.put(LauncherSettings.Favorites.TITLE, titleStr);

		String urlStr = url != null ? url : null;
		values.put(LauncherSettings.Favorites.URL, urlStr);

		if (customIcon) {
			values.put(LauncherSettings.Favorites.ICON_TYPE,
					LauncherSettings.Favorites.ICON_TYPE_BITMAP);
			writeBitmap(values, mIcon);
		} else {
			if (onExternalStorage && !usingFallbackIcon) {
				writeBitmap(values, mIcon);
			}
			values.put(LauncherSettings.Favorites.ICON_TYPE,
					LauncherSettings.Favorites.ICON_TYPE_RESOURCE);
			if (iconResource != null) {
				values.put(LauncherSettings.Favorites.ICON_RESOURCE,
						iconResource);
			}
		}
	}

	@Override
	public String toString() {
		return "ShortcutInfo(title=" + title.toString() + ")";
	}

	@Override
	void unbind() {
		super.unbind();
	}

	public static void dumpShortcutInfoList(String tag, String label,
			ArrayList<ShortcutInfo> list) {
		Log.d(tag, label + " size=" + list.size());
		for (ShortcutInfo info : list) {
			Log.d(tag, "   title=\"" + info.title + " icon=" + info.mIcon
					+ " customIcon=" + info.customIcon);
		}
	} 
}
