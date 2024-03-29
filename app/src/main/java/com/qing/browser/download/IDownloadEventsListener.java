package com.qing.browser.download;

/**
 * Interface for object listening to download events.
 */
public interface IDownloadEventsListener {
	
	/**
	 * The method run on download events.
	 * @param event The event.
	 * @param data Additional data.
	 */
	void onDownloadEvent(String event, Object data);

}
