package com.qing.browser.ui.recent;

import android.view.View;

public interface RecentsCallback {
    static final int SWIPE_LEFT = 0;
    static final int SWIPE_RIGHT = 1;
    static final int SWIPE_UP = 2;
    static final int SWIPE_DOWN = 3;

    void handleOnClick(View selectedView, int position);
    void handleSwipe(View selectedView, int position);
    void handleShowBackground(boolean show);
    void dismiss();

    // TODO: find another way to get this info from RecentsPanelView
    boolean isRecentsVisible();
}
