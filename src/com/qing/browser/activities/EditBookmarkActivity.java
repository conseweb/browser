package com.qing.browser.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qing.browser.R;
import com.qing.browser.providers.BookmarksUtil;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.ui.launcher.LauncherModel;
import com.qing.browser.ui.launcher.LauncherSettings;
import com.qing.browser.ui.launcher.ShortcutInfo;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.Tools;
import com.qing.browser.utils.UrlUtils;

/**
 * Add / Edit bookmark activity.
 */
public class EditBookmarkActivity extends BaseActivity {
	private TextView dialog_title;
	private EditText mTitleEditText;
	private EditText mUrlEditText;
	private CheckBox mAddScreen;
	private Button mOkButton;
	private Button mCancelButton;
	private long mRowId = -1;
	public final static int EditBookmark_OK = 1005;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_bookmark_activity);
		dialog_title = (TextView) findViewById(R.id.dialog_title);
		mTitleEditText = (EditText) findViewById(R.id.EditBookmarkActivity_TitleValue);
		mUrlEditText = (EditText) findViewById(R.id.EditBookmarkActivity_UrlValue);
		mAddScreen = (CheckBox) findViewById(R.id.EditBookmarkActivity_addScreen);
		mOkButton = (Button) findViewById(R.id.EditBookmarkActivity_BtnOk);
		mCancelButton = (Button) findViewById(R.id.EditBookmarkActivity_BtnCancel);

		mOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加书签判断条件
				if ("".equals(mTitleEditText.getText().toString())) {
					Toast.makeText(EditBookmarkActivity.this, "忘记输入书签标题了",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (UrlUtils.CheckUrl(mUrlEditText.getText().toString())) {
					setAsBookmark();
				} else {
					Toast.makeText(EditBookmarkActivity.this, "当前网址不符合规范",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent result = new Intent();
				result.putExtra("add_ok", false);
				setResult(EditBookmark_OK, result);
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String title = extras.getString(Constants.EXTRA_ID_BOOKMARK_TITLE);
			if ((title != null) && (title.length() > 0)) {
				mTitleEditText.setText(title);
			}

			String url = extras.getString(Constants.EXTRA_ID_BOOKMARK_URL);
			if ((url != null) && (url.length() > 0)) {
				mUrlEditText.setText(url);
			} else {
				mUrlEditText.setText("http://");
			}

			mRowId = extras.getLong(Constants.EXTRA_ID_BOOKMARK_ID);

		}

		if (mRowId == -1) {
			dialog_title.setText("添加书签");
		} else {
			dialog_title.setText("编辑书签");
		}
	}

	/**
	 * Set the current title and url values as a bookmark, e.g. adding a record
	 * if necessary or set only the bookmark flag.
	 */
	private void setAsBookmark() {
		int lanucherid = 0;
		int bookmarkid = 0;

		if (mRowId != -1) {
			BookmarksUtil
					.deleteStockBookmark(EditBookmarkActivity.this, mRowId);
		}

		String snapshot = Tools.generateMD5(mUrlEditText.getText().toString());

		bookmarkid = BookmarksUtil.setAsBookmark(EditBookmarkActivity.this,
				mRowId, mTitleEditText.getText().toString(), mUrlEditText
						.getText().toString(), true, null, snapshot);

		if (mAddScreen.isChecked()) {
			int count = LauncherModel.QueryCount(this, 1, true);
			ShortcutInfo info = new ShortcutInfo();
			info.title = mTitleEditText.getText().toString();
			info.url = mUrlEditText.getText().toString();
			info.customIcon = true;
			info.itemIndex = count;
			info.screen = 1;
			info.container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
			info.iconType = LauncherSettings.Favorites.ICON_TYPE_RESOURCE_SNAPSHOT;
			info.iconResource = Tools.generateMD5(mUrlEditText.getText()
					.toString());

			Uri result = LauncherModel.addItemToDatabase(this, info, true);
			if (result != null) {
				// 获取item的id
				lanucherid = Integer.parseInt(result.getPathSegments().get(1));
			}
			Launcher.loadSnapShot(mUrlEditText.getText().toString(),
					lanucherid, bookmarkid);
		} else {
			// 获取添加数据库的id,用于获取快照时更新

			Launcher.loadSnapShot(mUrlEditText.getText().toString(), bookmarkid);

		}

		Intent result = new Intent();
		result.putExtra("add_ok", true);
		setResult(EditBookmark_OK, result);
		finish();
	}
}
