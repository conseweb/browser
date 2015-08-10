package com.qing.browser.pic;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.qing.browser.utils.Constants;
import com.qing.browser.utils.IOUtils;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File( Environment.getExternalStoragePublicDirectory(IOUtils.APPLICATION_FOLDER),
					Constants.QINGBROWSER_USER_DIRECTORY);
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(String url){ 
        String filename =  convertUrlToFileName(url);//*/ String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    /** 将url转成文件名 **/
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1];
	}
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

}