package com.qing.browser.ui.launcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.TextView;

import com.qing.browser.R;
import com.qing.browser.utils.Tools;

public class SendZone extends TextView implements DropTarget,
		DragController.DragListener {
	private Launcher mLauncher;
	private Workspace mWorkspace;
	private final Paint mTrashPaint = new Paint();

	public SendZone(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SendZone(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		final int srcColor = context.getResources().getColor(
				R.color.delete_color_filter);
		mTrashPaint.setColorFilter(new PorterDuffColorFilter(srcColor,
				PorterDuff.Mode.SRC_ATOP));
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
		final ItemInfo item = (ItemInfo) dragInfo;
		if (item instanceof ShortcutInfo) {
			final ShortcutInfo mShortcutInfo = (ShortcutInfo) item;
			
			if((mShortcutInfo.title.equals("天气")||mShortcutInfo.title.equals("发现")
					||mShortcutInfo.title.equals("扫一扫")||mShortcutInfo.title.equals("账户")
				)&&mShortcutInfo.url.equals("")){
				shortcutCreate_ZhiDing(mLauncher,mShortcutInfo,mShortcutInfo.title.toString());
				
			}else{
				Tools.shortcutCreate(mLauncher, mShortcutInfo.title.toString(),mShortcutInfo.getIcon(),
						mShortcutInfo.url.toString());
			}

			CellLayout child = (CellLayout) mWorkspace
					.getChildAt(mShortcutInfo.screen);
			child.animateGapRover();
			child.requestLayout();
		}
	}
	
	/**
	 * 创建指定四个快捷方式 
	 * 天气 发现 扫一扫 账户
	 */
	private static void shortcutCreate_ZhiDing(Context context,ShortcutInfo mShortcutInfo,String type) {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra("duplicate", false);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, mShortcutInfo.title);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, mShortcutInfo.getIcon());

		Intent intent = new Intent(context, Launcher.class);

		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		Uri uri = Uri.parse(type);
		intent.setData(uri);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 发送广播
		context.sendBroadcast(shortcut);

	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(R.drawable.zone_bg_hover);
		dragView.setPaint(mTrashPaint);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		setBackgroundResource(R.drawable.zone_bg);
		dragView.setPaint(null);
	}

	void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	public void setWorkspace(Workspace mWorkspace) {
		this.mWorkspace = mWorkspace;
	}

	@Override
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnd() {
		// TODO Auto-generated method stub
		
	}
}
