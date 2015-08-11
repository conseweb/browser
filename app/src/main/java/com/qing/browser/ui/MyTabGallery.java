package com.qing.browser.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
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
import android.widget.Scroller;

import com.qing.browser.ui.launcher.CellLayout;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.menu.OnViewChangeListener;

public class MyTabGallery extends ViewGroup {

	private static final String TAG = "MyTabGallery";
	private static final int INVALID_SCREEN = -1;

	private static final int SNAP_VELOCITY = 400;

	private int mDefaultScreen;

	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private float mLastMotionX;
	private float mLastMotionY;

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;

	private double mTouchSlop;
	private int mMaximumVelocity;

	private static final float NANOTIME_DIV = 1000000000.0f;
	private static final float SMOOTHING_SPEED = 0.75f;
	private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math
			.log(SMOOTHING_SPEED));
	private float mSmoothingTime;
	private float mTouchX;

	private OnViewChangeListener mOnViewChangeListener;

	private int width;
	private int height;

	public MyTabGallery(Context context) {
		super(context);
		initMyTabGallery();
	}

	public MyTabGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMyTabGallery();
	}

	public MyTabGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initMyTabGallery();
	}

	private void initMyTabGallery() {
		Context context = getContext();
		mScroller = new Scroller(context);
		mCurrentScreen = mDefaultScreen;

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		// DisplayMetrics dm = new DisplayMetrics();
		// WindowManager wm = (WindowManager) getContext()
		// .getSystemService(Context.WINDOW_SERVICE);
		// Display display = wm.getDefaultDisplay();
		// display.getMetrics(dm);
		// mTouchSlop=display.getWidth()* 0.56;
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}

	int getCurrentScreen() {
		return mCurrentScreen;
	}

	public void setCurrentScreen(int currentScreen) {
		if (!mScroller.isFinished())
			mScroller.abortAnimation();
		mCurrentScreen = Math.max(0,
				Math.min(currentScreen, getChildCount() - 1));
		scrollTo(mCurrentScreen * (getWidth() - 150), 0);
		invalidate();
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
		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec - 200, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		int childLeft = 0;
		int childTop = 0;

		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {

				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight();

				childLeft = (width - childWidth) / 2;
				break;

			}
		}

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {

				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight();
				Log.i(TAG, "childTop->" + childTop);
				child.layout(childLeft, childTop, childLeft + childWidth,
						childTop + childHeight);
				childLeft += childWidth + (width - childWidth) / 4;
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
		Log.i(TAG, "onInterceptTouchEvent");

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final float xDiffSign = mLastMotionX - x;
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			int deltaX = (int) (mLastMotionX - x);
			int deltaY = (int) (mLastMotionY - y);

			Log.i(TAG, "xDiff= " + xDiff + " mTouchSlop= " + mTouchSlop);
			if ((Math.abs(deltaX) > Math.abs(deltaY)) && xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
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
		Log.i(TAG, "onTouchEvent");
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			Log.i(TAG, "onTouchEvent  ACTION_DOWN");
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
			Log.i(TAG, "onTouchEvent  ACTION_MOVE");
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
			Log.i(TAG, "onTouchEvent  ACTION_UP");
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
				Log.e(TAG, "snap left");
				snapToScreen(mCurrentScreen - 1);
				Log.e(TAG, "snap right->" + getCurrentScreen());
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurrentScreen < getChildCount() - 1) {
				// Fling enough to move right
				Log.e(TAG, "snap right");
				snapToScreen(mCurrentScreen + 1);

				Log.e(TAG, "snap right->" + getCurrentScreen());
			} else {
				snapToDestination();
				Log.e(TAG, "snapToDestination->" + getCurrentScreen());
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
		final int screenWidth = getChildAt(0).getWidth();
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
			Log.i(TAG, "getWidth()= " + getWidth());
			final int delta = whichScreen * (getWidth() - 150) - getScrollX();
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

	void moveToDefaultScreen(boolean animate) {
		if (animate) {
			snapToScreen(mDefaultScreen);
		} else {
			setCurrentScreen(mDefaultScreen);
		}
		getChildAt(mDefaultScreen).requestFocus();
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

}