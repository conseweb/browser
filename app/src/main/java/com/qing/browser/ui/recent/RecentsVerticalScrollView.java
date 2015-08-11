package com.qing.browser.ui.recent;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.qing.browser.R;
import com.qing.browser.ui.recent.RecentsPanelView.TaskDescriptionAdapter;

public class RecentsVerticalScrollView extends ScrollView {
	private LinearLayout mLinearLayout;
	private TaskDescriptionAdapter mAdapter;
	private RecentsCallback mCallback;
	protected int mLastScrollPosition;
	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mSlop;
	/**
	 * 滑动的最小速度
	 */
	private int mMinFlingVelocity;
	/**
	 * 滑动的最大速度
	 */
	private int mMaxFlingVelocity;
	/**
	 * 执行动画的时间
	 */
	protected long mAnimationTime = 150;
	/**
	 * 用来标记用户是否正在滑动中
	 */
	private boolean mSwiping;
	/**
	 * 滑动速度检测类
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * 手指按下的position
	 */
	private int mDownPosition;
	/**
	 * 按下的item对应的View
	 */
	private View mDownView;
	private float mDownX;
	private float mDownY;
	/**
	 * item的宽度
	 */
	private int mViewWidth;
	/**
	 * 当ListView的Item滑出界面回调的接口
	 */
	private OnDismissCallback onDismissCallback;

	/**
	 * 设置删除回调接口
	 * 
	 * @param onDismissCallback
	 */
	public void setOnDismissCallback(OnDismissCallback onDismissCallback) {
		this.onDismissCallback = onDismissCallback;
	}

