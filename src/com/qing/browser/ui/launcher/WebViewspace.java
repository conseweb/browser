package com.qing.browser.ui.launcher;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

import com.qing.browser.ui.menu.OnViewChangeListener;

public class WebViewspace extends ViewGroup {


	private static final int INVALID_SCREEN = -1;

	private Workspace mWorkspace;
	private Tabspace mTabspace;
	private static final int SNAP_VELOCITY = 400;

	private int mDefaultScreen = 0;

	private boolean mFirstLayout = true;

	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;

	private OnLongClickListener mLongClickListener;

	private boolean mAllowLongPress = true;

	private int mTouchSlop;
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

	public WebViewspace(Context context) {
		super(context);
		initWebViewspace();
	}

	public WebViewspace(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWebViewspace();
	}

	public WebViewspace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWebViewspace();
	}

	/**
	 * Initializes various states for this workspace.
	 */
	private void initWebViewspace() {
		Context context = getContext();
		mScroller = new Scroller(context);

		mCurrentScreen = mDefaultScreen;

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	boolean isDefaultScreenShowing() {
		return mCurrentScreen == mDefaultScreen;
	}

	int getCurrentScreen() {
		return mCurrentScreen;
	}

	void setCurrentScreen(int currentScreen) {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		mCurrentScreen = Math.max(0,
				Math.min(currentScreen, getChildCount() - 1));
		scrollTo(mCurrentScreen * getWidth(), 0);
		invalidate();
	}

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
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}

		/*final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}*/

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
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
			snapToScreen(screen);
			return true;
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

	void snapToScreen(int whichScreen) {
		snapToScreen(whichScreen, 0, false);
	}

	/**
	 * 根据当前布局的位置，滚动到目的页面
	 */
	public void snapToDestination() {
		/** 获取view的宽度 */
		final int screenWidth = getWidth();
		// Log.i(TAG, "screenWidth: " + screenWidth + " screenWidth/2: "
		// + screenWidth / 2);
		// Log.i(TAG, "getScrollX():" + getScrollX());
		/**
		 * getScrollX():获得滚动后view的横坐标
		 */
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	private void snapToScreen(int whichScreen, int velocity, boolean settle) {
		// 获取有效页面
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
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

	@Override
	protected Parcelable onSaveInstanceState() {
		final SavedState state = new SavedState(super.onSaveInstanceState());
		state.currentScreen = mCurrentScreen;
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		if (savedState.currentScreen != -1) {
			mCurrentScreen = savedState.currentScreen;
			Launcher.setScreen(mCurrentScreen);
		}
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

	public static class SavedState extends BaseSavedState {
		int currentScreen = -1;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentScreen = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(currentScreen);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public void setOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	private boolean IsCanMove(int deltaX) {
		// deltaX<0说明手指向右划
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		// deltaX>0说明手指向左划
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}
		return true;
	}

	public void setWorkspace(Workspace mWorkspace) {
		this.mWorkspace = mWorkspace;
	}

	public void setTabspace(Tabspace mTabspace) {
		this.mTabspace = mTabspace;
	}

}
