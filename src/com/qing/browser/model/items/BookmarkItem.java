package com.qing.browser.model.items;

/**
 * Represent a bookmark.
 */
public class BookmarkItem {
	
	private String mTitle;
	private String mUrl;
	private String mUUID;
	
	/**
	 * Constructor.
	 * @param title The bookmark title.
	 * @param url The bookmark url.
	 */
	public BookmarkItem(String title, String url, String uuid) {
		mTitle = title;
		mUrl = url;
		setmUUID(uuid);
	}
	
	/**
	 * Get the bookmark title.
	 * @return The bookmark title.
	 */
	public String getTitle() {
		return mTitle;
	}
	
	/**
	 * Get the bookmark url.
	 * @return The bookmark url.
	 */
	public String getUrl() {
		return mUrl;
	}

	public String getmUUID() {
		return mUUID;
	}

	public void setmUUID(String mUUID) {
		this.mUUID = mUUID;
	}

}
