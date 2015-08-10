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
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;

public class DockBarLayout extends ViewGroup implements DropTarget, DragSource,
		DragController.DragListener, View.OnLongClickListener {

	private Launcher mLauncher;

	private int[] mTargetCell = null;

	private DragController mDragController;

	private View mSelectedView;

	private boolean mPortrait;

	private int mCellWidth;
	private int mCellHeight;

	private int mLongAxisStartPadding;
	private int mLongAxisEndPadding;

	private int mShortAxisStartPadding;
	private int mShortAxisEndPadding;

	private int mShortAxisCells;
	private int mLongAxisCells;

	private int mWidthGap;
	private int mHeightGap;

	private final Rect mRect = new Rect();

	private RectF mDragRect = new RectF();

	private boolean mDirtyTag;
	private boolean mLastDownOnOccupiedCell = false;

	public DockBarLayout(Context context) {
		this(context, null);
	}

	public DockBarLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DockBarLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPortrait = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;// 新增代码
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CellLayout, defStyle, 0);

		mCellWidth = a.getDimensionPixelSize(R.styleable.CellLayout_cellWidth,
				10);
		mCellHeight = a.getDimensionPixelSize(
				R.styleable.CellLayout_cellHeight, 10);

		mLongAxisStartPadding = a.getDimensionPixelSize(
				R.styleable.CellLayout_longAxisPadding, 10);
		mLongAxisEndPadding = a.getDimensionPixelSize(
				R.styleable.CellLayout_longAxisPadding, 10);
		mShortAxisStartPadding = a.getDimensionPixelSize(
				R.styleable.CellLayout_shortAxisPadding, 10);
		mShortAxisEndPadding = a.getDimensionPixelSize(
				R.styleable.CellLayout_shortAxisPadding, 10);

		mShortAxisCells = a.getInt(R.styleable.CellLayout_shortAxisCells, 4);
		mLongAxisCells = a.getInt(R.styleable.CellLayout_longAxisCells, 4);

		a.recycle();

		setAlwaysDrawnWithCacheEnabled(false);
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	@Override
	public void cancelLongPress() {
		super.cancelLongPress();

		// Cancel long press for all children
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			child.cancelLongPress();
		}
	}

	int getCountX() {
		return mPortrait ? mShortAxisCells : mLongAxisCells;
	}

	int getCountY() {
		return mPortrait ? mLongAxisCells : mShortAxisCells;
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {

		super.addView(child, index, params);
	}

	@Override
	public void addView(View child) {

		super.addView(child);
	}

	@Override
	public void requestChildFocus(View child, View focused) {
		super.requestChildFocus(child, focused);
		if (child != null) {
			Rect r = new Rect();
			child.getDrawingRect(r);
			requestRectangleOnScreen(r);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	int getCellWidth() {
		return mCellWidth;
	}

	int getCellHeight() {
		return mCellHeight;
	}

	int getLeftPadding() {
		return mPortrait ? mShortAxisStartPadding : mLongAxisStartPadding;
	}

	int getTopPadding() {
		return mPortrait ? mLongAxisStartPadding : mShortAxisStartPadding;
	}

	int getRightPadding() {
		return mPortrait ? mShortAxisEndPadding : mLongAxisEndPadding;
	}

	int getBottomPadding() {
		return mPortrait ? mLongAxisEndPadding : mShortAxisEndPadding;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO: currently ignoring padding

		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode == MeasureSpec.UNSPECIFIED
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
			throw new RuntimeException(
					"CellLayout cannot have UNSPECIFIED dimensions");
		}

		final int shortAxisCells = mShortAxisCells;
		final int longAxisCells = mLongAxisCells;
		final int longAxisStartPadding = mLongAxisStartPadding;
		final int longAxisEndPadding = mLongAxisEndPadding;
		final int shortAxisStartPadding = mShortAxisStartPadding;
		final int shortAxisEndPadding = mShortAxisEndPadding;
		final int cellWidth = mCellWidth;
		final int cellHeight = mCellHeight;

		mPortrait = heightSpecSize > widthSpecSize;

		int numShortGaps = shortAxisCells - 1;
		int numLongGaps = longAxisCells - 1;

		if (mPortrait) {
			int vSpaceLeft = heightSpecSize - longAxisStartPadding
					- longAxisEndPadding - (cellHeight * longAxisCells);
			mHeightGap = vSpaceLeft / numLongGaps;

			int hSpaceLeft = widthSpecSize - shortAxisStartPadding
					- shortAxisEndPadding - (cellWidth * shortAxisCells);
			if (numShortGaps > 0) {
				mWidthGap = hSpaceLeft / numShortGaps;
			} else {
				mWidthGap = 0;
			}
		} else {
			int hSpaceLeft = widthSpecSize - longAxisStartPadding
					- longAxisEndPadding - (cellWidth * longAxisCells);
			mWidthGap = hSpaceLeft / numLongGaps;

			int vSpaceLeft = heightSpecSize - shortAxisStartPadding
					- shortAxisEndPadding - (cellHeight * shortAxisCells);
			if (numShortGaps > 0) {
				mHeightGap = vSpaceLeft / numShortGaps;
			} else {
				mHeightGap = 0;
			}
		}

		setMeasuredDimension(widthSpecSize, heightSpecSize);
	}

	@Override
	public View getTag() {
		final View view = (View) super.getTag();
		return view;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				// 判断child是Shortcut or UserFolder
				if (child instanceof ShortcutIcon) {
					final ItemInfo info = (ItemInfo) child.getTag();
					int p = i;// info.itemIndex;
					if (p > getChildCount()) {
						p = getChildCount();
						// LauncherModel.addOrMoveItemInDatabase(mLauncher,
						// info,
						// LauncherSettings.Favorites.CONTAINER_DOCKBAR,
						// 0, p);
					}
					int left = mLongAxisStartPadding + p / mShortAxisCells
							* (mCellWidth + mWidthGap);
					int top = 0;
					child.layout(left, top, left + mCellWidth, top
							+ mCellHeight);
				} else if (child instanceof FolderIcon) {
					final ItemInfo info = (ItemInfo) child.getTag();
					int p = i;
					if (p > getChildCount()) {
						p = getChildCount();
						// LauncherModel.addOrMoveItemInDatabase(mLauncher,
						// info,
						// LauncherSettings.Favorites.CONTAINER_DOCKBAR,
						// 0, p);
					}
					int left = mLongAxisStartPadding + p / mShortAxisCells
							* (mCellWidth + mWidthGap);
					int top = 0;
					child.layout(left, top, left + mCellWidth, top
							+ mCellHeight);
				}
			}
		}
	}

	@Override
	protected void setChildrenDrawingCacheEnabled(boolean enabled) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View view = getChildAt(i);
			view.setDrawingCacheEnabled(enabled);
			// Update the drawing caches
			view.buildDrawingCache(true);
		}
	}

	@Override
	protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
		super.setChildrenDrawnWithCacheEnabled(enabled);
	}

	public boolean lastDownOnOccupiedCell() {
		return mLastDownOnOccupiedCell;
	}

	void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	@Override
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDragController(DragController dragger) {
		mDragController = dragger;
	}

	@Override
	public void onDropCompleted(View target, boolean success, Object mSource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		// 需要处理拖动位置和自动生成文件夹效果
		// 主要是一些特殊情况的判断
		onDropExternal(x - xOffset, y - yOffset, dragInfo);
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		if (this.getChildCount() < 4) {
//			setBackgroundResource(R.drawable.dock_bg_press);
		}
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(R.drawable.dock_bg);
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// 接收什么类型的图标
		// 判断接收数量
		if (this.getChildCount() >= 4) {
			// Context context = LauncherApplication.getInstance();
			// Toast.makeText(context, "底部太挤了，在上面呼吸新鲜空气吧", 1000).show();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO 处理多种情况
		// ItemInfo itemInfo = (ItemInfo) v.getTag();
		// if (itemInfo.itemIndex < 3) {
		//
		// return false;
		// } else {
		// mSelectedView = v;
		// mDragController.startDrag(v, this, v.getTag(),
		// DragController.DRAG_ACTION_MOVE);
		// removeSelectedItem();
		// return true;
		// }
		return false;
	}

	private void removeSelectedItem() {
		if (mSelectedView == null)
			return;
		this.removeView(mSelectedView);
	}

	private void onDropExternal(int x, int y, Object dragInfo) {
		ItemInfo info = (ItemInfo) dragInfo;

		View view;

		switch (info.itemType) {
		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
			if (info.container == NO_ID) {

			}
			 view = ShortcutIcon.fromXml(R.layout.shortcut_icon, mLauncher, this,
						((ShortcutInfo) info)); 
			break;
		case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
			view = FolderIcon.fromXml(R.layout.folder_icon, mLauncher, this,
					((UserFolderInfo) info));
			break;
		default:
			throw new IllegalStateException("Unknown item type: "
					+ info.itemType);
		}
		int itemIndex = getLocation(x);
		this.addView(view);
		view.setHapticFeedbackEnabled(false);
		view.setOnLongClickListener(this);
		if (view instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) view);
		}

		LauncherModel.addOrMoveItemInDatabase(mLauncher, info,
				LauncherSettings.Favorites.CONTAINER_DOCKBAR, 1, 3/*
																 * info.
																 * itemIndex
																 */);
	}

	void addInDockBar(View child, int itemIndex) {

		this.addView(child);
		if (!(child instanceof Folder)) {
			child.setHapticFeedbackEnabled(false);
			child.setOnLongClickListener(this);
		}
		if (child instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) child);
		}
	}

	/*
	 * 传入x坐标，判断新图标的位置，此处仅判断竖屏 判断重复情况，做生成文件夹效果 位置计算精度一般，如要求高，再调整
	 */
	public int getLocation(int x) {

		if (x > 0
				&& x + mCellWidth / 2 < mLongAxisStartPadding + mCellWidth
						+ mWidthGap / 2) {
			return 0;
		} else if (x + mCellWidth / 2 > mLongAxisStartPadding + mCellWidth
				+ mWidthGap / 2
				&& x + mCellWidth / 2 < mLongAxisStartPadding
						+ (mCellWidth + mWidthGap) * 2 - mWidthGap / 2) {
			return 1;
		} else if (x + mCellWidth / 2 > mLongAxisStartPadding
				+ (mCellWidth + mWidthGap) * 2 - mWidthGap / 2
				&& x + mCellWidth / 2 < mLongAxisStartPadding
						+ (mCellWidth + mWidthGap) * 3 - mWidthGap / 2) {
			return 2;
		} else if (x + mCellWidth / 2 > mLongAxisStartPadding
				+ (mCellWidth + mWidthGap) * 3 - mWidthGap / 2
				&& x + mCellWidth / 2 < mLongAxisStartPadding
						+ (mCellWidth + mWidthGap) * 4 - mWidthGap / 2) {
			return 3;
		} else {
			return 0;
		}
	}
}
