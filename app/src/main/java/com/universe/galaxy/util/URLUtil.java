package com.universe.galaxy.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.qing.browser.utils.ConstantsUrl;
import com.universe.galaxy.ad.AdInfo;

public class URLUtil {

	private static Map<String, String> map;

	private static URLUtil urlUtil;


	private URLUtil() {

	}

	public static URLUtil getInstance() {
		if (urlUtil == null)
			urlUtil = new URLUtil();
		return urlUtil;
	}

	

	public AdInfo getAd(String address) {
		return new AdInfo(address);
	}


	public Map<String, String> getNetContetnt(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		Map<String, String> map = this.getMap(ConstantsUrl.ADVERTISING_DOMAIN_QING,
					Tools.getADPoststring(context));
		return map;
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
			Log.i("CNCOMAN", address + postString);
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
			Log.i("CNCOMAN", "下发内容" + result);
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

	/**
	 * 联网获取"ok"则联网销量成功
	 * 
	 * @param address
	 * @param postString
	 * @return
	 */
	public boolean getNetSalesResult(String address, String postString) {
		boolean isOK = false;
		if (StringUtil.isNull(address) || StringUtil.isNull(postString))
			return isOK;

		try {
			String result = this.invokeURL(address, postString);
			if ("ok".equals(result)) {
				isOK = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
		return isOK;
	}

	// 会员功能设计
	public String getVIPJson(String address, String postString) {
		String result = null;

		try {
			result = this.invokeURL(address, postString);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}
		Log.i("CNCOMAN", result);
		return result;
	}
}
