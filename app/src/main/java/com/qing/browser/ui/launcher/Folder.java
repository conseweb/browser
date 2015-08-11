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

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.qing.browser.R;
import com.qing.browser.activities.SettingActivity;
import com.qing.browser.utils.Constants;
/**
 * Represents a set of icons chosen by the user or generated by the system.
 */
public class Folder extends LinearLayout implements DragSource, DropTarget,
		OnItemLongClickListener, OnItemClickListener, OnClickListener,
		View.OnLongClickListener {

	protected GridView mContent;
	protected DragController mDragController;

	protected Launcher mLauncher;

	protected Button mCloseButton;

	protected TextView mTitleTextView;

	protected FolderInfo mInfo;

	/**
	 * Which item is being dragged
	 */
	protected ShortcutInfo mDragItem;

	/**
	 * Used to inflate the Workspace from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization
	 *            values.
	 */
	public Folder(Context context, AttributeSet attrs) {
		super(context, attrs);
		// LayoutInflater.from(context).inflate(R.layout.userfolder, this,
		// true);
		setAlwaysDrawnWithCacheEnabled(false);
	}

	@Override
	protected void onFinishInflate() {
		// super.onFinishInflate();

		mContent = (GridView) findViewById(R.id.folder_content);
		mContent.setOnItemClickListener(this);
		mContent.setOnItemLongClickListener(this);

		mCloseButton = (Button) findViewById(R.id.folder_close);
		mCloseButton.setOnClickListener(this);

		mTitleTextView = (TextView) findViewById(R.id.folder_title);
		mTitleTextView.setOnLongClickListener(this);
	}

	public void onItemClick(AdapterView parent, View v, int position, long id) {
		ShortcutInfo shortcutInfo = (ShortcutInfo) parent
				.getItemAtPosition(position);

		if (Constants.SHORTCUT_TIANQI.equals(shortcutInfo.url)) {
			mLauncher
					.startActivity(new Intent(mLauncher, CaptureActivity.class));
		} else if (Constants.SHORTCUT_ZHUANKE.equals(shortcutInfo.url)) {
			mLauncher
					.startActivity(new Intent(mLauncher, SettingActivity.class));
		} else if ("扫一扫".equals(shortcutInfo.title)) {
			mLauncher
					.startActivity(new Intent(mLauncher, CaptureActivity.class));
		} else {
			mLauncher.addTab(shortcutInfo.url);
		}

		mLauncher.closeFolder(this);
	}

	public void onClick(View v) {
		mLauncher.closeFolder(this);
	}

	public boolean onLongClick(View v) {
		// 处理最常访问 文件夹重命名问题
		if (mInfo.container != -200) {
			mLauncher.closeFolder(this);
			mLauncher.showRenameDialog(mInfo);
		}
		return true;
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (!view.isInTouchMode()) {
			return false;
		}

		ShortcutInfo app = (ShortcutInfo) parent.getItemAtPosition(position);

		// DRAG_ACTION_COPY
		mDragController.startDrag(view, this, app,
				DragController.DRAG_ACTION_MOVE);
		mLauncher.closeFolder(this);
		mDragItem = app;

		return true;
	}

	public void setDragController(DragController dragController) {
		mDragController = dragController;
	}

	public void onDropCompleted(View target, boolean success, Object mSource) {
		if (success) {
			ShortcutsAdapter adapter = (ShortcutsAdapter) mContent.getAdapter();
			adapter.remove(mDragItem);
			((UserFolderInfo) mInfo).mFolderIcon.updateFolderIcon();
		}
	}

	/**
	 * Sets the adapter used to populate the content area. The adapter must only
	 * contains ShortcutInfo items.
	 * 
	 * @param adapter
	 *            The list of applications to display in the folder.
	 */
	void setContentAdapter(BaseAdapter adapter) {
		mContent.setAdapter(adapter);
	}

	void notifyDataSetChanged() {
		((BaseAdapter) mContent.getAdapter()).notifyDataSetChanged();
	}

	void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	/**
	 * @return the FolderInfo object associated with this folder
	 */
	FolderInfo getInfo() {
		return mInfo;
	}

	// When the folder opens, we need to refresh the GridView's selection by
	// forcing a layout
	void onOpen() {
		mContent.requestLayout();
		requestFocus();
	}

	void onClose() {
		// final Workspace workspace = mLauncher.getWorkspace();
		// workspace.getChildAt(workspace.getCurrentScreen()).requestFocus();
	}

	void bind(FolderInfo info) {
		mInfo = info;
		mTitleTextView.setText(info.title);
		setContentAdapter(new ShortcutsAdapter(getContext(),
				((UserFolderInfo) info).contents));
	}

	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		final ItemInfo item = (ItemInfo) dragInfo;
		final int itemType = item.itemType;
		return (itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT)
				&& item.container != mInfo.id;
	}

	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}

	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		ShortcutInfo item;

		item = (ShortcutInfo) dragInfo;

		((ShortcutsAdapter) mContent.getAdapter()).add(item);
		LauncherModel.addOrMoveItemInDatabase(mLauncher, item, mInfo.id,
				item.screen, item.itemIndex);
		((UserFolderInfo) mInfo).mFolderIcon.updateFolderIcon();
	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

}
