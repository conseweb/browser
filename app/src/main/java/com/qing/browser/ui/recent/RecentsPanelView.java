package com.qing.browser.ui.recent;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.qing.browser.R;
import com.qing.browser.components.CustomWebView;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.launcher.WebViewspace;

public class RecentsPanelView extends RelativeLayout implements
		OnItemClickListener, RecentsCallback, Animator.AnimatorListener,
		View.OnTouchListener {
	static final String TAG = "RecentsPanelView H";
	private Context mContext;
	private View mRecentsScrim;
	private ViewGroup mRecentsContainer;

	private boolean mShowing;

	private ArrayList<TaskDescription> mRecentTaskDescriptions;
	private Runnable mPreloadTasksRunnable;
	private TaskDescriptionAdapter mListAdapter;
	private int mThumbnailWidth;

	final static class ViewHolder {
		View thumbnailView;
		ImageView thumbnailViewImage;
		Bitmap thumbnailViewImageBitmap;
		TextView labelView;
		TaskDescription taskDescription;
		int position;
	}

	final class TaskDescriptionAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public TaskDescriptionAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mRecentTaskDescriptions != null ? mRecentTaskDescriptions
					.size() : 0;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.status_bar_recent_item, parent, false);
				holder = new ViewHolder();
				holder.thumbnailView = convertView
						.findViewById(R.id.app_thumbnail);
				holder.thumbnailViewImage = (ImageView) convertView
						.findViewById(R.id.app_thumbnail_image);
				updateThumbnail(holder, mRecentTaskDescriptions.get(position)
						.getThumbnail(), false, false);
				
				holder.labelView = (TextView) convertView
						.findViewById(R.id.app_label);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final TaskDescription td = mRecentTaskDescriptions.get(position);
			Log.d(TAG,
					"getView position = " + position + " td="
							+ td.getPosition() + " " + td.getLabel()
							+ " CurrentIndex=" + Launcher.mCurrentIndex + " 截图=" + td.getThumbnail());
			holder.labelView.setText(td.getLabel());
			holder.thumbnailView.setContentDescription(td.getLabel());
			updateThumbnail(holder, td.getThumbnail(), true, false);

			holder.thumbnailView.setTag(td);
			holder.taskDescription = td;
			holder.position = position;

			if (position == Launcher.mCurrentIndex) {
				holder.thumbnailView.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.recents_thumbnail_bg_dragging));
			} else {
				holder.thumbnailView.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.recents_thumbnail_bg));
			}

			return convertView;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
			show(false, true);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public boolean isInContentArea(int x, int y) {
		// use mRecentsContainer's exact bounds to determine horizontal position
		final int l = mRecentsContainer.getLeft();
		final int r = mRecentsContainer.getRight();
		final int t = mRecentsContainer.getTop();
		final int b = mRecentsContainer.getBottom();
		return x >= l && x < r && y >= t && y < b;
	}

	public void show(boolean show, boolean animate) {
		show(show, animate, null);
	}

	public void show(boolean show, boolean animate,
			ArrayList<TaskDescription> recentTaskDescriptions) {
		animate=false;
		if (show) {
			refreshRecentTasksList(recentTaskDescriptions);
		}
		if (animate) {
			if (mShowing != show) {
				mShowing = show;
				if (show) {
					setVisibility(View.VISIBLE);
				}
			}
		} else {
			mShowing = show;
			setVisibility(show ? View.VISIBLE : View.GONE);
			onAnimationEnd(null);
		}
		if (show) {
			setFocusable(true);
			setFocusableInTouchMode(true);
			requestFocus();
		}
	}

	public void dismiss() {
		hide(true);
	}

	public void hide(boolean animate) {
		if (!animate) {
			setVisibility(View.GONE);
		}
	}

	public void handleShowBackground(boolean show) {
		if (show) {
			mRecentsScrim
					.setBackgroundResource(R.drawable.status_bar_recents_background_solid);
		} else {
			mRecentsScrim.setBackgroundDrawable(null);
		}
	}

	public boolean isRecentsVisible() {
		return getVisibility() == VISIBLE;
	}

	public void onAnimationCancel(Animator animation) {
	}

	public void onAnimationEnd(Animator animation) {
		
	}

	public void onAnimationRepeat(Animator animation) {
	}

	public void onAnimationStart(Animator animation) {
	}

	/**
	 * We need to be aligned at the bottom. LinearLayout can't do this, so
	 * instead, let LinearLayout do all the hard work, and then shift everything
	 * down to the bottom.
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	//@Override
	/*public boolean dispatchHoverEvent(MotionEvent event) {
		// Ignore hover events outside of this panel bounds since such events
		// generate spurious accessibility events with the panel content when
		// tapping outside of it, thus confusing the user.
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
			//return super.dispatchHoverEvent(event);
		}
		return true;
	}*/

	/**
	 * Whether the panel is showing, or, if it's animating, whether it will be
	 * when the animation is done.
	 */
	public boolean isShowing() {
		return mShowing;
	}

	public RecentsPanelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RecentsPanelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		updateValuesFromResources();
	}

	public void updateValuesFromResources() {
		final Resources res = mContext.getResources();
		mThumbnailWidth = Math.round(res
				.getDimension(R.dimen.status_bar_recents_thumbnail_width));
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mRecentsScrim = findViewById(R.id.recents_bg_protect);
		mRecentsContainer = (ViewGroup) findViewById(R.id.recents_container);
		mListAdapter = new TaskDescriptionAdapter(mContext);

		if (mRecentsContainer instanceof RecentsVerticalScrollView) {
			RecentsVerticalScrollView scrollView = (RecentsVerticalScrollView) mRecentsContainer;
			scrollView.setAdapter(mListAdapter);
			scrollView.setCallback(this);
			scrollView.setOnDismissCallback(new OnDismissCallback() {
				
				@Override
				public void onDismiss(View dismissView, int dismissPosition) {
					handleSwipe(dismissView,dismissPosition);
				}
			});
		} else if (mRecentsContainer instanceof RecentsHorizontalScrollView) {
			RecentsHorizontalScrollView scrollView = (RecentsHorizontalScrollView) mRecentsContainer;
			scrollView.setAdapter(mListAdapter);
			scrollView.setCallback(this);
			scrollView.setOnDismissCallback(new OnDismissCallback() {
				
				@Override
				public void onDismiss(View dismissView, int dismissPosition) {
					handleSwipe(dismissView,dismissPosition);
				}
			});
		} else {
			throw new IllegalArgumentException(
					"missing Recents[Horizontal]ScrollView");
		}

		if (mRecentsScrim != null
				&& mRecentsScrim.getBackground() instanceof BitmapDrawable) {
			((BitmapDrawable) mRecentsScrim.getBackground())
					.setTileModeY(TileMode.REPEAT);
		}

		mPreloadTasksRunnable = new Runnable() {
			public void run() {
				setVisibility(INVISIBLE);
				refreshRecentTasksList();
			}
		};
	}

	private void updateThumbnail(ViewHolder h, Bitmap thumbnail, boolean show,
			boolean anim) {
		if (thumbnail != null) {
			h.thumbnailViewImage.setImageBitmap(thumbnail);

			if (h.thumbnailViewImageBitmap == null
					|| h.thumbnailViewImageBitmap.getWidth() != thumbnail
							.getWidth()
					|| h.thumbnailViewImageBitmap.getHeight() != thumbnail
							.getHeight()) {

				Matrix scaleMatrix = new Matrix();
				float scale = mThumbnailWidth / (float) thumbnail.getWidth();
				scaleMatrix.setScale(scale, scale);
				h.thumbnailViewImage.setScaleType(ScaleType.MATRIX);
				h.thumbnailViewImage.setImageMatrix(scaleMatrix);

			}
			if (show && h.thumbnailView.getVisibility() != View.VISIBLE) {
				if (anim) {
					h.thumbnailView.setAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.recent_appear));
				}
				h.thumbnailView.setVisibility(View.VISIBLE);
			}
			h.thumbnailViewImageBitmap = thumbnail;
		}else{
			Log.e(TAG, "截图为空 " );
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		if (!mShowing) {
			int action = ev.getAction() & MotionEvent.ACTION_MASK;
			if (action == MotionEvent.ACTION_DOWN) {
				post(mPreloadTasksRunnable);
			} else if (action == MotionEvent.ACTION_CANCEL) {
				setVisibility(GONE);
				clearRecentTasksList();
				removeCallbacks(mPreloadTasksRunnable);
			} else if (action == MotionEvent.ACTION_UP) {
				removeCallbacks(mPreloadTasksRunnable);
				if (!v.isPressed()) {
					setVisibility(GONE);
					clearRecentTasksList();
				}
			}
		}
		return false;
	}

	public void clearRecentTasksList() {
		if (mRecentTaskDescriptions != null) {
			Log.d(TAG, "clearRecentTasksList");
			mRecentTaskDescriptions.clear();
			mListAdapter.notifyDataSetInvalidated();
		}
	}

	public void refreshRecentTasksList() {
		refreshRecentTasksList(null);
	}

	public void refreshRecentTasksList(
			ArrayList<TaskDescription> recentTasksList) {
		mRecentTaskDescriptions = recentTasksList;

		mListAdapter.notifyDataSetInvalidated();

		updateUiElements(getResources().getConfiguration());

	}

	public ArrayList<TaskDescription> getRecentTasksList() {
		return mRecentTaskDescriptions;
	}

	private void updateUiElements(Configuration config) {
		final int items = mRecentTaskDescriptions.size();

		mRecentsContainer.setVisibility(items > 0 ? View.VISIBLE : View.GONE);

	}

	public void handleOnClick(View view, int position) {

		TaskDescription td = ((ViewHolder) view.getTag()).taskDescription;
		int index = ((ViewHolder) view.getTag()).position;
		Log.i(TAG,
				"handleOnClick 单击  td=" + td.getPosition() + " "
						+ td.getLabel() + " index=" + index + " position="
						+ position + " CurrentIndex=" + Launcher.mCurrentIndex);

		hide(true);
		if (position == Launcher.mCurrentIndex) {
			Launcher.tabs_gallery.setVisibility(View.GONE);
			Launcher.tabs_gallery_Flag = false;
			return;
		}

		HashMap<String, Object> hashmap = Launcher.mWebViewStates.get(position);

		Log.d(TAG,
				"handleOnClick 单击 isHome=" + hashmap.get("isHome").toString()
						+ " isWebview="
						+ String.valueOf((Boolean) hashmap.get("isWebview")));
		if (Launcher.CURRENT_SCREEN_WORKSPACE_NOT == Integer.parseInt((hashmap
				.get("isHome").toString()))) {
			Launcher.mHomespace
					.setCurrentScreen(Launcher.CURRENT_SCREEN_TABSPACE);
		} else {
			if (Launcher.CURRENT_SCREEN_WORKSPACE_LEFT == Integer
					.parseInt((hashmap.get("isHome").toString()))) {
				Launcher.mWorkspace
						.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_LEFT);
			} else if (Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE == Integer
					.parseInt((hashmap.get("isHome").toString()))) {
				Launcher.mWorkspace
						.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE);
			} else if (Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT == Integer
					.parseInt((hashmap.get("isHome").toString()))) {
				Launcher.mWorkspace
						.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT);
			}
			Launcher.mHomespace
					.setCurrentScreen(Launcher.CURRENT_SCREEN_WORKSPACE);
			Launcher.mUrlEditText
					.removeTextChangedListener(Launcher.mUrlTextWatcher);
			Launcher.mUrlEditText.setText("");
			Launcher.mUrlEditText
					.addTextChangedListener(Launcher.mUrlTextWatcher);
			Launcher.urlText.setText("");
		}
		if (false == (Boolean) hashmap.get("isWebview")) {
			Launcher.mTabspace.setVisibility(View.GONE);
		} else {
			Launcher.mTabspace.setChildVisibility(position);
			Launcher.mTabspace.setVisibility(View.VISIBLE);
			WebViewspace mWebViewspace = (WebViewspace) Launcher.mTabspace
					.getChildAt(position);
			View childView = mWebViewspace.getChildAt(0);
			CustomWebView mCustomWebView = (CustomWebView) childView
					.findViewById(R.id.webview);
			Launcher.urlText.setText(mCustomWebView.getTitle());
			Launcher.mUrlEditText
					.removeTextChangedListener(Launcher.mUrlTextWatcher);
			Launcher.mUrlEditText.setText(mCustomWebView.getUrl());
			Launcher.mUrlEditText
					.addTextChangedListener(Launcher.mUrlTextWatcher);
		}

		Launcher.tabs_gallery.setVisibility(View.GONE);
		Launcher.tabs_gallery_Flag = false;
		Launcher.mCurrentIndex = position;

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		handleOnClick(view, position);
	}

	public void handleSwipe(View view, int position) {
		TaskDescription td = ((ViewHolder) view.getTag()).taskDescription;

		// TODO 已删除
		int index = ((ViewHolder) view.getTag()).position;
		Log.i(TAG,
				"handleSwipe 已删除  td=" + td.getPosition() + " " + td.getLabel()
						+ " index=" + index + " CurrentIndex="
						+ Launcher.mCurrentIndex);

		mRecentTaskDescriptions.remove(td);

		if (mRecentTaskDescriptions.size() == 0) {
			clearRecentTasksList();
			Launcher.tabs_gallery.setVisibility(View.GONE);
			Launcher.tabs_gallery_Flag = false;
			hide(true);
		}

		if (Launcher.tabCurrentItem != 0) {
			Launcher.tabCurrentItem--;

			if (Launcher.mCurrentIndex != 0) {
				Launcher.mCurrentIndex--;
			}

			Launcher.mWebViewBitmaps.remove(position);
			Launcher.mWebViewStates.remove(position);

			WebViewspace mmWebViewspace = (WebViewspace) Launcher.mTabspace
					.getChildAt(position);
			View mchildView = mmWebViewspace.getChildAt(0);
			if (mchildView != null) {
				CustomWebView mmCustomWebView = (CustomWebView) mchildView
						.findViewById(R.id.webview);
				mmCustomWebView.loadUrl("");
				mmCustomWebView.stopLoading();
			}

			Launcher.mTabspace.removeViewAt(position);

			// 设置需要显示的tab

			// TODO 根据Tab的状态,判断显示效果
			HashMap<String, Object> hashmap = Launcher.mWebViewStates
					.get(Launcher.tabCurrentItem);
			Log.d(TAG,
					"handleSwipe 单击 isHome="
							+ hashmap.get("isHome").toString()
							+ " isWebview="
							+ String.valueOf((Boolean) hashmap.get("isWebview")));
			if (Launcher.CURRENT_SCREEN_WORKSPACE_NOT == Integer
					.parseInt((hashmap.get("isHome").toString()))) {
				Launcher.mHomespace
						.setCurrentScreen(Launcher.CURRENT_SCREEN_TABSPACE);
			} else {
				if (Launcher.CURRENT_SCREEN_WORKSPACE_LEFT == Integer
						.parseInt((hashmap.get("isHome").toString()))) {
					Launcher.mWorkspace
							.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_LEFT);
				} else if (Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE == Integer
						.parseInt((hashmap.get("isHome").toString()))) {
					Launcher.mWorkspace
							.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE);
				} else if (Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT == Integer
						.parseInt((hashmap.get("isHome").toString()))) {
					Launcher.mWorkspace
							.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT);
				}
				Launcher.mHomespace
						.setCurrentScreen(Launcher.CURRENT_SCREEN_WORKSPACE);
				Launcher.mUrlEditText
						.removeTextChangedListener(Launcher.mUrlTextWatcher);
				Launcher.mUrlEditText.setText("");
				Launcher.mUrlEditText
						.addTextChangedListener(Launcher.mUrlTextWatcher);
				Launcher.urlText.setText("");
			}

			if (false == (Boolean) hashmap.get("isWebview")) {
				Launcher.mTabspace.setVisibility(View.GONE);
			} else {
				Launcher.mTabspace.setChildVisibility(Launcher.tabCurrentItem);
				Launcher.mTabspace.setVisibility(View.VISIBLE);
				WebViewspace mWebViewspace = (WebViewspace) Launcher.mTabspace
						.getChildAt(Launcher.tabCurrentItem);
				View childView = mWebViewspace.getChildAt(0);
				CustomWebView mCustomWebView = (CustomWebView) childView
						.findViewById(R.id.webview);
				Launcher.urlText.setText(mCustomWebView.getTitle());
				Launcher.mUrlEditText
						.removeTextChangedListener(Launcher.mUrlTextWatcher);
				Launcher.mUrlEditText.setText(mCustomWebView.getUrl());
				Launcher.mUrlEditText
						.addTextChangedListener(Launcher.mUrlTextWatcher);
			}

		} else {

			// 判断多窗体的数量
			if (Launcher.mTabspace.getChildCount() >= 2) {
				Launcher.mWebViewBitmaps.remove(position);
				Launcher.mWebViewStates.remove(position);

				WebViewspace mmWebViewspace = (WebViewspace) Launcher.mTabspace
						.getChildAt(position);
				View mchildView = mmWebViewspace.getChildAt(0);
				if (mchildView != null) {
					CustomWebView mmCustomWebView = (CustomWebView) mchildView
							.findViewById(R.id.webview);
					mmCustomWebView.loadUrl("");
					mmCustomWebView.stopLoading();
				}
				Launcher.mTabspace.removeViewAt(position);

				HashMap<String, Object> hashmap = Launcher.mWebViewStates
						.get(Launcher.tabCurrentItem);

				if (Launcher.CURRENT_SCREEN_WORKSPACE_NOT == Integer
						.parseInt((hashmap.get("isHome").toString()))) {
					Launcher.mHomespace
							.setCurrentScreen(Launcher.CURRENT_SCREEN_TABSPACE);
				} else {
					if (Launcher.CURRENT_SCREEN_WORKSPACE_LEFT == Integer
							.parseInt((hashmap.get("isHome").toString()))) {
						Launcher.mWorkspace
								.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_LEFT);
					} else if (Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE == Integer
							.parseInt((hashmap.get("isHome").toString()))) {
						Launcher.mWorkspace
								.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_MIDDLE);
					} else if (Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT == Integer
							.parseInt((hashmap.get("isHome").toString()))) {
						Launcher.mWorkspace
								.moveToDefaultScreen(Launcher.CURRENT_SCREEN_WORKSPACE_RIGHT);
					}
					Launcher.mHomespace
							.setCurrentScreen(Launcher.CURRENT_SCREEN_WORKSPACE);
					Launcher.mUrlEditText
							.removeTextChangedListener(Launcher.mUrlTextWatcher);
					Launcher.mUrlEditText.setText("");
					Launcher.mUrlEditText
							.addTextChangedListener(Launcher.mUrlTextWatcher);
					Launcher.urlText.setText("");
				}
				if (false == (Boolean) hashmap.get("isWebview")) {
					Launcher.mTabspace.setVisibility(View.GONE);
				} else {
					Launcher.mTabspace
							.setChildVisibility(Launcher.tabCurrentItem);
					Launcher.mTabspace.setVisibility(View.VISIBLE);
					WebViewspace mWebViewspace = (WebViewspace) Launcher.mTabspace
							.getChildAt(Launcher.tabCurrentItem);
					View childView = mWebViewspace.getChildAt(0);
					CustomWebView mCustomWebView = (CustomWebView) childView
							.findViewById(R.id.webview);
					Launcher.urlText.setText(mCustomWebView.getTitle());
					Launcher.mUrlEditText
							.removeTextChangedListener(Launcher.mUrlTextWatcher);
					Launcher.mUrlEditText.setText(mCustomWebView.getUrl());
					Launcher.mUrlEditText
							.addTextChangedListener(Launcher.mUrlTextWatcher);
				}

			} else {
				if (Launcher.mCurrentIndex != 0) {
					Launcher.mCurrentIndex--;
				}

				WebViewspace mmWebViewspace = (WebViewspace) Launcher.mTabspace
						.getChildAt(position);
				View mchildView = mmWebViewspace.getChildAt(0);
				if (mchildView != null) {
					CustomWebView mmCustomWebView = (CustomWebView) mchildView
							.findViewById(R.id.webview);
					mmCustomWebView.loadUrl("");
					mmCustomWebView.stopLoading();
				}
				HashMap<String, Object> hashmap = new HashMap<String, Object>();

				// 存储当前tab显示状态 workspace false tabspace true
				HashMap<String, Object> bitmaphashmap = new HashMap<String, Object>();

				Bitmap bitmap = getViewBitmap(Launcher.mHomespace);
				bitmaphashmap.put("bitmap", bitmap);
				hashmap.put("isWebview", false);
				hashmap.put("isHome", Launcher.mWorkspace.getCurrentScreen());

				bitmaphashmap.put("title", "主页");
				if (Launcher.mWebViewBitmaps.size() > Launcher.mCurrentIndex) {
					Launcher.mWebViewBitmaps.set(Launcher.mCurrentIndex,
							bitmaphashmap);
				} else {
					Launcher.mWebViewBitmaps.add(Launcher.mCurrentIndex,
							bitmaphashmap);
				}
				if (Launcher.mWebViewStates.size() > Launcher.mCurrentIndex) {
					Launcher.mWebViewStates
							.set(Launcher.mCurrentIndex, hashmap);
				} else {
					Launcher.mWebViewStates
							.add(Launcher.mCurrentIndex, hashmap);
				}

				Launcher.mTabspace.setVisibility(View.GONE);

				Launcher.mUrlEditText
						.removeTextChangedListener(Launcher.mUrlTextWatcher);
				Launcher.mUrlEditText.setText("");
				Launcher.mUrlEditText
						.addTextChangedListener(Launcher.mUrlTextWatcher);
				Launcher.urlText.setText("");

				Launcher.mHomespace
						.setCurrentScreen(Launcher.CURRENT_SCREEN_WORKSPACE);

				Launcher.tabs_gallery.setVisibility(View.GONE);
				Launcher.tabs_gallery_Flag = false;
				Launcher.mCurrentIndex = position;
			}

		}

		mListAdapter.notifyDataSetChanged();
		Launcher.updateRecentsBottomTabsNum();

		if (Launcher.tabCurrentItem == 0) {
			return;
		}

	}

	public Bitmap getViewBitmap(View view) {
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = null;
		try {
			if (null != view.getDrawingCache()) {
				Launcher.mWidth = Launcher.mHomespace.getWidth();
				Launcher.mHeight = Launcher.mHomespace.getHeight();
				bitmap = Bitmap.createScaledBitmap(view.getDrawingCache(),
						Launcher.mWidth, Launcher.mHeight, false);
			} else {
				bitmap = ((BitmapDrawable) (getResources()
						.getDrawable(R.drawable.welcome))).getBitmap();
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			view.setDrawingCacheEnabled(false);
			view.destroyDrawingCache();
		}

		return bitmap;
	}
}