	public RecentsVerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);

		ViewConfiguration vc = ViewConfiguration.get(context);
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 8; // 获取滑动的最小速度
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity(); // 获取滑动的最大速度
	}

	private int scrollPositionOfMostRecent() {
		return mLinearLayout.getHeight() - getHeight();
	}

	private void update() {
		mLinearLayout.removeAllViews();
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View old = null;
			if (i < mLinearLayout.getChildCount()) {
				old = mLinearLayout.getChildAt(i);
				old.setVisibility(View.VISIBLE);
			}
			final View view = mAdapter.getView(i, old, mLinearLayout);

			if (old == null) {
				OnTouchListener noOpListener = new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return true;
					}
				};

				view.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mCallback.dismiss();
					}
				});

				view.setSoundEffectsEnabled(false);

				final int position = i;
				OnClickListener launchAppListener = new OnClickListener() {
					public void onClick(View v) {
						mCallback.handleOnClick(view, position);
					}
				};

				final View thumbnailView = view
						.findViewById(R.id.app_thumbnail);
				thumbnailView.setClickable(true);
				thumbnailView.setOnClickListener(launchAppListener);

				final View appTitle = view.findViewById(R.id.app_label);
				appTitle.setContentDescription(" ");
				appTitle.setOnTouchListener(noOpListener);
				final View calloutLine = view
						.findViewById(R.id.recents_callout_line);
				calloutLine.setOnTouchListener(noOpListener);
				mLinearLayout.addView(view);
			}
		}
		for (int i = mAdapter.getCount(); i < mLinearLayout.getChildCount(); i++) {
			mLinearLayout.getChildAt(i).setVisibility(View.GONE);
		}
		// Scroll to end after layout.
		post(new Runnable() {
			public void run() {
				mLastScrollPosition = scrollPositionOfMostRecent();
				scrollTo(0, mLastScrollPosition);
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handleActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			float deltaX = ev.getX() - mDownX;
			float deltaY = ev.getY() - mDownY;

			// X方向滑动的距离大于mSlop并且Y方向滑动的距离小于mSlop，表示可以滑动
			if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < mSlop) {
				return true;
			}
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handleActionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
			return handleActionMove(ev);
		case MotionEvent.ACTION_UP:
			handleActionUp(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 按下事件处理
	 * 
	 * @param ev
	 * @return
	 */
	private void handleActionDown(MotionEvent ev) {
		mDownX = ev.getX();
		mDownY = ev.getY();

		mDownPosition = pointToPosition(ev);

		if (mDownPosition == AdapterView.INVALID_POSITION) {
			return;
		}

		mDownView = getChildAtPosition(ev);

		if (mDownView != null) {
			mViewWidth = mDownView.getWidth();
		}

		// 加入速度检测
		mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(ev);
	}

	public int pointToPosition(MotionEvent ev) {
		final float x = ev.getX() + getScrollX();
		final float y = ev.getY() + getScrollY();
		for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
			View item = mLinearLayout.getChildAt(i);
			if (item.getVisibility() == View.VISIBLE && x >= item.getLeft()
					&& x < item.getRight() && y >= item.getTop()
					&& y < item.getBottom()) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * 处理手指滑动的方法
	 * 
	 * @param ev
	 * @return
	 */
	private boolean handleActionMove(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null) {
			return super.onTouchEvent(ev);
		}

		float deltaX = ev.getX() - mDownX;
		float deltaY = ev.getY() - mDownY;

		// X方向滑动的距离大于mSlop并且Y方向滑动的距离小于mSlop，表示可以滑动
		if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < mSlop) {
			mSwiping = true;

			// 当手指滑动item,取消item的点击事件，不然我们滑动Item也伴随着item点击事件的发生
			MotionEvent cancelEvent = MotionEvent.obtain(ev);
			cancelEvent
					.setAction(MotionEvent.ACTION_CANCEL
							| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
			//onTouchEvent(cancelEvent);
		}

		if (mSwiping) {
			// 跟谁手指移动item
			ViewHelper.setTranslationX(mDownView, deltaX);
			// 透明度渐变
			ViewHelper.setAlpha(
					mDownView,
					Math.max(
							0f,
							Math.min(1f, 1f - 2f * Math.abs(deltaX)
									/ mViewWidth)));

			// 手指滑动的时候,返回true，表示SwipeDismissListView自己处理onTouchEvent,其他的就交给父类来处理
			return true;
		}

		return super.onTouchEvent(ev);

	}

	/**
	 * 手指抬起的事件处理
	 * 
	 * @param ev
	 */
	private void handleActionUp(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null || !mSwiping) {
			return;
		}

		float deltaX = ev.getX() - mDownX;

		// 通过滑动的距离计算出X,Y方向的速度
		mVelocityTracker.computeCurrentVelocity(1000);
		float velocityX = Math.abs(mVelocityTracker.getXVelocity());
		float velocityY = Math.abs(mVelocityTracker.getYVelocity());

		boolean dismiss = false; // item是否要滑出屏幕
		boolean dismissRight = false;// 是否往右边删除

		// 当拖动item的距离大于item的一半，item滑出屏幕
		if (Math.abs(deltaX) > mViewWidth / 2) {
			dismiss = true;
			dismissRight = deltaX > 0;

			// 手指在屏幕滑动的速度在某个范围内，也使得item滑出屏幕
		} else if (mMinFlingVelocity <= velocityX
				&& velocityX <= mMaxFlingVelocity && velocityY < velocityX) {
			dismiss = true;
			dismissRight = mVelocityTracker.getXVelocity() > 0;
		}

		if (dismiss) {
			ViewPropertyAnimator.animate(mDownView)
					.translationX(dismissRight ? mViewWidth : -mViewWidth)
					// X轴方向的移动距离
					.alpha(0).setDuration(mAnimationTime)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							// Item滑出界面之后执行删除
							performDismiss(mDownView, mDownPosition);
						}
					});
		} else {
			// 将item滑动至开始位置
			ViewPropertyAnimator.animate(mDownView).translationX(0).alpha(1)
					.setDuration(mAnimationTime).setListener(null);
		}

		// 移除速度检测
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		mSwiping = false;
	}

	/**
	 * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
	 * 
	 * @param dismissView
	 * @param dismissPosition
	 */
	private void performDismiss(final View dismissView,
			final int dismissPosition) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
		final int originalHeight = dismissView.getHeight();// item的高度

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
				.setDuration(mAnimationTime);
		animator.start();

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (onDismissCallback != null) {
					onDismissCallback.onDismiss(dismissView, dismissPosition);
				}

				// 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
				// 所以我们在动画执行完毕之后将item设置回来
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.height = originalHeight;
				dismissView.setLayoutParams(lp);

			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
				lp.height = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

	}

	public View getChildAtPosition(MotionEvent ev) {
		final float x = ev.getX() + getScrollX();
		final float y = ev.getY() + getScrollY();
		for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
			View item = mLinearLayout.getChildAt(i);
			if (item.getVisibility() == View.VISIBLE && x >= item.getLeft()
					&& x < item.getRight() && y >= item.getTop()
					&& y < item.getBottom()) {
				return item;
			}
		}
		return null;
	}

	public View getChildContentView(View v) {
		return v.findViewById(R.id.recent_item);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		setScrollbarFadingEnabled(true);
		mLinearLayout = (LinearLayout) findViewById(R.id.recents_linear_layout);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		// scroll to bottom after reloading
		if (visibility == View.VISIBLE && changedView == this) {
			post(new Runnable() {
				public void run() {
					update();
				}
			});
		}
	}

	public void setAdapter(TaskDescriptionAdapter adapter) {
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(new DataSetObserver() {
			public void onChanged() {
				update();
			}

			public void onInvalidated() {
				update();
			}
		});
	}

	public void setCallback(RecentsCallback callback) {
		mCallback = callback;
	}
}
