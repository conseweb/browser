package com.qing.browser.ui.launcher;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.qing.browser.R;

public class Zone extends LinearLayout implements DropTarget,
		DragController.DragListener {
	private static final int ANIMATION_DURATION = 200;

	private final int[] mLocation = new int[2];

	private Launcher mLauncher;
	private boolean mTrashMode;

	private AnimationSet mInAnimation;
	private AnimationSet mOutAnimation;
	private Animation mHandleInAnimation;
	private Animation mHandleOutAnimation;

	private View mHandle;

	private SendZone mSendZone;
	private DeleteZone mDeleteZone;

	public Zone(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public Zone(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
	}

	/**
	 * @return the mSendZone
	 */
	public SendZone getmSendZone() {
		return mSendZone;
	}

	/**
	 * @param mSendZone
	 *            the mSendZone to set
	 */
	public void setmSendZone(SendZone mSendZone) {
		this.mSendZone = mSendZone;
	}

	/**
	 * @return the mDeleteZone
	 */
	public DeleteZone getmDeleteZone() {
		return mDeleteZone;
	}

	/**
	 * @param mDeleteZone
	 *            the mDeleteZone to set
	 */
	public void setmDeleteZone(DeleteZone mDeleteZone) {
		this.mDeleteZone = mDeleteZone;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		return true;
	}

	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}

	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {

	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(R.drawable.zone_bg_hover);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(R.drawable.zone_bg);
	}

	public void onDragStart(DragSource source, Object info, int dragAction) {
		final ItemInfo item = (ItemInfo) info;
		final int[] location = mLocation;
		getLocationOnScreen(location);
		if (item.screen == 2) {
			if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER) {
				return;
			} else if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
				mDeleteZone.setVisibility(View.GONE);
				mDeleteZone.layout(0, 0, 0, 0);

				mSendZone.setVisibility(View.VISIBLE);
				mSendZone.layout(0, 0, location[0] + this.getWidth(), location[1] + 50);
			}
		}
		if (item.screen == 1) {
			if (item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER) {
				mSendZone.setVisibility(View.GONE);
				mSendZone.layout(0, 0, 0, 0);

				mDeleteZone.setVisibility(View.VISIBLE);
				mDeleteZone.layout(0, 0, location[0] + this.getWidth(), location[1] + 50);

			} else {
				mSendZone.setVisibility(View.VISIBLE);
				mSendZone.layout(0, 0, location[0] + this.getWidth() / 2, location[1] + 50);

				mDeleteZone.setVisibility(View.VISIBLE);
				mDeleteZone.layout(0, 0, location[0] + this.getWidth() / 2, location[1] + 50);
			}
		}
		// mLauncher.initAllZone();

		if (item != null) {
			mTrashMode = true;
			createAnimations();
			startAnimation(mInAnimation);
			mHandle.startAnimation(mHandleOutAnimation);
			setVisibility(VISIBLE);
		}
	}

	public void onDragEnd() {
		if (mTrashMode) {
			mTrashMode = false;
			startAnimation(mOutAnimation);
			mHandle.startAnimation(mHandleInAnimation);
			setVisibility(GONE);
		}
	}

	private void createAnimations() {
		if (mInAnimation == null) {
			mInAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mInAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
			animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
			animationSet.addAnimation(new TranslateAnimation(
					Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
					Animation.RELATIVE_TO_SELF, -1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f));
			animationSet.setDuration(ANIMATION_DURATION);
		}
		if (mHandleInAnimation == null) {
			mHandleInAnimation = new AlphaAnimation(0.0f, 1.0f);
			mHandleInAnimation.setDuration(ANIMATION_DURATION);
		}
		if (mOutAnimation == null) {
			mOutAnimation = new FastAnimationSet();
			final AnimationSet animationSet = mOutAnimation;
			animationSet.setInterpolator(new AccelerateInterpolator());
			animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
			animationSet.addAnimation(new FastTranslateAnimation(
					Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, -1.0f));
			animationSet.setDuration(ANIMATION_DURATION);
		}
		if (mHandleOutAnimation == null) {
			mHandleOutAnimation = new AlphaAnimation(1.0f, 0.0f);
			mHandleOutAnimation.setFillAfter(true);
			mHandleOutAnimation.setDuration(ANIMATION_DURATION);
		}
	}

	void setHandle(View view) {
		mHandle = view;
	}

	/**
	 * @return the mLauncher
	 */
	public Launcher getmLauncher() {
		return mLauncher;
	}

	/**
	 * @param mLauncher
	 *            the mLauncher to set
	 */
	public void setmLauncher(Launcher mLauncher) {
		this.mLauncher = mLauncher;
	}

	private static class FastTranslateAnimation extends TranslateAnimation {
		public FastTranslateAnimation(int fromXType, float fromXValue,
				int toXType, float toXValue, int fromYType, float fromYValue,
				int toYType, float toYValue) {
			super(fromXType, fromXValue, toXType, toXValue, fromYType,
					fromYValue, toYType, toYValue);
		}

		@Override
		public boolean willChangeTransformationMatrix() {
			return true;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}

	private static class FastAnimationSet extends AnimationSet {
		FastAnimationSet() {
			super(false);
		}

		@Override
		public boolean willChangeTransformationMatrix() {
			return true;
		}

		@Override
		public boolean willChangeBounds() {
			return false;
		}
	}
}
