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

		// ����title����Ϊ5�ַ�
		String title = shortcutInfo.title.toString();
		int length = title.length();
		if (length > 6)
			title = title.substring(0, 5);
		icon.setText(title);
		icon.setTag(shortcutInfo);
		icon.setOnClickListener(launcher);
		icon.mInfo = shortcutInfo;
		icon.mLauncher = launcher;

		icon.updateShortcutIcon(); // ����ͼ�� 
		 
		shortcutInfo.setShortcutIcon(icon);
		return icon;
	}

	
	public void updateShortcutIcon() {
		Bitmap closebmp = mInfo.getIcon(); // ��ȡ����ͼ��
		final Resources resources = mLauncher.getResources();
		  
		if(null == closebmp){ 
			closebmp = BitmapFactory.decodeResource(resources,
				R.drawable.hotseat_browser_bg); // ��ȡFolderIcon�ر�ʱ�ı���ͼ
		}
		int iconWidth = closebmp.getWidth(); // icon�Ŀ��
		int iconHeight = closebmp.getHeight();
		Bitmap folderclose = Bitmap.createBitmap(iconWidth, iconHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(folderclose);
		canvas.drawBitmap(closebmp, 0, 0, null); // ���Ʊ���
		mCloseIcon = new FastBitmapDrawable(folderclose); // ��bitmapת��ΪDrawable
		setCompoundDrawablesWithIntrinsicBounds(null, mCloseIcon, null, null);

		  
		Matrix matrix = new Matrix(); // ��������ͼƬ�õ�Matrix����
		float scaleWidth = iconWidth - 10;// ��������ͼ�Ŀ�(�������ͬ)
		float scale = (scaleWidth / iconWidth); // �������ű���
		matrix.postScale(scale, scale); // �������ű���
		Bitmap openbmp = BitmapFactory.decodeResource(resources,
					R.drawable.ic_launcher_shortcut_open); // ��ȡshortcut��ʱ�ı���ͼ
		int mOpeniconWidth = openbmp.getWidth(); // icon�Ŀ��
		int mOpeniconHeight = openbmp.getHeight();
		Bitmap folderopen = Bitmap.createBitmap(mOpeniconWidth, mOpeniconHeight,
				Bitmap.Config.ARGB_8888); 
		canvas = new Canvas(folderopen);
		canvas.drawBitmap(openbmp, 0, 0, null);
		 
		// ����ͼƬ���ŵ����� 
		int x = (iconWidth - (int)scaleWidth) /2 ; 
		Bitmap scalebmp = Bitmap.createBitmap(closebmp, 0, 0, iconWidth,
				iconHeight, matrix, true);
		canvas.drawBitmap(scalebmp, x, x, null);
		
		mOpenIcon = new FastBitmapDrawable(folderopen); // ����openͼƬ
	
	
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
		
		//TODO �����ļ���
		int shortId = mLauncher.addFolder(item.screen, mInfo.itemIndex); 
	
		
		LauncherModel.addOrMoveItemInDatabase(mLauncher, mInfo,
				shortId, item.screen, mInfo.itemIndex);
		LauncherModel.addOrMoveItemInDatabase(mLauncher,item, 
				shortId, item.screen, item.itemIndex); 
		
		updateShortcutIcon(); // ��ק����ʱ����
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
