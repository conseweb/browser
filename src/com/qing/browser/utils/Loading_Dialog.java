package com.qing.browser.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.qing.browser.R;


public class Loading_Dialog extends AlertDialog {
	private Dialog Loaddialog = null;
	private View Loaddiloglayout= null;
	private ImageView LoadImage = null;
	private TextView LoadText = null;
	private AnimationDrawable anim = null;
	private static boolean STATE_RUNNING = true; 
	
	public Loading_Dialog(Context context) {
		super(context); 
		Loaddialog = new Dialog(context,R.style.waitdailog);  
		LayoutInflater inflater = getLayoutInflater();
		
		Loaddiloglayout = inflater.inflate(R.layout.loading_dialog_page,
				(ViewGroup) findViewById(R.id.loading_view));
			
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Loaddialog.addContentView(Loaddiloglayout, params);
		LoadImage = (ImageView)Loaddiloglayout.findViewById(R.id.loading_process_dialog_progressBar);
		LoadText = (TextView)Loaddiloglayout.findViewById(R.id.loading_text);
		Loaddialog.show(); 
		
	}
	
	public void Loading_colse(){
		Loaddialog.dismiss();
	}
	
	public void Loading_ZhuanDong(){ 
		new AlertThread(handler).start();	
	}
	
	
	public void Loading_SetFailImage(){
		LoadImage.setBackgroundResource(R.drawable.load_gif);
	}
	
	
	public void Loading_SetText(String str){
		LoadText.setText(str);
	}
	
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
			Object ob = LoadImage.getBackground();  
	        anim = (AnimationDrawable) ob;  
	        anim.start();
	        STATE_RUNNING = false;
         }  
	};

    private class AlertThread extends Thread {  
       Handler mHandler;  
       AlertThread(Handler h) {  
           mHandler = h;  
       }  
       public void run() {   
           Log.v("L","run()   ");
           STATE_RUNNING =true;
           if(STATE_RUNNING) {  
               try {  
                   Thread.sleep(83);  
               } catch (InterruptedException e) {  
                   Log.e("ERROR", "Thread Interrupted");  
               }  
               Message msg = mHandler.obtainMessage();  
               Bundle b = new Bundle();  
               msg.setData(b);  
               mHandler.sendMessage(msg);  
           } 
       }
   }
	
}
