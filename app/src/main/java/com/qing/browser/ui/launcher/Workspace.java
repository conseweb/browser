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

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Scroller;

import com.qing.browser.R;
import com.qing.browser.components.CustomWebView;
import com.qing.browser.ui.menu.OnViewChangeListener;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.ConstantsUrl;
import com.qing.browser.utils.Tools;

/**
 * The workspace is a wide area with a wallpaper and a finite number of screens.
 * Each screen contains a number of icons, folders or widgets the user can
 * interact with. A workspace is meant to be used with a fixed width only.
 */
public class Workspace extends ViewGroup {

	private static final int INVALID_SCREEN = -1;

	/**
	 * The velocity at which a fling gesture will cause us to snap to the next
	 * screen
	 */
	private static final int SNAP_VELOCITY = 600;

	private int mDefaultScreen;

	private boolean mFirstLayout = true;

	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private final static int TOUCH_STATE_SCROLLY = 2;

	private int mTouchState = TOUCH_STATE_REST;

	private OnLongClickListener mLongClickListener;

	private Launcher mLauncher;
	private DragController mDragController;

	private boolean mAllowLongPress = true;

	private double mTouchSlop;
	private long downTime;
	private int mMaximumVelocity;

	private static final int INVALID_POINTER = -1;

	private int mActivePointerId = INVALID_POINTER;

