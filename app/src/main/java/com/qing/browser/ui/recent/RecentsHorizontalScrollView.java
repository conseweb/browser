package com.qing.browser.ui.recent;
 
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.qing.browser.R;
import com.qing.browser.ui.recent.RecentsPanelView.TaskDescriptionAdapter;

public class RecentsHorizontalScrollView extends HorizontalScrollView{
    private LinearLayout mLinearLayout;
    private TaskDescriptionAdapter mAdapter;
    private RecentsCallback mCallback;
    protected int mLastScrollPosition;
	/**
	 * ��Ϊ���û���������С����
	 */
	private int mSlop;
	/**
	 * ��������С�ٶ�
	 */
	private int mMinFlingVelocity;
	/**
	 * ����������ٶ�
	 */
	private int mMaxFlingVelocity;
	/**
	 * ִ�ж�����ʱ��
	 */
	protected long mAnimationTime = 150;
	/**
	 * ��������û��Ƿ����ڻ�����
	 */
	private boolean mSwiping;
	/**
	 * �����ٶȼ����
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * ��ָ���µ�position
	 */
	private int mDownPosition;
	/**
	 * ���µ�item��Ӧ��View
	 */
	private View mDownView;
	private float mDownX;
	private float mDownY;
	/**
	 * item�Ŀ��
	 */
	private int mViewWidth;
	/**
	 * ��ListView��Item��������ص��Ľӿ�
	 */
	private OnDismissCallback onDismissCallback;

	/**
	 * ����ɾ���ص��ӿ�
	 * 
	 * @param onDismissCallback
	 */
	public void setOnDismissCallback(OnDismissCallback onDismissCallback) {
		this.onDismissCallback = onDismissCallback;
	}

