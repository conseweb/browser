package com.qing.browser.ui.launcher;

import java.util.ArrayList;

import android.content.ContentValues;

/**
 * Represents a folder containing shortcuts or apps.
 */
public class UserFolderInfo extends FolderInfo {
	/**
	 * The apps and shortcuts
	 */
	public ArrayList<ShortcutInfo> contents = new ArrayList<ShortcutInfo>();

	public UserFolderInfo() {
		itemType = LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER;
	}

	/**
	 * Add an app or shortcut
	 * 
	 * @param item
	 */
	public void add(ShortcutInfo item) {
		contents.add(item);
	}

	/**
	 * Remove an app or shortcut. Does not change the DB.
	 * 
	 * @param item
	 */
	public void remove(ShortcutInfo item) {
		contents.remove(item);
	}

	@Override
	void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);
		values.put(LauncherSettings.Favorites.TITLE, title.toString());
	}

	protected FolderIcon mFolderIcon = null; // add by hmg25 for Folder

	// add by hmg25 for Folder {
	void setFolderIcon(FolderIcon icon) {
		mFolderIcon = icon;
	}
	// add by hmg25 for Folder }

}
