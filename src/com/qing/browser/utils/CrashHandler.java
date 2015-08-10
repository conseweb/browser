package com.qing.browser.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException������,��������Uncaught�쳣��ʱ��,�и������ӹܳ���,����¼���ʹ��󱨸�.
 * 
 * @author user
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	public static final String TAG = "CrashHandler";
	
	//ϵͳĬ�ϵ�UncaughtException������ 
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//�����Context����
	private Context mContext;
	//�����洢�豸��Ϣ���쳣��Ϣ
	private Map<String, String> infos = new HashMap<String, String>();

	//���ڸ�ʽ������,��Ϊ��־�ļ�����һ����
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

 
 
    //CrashHandlerʵ��       
    private static CrashHandler instance;    
	
	/** ��ȡCrashHandlerʵ�� ,����ģʽ */      
    public static CrashHandler getInstance() {      
        if(instance == null)  
            instance = new CrashHandler();     
        return instance;      
    }

	/**
	 * ��ʼ��
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		//��ȡϵͳĬ�ϵ�UncaughtException������
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//���ø�CrashHandlerΪ�����Ĭ�ϴ�����
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
    
	
	/**
	 * ��UncaughtException����ʱ��ת��ú���������
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//����û�û�д�������ϵͳĬ�ϵ��쳣������������
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			//�˳�����
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	
	/**
	 * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
	 * 
	 * @param ex
	 * @return true:��������˸��쳣��Ϣ;���򷵻�false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		//ʹ��Toast����ʾ�쳣��Ϣ
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				//TODO �ر�Toast�쳣��Ϣ
//				Toast.makeText(mContext, "�ܱ�Ǹ,��������쳣,�����˳�.", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		//�ռ��豸������Ϣ 
		collectDeviceInfo(mContext);
		
		 //������־�ļ�        
        saveCatchInfo2File(ex); 
		return true;
	}
	
	/**
	 * �ռ��豸������Ϣ
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}
	
	
	
	/**
	 * �����Ƿ����
	 * 
	 * @param context
	 * @return
	 *//*
	private boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
	    if (info != null) {
	    	for (int i = 0; i < info.length; i++) {
	    		if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	    			return true;
	    		}
	    	}
	    }
		return false;
	}*/
	
	/**
	 * 
	 * @param _context
	 * @param filePath
	 */
	@SuppressWarnings("unused")
    private void sendErrorMail(Context _context , /*String content*/ String filePath ) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
//		Intent sendIntent = new Intent("com.google.android.gm.action.AUTO_SEND"); //Gmail֧���Զ����� 3.0����
		sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String subject = "Error Description";
		String body = "Sorry for your inconvenience .\nWe assure you that we will solve this problem as soon possible." 
					+ "\n\nThanks for using app." + "\n\n\n"/* + content*/;
		
		sendIntent.setType("plain/text");
		sendIntent.putExtra(Intent.EXTRA_EMAIL,new String[] {"huangting@data123.com.cn"});// TODO
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)) );
		sendIntent.setType("message/rfc822");
//		sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		
//		_context.startActivity( Intent.createChooser(sendIntent, "App_name"));
		_context.startActivity(  sendIntent );
	 
		
	}
	
     
    /**   
     * ���������Ϣ���ļ���   
     *    
     * @param ex   
     * @return  �����ļ�����,���ڽ��ļ����͵�������   
     */      
    private String saveCatchInfo2File(Throwable ex) {      
              
        StringBuffer sb = new StringBuffer();      
        for (Map.Entry<String, String> entry : infos.entrySet()) {      
            String key = entry.getKey();      
            String value = entry.getValue();      
            sb.append(key + "=" + value + "\n");
          
        }      
              
        Writer writer = new StringWriter();      
        PrintWriter printWriter = new PrintWriter(writer);      
        ex.printStackTrace(printWriter);      
        Throwable cause = ex.getCause();      
        while (cause != null) {      
            cause.printStackTrace(printWriter);      
            cause = cause.getCause();      
        }      
        printWriter.close();      
        String result = writer.toString();
        Log.e(TAG, result);  
        sb.append(result);      
        try {      
            long timestamp = System.currentTimeMillis();      
            String time = formatter.format(new Date());      
            String fileName = "crash-" + time + "-" + timestamp + ".log";      
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            	
            	File dir = new File( Environment.getExternalStoragePublicDirectory(
            			IOUtils.APPLICATION_FOLDER), "crash/");
            	      
                if (!dir.exists()) {      
                    dir.mkdirs();      
                }  
            	
                File f = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(f);      
                fos.write(sb.toString().getBytes());    
                //���͸�������Ա   
                sendCrashLog2PM(  fileName);  
                fos.close();      
            }      
            return fileName;      
        } catch (Exception e) {      
            Log.e(TAG, "an error occured while writing file...", e);      
        }      
        return null;      
    }      
      
    /** 
     * ������ĵ��±����Ĵ�����Ϣ���͸�������Ա 
     *  
     * Ŀǰֻ��log��־������sdcard �������LogCat�У���δ���͸���̨�� 
     */  
    private void sendCrashLog2PM(String fileName){  
        if(!new File(fileName).exists()){  
            Log.d("CrashHandler", "��־�ļ������ڣ�");  
            return;  
        }
        
//        sendErrorMail(mContext,  fileName); // TODO 
        
        FileInputStream fis = null;  
        BufferedReader reader = null;  
        String s = null;  
        try {  
            fis = new FileInputStream(fileName);  
            reader = new BufferedReader(new InputStreamReader(fis, "GBK"));  
            while(true){  
                s = reader.readLine();  
                if(s == null) break;  
                //����Ŀǰ��δȷ���Ժ��ַ�ʽ���ͣ������ȴ��log��־��   
                Log.i("info", s.toString());
                
            }
           
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{   // �ر���   
            try {  
                reader.close();  
                fis.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  

	
}
