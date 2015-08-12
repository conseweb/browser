package com.qing.browser.ui.launcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Scroller;

import com.qing.browser.R;
import com.qing.browser.ui.menu.OnViewChangeListener;

/**
 * The workspace is a wide area with a wallpaper and a finite number of screens.
 * Each screen contains a number of icons, folders or widgets the user can
 * interact with. A workspace is meant to be used with a fixed width only.
 */
public class Homespace extends ViewGroup {

	private static final int INVALID_SCREEN = -1;

	private WebViewspace mWebViewspace;
	private Workspace mWorkspace;
	private Tabspace mTabspace;
	private static final int SNAP_VELOCITY = 400;

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
	
	private Context context;

	public static boolean isRightScreenDrag = false;
	
	private long downTime;

	public Homespace(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}

	public Homespace(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Workspace, defStyle, 0);
		mDefaultScreen = a.getInt(R.styleable.Workspace_defaultScreen, 0);
		a.recycle();

		setHapticFeedbackEnabled(false);
		initHomespace();
	}

	/**
	 * Initializes various states for this workspace.
	 */
	private void initHomespace() {
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

	public void setCurrentScreen(int currentScreen) {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		mCurrentScreen = Math.max(0,
				Math.min(currentScreen, getChildCount() - 1));
		scrollTo(mCurrentScreen * getWidth(), 0);
		invalidate();
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
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
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
			// final View child = getChildAt(i);
			// if (child.getVisibility() != View.GONE) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
			// }
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
				final int childHeight =child.getMeasuredHeight();
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

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);

		int width = display.getWidth();
		if(mWorkspace.getCurrentScreen() == Launcher.CURRENT_SCREEN_WORKSPACE_LEFT){
			return false;
		}
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final float xDiffSign = mLastMotionX - x;
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			int deltaX = (int) (mLastMotionX - x);
			int deltaY = (int) (mLastMotionY - y);
			// 处理传递给workspace,workspace 三屏切换
			// 如果Homespace当前屏为workspace,则优先判断workspace的页面切换情况,
			// 然后是workspacewebviewspace的切换情况
			if (mCurrentScreen == Launcher.CURRENT_SCREEN_WORKSPACE) {
				// 判断当前为workspace
				// 判断workspace的当前屏幕
				if (mWorkspace.getCurrentScreen() == Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT) {
					// 在workspace的第三屏向左滑动
					// 如果tabspace属性可见,则显示tabspace
					if (Homespace.isRightScreenDrag) {
						return false;
					}
					if (xDiffSign > 0 && xDiffSign > mTouchSlop
							&& mTabspace.getVisibility() == View.VISIBLE) {
						return true;
					} else {
						return false;
					}
				}			
				return false;
			} else if (mCurrentScreen == Launcher.CURRENT_SCREEN_TABSPACE) {
				// TODO 网页手势处理
				// 判断tabspace中的webviewspace是否包含多个页面
				// 判断webviewspace中当前页面为0,则左滑动时,返回workspace
				long currentTime = ev.getEventTime();

				
				if (mWebViewspace.getCurrentScreen() == 0) {
					if ((Math.abs(deltaX) > Math.abs(deltaY)) && xDiffSign < 0
							&& xDiff > width *0.3) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}

			}

			if ((Math.abs(deltaX) > Math.abs(deltaY)) && xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			downTime = ev.getEventTime();
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
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
				snapToDestination();
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

	void moveToDefaultScreen(int whichScreen) {
		snapToScreen(whichScreen);
		getChildAt(whichScreen).requestFocus();
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

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	private boolean IsCanMove(int deltaX) {
		// deltaX<0说明手指向右划
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		// deltaX>0说明手指向左划
		if (getScrollX() >= (getChildCount() - 1) *  getScreenWidth() && deltaX > 0) {
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

	public void setWebViewspace(WebViewspace mWebViewspace) {
		this.mWebViewspace = mWebViewspace;

	}
	
	private int getScreenWidth(){
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		display.getMetrics(dm);
		return display.getWidth();
	}
}
