/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qing.browser.ui.launcher;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Settings related utilities.
 */
public class LauncherSettings {
	/**
	 * Favorites.
	 */
	public static final class Favorites implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ LauncherProvider.AUTHORITY + "/"
				+ LauncherProvider.TABLE_FAVORITES + "?"
				+ LauncherProvider.PARAMETER_NOTIFY + "=true");

		/**
		 * The content:// style URL for this table. When this Uri is used, no
		 * notification is sent if the content changes.
		 */
		public static final Uri CONTENT_URI_NO_NOTIFICATION = Uri
				.parse("content://" + LauncherProvider.AUTHORITY + "/"
						+ LauncherProvider.TABLE_FAVORITES + "?"
						+ LauncherProvider.PARAMETER_NOTIFY + "=false");

		/**
		 * The content:// style URL for a given row, identified by its id.
		 * 
		 * @param id
		 *            The row id.
		 * @param notify
		 *            True to send a notification is the content changes.
		 * 
		 * @return The unique content URL for the specified row.
		 */
		static Uri getContentUri(long id, boolean notify) {
			return Uri.parse("content://" + LauncherProvider.AUTHORITY + "/"
					+ LauncherProvider.TABLE_FAVORITES + "/" + id + "?"
					+ LauncherProvider.PARAMETER_NOTIFY + "=" + notify);
		}

		public static final String SHORTID = "shortid";

		public static final String TITLE = "title";

		public static final String URL = "url";

		public static final String CONTAINER = "container";
		public static final int CONTAINER_DESKTOP = -100;
		public static final int CONTAINER_DOCKBAR = -200;

		public static final String SCREEN = "screen";

		public static final String ITEM_INDEX = "itemIndex";

		public static final String ITEM_TYPE = "itemType";
		public static final int ITEM_TYPE_APPLICATION = 0;
		public static final int ITEM_TYPE_SHORTCUT = 1;
		public static final int ITEM_TYPE_USER_FOLDER = 2;

		public static final String ICON_TYPE = "iconType";
		public static final int ICON_TYPE_RESOURCE = 0;
		public static final int ICON_TYPE_BITMAP = 1;
		public static final int ICON_TYPE_RESOURCE_UNIVERSAL = 2;
		public static final int ICON_TYPE_RESOURCE_SNAPSHOT = 3;

		public static final String ICON_RESOURCE = "iconResource";

		public static final String ICON = "icon";

		public static final String USER_TYPE = "userType";
		public static final int USER_TYPE_USER = 0;
		public static final int USER_TYPE_SERVER = 1;
		public static final int USER_TYPE_DEFAULT = 2;

		public static final String UPDATE_FLAG = "updateFlag";
		public static final int UPDATE_FLAG_INIT = 0;
		public static final int UPDATE_FLAG_UPDATE = 1;
		public static final int UPDATE_FLAG_HIDE = 2;
		
		public static final String ICON_URL = "iconUrl";

	}
}
