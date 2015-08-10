package com.qing.browser.ui.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qing.browser.R;

/**
 * An icon that can appear on in the workspace representing an
 * {@link UserFolder}.
 */
public class FolderIcon extends TextView implements DropTarget {

	private static final String TAG = "FolderIcon";

	private UserFolderInfo mInfo;
	private Launcher mLauncher;
	private Drawable mCloseIcon;
	private Drawable mOpenIcon;

	private static final int ICON_COUNT = 4; // 可显示的缩略图数
	private static final int NUM_COL = 2; // 每行显示的个数
	private static final int PADDING = 5; // 内边距
	private static final int MARGIN = 7; // 外边距

	public FolderIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FolderIcon(Context context) {
		super(context);
	}

	public Launcher getLauncher() {
		return mLauncher;
	}

	public void setLauncher(Launcher mLauncher) {
		this.mLauncher = mLauncher;
	}

	@Override
	public void setTag(Object tag) {
		// TODO Auto-generated method stub
		mInfo = (UserFolderInfo) tag;
		super.setTag(tag);
	}

	static FolderIcon fromXml(int resId, Launcher launcher, ViewGroup group,
			UserFolderInfo folderInfo) {

		FolderIcon icon = (FolderIcon) LayoutInflater.from(launcher).inflate(
				resId, group, false);

		// 处理title长度为5字符
		String title = folderInfo.title.toString();
		int length = title.length();
		if (length > 6)
			title = title.substring(0, 5);
		icon.setText(title);
		icon.setTag(folderInfo);
		icon.setOnClickListener(launcher);
		icon.mInfo = folderInfo;
		icon.mLauncher = launcher;

		icon.updateFolderIcon(); // 更新图标
		folderInfo.setFolderIcon(icon); // 设置FolderIcon
		return icon;
	}

	public void updateFolderIcon() {
		float x, y;
		final Resources resources = mLauncher.getResources();
		Bitmap closebmp = BitmapFactory.decodeResource(resources,
				R.drawable.ic_launcher_folder); // 获取FolderIcon关闭时的背景图
		Bitmap openbmp = BitmapFactory.decodeResource(resources,
				R.drawable.ic_launcher_folder_open); // 获取FolderIcon打开时的背景图

		int iconWidth = closebmp.getWidth(); // icon的宽度
		int iconHeight = closebmp.getHeight();
		Bitmap folderclose = Bitmap.createBitmap(iconWidth, iconHeight,
				Bitmap.Config.ARGB_8888);
		Bitmap folderopen = Bitmap.createBitmap(iconWidth, iconHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(folderclose);
		canvas.drawBitmap(closebmp, 0, 0, null); // 绘制背景
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = (iconWidth - MARGIN * 2) / NUM_COL - 2 * PADDING;// 计算缩略图的宽(高与宽相同)
		float scale = (scaleWidth / iconWidth); // 计算缩放比例
		matrix.postScale(scale, scale); // 设置缩放比例
		for (int i = 0; i < ICON_COUNT; i++) {
			if (i < mInfo.contents.size()) {
				x = MARGIN + PADDING * (2 * (i % NUM_COL) + 1) + scaleWidth
						* (i % NUM_COL);
				y = MARGIN + PADDING * (2 * (i / NUM_COL) + 1) + scaleWidth
						* (i / NUM_COL);
				ShortcutInfo scInfo = (ShortcutInfo) mInfo.contents.get(i);
				Bitmap iconbmp = scInfo.getIcon(); // 获取缩略图标
				// 处理图片缩放的问题
				int iconbmpW = iconbmp.getWidth();
				int iconbmpH = iconbmp.getHeight();

				Bitmap scalebmp = Bitmap.createBitmap(iconbmp, 0, 0, iconbmpW,
						iconbmpH, matrix, true);
				canvas.drawBitmap(scalebmp, x, y, null);
			}
		}
		mCloseIcon = new FastBitmapDrawable(folderclose); // 将bitmap转换为Drawable
		setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null, null);

		canvas = new Canvas(folderopen);
		canvas.drawBitmap(folderclose, 0, 0, null);
		canvas.drawBitmap(openbmp, 0, 0, null);
		matrix = new Matrix(); // 创建操作图片用的Matrix对象
		scaleWidth = (iconWidth - MARGIN * 2) / NUM_COL - 2 * PADDING; // 计算缩略图的宽(高与宽相同)
		scale = (scaleWidth / iconWidth); // 计算缩放比例
		matrix.postScale(scale, scale); // 设置缩放比例
		for (int i = 0; i < ICON_COUNT; i++) {
			if (i < mInfo.contents.size()) {
				x = MARGIN + PADDING * (2 * (i % NUM_COL) + 1) + scaleWidth
						* (i % NUM_COL);
				y = MARGIN + PADDING * (2 * (i / NUM_COL) + 1) + scaleWidth
						* (i / NUM_COL);
				ShortcutInfo scInfo = (ShortcutInfo) mInfo.contents.get(i);
				Bitmap iconbmp = scInfo.getIcon(); // 获取缩略图标
				// 处理图片缩放的问题
				int iconbmpW = iconbmp.getWidth();
				int iconbmpH = iconbmp.getHeight();

				Bitmap scalebmp = Bitmap.createBitmap(iconbmp, 0, 0, iconbmpW,
						iconbmpH, matrix, true);
				canvas.drawBitmap(scalebmp, x, y, null);
			}
		}
		mOpenIcon = new FastBitmapDrawable(folderopen); // 绘制open图片
	}

	// add by hmg25 for FolderIcon }
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		final ItemInfo item = (ItemInfo) dragInfo;
		final int itemType = item.itemType;
		boolean folderflag = true;
		
		if(CellLayout.FolderFlag!=0&&CellLayout.FolderFlag!=-1){
			folderflag = false;
		}
		
		return (itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT)
				&& item.container != mInfo.id&&folderflag;
	}

	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}

	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		ShortcutInfo item;
		item = (ShortcutInfo) dragInfo;
		mInfo.add(item);
		LauncherModel.addOrMoveItemInDatabase(mLauncher, item, mInfo.shortid,
				item.screen, item.itemIndex);
		updateFolderIcon(); // 拖拽放入时更新
	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		updateFolderIcon();
		setCompoundDrawablesWithIntrinsicBounds(null, mOpenIcon, null, null);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		updateFolderIcon();
		setCompoundDrawablesWithIntrinsicBounds(null, mOpenIcon, null, null);
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		updateFolderIcon();
		setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null, null);
	}

}
