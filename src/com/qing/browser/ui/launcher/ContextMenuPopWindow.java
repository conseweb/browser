package com.qing.browser.ui.launcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.qing.browser.R;

public class ContextMenuPopWindow extends PopupWindow {

	private LayoutInflater contextMenuPopWindowInflater;
	private View contextMenuPopWindowInflaterView;
	private Context context;

	public ContextMenuPopWindow(Context context) {
		super(context);
		this.context = context;

		// 创建
		this.initTab();
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);

		setContentView(this.contextMenuPopWindowInflaterView);
		setOutsideTouchable(true);
		setFocusable(true);
	}

	// 实例化
	private void initTab() {
		this.contextMenuPopWindowInflater = LayoutInflater.from(this.context);
		this.contextMenuPopWindowInflaterView = this.contextMenuPopWindowInflater
				.inflate(R.layout.webview_longclick_window, null);
	}

	public View getView(int id) {
		return this.contextMenuPopWindowInflaterView.findViewById(id);
	}
}