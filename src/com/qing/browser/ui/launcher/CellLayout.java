package com.qing.browser.ui.launcher;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;

import com.qing.browser.R;
import com.qing.browser.components.CustomWebView;

public class CellLayout extends ViewGroup implements DropTarget, DragSource,
		DragScroller {

	private boolean mPortrait = true;

	protected int lastX = -1, lastY = -1;

	protected boolean enabled = true, touching = false, iscell = false;

	protected float lastDelta = 0;

	protected int scroll = 0;

	private float mLastMotionY;// 最后点击的点
	// private GestureDetector detector;
	int move = 0;// 移动距离
	int MAXMOVE = 450;// 最大允许的移动距离
	private Scroller mScroller;
	int up_excess_move = 0;// 往上多移的距离
	int down_excess_move = 0;// 往下多移的距离

	public static int animT = 150;
	protected ArrayList<Integer> newPositions = new ArrayList<Integer>();

	private int dragged = -1;

	private int lastTarget = -1;
	
	static int FolderFlag = 0;

	private int mTouchSlop;

	private int mWidth;
	private int mHeight;

	private int mCellWidth;
	private int mCellHeight;

	private int mLongAxisPadding;

	private int mShortAxisPadding;

	private int mShortAxisCells;
	private int mLongAxisCells;

	private int mWidthGap;
	private int mHeightGap;

	private final Rect mRect = new Rect();

	private Launcher mLauncher;
	private DragController mDragController;
//	public static boolean IconMove = false;
	private OnLongClickListener mLongClickListener;
	
	/*用来更新数据用  
	 * 0：则全部更新   
	 * 1：更新左屏数据
	 * 2：更新右屏幕的数据
	 * */
	public static int update_screen = 0;   
	

	// protected boolean isCreateFolder = false;
	public CellLayout(Context context) {
		this(context, null);
	}

	public CellLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CellLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CellLayout, defStyle, 0);

		mCellWidth = a.getDimensionPixelSize(R.styleable.CellLayout_cellWidth,
				10);
		mCellHeight = a.getDimensionPixelSize(
				R.styleable.CellLayout_cellHeight, 10);

		mLongAxisPadding = a.getDimensionPixelSize(
				R.styleable.CellLayout_longAxisPadding, 10);
		mShortAxisPadding = a.getDimensionPixelSize(
				R.styleable.CellLayout_shortAxisPadding, 10);

		// 自动调整 每行书签个数

		mShortAxisCells = a.getInt(R.styleable.CellLayout_shortAxisCells, 4);
		mLongAxisCells = a.getInt(R.styleable.CellLayout_longAxisCells, 5);

		a.recycle();

		setAlwaysDrawnWithCacheEnabled(false);
		mScroller = new Scroller(context);
		// 获得可以认为是滚动的距离
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledTouchSlop();
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

	@Override
	public void addView(View child) {
		super.addView(child);
		newPositions.add(-1);
	};

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
		newPositions.add(-1);
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

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		int action = ev.getAction();
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			// 获取当前点击方法错误，未处理滑动屏幕
			final Rect frame = mRect;

			final int count = getChildCount();
			View view = null;
			for (int i = count - 1; i >= 0; i--) {
				final View child = getChildAt(i);

				if ((child.getVisibility()) == VISIBLE
						|| child.getAnimation() != null) {
					child.getHitRect(frame);
					// move为上下滑动偏移量
					if (frame.contains(x, y + move)) {
						view = child;
						break;
					}
				}
			}
			setTag(view);
			mLastMotionY = y;
			iscell = false;
			break;
		case MotionEvent.ACTION_MOVE:

			final int yDiff = (int) Math.abs(y - mLastMotionY);
			boolean yMoved = yDiff > mTouchSlop;
			// 判断是否是移动
			if (!yMoved) {
				break;
			} else {
				iscell = true;
			}

			break;
		case MotionEvent.ACTION_UP:
			iscell = false;
			break;
		}

		if (mLauncher.getCurrentWorkspaceScreen() == 0) {
			return false; // ADD LS
		}
		return iscell;
	}

	@Override
	public View getTag() {
		final View view = (View) super.getTag();
		return view;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		final float y = event.getY();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:

			if (!mScroller.isFinished()) {
				mScroller.forceFinished(true);
				move = mScroller.getFinalY();
			}
			mLastMotionY = y;
			touching = true;
			break;
		case MotionEvent.ACTION_MOVE:
			int delta = lastY - (int) event.getY();

			final int yDiff = (int) Math.abs(y - mLastMotionY);
			boolean yMoved = yDiff > mTouchSlop;
			// 判断是否是移动
			if (!yMoved) {
				break;
			}

			// 随手指 拖动的代码
			int deltaY = 0;
			deltaY = (int) (mLastMotionY - y);
			mLastMotionY = y;
			if (deltaY < 0) {
				// 下移
				// 判断上移 是否滑过头
				if (up_excess_move == 0) {
					if (move >= 0) {
						int move_this = Math.max(-move, deltaY);
						move = move + move_this;
						scrollBy(0, move_this);
					} else if (move == 0) {// 如果已经是最顶端 继续往下拉
						// down_excess_move = down_excess_move - deltaY / 2;//
						// 记录下多往下拉的值
						// scrollBy(0, deltaY / 2);
					}
				} else if (up_excess_move > 0)// 之前有上移过头
				{
					if (up_excess_move >= (-deltaY)) {
						up_excess_move = up_excess_move + deltaY;
						scrollBy(0, deltaY);
					} else {
						up_excess_move = 0;
						scrollBy(0, -up_excess_move);
					}
				}
			} else if (deltaY > 0) {
				// 上移
				if (down_excess_move == 0) {
					if (MAXMOVE - move >= 0) {
						int move_this = Math.min(MAXMOVE - move, deltaY);
						move = move + move_this;
						scrollBy(0, move_this);
					} else if (MAXMOVE - move == 0) {
						// if (up_excess_move <= 100) {
						// up_excess_move = up_excess_move + deltaY / 2;
						// scrollBy(0, deltaY / 2);
						// }
					}
				} else if (down_excess_move > 0) {
					if (down_excess_move >= deltaY) {
						down_excess_move = down_excess_move - deltaY;
						scrollBy(0, deltaY);
					} else {
						down_excess_move = 0;
						scrollBy(0, down_excess_move);
					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			// 多滚是负数 记录到move里
			if (up_excess_move > 0) {
				// 多滚了 要弹回去
				scrollBy(0, -up_excess_move);
				invalidate();
				up_excess_move = 0;
			}
			if (down_excess_move > 0) {
				// 多滚了 要弹回去
				scrollBy(0, down_excess_move);
				invalidate();
				down_excess_move = 0;
			}
			touching = false;
			break;
		}
		return touching;
	}

	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			touching = true;
			break;
		case MotionEvent.ACTION_MOVE:
			int delta = lastY - (int) event.getY();

			scroll += delta;
			clampScroll();
			if (Math.abs(delta) > 2)
				enabled = false;
			onLayout(true, getLeft(), getTop(), getRight(), getBottom());

			lastX = (int) event.getX();
			lastY = (int) event.getY();
			lastDelta = delta;
			break;
		case MotionEvent.ACTION_UP:
			touching = false;
			break;
		}
		return false;
	}

	protected void clampScroll() {
		int stretch = 3, overreach = getHeight() / 2;
		int max = getMaxScroll();
		max = Math.max(max, 0);

		if (scroll < -overreach) {
			scroll = -overreach;
			lastDelta = 0;
		} else if (scroll > max + overreach) {
			scroll = max + overreach;
			lastDelta = 0;
		} else if (scroll < 0) {
			if (scroll >= -stretch)
				scroll = 0;
			else if (!touching)
				scroll -= scroll / stretch;
		} else if (scroll > max) {
			if (scroll <= max + stretch)
				scroll = max;
			else if (!touching)
				scroll += (max - scroll) / stretch;
		}
	}

	protected int getMaxScroll() {
		int rowCount = (int) Math.ceil((double) getChildCount()
				/ mShortAxisCells);
		int max = rowCount * mCellHeight + (rowCount + 1) * mLongAxisPadding
				- getHeight();
		return max;
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

		mPortrait = heightSpecSize > widthSpecSize;

		mWidth = widthSpecSize;
		mHeight = heightSpecSize;

		mLongAxisPadding=mShortAxisPadding = mWidthGap = (mWidth - mCellWidth
				* (mPortrait ? mShortAxisCells : mLongAxisCells))
				/ ((mPortrait ? mShortAxisCells : mLongAxisCells) + 1);
		mHeightGap = 5;

		setMeasuredDimension(widthSpecSize, heightSpecSize);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			CustomWebView WebView = (CustomWebView) getChildAt(i).findViewById(
					R.id.webview);
			if (WebView != null) {
				getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			}
		}

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
					if (info.itemIndex != i) {
						LauncherModel.moveItemInDatabase(mLauncher, info,
								LauncherSettings.Favorites.CONTAINER_DESKTOP,
								info.screen, i);
					}
					int left = mShortAxisPadding + p
							% (mPortrait ? mShortAxisCells : mLongAxisCells)
							* (mCellWidth + mWidthGap);
					int top = mLongAxisPadding + p
							/ (mPortrait ? mShortAxisCells : mLongAxisCells)
							* (mCellHeight + mHeightGap);
					child.layout(left, top, left + mCellWidth, top
							+ mCellHeight);
				} else if (child instanceof FolderIcon) {
					final ItemInfo info = (ItemInfo) child.getTag();
					int p = i;// info.itemIndex;
					if (info.itemIndex != i) {
						LauncherModel.moveItemInDatabase(mLauncher, info,
								LauncherSettings.Favorites.CONTAINER_DESKTOP,
								info.screen, i);
					}
					int left = mShortAxisPadding + p
							% (mPortrait ? mShortAxisCells : mLongAxisCells)
							* (mCellWidth + mWidthGap);
					int top = mLongAxisPadding + p
							/ (mPortrait ? mShortAxisCells : mLongAxisCells)
							* (mCellHeight + mHeightGap);
					child.layout(left, top, left + mCellWidth, top
							+ mCellHeight);
				} else if (child instanceof Folder) {
					child.layout(0, 0, mWidth, mHeight);
				} else {
					child.layout(0, 0, mWidth, mHeight);
				}
			}
		}

		if (count % (mPortrait ? mShortAxisCells : mLongAxisCells) > 0) {
			MAXMOVE = mLongAxisPadding + count
					/ (mPortrait ? mShortAxisCells : mLongAxisCells)
					* (mCellHeight + mHeightGap) + mCellHeight - getHeight()
					+ 30;
		} else {
			MAXMOVE = mLongAxisPadding + count
					/ (mPortrait ? mShortAxisCells : mLongAxisCells)
					* (mCellHeight + mHeightGap) - getHeight() + 30;
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

	void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	@Override
	public void setDragController(DragController dragController) {
		mDragController = dragController;
	}

	@Override
	public void scrollLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scrollRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scrollY() {
		// TODO Auto-generated method stub
		scrollBy(0, 10);
	}

	@Override
	public void onDropCompleted(View target, boolean success, Object mDragInfo) {
		// TODO Auto-generated method stub
		// removeAllViews();
		if (Homespace.isRightScreenDrag == true) {
			Homespace.isRightScreenDrag = false;
		}
		onLayout(true, getLeft(), getTop(), getRight(), getBottom());
		// requestLayout();
		showAddShortcut();
		 
		if(mDragInfo != null){
			Log.d("H", "CellLayout onDropCompleted 文件夹移动 lastTarget=" + lastTarget);
			if(lastTarget == -1){ return;}
			update();	 
		}
//		IconMove = false;

	}

	private void onDropExternal(int x, int y, Object dragInfo) {
		// Drag from somewhere else
		ItemInfo info = (ItemInfo) dragInfo;

		View view;
		switch (info.itemType) {
		case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
			view = ShortcutIcon.fromXml(R.layout.shortcut_icon, mLauncher,
					this, ((ShortcutInfo) info));
			break;
		case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
			view = FolderIcon.fromXml(R.layout.folder_icon, mLauncher, this,
					((UserFolderInfo) info));
			break;
		default:
			throw new IllegalStateException("Unknown item type: "
					+ info.itemType);
		}

		this.addView(view);
		view.setHapticFeedbackEnabled(false);
		view.setOnLongClickListener(mLongClickListener);
		if (view instanceof DropTarget) {
			mDragController.addDropTarget((DropTarget) view);
		}
		LauncherModel.addOrMoveItemInDatabase(mLauncher, info,
				LauncherSettings.Favorites.CONTAINER_DESKTOP, info.screen,
				this.getChildCount() - 1);
	}

	private void update(){
		// 排序方法有问题，需要调整
		ArrayList<View> children = new ArrayList<View>();
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).clearAnimation();
			children.add(getChildAt(i));
		}
		removeAllViews(); 
		while (dragged != lastTarget)
			if (lastTarget == children.size()) // dragged and dropped to the
												// right of the last element
			{
				children.add(children.remove(dragged));
				dragged = lastTarget;
			} else if (dragged < lastTarget) // shift to the right
			{
				Collections.swap(children, dragged, dragged + 1);
				dragged++;
			} else if (dragged > lastTarget) // shift to the left
			{
				Collections.swap(children, dragged, dragged - 1);
				dragged--;
			}
		for (int i = 0; i < children.size(); i++) {
			newPositions.set(i, -1);
			addView(children.get(i));
		}

		for (int i = 0; i < getChildCount(); i++) {
			ItemInfo child = (ItemInfo) getChildAt(i).getTag();
			if (child.itemIndex != i) {
				LauncherModel.moveItemInDatabase(mLauncher, child,
						child.container, child.screen, i);
			}
		}
	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// 判断是否来自桌面
		if (source != this) {
			onDropExternal(x, y, dragInfo);
		} else {
			if(lastTarget == -1){ return;}
			update();
		}
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		FolderFlag = 0;
		hiddenAddShortcut();
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// move为上下滑动偏移量
		ItemInfo dragItemInfo = (ItemInfo) dragInfo;
		int target = getTargetFromCoor(x, y + move);
		if(target==0||target==-1||lastTarget != target){
			FolderFlag = 0;
		}else {
			FolderFlag = target;
		}
		if (lastTarget != target) {
			if (target != -1) {
				if (target == 0) {
					int a = getIndexFromCoor(x - (mCellWidth / 4), y);
					if (dragItemInfo.itemIndex != a) {
						lastTarget = a;
					}
				} else {
					animateGap(target);
					lastTarget = target;
				}
			}
		}

		// requestLayout();
		// invalidate();
	}

	protected int getTargetFromCoor(int x, int y) {

		int leftPos = getIndexFromCoor(x - (mCellWidth / 4), y);
		int rightPos = getIndexFromCoor(x + (mCellWidth / 4), y);

		if (leftPos == -1 && rightPos == -1) // touch is in the middle of

			return -1;
		if (leftPos == rightPos) // touch is in the middle of a visual
			return 0;

		int target = -1;
		if (rightPos > -1)
			target = rightPos;
		else if (leftPos > -1)
			target = leftPos + 1;
		if (dragged < target)
			return target - 1;
		return target;
	}

	public int getIndexFromCoor(int x, int y) {
		int index = -1;
		final Rect frame = new Rect();
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View childview = getChildAt(i);
			childview.getHitRect(frame);
			if (frame.contains(x, y)) {
				index = i;
				break;
			}
		}

		return index;
	}

	protected void animateGap(int target) {
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			ItemInfo child = (ItemInfo) getChildAt(i).getTag();
			if (i == dragged)
				continue;
			int newPos = i;
			if (dragged < target && i >= dragged + 1 && i <= target)
				newPos--;
			else if (target < dragged && i >= target && i < dragged)
				newPos++;

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

			Point oldXY = getCoorFromIndex(oldPos);
			Point newXY = getCoorFromIndex(newPos);
			Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y
					- v.getTop());
			Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y
					- v.getTop());

			TranslateAnimation translate = new TranslateAnimation(
					Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
					newOffset.x, Animation.ABSOLUTE, oldOffset.y,
					Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(animT);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);

			newPositions.set(i, newPos);
			// ItemInfo iteminfo = (ItemInfo) getChildAt(oldPos).getTag();
			// LauncherModel.moveItemInDatabase(mLauncher, iteminfo,
			// LauncherSettings.Favorites.CONTAINER_DESKTOP,
			// iteminfo.screen, newPos);
		}
		for (int j = 0; j < newPositions.size(); j++) {
			if (newPositions.get(j) != -1) {
				// Log.i("animateGapRover", "newPositions j=" + j + " value="
				// + newPositions.get(j));
			}
		}

	}

	public void animateGapRover() {

		// lastTarget--;
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			ItemInfo child = (ItemInfo) getChildAt(i).getTag();
			if (i == dragged)
				continue;

			int newPos = i;
			// if (dragged < lastTarget && i >= dragged + 1 && i <= lastTarget)
			// newPos++;
			// else if (lastTarget < dragged && i >= lastTarget && i < dragged)
			// newPos--;

			// animate
			int oldPos = i;
			if (newPositions.get(i) != -1)
				oldPos = newPositions.get(i);
			if (oldPos == newPos)
				continue;

			Point oldXY = getCoorFromIndex(oldPos);
			Point newXY = getCoorFromIndex(newPos);
			Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y
					- v.getTop());
			Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y
					- v.getTop());

			TranslateAnimation translate = new TranslateAnimation(
					Animation.ABSOLUTE, oldOffset.x, Animation.ABSOLUTE,
					newOffset.x, Animation.ABSOLUTE, oldOffset.y,
					Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(animT);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);
			v.clearAnimation();
			newPositions.set(i, -1);

			// this.removeViewAt(i);
			// ItemInfo iteminfo = (ItemInfo) getChildAt(oldPos).getTag();
			// LauncherModel.moveItemInDatabase(mLauncher, iteminfo,
			// LauncherSettings.Favorites.CONTAINER_DESKTOP,
			// iteminfo.screen, newPos);
		}
	}

	protected Point getCoorFromIndex(int index) {

		int col = index % mShortAxisCells;
		int row = index / mShortAxisCells;
		return new Point(mShortAxisPadding + (mCellWidth + mWidthGap) * col,
				mLongAxisPadding + (mCellHeight + mHeightGap) * row);
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 长按后,开始拖动
	 */
	public void startDrag(View child) {

		if (!child.isInTouchMode()) {
			return;
		}

		hiddenAddShortcut();

		ItemInfo iteminfo = (ItemInfo) child.getTag();

		// 处理右屏拖动冲突
		if (iteminfo.screen == Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT) {
			Homespace.isRightScreenDrag = true;
		}

		// 设置 拖动View的索引值
		dragged = iteminfo.itemIndex;
		lastTarget = dragged;
		// mDragInfo = cellInfo;

		// this.onDragChild(child);
		mDragController.startDrag(child, this, child.getTag(),
				DragController.DRAG_ACTION_MOVE);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			// 返回当前滚动X方向的偏移
			scrollTo(0, mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mLongClickListener = l;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			this.setOnLongClickListener(l);
		}
	}

	private void hiddenAddShortcut() {
		for (int i = getChildCount() - 1; i > 0; i--) {
			View addView = getChildAt(i);
			ItemInfo iteminfo = (ItemInfo) addView.getTag();
			if (iteminfo.screen != Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE) {
				break;
			}
			if ("添加".equals(iteminfo.title)) {
				addView.setVisibility(View.GONE);
				break;
			}
		}
	}

	private void showAddShortcut() {
		for (int i = getChildCount() - 1; i > 0; i--) {
			View addView = getChildAt(i);
			ItemInfo iteminfo = (ItemInfo) addView.getTag();
			if (iteminfo.screen != Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE) {
				break;
			}
			if ("添加".equals(iteminfo.title)) {
				addView.setVisibility(View.VISIBLE);
				break;
			}
		}
	}

}
