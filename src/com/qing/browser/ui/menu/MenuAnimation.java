package com.qing.browser.ui.menu;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class MenuAnimation {

	Animation showMenu, hideMenu;

	public Animation show() {
		showMenu = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 1);
		showMenu.setDuration(100);
		return showMenu;
	}

	public Animation hide() {
		hideMenu = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 1);
		hideMenu.setDuration(100);
		return hideMenu;
	}

}