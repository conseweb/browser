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

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;

import com.qing.browser.controllers.Controller;

/**
 * Url management utils.
 */
public class UrlUtils {

	/**
	 * Check if a string is an url. For now, just consider that if a string
	 * contains a dot, it is an url.
	 * 
	 * @param url
	 *            The url to check.
	 * @return True if the string is an url.
	 */
	public static boolean isUrl(String url) {
		return url.equals(Constants.URL_ABOUT_BLANK)
				|| url.equals(Constants.URL_ABOUT_START) || url.contains(".");
	}

	/**
	 * Get the current search url.
	 * 
	 * @param context
	 *            The current context.
	 * @param searchTerms
	 *            The terms to search for.
	 * @return The search url.
	 */
	public static String getSearchUrl(Context context, String searchTerms) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String currentSearchUrl = sp.getString(
				Constants.PREFERENCES_GENERAL_SEARCH_URL,
				Constants.URL_SEARCH_GOOGLE);
		return String.format(currentSearchUrl, searchTerms);
	}

	/**
	 * Check en url. Add http:// before if missing.
	 * 
	 * @param url
	 *            The url to check.
	 * @return The modified url if necessary.
	 */
	public static String checkUrl(String url) {
		if ((url != null) && (url.length() > 0)) {

			if ((!url.startsWith("http://")) && (!url.startsWith("https://"))
					&& (!url.startsWith("file://"))
					&& (!url.startsWith(Constants.URL_ABOUT_BLANK))
					&& (!url.startsWith(Constants.URL_ABOUT_START))) {

				url = "http://" + url;

			}
		}

		return url;
	}

	/**
	 * 检查是否是url地址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean CheckUrl(String url) {
		boolean result = false;
		//http://electronics.cnet.com/electronics/0-6342366-8-8994967-1.html
		String strPattern = "(http|ftp|https|content)\\://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
		Pattern p = Pattern.compile(strPattern);
		 
		if(p.matcher(url).matches()){
			result = true;
		}else{
			//baidu.com
			strPattern = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";
			p = Pattern.compile(strPattern);
			if(p.matcher(url).matches()){
				result = true;
			}else{
				// www.baidu.com
				strPattern = "\b(([\\w-]+://?|www[.])[^\\s()<>]+(?:\\([\\w\\d]+\\)|([^[:punct:]\\s]|/)))";
				p = Pattern.compile(strPattern);
				if(p.matcher(url).matches()){
					result = true;
				}else{
					//http://www.sysrage.net | https://64.81.85.161/site/file.php?cow=moo's |ftp://user:pass@host.com:123
					strPattern = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?" +
							"((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\." +
							"(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\." +
							"(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\." +
							"(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|" +
							"([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?" +
							"(/[^/][a-zA-Z0-9\\.\\,\\?\'\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";
					p = Pattern.compile(strPattern);
					if(p.matcher(url).matches()){
						result = true;
					}
					
				}
			}
		}
		
		return result;

	}

}
