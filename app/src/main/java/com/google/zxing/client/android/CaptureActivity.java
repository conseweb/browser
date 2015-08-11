package com.google.zxing.client.android;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.client.android.result.URIResultHandler;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.common.HybridBinarizer;
import com.qing.browser.R;
import com.qing.browser.ui.launcher.Launcher;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The barcode reader activity itself. This is loosely based on the CameraPreview
 * example included in the Android SDK.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback , ViewFactory {

  private static final String TAG = CaptureActivity.class.getSimpleName();

  private static final int SHARE_ID = Menu.FIRST;
  private static final int HISTORY_ID = Menu.FIRST + 1;
  private static final int SETTINGS_ID = Menu.FIRST + 2;
  private static final int HELP_ID = Menu.FIRST + 3;
  private static final int ABOUT_ID = Menu.FIRST + 4;

  private static final long INTENT_RESULT_DURATION = 1500L;
  private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;
  private static final float BEEP_VOLUME = 0.10f;
  private static final long VIBRATE_DURATION = 200L;

  private static final String PACKAGE_NAME = "com.google.zxing.client.android";
  private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";
  private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";
  private static final String ZXING_URL = "http://zxing.appspot.com/scan";
  private static final String RETURN_CODE_PLACEHOLDER = "{CODE}";
  private static final String RETURN_URL_PARAM = "ret";

  public static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;
  static {
    DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>(5);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
    DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
  }

  private enum Source {
    NATIVE_APP_INTENT,
    PRODUCT_SEARCH_LINK,
    ZXING_LINK,
    NONE
  }

  private CaptureActivityHandler handler;

  private ViewfinderView viewfinderView;
  private TextView statusView;
  private MediaPlayer mediaPlayer;
  private Result lastResult;
  private boolean hasSurface;
  private boolean playBeep;
  private boolean vibrate;
  private boolean copyToClipboard;
  private Source source;
  private String sourceUrl;
  private String returnUrlTemplate;
  private Vector<BarcodeFormat> decodeFormats;
  private String characterSet;
  private String versionName;
  private HistoryManager historyManager;
  private InactivityTimer inactivityTimer;

  private TextView textView =null;
  private TextView title_search= null;
  private TextView saoma =null;
  private TextView xiangce =null;
  private TextView wode =null;
  
  private PopupWindow SearWindow;
  private PopupWindow popupWindow;
  
  private static final int IMAGE_REQUEST_CODE = 0;
  private static final int CAMERA_REQUEST_CODE = 1;
  private static final int RESULT_REQUEST_CODE = 2;
  private Bundle bundle = null;
  
  public static Context context ; 
  
  /**
   * When the beep has finished playing, rewind to queue up another one.
   */
  private final OnCompletionListener beepListener = new OnCompletionListener() {
    public void onCompletion(MediaPlayer mediaPlayer) {
      mediaPlayer.seekTo(0);
    }
  };

  private final DialogInterface.OnClickListener aboutListener =
      new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialogInterface, int i) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.zxing_url)));
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      startActivity(intent);
    }
  };

  ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  public Handler getHandler() {
    return handler;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.capture);

    RelativeLayout layout_title_bar = (RelativeLayout)findViewById(R.id.layout_title_bar);
	layout_title_bar.setVisibility(View.GONE);
    textView =(TextView)findViewById(R.id.item_title);
	textView.setText("扫一扫");
	title_search = (TextView)findViewById(R.id.title_search);
	title_search.setText("更多");
	title_search.setVisibility(View.VISIBLE);
	title_search.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			showSearWindow();
		}
	});
	ImageView item_back = (ImageView)findViewById(R.id.item_back);
	item_back.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			finish();	
		}});
    
	saoma = (TextView)findViewById(R.id.saoma);
	xiangce = (TextView)findViewById(R.id.xiangce);
	wode = (TextView)findViewById(R.id.wode);
	xiangce.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
			Intent intent = new Intent(CaptureActivity.this,
					CaptureActivity.class);
			intent.putExtra("String", "xiangche");
			startActivity(intent);
		}
	});
	wode.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(CaptureActivity.this, ErWeiMaListActivity.class));
		}
	});
	
	context = this;
	
    CameraManager.init(getApplication());
    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    statusView = (TextView) findViewById(R.id.status_view);
    handler = null;
    lastResult = null;
    hasSurface = false;
    historyManager = new HistoryManager(this);
    historyManager.trimHistory();
    inactivityTimer = new InactivityTimer(this);
    
    bundle = getIntent().getExtras();
	if (bundle != null) {
		if (this.getIntent().hasExtra("String")) {
			if (bundle.getString("String").equals("xiangche")) {
				Intent image = new Intent();
				image.setType("image/*");
				image.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(image, IMAGE_REQUEST_CODE);
			}
			
		}
	}

