package com.qing.browser.ui.launcher;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.qing.browser.R;

public class DeleteZone extends TextView implements DropTarget,
		DragController.DragListener {
	private Launcher mLauncher;
	private final Paint mTrashPaint = new Paint();

	public DeleteZone(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DeleteZone(Context context, AttributeSet attrs, int defStyle) {
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

		if (item.container == -1)
			return;

		if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
			Log.i("DeleteZone", "DeleteZone");
		} else {
			if (source instanceof Folder) {
				final Folder userFolder = (Folder) source;
				final UserFolderInfo userFolderInfo = (UserFolderInfo) userFolder
						.getInfo();
				userFolderInfo.remove((ShortcutInfo) item);
			}
		}
		if (item instanceof UserFolderInfo) {
			final UserFolderInfo userFolderInfo = (UserFolderInfo) item;
			LauncherModel.deleteUserFolderContentsFromDatabase(mLauncher,
					userFolderInfo);
			mLauncher.removeFolder(userFolderInfo);
		}
		LauncherModel.deleteItemFromDatabase(mLauncher, item.id);
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

	@Override
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnd() {
		// TODO Auto-generated method stub
		
	}
}
