package com.qing.browser.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.qing.browser.ui.launcher.LauncherApplication;
import com.qing.browser.utils.Constants;
import com.qing.browser.utils.StringUtil;
import com.qing.browser.utils.TransferUtils;

public class URLUtil {

	private final static String TAG = "URLUtil";

	private static Map<String, String> map;

	private static URLUtil urlUtil;

	private URLUtil() {

	}

	public static URLUtil getInstance() {
		if (urlUtil == null)
			urlUtil = new URLUtil();
		return urlUtil;
	}

	/**
	 * 
	 * @param address
	 * @param postString
	 *            以&开始, 以&结束
	 * @return
	 */
	public String invokeURL(String address, String postString) {
		String result = "";
		URL url = null;
		URLConnection conn = null;
		OutputStreamWriter out = null;
		BufferedReader reader = null;
		StringBuffer sb = null;
		String line = null;
		try {
			Log.i(TAG, address);
			Log.i(TAG, postString);
			url = new URL(address);
			conn = url.openConnection();
			conn.setDoOutput(true);
			out = new OutputStreamWriter(conn.getOutputStream());
			postString = "&TEMP=TEMP" + postString; // 历史遗留问题
			out.write(postString);
			out.flush();
			out.close();
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
			Log.i(TAG, "下发内容" + result);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				url = null;
				conn = null;
				if (out != null) {
					out.close();
					out = null;
				}
				reader = null;
				sb = null;
				line = null;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * @param address
	 * @param postString
	 *            以&开始，以&结束
	 * @return
	 */
	public Map<String, String> getMap(String address, String postString) {
		if (StringUtil.isNull(address) || StringUtil.isNull(postString))
			return null;
		StringTokenizer st = null;
		try {
			String result = this.invokeURL(address, postString);
			if (!StringUtil.isNull(result) && !"fail".equals(result)) {
				st = new StringTokenizer(result.trim(), "&");
				map = new Hashtable<String, String>();
				String param = "";
				int idx = 0;
				while (st.hasMoreTokens()) {
					param = st.nextToken();
					idx = param.indexOf("=");
					map.put(param.substring(0, idx), TransferUtils
							.decodeString(param.substring(idx + 1)));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			st = null;
		}
		return map;
	}

	//
	public String getJson(String address, String postString) {
		String result = null;

		try {
			result = this.invokeURL(address, postString);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
		return result;
	}

	private static final int REQUEST_TIMEOUT = 5 * 1000;
	private static final int SO_TIMEOUT = 10 * 1000;

	public static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	/**
	 * 登陆
	 * 
	 * @param url
	 * @return
	 */
	public String LoginServer(String url, int isLogin) {
		String returnStr = null;
		Log.i("H", "loginServer url=" + url);
		HttpPost post = new HttpPost(url);
		HttpClient client = getHttpClient();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			int id = response.getStatusLine().getStatusCode();
			if (200 == id) {
				returnStr = EntityUtils.toString(response.getEntity(), "UTF_8");
				Log.i("H", "LogIn returnStr=" + returnStr);
				CookieStore cookies = ((AbstractHttpClient) client)
						.getCookieStore();
				if (isLogin == Constants.CookieTypeLogin) {
					LauncherApplication.setLoginCookie(cookies);
				} else if (isLogin == Constants.CookieTypeShop) {
					LauncherApplication.setShopCookie(cookies);
				}

			} else if (404 == id) {
				Log.e("H", "返回值=%d  访问的页面暂时不存在" + id);
				returnStr = "";
			} else {
				returnStr = "";
				Log.e("H", "返回值=%d  网络异常 " + id);
			}
		} catch (UnsupportedEncodingException e) {
			Log.e("H", "UnsupportedEncodingException异常：" + e);
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e("H", "ClientProtocolException异常:" + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("H", "IOException异常:" + e);
			e.printStackTrace();
		}

		return returnStr;
	}

	/**
	 * 用户联网操作 需要带上Cookie
	 * 
	 * @param url
	 * @param key
	 * @param content
	 * @return
	 */
	public String UserServer(String url, String key, String content) {
		String returnStr = null;
		url = url.replaceAll(" ", "%20");// 有空格 转码
		url = url.replaceAll("\n", "%20");// 有换行 转码
		Log.i("H", "url=" + url);
		HttpPost post = new HttpPost(url);
		HttpClient client = getHttpClient();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			// params.add(new BasicNameValuePair(key,
			// java.net.URLEncoder.encode(content, "UTF-8")));
			params.add(new BasicNameValuePair(key, content));
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("H", "UserServer 异常：" + ex);
		}

		((AbstractHttpClient) client).setCookieStore(LauncherApplication
				.getLoginCookie());
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			int id = response.getStatusLine().getStatusCode();
			if (200 == id) {
				returnStr = EntityUtils.toString(response.getEntity(), "UTF_8");
				Log.d("H", "result=" + returnStr);

			} else if (404 == id) {
				returnStr = "";
				Log.e("H", "访问的页面暂时不存在 返回 " + id);
			} else {
				returnStr = "";
				Log.e("H", "网络异常 返回值=" + id);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e("H", "异常 UnsupportedEncodingException" + e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("H", "异常 ClientProtocolException" + e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("H", "异常 IOException" + e);
		}

		return returnStr;
	}
	
	/**
	 * 用户联网操作 需要带上Cookie
	 * 
	 * @param url
	 * @param key
	 * @param content
	 * @return
	 */
	public String UserServer(String url, List<NameValuePair> params) {
		String returnStr = null;
		url = url.replaceAll(" ", "%20");// 有空格 转码
		url = url.replaceAll("\n", "%20");// 有换行 转码
		Log.i("H", "url=" + url);
		HttpPost post = new HttpPost(url);
		HttpClient client = getHttpClient();
		((AbstractHttpClient) client).setCookieStore(LauncherApplication
				.getLoginCookie());
		try {
			HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			int id = response.getStatusLine().getStatusCode();
			if (200 == id) {
				returnStr = EntityUtils.toString(response.getEntity(), "UTF_8");
				Log.d("H", "result=" + returnStr);

			} else if (404 == id) {
				returnStr = "";
				Log.e("H", "访问的页面暂时不存在 返回 " + id);
			} else {
				returnStr = "";
				Log.e("H", "网络异常 返回值=" + id);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e("H", "异常 UnsupportedEncodingException" + e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("H", "异常 ClientProtocolException" + e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("H", "异常 IOException" + e);
		}

		return returnStr;
	}
}
