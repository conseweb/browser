/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.qing.browser.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.model.items.BookmarkItem;
import com.qing.browser.model.items.HistoryItem;
import com.qing.browser.providers.BookmarksProviderWrapper;

/**
 * Application utilities.
 */
public class ApplicationUtils {
	private static String mAdSweepString = null;

	private static int mFaviconSize = -1;
	private static int mImageButtonSize = -1;
	private static int mFaviconSizeForBookmarks = -1;

	public static int getImageButtonSize(Activity activity) {
		if (mImageButtonSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				mImageButtonSize = 16;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				mImageButtonSize = 32;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				mImageButtonSize = 48;
				break;
			default:
				mImageButtonSize = 32;
			}
		}

		return mImageButtonSize;
	}

	/**
	 * Get the required size of the favicon, depending on current screen
	 * density.
	 * 
	 * @param activity
	 *            The current activity.
	 * @return The size of the favicon, in pixels.
	 */
	public static int getFaviconSize(Activity activity) {
		if (mFaviconSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				mFaviconSize = 12;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				mFaviconSize = 24;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				mFaviconSize = 32;
				break;
			default:
				mFaviconSize = 24;
			}
		}

		return mFaviconSize;
	}

	/**
	 * Get the required size of the favicon, depending on current screen
	 * density.
	 * 
	 * @param activity
	 *            The current activity.
	 * @return The size of the favicon, in pixels.
	 */
	public static int getFaviconSizeForBookmarks(Activity activity) {
		if (mFaviconSizeForBookmarks == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				mFaviconSizeForBookmarks = 12;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				mFaviconSizeForBookmarks = 16;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				mFaviconSizeForBookmarks = 24;
				break;
			default:
				mFaviconSizeForBookmarks = 16;
			}
		}

		return mFaviconSizeForBookmarks;
	}

	/**
	 * Check if the SD card is available. Display an alert if not.
	 * 
	 * @param context
	 *            The current context.
	 * @param showMessage
	 *            If true, will display a message for the user.
	 * @return True if the SD card is available, false otherwise.
	 */
	public static boolean checkCardState(Context context, boolean showMessage) {
		// Check to see if we have an SDCard
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {

			int messageId;

			// Check to see if the SDCard is busy, same as the music app
			if (status.equals(Environment.MEDIA_SHARED)) {
				messageId = R.string.Commons_SDCardErrorSDUnavailable;
			} else {
				messageId = R.string.Commons_SDCardErrorNoSDMsg;
			}

			if (showMessage) {
				ApplicationUtils.showErrorDialog(context,
						R.string.Commons_SDCardErrorTitle, messageId);
			}

			return false;
		}

		return true;
	}

	/**
	 * Show an error dialog.
	 * 
	 * @param context
	 *            The current context.
	 * @param title
	 *            The title string id.
	 * @param message
	 *            The message string id.
	 */
	public static void showErrorDialog(Context context, int title, int message) {
		new AlertDialog.Builder(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setPositiveButton(R.string.Commons_Ok, null).show();
	}

	public static void showErrorDialog(Context context, int title,
			String message) {
		new AlertDialog.Builder(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(message)
				.setPositiveButton(R.string.Commons_Ok, null).show();
	}
	/**
	 * Load the AdSweep script if necessary.
	 * 
	 * @param context
	 *            The current context.
	 * @return The AdSweep script.
	 */
	public static String getAdSweepString(Context context) {

		if (mAdSweepString == null) {
			InputStream is = context.getResources().openRawResource(R.raw.adsweep);
			if (is != null) {
				StringBuilder sb = new StringBuilder();
				String line;

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						if ((line.length() > 0) &&
								(!line.startsWith("//"))) {
							sb.append(line).append("\n");
						}
					}
				} catch (IOException e) {

				} finally {
					try {
						is.close();
					} catch (IOException e) {
	
					}
				}
				mAdSweepString = sb.toString();
			} else {        
				mAdSweepString = "";
			}
		}
		return mAdSweepString;
	}

	/**
	 * Copy a text to the clipboard.
	 * 
	 * @param context
	 *            The current context.
	 * @param text
	 *            The text to copy.
	 * @param toastMessage
	 *            The message to show in a Toast notification. If empty or null,
	 *            does not display notification.
	 */
	public static void copyTextToClipboard(Context context, String text,
			String toastMessage) {
		ClipboardManager clipboard = (ClipboardManager) context
				.getSystemService(Activity.CLIPBOARD_SERVICE);
		clipboard.setText(text);

		if ((toastMessage != null) && (toastMessage.length() > 0)) {
			Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
		}
	}

}
