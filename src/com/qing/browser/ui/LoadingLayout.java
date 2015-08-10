package com.qing.browser.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qing.browser.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadingLayout extends FrameLayout {

	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private final ImageView headerImage, loadingImage;
//	private final ProgressBar headerProgress;
	private final TextView headerText, refresh_time, last_refresh;

	private String pullLabel;//��������ˢ��
	private String refreshingLabel;//���ڼ���
	private String releaseLabel;//�ɿ�����ˢ��
	private AnimationDrawable anim = null;
	private final Animation rotateAnimation, resetRotateAnimation;

	public LoadingLayout(Context context, final int mode, String releaseLabel, String pullLabel, String refreshingLabel) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this);
		headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		headerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
//		headerProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);
		loadingImage = (ImageView)header.findViewById(R.id.pull_to_refresh_loading_progressBar);
		refresh_time = (TextView) header.findViewById(R.id.refresh_time);
		last_refresh = (TextView) header.findViewById(R.id.last_refresh);

		

		refresh_time.setVisibility(View.GONE);
		last_refresh.setVisibility(View.GONE);
		
		
		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		        0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
		        Animation.RELATIVE_TO_SELF, 0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;
		this.refreshingLabel = refreshingLabel;

		switch (mode) {
			case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
				headerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
				last_refresh.setVisibility(View.GONE);
				refresh_time.setVisibility(View.GONE);
				break;
			case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
			default:
				headerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
				break;
		}
	}

	/**
	 * ��������ˢ��
	 */
	public void reset() {
		headerText.setText(pullLabel);
		headerImage.setVisibility(View.VISIBLE);
//		headerProgress.setVisibility(View.GONE);
		loadingImage.setVisibility(View.GONE);//
		if (anim != null){
			anim.stop();
		}
	}
	
	/**
	 * �ɿ�����ˢ��
	 */
	public void releaseToRefresh() {
		headerText.setText(releaseLabel);
		headerImage.clearAnimation();
		headerImage.startAnimation(rotateAnimation);
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
	}

	
	/**
	 * ���ڼ���
	 */
	public void refreshing() {
		headerText.setText(refreshingLabel);
		headerImage.clearAnimation();
		headerImage.setVisibility(View.INVISIBLE);
//		headerProgress.setVisibility(View.GONE);//VISIBLE
		loadingImage.setVisibility(View.VISIBLE);//
		if (loadingImage != null) {
			Object ob = loadingImage.getBackground();
			anim = (AnimationDrawable) ob;
			anim.start();
		}
	}

	public void setRefreshingLabel(String refreshingLabel) {
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		headerText.setText(pullLabel);//��������ˢ��
		headerImage.clearAnimation();
		headerImage.startAnimation(resetRotateAnimation);
	}

	public void setTextColor(int color) {
		headerText.setTextColor(color);
	}

}
