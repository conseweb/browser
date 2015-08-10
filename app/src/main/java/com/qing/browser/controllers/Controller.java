package com.qing.browser.controllers;

import java.util.List;

import android.content.SharedPreferences;


/**
 * Controller implementation.
 */
public final class Controller {
	
	private SharedPreferences mPreferences;

	private List<Object> mWebViewList;
	
	/**
	 * Holder for singleton implementation.
	 */
	private static final class ControllerHolder {
		private static final Controller INSTANCE = new Controller();
		/**
		 * Private Constructor.
		 */
		private ControllerHolder() { }
	}
	
	/**
	 * Get the unique instance of the Controller.
	 * @return The instance of the Controller
	 */
	public static Controller getInstance() {
		return ControllerHolder.INSTANCE;
	}
	
	/**
	 * Private Constructor.
	 */
	private Controller() {

	}
		
	/**
	 * Get the list of current WebViews.
	 * @return The list of current WebViews.
	 */
	public List<Object> getWebViewList() {
		return mWebViewList;
	}
	
	/**
	 * Set the list of current WebViews.
	 * @param list The list of current WebViews.
	 */
	public void setWebViewList(List<Object> list) {
		mWebViewList = list;
	}
	
	/**
	 * Get a SharedPreferences instance.
	 * @return The SharedPreferences instance.
	 */
	public SharedPreferences getPreferences() {
		return mPreferences;
	}

	/**
	 * Set the SharedPreferences instance.
	 * @param preferences The SharedPreferences instance.
	 */
	public void setPreferences(SharedPreferences preferences) {
		this.mPreferences = preferences;
	}
	
}