    public RecentsHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
		ViewConfiguration vc = ViewConfiguration.get(context);
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 8; // ��ȡ��������С�ٶ�
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity(); // ��ȡ����������ٶ�
    }

    private int scrollPositionOfMostRecent() {
        return mLinearLayout.getWidth() - getWidth();
    }

    private void update() {
        mLinearLayout.removeAllViews();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final View view = mAdapter.getView(i, null, mLinearLayout);

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
            // We don't want a click sound when we dimiss recents
            view.setSoundEffectsEnabled(false);

            final int position = i;
            OnClickListener launchAppListener = new OnClickListener() {
                public void onClick(View v) {
                    mCallback.handleOnClick(view, position);
                }
            };

            final View thumbnailView = view.findViewById(R.id.app_thumbnail);
            thumbnailView.setClickable(true);
            thumbnailView.setOnClickListener(launchAppListener);

            final View appTitle = view.findViewById(R.id.app_label);
            appTitle.setContentDescription(" ");
            appTitle.setOnTouchListener(noOpListener);
            mLinearLayout.addView(view);
        }
        // Scroll to end after layout.
        post(new Runnable() {
            public void run() {
                mLastScrollPosition = scrollPositionOfMostRecent();
                scrollTo(mLastScrollPosition, 0);
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

			// X���򻬶��ľ������mSlop����Y���򻬶��ľ���С��mSlop����ʾ���Ի���
			if (Math.abs(deltaY) > mSlop && Math.abs(deltaX) < mSlop) {
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
	 * �����¼�����
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

		// �����ٶȼ��
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
	 * ������ָ�����ķ���
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

		// X���򻬶��ľ������mSlop����Y���򻬶��ľ���С��mSlop����ʾ���Ի���
		if (Math.abs(deltaY) > mSlop && Math.abs(deltaX) < mSlop) {
			mSwiping = true;

			// ����ָ����item,ȡ��item�ĵ���¼�����Ȼ���ǻ���ItemҲ������item����¼��ķ���
			MotionEvent cancelEvent = MotionEvent.obtain(ev);
			cancelEvent
					.setAction(MotionEvent.ACTION_CANCEL
							| (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
			onTouchEvent(cancelEvent);
		}

		if (mSwiping) {
			// ��˭��ָ�ƶ�item
			ViewHelper.setTranslationY(mDownView, deltaY);
			// ͸���Ƚ���
			ViewHelper.setAlpha(
					mDownView,
					Math.max(
							0f,
							Math.min(1f, 1f - 2f * Math.abs(deltaY)
									/ mViewWidth)));

			// ��ָ������ʱ��,����true����ʾSwipeDismissListView�Լ�����onTouchEvent,�����ľͽ�������������
			return true;
		}

		return super.onTouchEvent(ev);

	}

	/**
	 * ��ָ̧����¼�����
	 * 
	 * @param ev
	 */
	private void handleActionUp(MotionEvent ev) {
		if (mVelocityTracker == null || mDownView == null || !mSwiping) {
			return;
		}

		float deltaY = ev.getY() - mDownY;

		// ͨ�������ľ�������X,Y������ٶ�
		mVelocityTracker.computeCurrentVelocity(1000);
		float velocityX = Math.abs(mVelocityTracker.getXVelocity());
		float velocityY = Math.abs(mVelocityTracker.getYVelocity());

		boolean dismiss = false; // item�Ƿ�Ҫ������Ļ
		boolean dismissRight = false;// �Ƿ����ұ�ɾ��

		// ���϶�item�ľ������item��һ�룬item������Ļ
		if (Math.abs(deltaY) > mViewWidth / 2) {
			dismiss = true;
			dismissRight = deltaY > 0;

			// ��ָ����Ļ�������ٶ���ĳ����Χ�ڣ�Ҳʹ��item������Ļ
		} else if (mMinFlingVelocity <= velocityY
				&& velocityY <= mMaxFlingVelocity && velocityY > velocityX) {
			dismiss = true;
			dismissRight = mVelocityTracker.getYVelocity() > 0;
		}

		if (dismiss) {
			ViewPropertyAnimator.animate(mDownView)
					.translationY(dismissRight ? mViewWidth : -mViewWidth)
					// X�᷽����ƶ�����
					.alpha(0).setDuration(mAnimationTime)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							// Item��������֮��ִ��ɾ��
							performDismiss(mDownView, mDownPosition);
						}
					});
		} else {
			// ��item��������ʼλ��
			ViewPropertyAnimator.animate(mDownView).translationY(0).alpha(1)
					.setDuration(mAnimationTime).setListener(null);
		}

		// �Ƴ��ٶȼ��
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		mSwiping = false;
	}

	/**
	 * �ڴ˷�����ִ��itemɾ��֮��������item���ϻ������¹����Ķ��������ҽ�position�ص�������onDismiss()��
	 * 
	 * @param dismissView
	 * @param dismissPosition
	 */
	private void performDismiss(final View dismissView,
			final int dismissPosition) {
		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// ��ȡitem�Ĳ��ֲ���
		final int originalWidth = dismissView.getWidth();// item�ĸ߶�

		ValueAnimator animator = ValueAnimator.ofInt(originalWidth, 0)
				.setDuration(mAnimationTime);
		animator.start();

		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (onDismissCallback != null) {
					onDismissCallback.onDismiss(dismissView, dismissPosition);
				}

				// ��δ������Ҫ����Ϊ���ǲ�û�н�item��ListView���Ƴ������ǽ�item�ĸ߶�����Ϊ0
				// ���������ڶ���ִ�����֮��item���û���
				ViewHelper.setAlpha(dismissView, 1f);
				ViewHelper.setTranslationX(dismissView, 0);
				ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
				lp.width = originalWidth;
				dismissView.setLayoutParams(lp);

			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// ��δ����Ч����ListViewɾ��ĳitem֮��������item���ϻ�����Ч��
				lp.width = (Integer) valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

	}
    public View getChildAtPosition(MotionEvent ev) {
        final float x = ev.getX() + getScrollX();
        final float y = ev.getY() + getScrollY();
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            View item = mLinearLayout.getChildAt(i);
            if (x >= item.getLeft() && x < item.getRight()
                && y >= item.getTop() && y < item.getBottom()) {
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