//    showHelpOnFirstLaunch();
  }

  @Override
  protected void onResume() {
    super.onResume();
    
    if (bundle != null) {
		if (this.getIntent().hasExtra("String")) {
			if (bundle.getString("String").equals("xiangche")) {
				//return ;
			}
		}
	}
    resetStatusView();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder);
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this);
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    Intent intent = getIntent();
    String action = intent == null ? null : intent.getAction();
    String dataString = intent == null ? null : intent.getDataString();
    if (intent != null && action != null) {
      if (action.equals(Intents.Scan.ACTION)) {
        // Scan the formats the intent requested, and return the result to the calling activity.
        source = Source.NATIVE_APP_INTENT;
        decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
      } else if (dataString != null && dataString.contains(PRODUCT_SEARCH_URL_PREFIX) &&
          dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {
        // Scan only products and send the result to mobile Product Search.
        source = Source.PRODUCT_SEARCH_LINK;
        sourceUrl = dataString;
        decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;
      } else if (dataString != null && dataString.startsWith(ZXING_URL)) {
        // Scan formats requested in query string (all formats if none specified).
        // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
        source = Source.ZXING_LINK;
        sourceUrl = dataString;
        Uri inputUri = Uri.parse(sourceUrl);
        returnUrlTemplate = inputUri.getQueryParameter(RETURN_URL_PARAM);
        decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
      } else {
        // Scan all formats and handle the results ourselves (launched from Home).
        source = Source.NONE;
        decodeFormats = null;
      }
      characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
    } else {
      source = Source.NONE;
      decodeFormats = null;
      characterSet = null;
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    playBeep = prefs.getBoolean(PreferencesActivity.KEY_PLAY_BEEP, true);
    if (playBeep) {
      // See if sound settings overrides this
      AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
      if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
        playBeep = false;
      }
    }
    vibrate = prefs.getBoolean(PreferencesActivity.KEY_VIBRATE, false);
    copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true);
    initBeepSound();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    
    CameraManager.get().closeDriver();
  }

  @Override
  protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (source == Source.NATIVE_APP_INTENT) {
        setResult(RESULT_CANCELED);
        finish();
        return true;
      } else if ((source == Source.NONE || source == Source.ZXING_LINK) && lastResult != null) {
        resetStatusView();
        if (handler != null) {
          handler.sendEmptyMessage(R.id.restart_preview);
        }
        return true;
      }
    } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
      // Handle these events so they don't launch the Camera app
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  // TODO 去除帮助等菜单。  有需求可以加上  LINSHUO
/*  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
   menu.add(0, SHARE_ID, 0, R.string.menu_share)
        .setIcon(android.R.drawable.ic_menu_share);
    menu.add(0, HISTORY_ID, 0, R.string.menu_history)
        .setIcon(android.R.drawable.ic_menu_recent_history);
    menu.add(0, SETTINGS_ID, 0, R.string.menu_settings)
        .setIcon(android.R.drawable.ic_menu_preferences);
    menu.add(0, HELP_ID, 0, R.string.menu_help)
        .setIcon(android.R.drawable.ic_menu_help);
    menu.add(0, ABOUT_ID, 0, R.string.menu_about)
        .setIcon(android.R.drawable.ic_menu_info_details);
    return true;
  }

  // Don't display the share menu item if the result overlay is showing.
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(SHARE_ID).setVisible(lastResult == null);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case SHARE_ID: {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClassName(this, ShareActivity.class.getName());
        startActivity(intent);
        break;
      }
      case HISTORY_ID: {
        AlertDialog historyAlert = historyManager.buildAlert();
        historyAlert.show();
        break;
      }
      case SETTINGS_ID: {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClassName(this, PreferencesActivity.class.getName());
        startActivity(intent);
        break;
      }
      case HELP_ID: {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClassName(this, HelpActivity.class.getName());
        startActivity(intent);
        break;
      }
      case ABOUT_ID:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_about) + versionName);
        builder.setMessage(getString(R.string.msg_about) + "\n\n" + getString(R.string.zxing_url));
        builder.setIcon(R.drawable.launcher_icon);
        builder.setPositiveButton(R.string.button_open_browser, aboutListener);
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
*/
  @Override
  public void onConfigurationChanged(Configuration config) {
    // Do nothing, this is to prevent the activity from being restarted when the keyboard opens.
    super.onConfigurationChanged(config);
  }

  public void surfaceCreated(SurfaceHolder holder) {
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder);
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
  public void handleDecode(Result rawResult, Bitmap barcode) {
    inactivityTimer.onActivity();
    lastResult = rawResult;
    historyManager.addHistoryItem(rawResult);
    if (barcode == null) {
      // This is from history -- no saved barcode
      handleDecodeInternally(rawResult, null);
    } else {
      playBeepSoundAndVibrate();
      drawResultPoints(barcode, rawResult);
      switch (source) {
        case NATIVE_APP_INTENT:
        case PRODUCT_SEARCH_LINK:
          handleDecodeExternally(rawResult, barcode);
          break;
        case ZXING_LINK:
          if (returnUrlTemplate == null){
            handleDecodeInternally(rawResult, barcode);
          } else {
            handleDecodeExternally(rawResult, barcode);
          }
          break;
        case NONE:
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
          if (prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
            Toast.makeText(this, R.string.msg_bulk_mode_scanned, Toast.LENGTH_SHORT).show();
            // Wait a moment or else it will scan the same barcode continuously about 3 times
            if (handler != null) {
              handler.sendEmptyMessageDelayed(R.id.restart_preview, BULK_MODE_SCAN_DELAY_MS);
            }
            resetStatusView();
          } else {
            handleDecodeInternally(rawResult, barcode);
          }
          break;
      }
    }
  }

  /**
   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
   *
   * @param barcode   A bitmap of the captured image.
   * @param rawResult The decoded results which contains the points to draw.
   */
  private void drawResultPoints(Bitmap barcode, Result rawResult) {
    ResultPoint[] points = rawResult.getResultPoints();
    if (points != null && points.length > 0) {
      Canvas canvas = new Canvas(barcode);
      Paint paint = new Paint();
      paint.setColor(getResources().getColor(R.color.result_image_border));
      paint.setStrokeWidth(3.0f);
      paint.setStyle(Paint.Style.STROKE);
      Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
      canvas.drawRect(border, paint);

      paint.setColor(getResources().getColor(R.color.result_points));
      if (points.length == 2) {
        paint.setStrokeWidth(4.0f);
        drawLine(canvas, paint, points[0], points[1]);
      } else if (points.length == 4 &&
                 (rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A)) ||
                 (rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
        // Hacky special case -- draw two lines, for the barcode and metadata
        drawLine(canvas, paint, points[0], points[1]);
        drawLine(canvas, paint, points[2], points[3]);
      } else {
        paint.setStrokeWidth(10.0f);
        for (ResultPoint point : points) {
          canvas.drawPoint(point.getX(), point.getY(), paint);
        }
      }
    }
  }

  private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
    canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
  }

  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(Result rawResult, Bitmap barcode) {
    
    if(Tools.sdCardExist()){
		writeJSONObjectToSdCard(createJSONObject(rawResult));  
    }
    
    if (bundle != null) {
		if (this.getIntent().hasExtra("String")) {
			if(bundle.getString("String").equals("dingbusousuo")){
				ParsedResult result = ResultParser.parseResult(rawResult);
			    ParsedResultType type = result.getType();
			    if (type.equals(ParsedResultType.URI)) {
			    	if(Launcher.top_bar_inpu_ErWeiMa!=null){
			    		Launcher.top_bar_inpu_ErWeiMa.setVisibility(View.GONE);
			    	}
			    	if(Launcher.top_bar_mai_ErWeiMa!=null){
			    		Launcher.top_bar_mai_ErWeiMa.setVisibility(View.VISIBLE);
			    	}
			    	if(Launcher.mHomespac_ErWeiMa!=null){
			    		Launcher.mHomespac_ErWeiMa.setVisibility(View.VISIBLE);
			    	}
			    	Launcher.top_bar_input_Flag = false;
			    	
			        Launcher.mLauncher_ErWeiMa.addTab(result.getDisplayResult());
			        finish();
			        return;
			    }
			}/*else if(bundle.getString("String").equals("xiangche")){
				finish();
			}*/
		}
	}
    
    
    ErWeiMaJieGuoActivity.SetResult(rawResult);
    ErWeiMaJieGuoActivity.Setbarcode(barcode);
    Intent intent = new Intent(CaptureActivity.this,
			ErWeiMaJieGuoActivity.class);
	startActivity(intent);	

  }

  // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
  private void handleDecodeExternally(Result rawResult, Bitmap barcode) {
    viewfinderView.drawResultBitmap(barcode);

    // Since this message will only be shown for a second, just tell the user what kind of
    // barcode was found (e.g. contact info) rather than the full contents, which they won't
    // have time to read.
    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
    statusView.setText(getString(resultHandler.getDisplayTitle()));

    if (copyToClipboard) {
      ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
      clipboard.setText(resultHandler.getDisplayContents());
    }

    if (source == Source.NATIVE_APP_INTENT) {
      // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
      // the deprecated intent is retired.
      Intent intent = new Intent(getIntent().getAction());
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
      intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
      Message message = Message.obtain(handler, R.id.return_scan_result);
      message.obj = intent;
      handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
    } else if (source == Source.PRODUCT_SEARCH_LINK) {
      // Reformulate the URL which triggered us into a query, so that the request goes to the same
      // TLD as the scan URL.
      Message message = Message.obtain(handler, R.id.launch_product_query);
      int end = sourceUrl.lastIndexOf("/scan");
      message.obj = sourceUrl.substring(0, end) + "?q=" +
          resultHandler.getDisplayContents().toString() + "&source=zxing";
      handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
    } else if (source == Source.ZXING_LINK) {
      // Replace each occurrence of RETURN_CODE_PLACEHOLDER in the returnUrlTemplate
      // with the scanned code. This allows both queries and REST-style URLs to work.
      Message message = Message.obtain(handler, R.id.launch_product_query);
      message.obj = returnUrlTemplate.replace(RETURN_CODE_PLACEHOLDER,
          resultHandler.getDisplayContents().toString());
      handler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
    }
  }

  /**
   * We want the help screen to be shown automatically the first time a new version of the app is
   * run. The easiest way to do this is to check android:versionCode from the manifest, and compare
   * it to a value stored as a preference.
   */
  private boolean showHelpOnFirstLaunch() {
    try {
      PackageInfo info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
      int currentVersion = info.versionCode;
      // Since we're paying to talk to the PackageManager anyway, it makes sense to cache the app
      // version name here for display in the about box later.
      this.versionName = info.versionName;
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      int lastVersion = prefs.getInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN, 0);
      if (currentVersion > lastVersion) {
        prefs.edit().putInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN, currentVersion).commit();
        Intent intent = new Intent(this, HelpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Show the default page on a clean install, and the what's new page on an upgrade.
        String page = (lastVersion == 0) ? HelpActivity.DEFAULT_PAGE : HelpActivity.WHATS_NEW_PAGE;
        intent.putExtra(HelpActivity.REQUESTED_PAGE_KEY, page);
        startActivity(intent);
        return true;
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.w(TAG, e);
    }
    return false;
  }

  /**
   * Creates the beep MediaPlayer in advance so that the sound can be triggered with the least
   * latency possible.
   */
  private void initBeepSound() {
    if (playBeep && mediaPlayer == null) {
      // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
      // so we now play on the music stream.
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setOnCompletionListener(beepListener);

      AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
      try {
        mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
            file.getLength());
        file.close();
        mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
        mediaPlayer.prepare();
      } catch (IOException e) {
        mediaPlayer = null;
      }
    }
  }

  private void playBeepSoundAndVibrate() {
    if (playBeep && mediaPlayer != null) {
      mediaPlayer.start();
    }
    if (vibrate) {
      Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    try {
      CameraManager.get().openDriver(surfaceHolder);
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
      displayFrameworkBugMessageAndExit();
      return;
    } catch (RuntimeException e) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializating camera", e);
      displayFrameworkBugMessageAndExit();
      return;
    }
    if (handler == null) {
      handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
    }
  }

  private void displayFrameworkBugMessageAndExit() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.app_name));
    builder.setMessage(getString(R.string.msg_camera_framework_bug));
    builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
    builder.setOnCancelListener(new FinishListener(this));
    builder.show();
  }

  private void resetStatusView() {
    statusView.setText(R.string.msg_default_status);
    statusView.setVisibility(View.GONE);    //这里改动之后 显示提示
    viewfinderView.setVisibility(View.VISIBLE);
    lastResult = null;
  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();
  }
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  
	  	String imageFilePath = null;
	  	
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				try {
					Uri uri = data.getData();
								
					Cursor cursor = getContentResolver().query(uri, null, null,
							null, null);
					if(cursor == null){
						imageFilePath=uri.toString().replace("file://", "");
						if(imageFilePath==null){
							Toast.makeText(CaptureActivity.this,"请用系统图片浏览器选择图片！",1000).show();
							//finish();
							break;
						}
					}else{
						cursor.moveToFirst();
						imageFilePath = cursor.getString(1);
					}
					

					FileInputStream fis = new FileInputStream(imageFilePath);
					Bitmap bitmap = BitmapFactory.decodeStream(fis);
					
						if(bitmap.getWidth()>1200||bitmap.getHeight()>1200){
							startPhotoZoom(uri);
						}else{		
							if (null != bitmap) {
								BinaryBitmap localBinaryBitmap = new BinaryBitmap(
										new HybridBinarizer(new RGBLuminanceSource(
												bitmap)));
								try {	
									Result rawResult = new MultiFormatReader()
											.decode(localBinaryBitmap);
									
									handleDecodeInternally(rawResult,bitmap);
								} catch (com.google.zxing.NotFoundException e) {
									fis.close();
									if(cursor!=null)
									cursor.close();
									Toast.makeText(CaptureActivity.this,"图片未扫描成功",1000).show();
									//finish();
									//e.printStackTrace();
								}
							}
						}
					fis.close();
					if(cursor!=null)
						cursor.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case RESULT_REQUEST_CODE:
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap bitmap = extras.getParcelable("data");
						if(bitmap.getWidth()>1200||bitmap.getHeight()>1200){
							startPhotoZoom(data.getData());
						}else{		
							if (null != bitmap) {
								BinaryBitmap localBinaryBitmap = new BinaryBitmap(
										new HybridBinarizer(new RGBLuminanceSource(
												bitmap)));
								try {	
									Result rawResult = new MultiFormatReader()
											.decode(localBinaryBitmap);
									
									handleDecodeInternally(rawResult,bitmap);
								} catch (com.google.zxing.NotFoundException e) {
									Toast.makeText(CaptureActivity.this,"图片未扫描成功",1000).show();
									//finish();
									//e.printStackTrace();
								}
							}
						}
					}
				break;
			}
		}else {
			//finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
  
  
/**
 *  创建以下的JSON对象   
 * @return
 */
	private JSONObject createJSONObject(Result str) {  
	      // 最外层是｛｝，也就是一个JSONObject对象  
	  JSONObject person = new JSONObject();  
	  try {
		  DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		  String formattedTime = formatter.format(new Date(str.getTimestamp()));
		  person.put("time", "20"+formattedTime);
	      person.put("neirong", str.getText());   
	      } catch (JSONException e) {  
	          e.printStackTrace();  
	      }  
	      return person;  
	  }  


	private void writeJSONObjectToSdCard(JSONObject person) { 
		RandomAccessFile raf = null;
		File file = null;
		try {
			
			File dirFile1 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/");
			if (!dirFile1.exists()) {
				dirFile1.mkdir();
			}
			File dirFile2 = new File(ErWeiMaChaKanActivity.getSDPath() + "/Qing/erweima/");
			if (!dirFile2.exists()) {
				dirFile2.mkdir();
			}
			
			file = new File(ErWeiMaChaKanActivity.getSDPath()+"/Qing/erweima/erweimajilu.txt");
			raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write((","+person.toString()).getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (raf != null)
					raf.close();
				raf = null;
				file = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public View makeView() {
		ImageView iv = new ImageView(this);
		iv.setBackgroundColor(0xFF000000);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iv.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return iv;
	}
	
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 350);
		intent.putExtra("outputY", 350);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}
	
	/**
	 * 显示搜索引擎弹框
	 */
	private void showSearWindow() {

		if (SearWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.top_bar_search_pop,
					null);
			GridView search_engine_pop_gridview = (GridView) view
					.findViewById(R.id.search_engine_pop_gridview);

			String[] itemSub = { "从相册选择", "我的二维码"};
			final int[] imageSub = { R.drawable.erweima_xiangce,R.drawable.erweima_wode};

			ArrayList<HashMap<String, Object>> Itemload = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < imageSub.length; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ItemImage", imageSub[i]);
				map.put("ItemText", itemSub[i]);
				Itemload.add(map);
			}

			search_engine_pop_gridview
					.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if(arg2==0){
								if (Tools.sdCardExist()) {
									finish();
									Intent intent = new Intent(CaptureActivity.this,
											CaptureActivity.class);
									intent.putExtra("String", "xiangche");
									startActivity(intent);
									
								} else {
									Toast.makeText(CaptureActivity.this,"SD卡已卸载或不存在.", 1000).show();
								}
							}else if(arg2==1){
								startActivity(new Intent(CaptureActivity.this, ErWeiMaListActivity.class));
							}
							SearWindow.dismiss();
						}
					});

			SimpleAdapter SA = new SimpleAdapter(this, Itemload,
					R.layout.gridview_menu_item, new String[] { "ItemImage",
							"ItemText" }, new int[] { R.id.ItemImage,
							R.id.ItemText });

			search_engine_pop_gridview.setAdapter(SA);

			DisplayMetrics dm = new DisplayMetrics();
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			display.getMetrics(dm);
			// 创建一个PopuWidow对象
			SearWindow = new PopupWindow(view, 300, 220);
		}

		// 使其聚集
		SearWindow.setFocusable(true);
		// 设置允许在外点击消失
		SearWindow.setOutsideTouchable(true);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		SearWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置layout在PopupWindow中显示的位置

		SearWindow.showAsDropDown(title_search, 0, 0);

	}
}
