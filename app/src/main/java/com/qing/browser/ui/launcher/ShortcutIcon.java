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
public class ShortcutIcon extends TextView implements DropTarget {

	private ShortcutInfo mInfo;
	private Launcher mLauncher;
	private Drawable mCloseIcon;
	private Drawable mOpenIcon; 

	public ShortcutIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShortcutIcon(Context context) {
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
		mInfo = (ShortcutInfo) tag;
		super.setTag(tag);
	}

	static ShortcutIcon fromXml(int resId, Launcher launcher, ViewGroup group,
			ShortcutInfo shortcutInfo) {

		ShortcutIcon icon = (ShortcutIcon) LayoutInflater.from(launcher).inflate(
				resId, group, false);

		// 处理title长度为5字符
		String title = shortcutInfo.title.toString();
		int length = title.length();
		if (length > 6)
			title = title.substring(0, 5);
		icon.setText(title);
		icon.setTag(shortcutInfo);
		icon.setOnClickListener(launcher);
		icon.mInfo = shortcutInfo;
		icon.mLauncher = launcher;

		icon.updateShortcutIcon(); // 更新图标 
		 
		shortcutInfo.setShortcutIcon(icon);
		return icon;
	}

	
	public void updateShortcutIcon() {
		Bitmap closebmp = mInfo.getIcon(); // 获取缩略图标
		final Resources resources = mLauncher.getResources();
		  
		if(null == closebmp){ 
			closebmp = BitmapFactory.decodeResource(resources,
				R.drawable.hotseat_browser_bg); // 获取FolderIcon关闭时的背景图
		}
		int iconWidth = closebmp.getWidth(); // icon的宽度
		int iconHeight = closebmp.getHeight();
		Bitmap folderclose = Bitmap.createBitmap(iconWidth, iconHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(folderclose);
		canvas.drawBitmap(closebmp, 0, 0, null); // 绘制背景
		mCloseIcon = new FastBitmapDrawable(folderclose); // 将bitmap转换为Drawable
		setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null, null);

		  
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = iconWidth - 10;// 计算缩略图的宽(高与宽相同)
		float scale = (scaleWidth / iconWidth); // 计算缩放比例
		matrix.postScale(scale, scale); // 设置缩放比例
		Bitmap openbmp = BitmapFactory.decodeResource(resources,
					R.drawable.ic_launcher_shortcut_open); // 获取shortcut打开时的背景图
		int mOpeniconWidth = openbmp.getWidth(); // icon的宽度
		int mOpeniconHeight = openbmp.getHeight();
		Bitmap folderopen = Bitmap.createBitmap(mOpeniconWidth, mOpeniconHeight,
				Bitmap.Config.ARGB_8888); 
		canvas = new Canvas(folderopen);
		canvas.drawBitmap(openbmp, 0, 0, null);
		 
		// 处理图片缩放的问题 
		int x = (iconWidth - (int)scaleWidth) /2 ; 
		Bitmap scalebmp = Bitmap.createBitmap(closebmp, 0, 0, iconWidth,
				iconHeight, matrix, true);
		canvas.drawBitmap(scalebmp, x, x, null);
		
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
		
		if(mInfo.title.equals(item.title)){
			return;
		}
		
		//TODO 创建文件夹
		int shortId = mLauncher.addFolder(item.screen, mInfo.itemIndex); 
	
		
		LauncherModel.addOrMoveItemInDatabase(mLauncher, mInfo,
				shortId, item.screen, mInfo.itemIndex);
		LauncherModel.addOrMoveItemInDatabase(mLauncher,item, 
				shortId, item.screen, item.itemIndex); 
		
		updateShortcutIcon(); // 拖拽放入时更新
	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		updateShortcutIcon();
		setCompoundDrawablesWithIntrinsicBounds(null, mOpenIcon, null, null);
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		updateShortcutIcon();
		setCompoundDrawablesWithIntrinsicBounds(null, mOpenIcon, null, null);
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		updateShortcutIcon();
		setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null, null);
	}

}
