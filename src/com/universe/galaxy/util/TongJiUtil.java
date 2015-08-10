package com.universe.galaxy.util;

import java.io.File;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.qing.browser.utils.ConstantsUrl;
import com.universe.galaxy.util.Tools;

public class TongJiUtil implements Runnable {

	private Context context;
	private Handler handler;
	public static String FILE_PATH = "/data/data/com.qing.browser/files/tongji.txt";

	@Override
	public void run() {
		try {
			File tongjiFile = new File(FILE_PATH);
			List<FormBodyPart> postParams = Tools.getPostFormBodyPart(context);
			boolean flag = httpUpload(tongjiFile,
					ConstantsUrl.YONG_HU_TONG_JI_URL, postParams);
			if (flag) {
				Message msg = new Message();
				msg.obj = "";
				msg.what = 0;
				handler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.obj = "";
				msg.what = 1;
				handler.sendMessage(msg);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean httpUpload(File doc, String RequestURL,
			List<FormBodyPart> postParams) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(RequestURL);
		post.addHeader("charset", HTTP.UTF_8);
		MultipartEntity me = new MultipartEntity();
		boolean flag = false;
		try {
			for (int i = 0; i < postParams.size(); i++) {
				me.addPart(postParams.get(i));
			}
			if (doc != null && doc.length() > 0)
				me.addPart("file", new FileBody(doc));
			post.setEntity(me);
			HttpResponse res = client.execute(post);
			HttpEntity resEntity = res.getEntity();
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				flag = true;
			if (resEntity != null) {
				String json = EntityUtils.toString(resEntity, "utf-8");
				JSONObject p = null;
				try {
					p = new JSONObject(json);
					String state = (String) p.optString("State");
					String error = (String) p.optString("Error");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}
			client.getConnectionManager().shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	private TongJiUtil(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	private static TongJiUtil tongjiUtil;

	public static TongJiUtil getInstance(Context context, Handler handler) {
		if (tongjiUtil == null)
			return new TongJiUtil(context, handler);
		return tongjiUtil;
	}
}