	private static final float NANOTIME_DIV = 1000000000.0f;
	private static final float SMOOTHING_SPEED = 0.75f;
	private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math
			.log(SMOOTHING_SPEED));
	private float mSmoothingTime;
	private float mTouchX;

	private static final float BASELINE_FLING_VELOCITY = 2500.f;
	private static final float FLING_VELOCITY_INFLUENCE = 0.4f;

	private OnViewChangeListener mOnViewChangeListener;

	private Context context;

	private int width;
	private int height;

	private int screenWidth;

	/**
	 * 0左 1右 -1出错
	 */
	private int screen_left_or_right = -1;
	private int left_or_right_getScrollX = 0;

	public Workspace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}

	public Workspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workspace, defStyle, 0);
		mDefaultScreen = a.getInt(R.styleable.Workspace_defaultScreen, 1);
		a.recycle();

		setHapticFeedbackEnabled(false);
		initWorkspace();
	}

	/**
	 * Initializes various states for this workspace.
	 */
	private void initWorkspace() {
		Context context = getContext();
		mScroller = new Scroller(context);
		mCurrentScreen = mDefaultScreen;
		Launcher.setScreen(mCurrentScreen);

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		// 增大左右滑动判断距离
		// mTouchSlop = configuration.getScaledTouchSlop() * 10;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		mTouchSlop = display.getWidth() * 0.15;

		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	@Override
	public void addView(View child, int index, LayoutParams params) {
		super.addView(child, index, params);
	}

	@Override
	public void addView(View child) {
		super.addView(child);
	}

	@Override
	public void addView(View child, int index) {
		super.addView(child, index);
	}

	@Override
	public void addView(View child, int width, int height) {
		super.addView(child, width, height);
	}

	@Override
	public void addView(View child, LayoutParams params) {
		super.addView(child, params);
	}

	/**
	 * @return The open folder on the current screen, or null if there is none
	 */
	Folder getOpenFolder() {
		CellLayout currentScreen = (CellLayout) getChildAt(mCurrentScreen);
		int count = currentScreen.getChildCount();
		for (int i = 0; i < count; i++) {
			View child = currentScreen.getChildAt(i);
			if (child instanceof Folder) {
				return (Folder) child;
			}
		}
		return null;
	}

	ArrayList<Folder> getOpenFolders() {
		final int screens = getChildCount();
		ArrayList<Folder> folders = new ArrayList<Folder>(screens);
		for (int screen = 0; screen < screens; screen++) {
			CellLayout currentScreen = (CellLayout) getChildAt(screen);
			int count = currentScreen.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = currentScreen.getChildAt(i);
				if (child instanceof Folder) {
					folders.add((Folder) child);
					break;
				}
			}
		}

		return folders;
	}

	boolean isDefaultScreenShowing() {
		return mCurrentScreen == mDefaultScreen;
	}

	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	void setCurrentScreen(int currentScreen) {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		mCurrentScreen = Math.max(0,
				Math.min(currentScreen, getChildCount() - 1));

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			scrollTo(mCurrentScreen * height, 0);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			scrollTo(mCurrentScreen * width, 0);
		}

		invalidate();
	}

	public void addInScreen(View child, int screen, int index) {
		if (true) {
			if (screen < 0 || screen >= getChildCount()) {
				return;
			}

			final CellLayout group = (CellLayout) getChildAt(screen);
			group.addView(child);
			if (!(child instanceof Folder)) {
				child.setHapticFeedbackEnabled(false);
				child.setOnLongClickListener(mLongClickListener);
			}
			if (child instanceof DropTarget) {
				mDragController.addDropTarget((DropTarget) child);
			}
		}
	}

	public void addInLeftScreen(View child, int index, String loadurl) {

		int screen = 0;
		final CellLayout group = (CellLayout) getChildAt(screen);
		group.removeAllViews();
		CustomWebView mCustomWebView = ((CustomWebView) child
				.findViewById(R.id.webview));

		mCustomWebView.setVerticalScrollBarEnabled(false);
		mCustomWebView.setHorizontalScrollBarEnabled(false);
		
		String filePath = Constants.ONE_WEBVIEW_MOBILEPATH + "Qing/"+ "one_webview" + ".html";
		if (!isFileExists(filePath)) {
			((CustomWebView) child.findViewById(R.id.webview))
					.loadUrl(ConstantsUrl.ONE_WEBVIEW_URL);
			Tools.startSavePage(ConstantsUrl.ONE_WEBVIEW_URL,
					"one_webview", context);
		} else {
			String htmlPath = "content://com.qing.browser.html"
					+ Constants.ONE_WEBVIEW_MOBILEPATH + "Qing/"
					+ "one_webview" + ".html";
			((CustomWebView) child.findViewById(R.id.webview))
					.loadUrl(htmlPath);
		}

		mCustomWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});

		group.addView((child));

	}

	private static boolean isFileExists(String filePath) {
		File save = new File(filePath);
		if (save.exists()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Registers the specified listener on each screen contained in this
	 * workspace.
	 * 
	 * @param l
	 *            The listener used to respond to long clicks.
	 */
	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mLongClickListener = l;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).setOnLongClickListener(l);
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		mTouchX = x;
		mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {// 返回true，表示动画仍在进行，还没有停止
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());// 滚动到目标坐标
			postInvalidate(); // 使view重画
		}
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		computeScroll();
		mDragController.setWindowToken(getWindowToken());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		/*
		 * final int heightMode = MeasureSpec.getMode(heightMeasureSpec); if
		 * (heightMode != MeasureSpec.EXACTLY) { throw new
		 * IllegalStateException(
		 * "Workspace can only be used in EXACTLY mode."); }
		 */

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			setHorizontalScrollBarEnabled(false);
			scrollTo(mCurrentScreen * width, 0);
			setHorizontalScrollBarEnabled(true);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle,
			boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentScreen || !mScroller.isFinished()) {
			if (!mLauncher.isWorkspaceLocked()) {
				snapToScreen(screen);
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction,
			Rect previouslyFocusedRect) {
		if (!mLauncher.isAllAppsVisible()) {
			final Folder openFolder = getOpenFolder();
			if (openFolder != null) {
				return openFolder
						.requestFocus(direction, previouslyFocusedRect);
			} else {
				int focusableScreen;
				if (mNextScreen != INVALID_SCREEN) {
					focusableScreen = mNextScreen;
				} else {
					focusableScreen = mCurrentScreen;
				}
				getChildAt(focusableScreen).requestFocus(direction,
						previouslyFocusedRect);
			}
		}
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentScreen() > 0) {
				snapToScreen(getCurrentScreen() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentScreen() < getChildCount() - 1) {
				snapToScreen(getCurrentScreen() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction,
			int focusableMode) {
		if (!mLauncher.isAllAppsVisible()) {
			final Folder openFolder = getOpenFolder();
			if (openFolder == null) {
				getChildAt(mCurrentScreen).addFocusables(views, direction);
				if (direction == View.FOCUS_LEFT) {
					if (mCurrentScreen > 0) {
						getChildAt(mCurrentScreen - 1).addFocusables(views,
								direction);
					}
				} else if (direction == View.FOCUS_RIGHT) {
					if (mCurrentScreen < getChildCount() - 1) {
						getChildAt(mCurrentScreen + 1).addFocusables(views,
								direction);
					}
				}
			} else {
				openFolder.addFocusables(views, direction);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (mLauncher.isWorkspaceLocked() || mLauncher.isAllAppsVisible()) {
				return false;
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		/*
		 * if ((action == MotionEvent.ACTION_MOVE) && (mTouchState !=
		 * TOUCH_STATE_REST)) { return true; }
		 */

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);

			long currentTime = ev.getEventTime();

			if (getCurrentScreen() == 0) {
				if (xDiff > mTouchSlop ) {
					if ((Math.abs(mLastMotionY - y)
							/ Math.abs(mLastMotionX - x) < 1)) {
						return true;
					}
					mTouchState = TOUCH_STATE_SCROLLING;
				}
			} else {
				if (xDiff > mTouchSlop / 2) {
					if ((Math.abs(mLastMotionY - y)
							/ Math.abs(mLastMotionX - x) < 1)) {
						return true;
					}
					mTouchState = TOUCH_STATE_SCROLLING;
				}
			}

			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			left_or_right_getScrollX = getScrollX();
			downTime = ev.getEventTime();
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		if (mCurrentScreen == 0) {
			return false;
		}// ADD LS
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;

			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			int deltaY = (int) (mLastMotionY - y);

			if (IsCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}
				if (Math.abs(deltaX) > Math.abs(deltaY)) {
					// 正向或者负向移动，屏幕跟随手指移动
					mLastMotionX = x;
					scrollBy(deltaX, 0);
				}
			}

			break;

		case MotionEvent.ACTION_UP:

			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				// 得到X轴方向手指移动速度
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			// velocityX为正值说明手指向右滑动，为负值说明手指向左滑动
			if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
				// Fling enough to move left
				snapToScreen(mCurrentScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurrentScreen < getChildCount() - 1) {
				// Fling enough to move right
				snapToScreen(mCurrentScreen + 1);
			} else {
				//snapToDestination();
				long currentTime = event.getEventTime();

				if (currentTime - downTime < 500) {
					if (left_or_right_getScrollX - getScrollX() > 0) {
						screen_left_or_right = 1;
					} else if (left_or_right_getScrollX - getScrollX() < 0) {
						screen_left_or_right = 0;
					} else {
						screen_left_or_right = -1;
					}

					if (screen_left_or_right == 0) {
						if (getCurrentScreen() != 2) {
							snapToScreen(getCurrentScreen() + 1);
						} else {
							snapToDestination();
						}
					} else if (screen_left_or_right == 1) {
						if (getCurrentScreen() != 0) {
							snapToScreen(getCurrentScreen() - 1);
						} else {
							snapToDestination();
						}
					} else {
						snapToDestination();
					}
				} else {
					snapToDestination();
				}
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			break;
		}
		return true;
	}

	void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0, false);
	}

	/**
	 * 根据当前布局的位置，滚动到目的页面
	 */
	public void snapToDestination() {
		/** 获取view的宽度 */
		final int screenWidth = getScreenWidth();
		/**
		 * getScrollX():获得滚动后view的横坐标
		 */
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	private void snapToScreen(int whichScreen, int velocity, boolean settle) {
		// 获取有效页面
		final int screenWidth = getScreenWidth();
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * screenWidth)) {

			final int delta = whichScreen * screenWidth - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			if (mCurrentScreen == 0 && whichScreen == 0) {
				invalidate(); // 使view重画
				return;
			}
			mCurrentScreen = whichScreen;
			invalidate(); // 使view重画
			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurrentScreen);
			}
		}
	}

	/**
	 * If one of our descendant views decides that it could be focused now, only
	 * pass that along if it's on the current screen.
	 * 
	 * This happens when live folders requery, and if they're off screen, they
	 * end up calling requestFocus, which pulls it on screen.
	 */
	@Override
	public void focusableViewAvailable(View focused) {
		View current = getChildAt(mCurrentScreen);
		View v = focused;
		while (true) {
			if (v == current) {
				super.focusableViewAvailable(focused);
				return;
			}
			if (v == this) {
				return;
			}
			ViewParent parent = v.getParent();
			if (parent instanceof View) {
				v = (View) v.getParent();
			} else {
				return;
			}
		}
	}

	void enableChildrenCache(int fromScreen, int toScreen) {
		if (fromScreen > toScreen) {
			final int temp = fromScreen;
			fromScreen = toScreen;
			toScreen = temp;
		}

		final int count = getChildCount();

		fromScreen = Math.max(fromScreen, 0);
		toScreen = Math.min(toScreen, count - 1);

		for (int i = fromScreen; i <= toScreen; i++) {
			final CellLayout layout = (CellLayout) getChildAt(i);
			layout.setChildrenDrawnWithCacheEnabled(true);
			layout.setChildrenDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final CellLayout layout = (CellLayout) getChildAt(i);
			layout.setChildrenDrawnWithCacheEnabled(false);
		}
	}

	void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	public void setDragController(DragController dragController) {
		mDragController = dragController;
	}

	public void scrollLeft() {
		if (mScroller.isFinished()) {
			if (mCurrentScreen > 0)
				snapToScreen(mCurrentScreen - 1);
		} else {
			if (mNextScreen > 0)
				snapToScreen(mNextScreen - 1);
		}
	}

	public void scrollRight() {
		if (mScroller.isFinished()) {
			if (mCurrentScreen < getChildCount() - 1)
				snapToScreen(mCurrentScreen + 1);
		} else {
			if (mNextScreen < getChildCount() - 1)
				snapToScreen(mNextScreen + 1);
		}
	}

	public int getScreenForView(View v) {
		int result = -1;
		if (v != null) {
			ViewParent vp = v.getParent();
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (vp == getChildAt(i)) {
					return i;
				}
			}
		}
		return result;
	}

	public Folder getFolderForTag(Object tag) {
		int screenCount = getChildCount();
		for (int screen = 0; screen < screenCount; screen++) {
			CellLayout currentScreen = ((CellLayout) getChildAt(screen));
			int count = currentScreen.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = currentScreen.getChildAt(i);
				if (child instanceof Folder) {
					Folder f = (Folder) child;
					if (f.getInfo() == tag) {
						return f;
					}
				}
			}
		}
		return null;
	}

	public View getViewForTag(Object tag) {
		int screenCount = getChildCount();
		for (int screen = 0; screen < screenCount; screen++) {
			CellLayout currentScreen = ((CellLayout) getChildAt(screen));
			int count = currentScreen.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = currentScreen.getChildAt(i);
				if (child.getTag() == tag) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * @return True is long presses are still allowed for the current touch
	 */
	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	/**
	 * Set true to allow long-press events to be triggered, usually checked by
	 * {@link Launcher} to accept or block dpad-initiated long-presses.
	 */
	public void setAllowLongPress(boolean allowLongPress) {
		mAllowLongPress = allowLongPress;
	}

	void moveToDefaultScreen(boolean animate) {
		if (animate) {
			snapToScreen(mDefaultScreen);
		} else {
			setCurrentScreen(mDefaultScreen);
		}
		getChildAt(mDefaultScreen).requestFocus();
	}

	public void moveToDefaultScreen(int whichScreen) {
		snapToScreen(whichScreen);
		getChildAt(whichScreen).requestFocus();
	}

	void setIndicators(Drawable previous, Drawable next) {
		// mPreviousIndicator = previous;
		// mNextIndicator = next;
		previous.setLevel(mCurrentScreen);
		next.setLevel(mCurrentScreen);
	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	private boolean IsCanMove(int deltaX) {
		// deltaX<0说明手指向右划
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		// deltaX>0说明手指向左划
		if (getScrollX() >= (getChildCount() - 1) * getScreenWidth()
				&& deltaX > 0) {
			return false;
		}
		return true;
	}

	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		return display.getWidth();
	}

}
