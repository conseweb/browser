package com.qing.browser.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

public class WebPageSave {

	private static final int FILE_NAME_MAX_LEN = 250;
	private static final String SAVE_PAGE_PATH = "Qing";
	private static final String _FILES = "_files";
	private static final String UNTITLED = "untitled";
	private static int count = 0;

	public static void saveWebPage(final Context context, String url,
			final String storagePath, final String title) {
		// url网址作为输入源
		try {
			final Document doc = Jsoup.connect(url).timeout(60000).get();
			new Thread() {
				public void run() {
					try {
						// String time = DateFormat.format(
						// "yyyy-MM-dd hh-mm-ssaa",
						// System.currentTimeMillis()).toString();
						savePage(context, doc, storagePath + File.separator
								+ SAVE_PAGE_PATH, title);
						// savePage(context, doc, storagePath, time);
					} catch (final Exception e) {
						e.printStackTrace();
						if (e instanceof UnknownHostException
								|| e instanceof SocketTimeoutException) {
							// 无网络
						} else if (e instanceof MalformedURLException) {
							// 无效URL
						} else {
							// 内部错误
						}
					}
				}
			}.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void savePage(Context context, Document doc,
			String storagePath, String title) throws IOException {
		String savePagePrefix = null;
		try {
			savePagePrefix = getSavePagePrefixFile(storagePath, title);
			savePagePrefix = savePagePrefix.replaceAll(
					"[%\\*\\|\\\":\\<\\>\\?]", "_");
			handleSavedElements(context, doc, "img", "src", savePagePrefix,
					storagePath);
			handleSavedElements(context, doc, "link", "href", savePagePrefix,
					storagePath);
			handleSavedElements(context, doc, "script", "src", savePagePrefix,
					storagePath);
			handleSavedElements(context, doc, "applet", "code", savePagePrefix,
					storagePath);
			handleSavedElements(context, doc, "embed", "movie", savePagePrefix,
					storagePath);
			handleSavedElements(context, doc, "embed", "src", savePagePrefix,
					storagePath);
			handlePageElements(context, doc, "frame", "src", savePagePrefix,
					storagePath);
			handlePageElements(context, doc, "iframe", "src", savePagePrefix,
					storagePath);
			handlePageElements(context, doc, "area", "href", savePagePrefix,
					storagePath);
			String html = doc.html();
			FileWriter fw = new FileWriter(storagePath + File.separator
					+ savePagePrefix + ".html");
			fw.write(html);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (savePagePrefix != null) {
				delFile(new File(storagePath + File.separator + savePagePrefix
						+ ".html"));
				delFile(new File(storagePath + File.separator + savePagePrefix
						+ _FILES));
			}
			throw e;
		}
	}

	private static String getSavePagePrefixFile(String storagePath, String title) {
		int length = storagePath.getBytes().length + _FILES.getBytes().length;
		byte[] titleBytes = title.getBytes();
		if (titleBytes.length >= FILE_NAME_MAX_LEN - length) {
			title = new String(title.getBytes(), 0, FILE_NAME_MAX_LEN - length);
		}
		return title;
	}

	private static void handleSavedElements(Context context, Document doc,
			String label, String attr, String savePagePrefix, String storagePath)
			throws MalformedURLException, IOException {
		Elements imgs = doc.getElementsByTag(label);// img[src], link[href],
													// applet[code], |
													// frame/iframe[src],
													// area[herf]
		Iterator<Element> imgs_iter = imgs.iterator();
		while (imgs_iter.hasNext()) {
			Element img = imgs_iter.next();
			if (img.hasAttr(attr)) {
				String src = img.absUrl(attr);
				if (!doc.baseUri().equals(src)) {
					if (handleAttrValueWithUrl(context, src, label, attr,
							storagePath, savePagePrefix, img) < 0) {
						continue;
					}
				}
			}
		}
	}

	private static void handlePageElements(Context context, Document doc,
			String label, String attr, String savePagePrefix, String storagePath)
			throws IOException {
		Elements imgs = doc.getElementsByTag(label);// img[src], link[href],
													// applet[code], |
													// frame/iframe[src],
													// area[herf]
		Iterator<Element> imgs_iter = imgs.iterator();
		while (imgs_iter.hasNext()) {
			Element img = imgs_iter.next();
			if (img.hasAttr(attr)) {
				String src = img.absUrl(attr);
				if (src == null || src.trim().length() == 0) {
					continue;
				}
				// String fileName = src.substring(src.lastIndexOf('/') + 1);
				String fileName = URLUtil.guessFileName(src, null, null);
				String saveFile = savePagePrefix + _FILES + File.separator
						+ fileName;
				savePage(context, src, storagePath + File.separator
						+ savePagePrefix + _FILES, fileName);
				img.attr(attr, saveFile);
			}
		}
	}

	private static int handleAttrValueWithUrl(Context context, String src,
			String label, String attr, String storagePath,
			String savePagePrefix, Element img) throws MalformedURLException,
			IOException {
		if (src == null || src.trim().length() == 0) {
			return -1;
		}
		String fileName = URLUtil.guessFileName(src, null, null);
		if (fileName == null || fileName.trim().length() == 0) {
			return -1;
		}
		String saveFile = savePagePrefix + _FILES + File.separator + fileName;
		String returnsaveFile = saveFile(context, src, storagePath, saveFile);
		if(returnsaveFile.equals("")){
			return -1;
		}
		saveFile = savePagePrefix + _FILES + File.separator
				+ returnsaveFile;
		
		img.attr(attr, saveFile);
		return 1;
	}

	private static void delFile(File path) {
		if (path.exists()) {
			if (path.isFile()) {
				path.delete();
			} else {
				for (File file : path.listFiles()) {
					delFile(file);
				}
				path.delete();
			}
		}
	}

	private static void savePage(Context context, String url,
			String storagePath, String title) throws IOException {
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();
		savePage(context, doc, storagePath, title);
	}

	private static String saveFile(Context context, String url,
			String storagePath, String saveFile) throws MalformedURLException,
			IOException {
		InputStream is = null;
		if (is == null) {
			HttpURLConnection httpConn = (HttpURLConnection) new URL(url)
					.openConnection();
			int tryCount = 0;
			boolean toTry = true;
			while (toTry) {
				try {
					httpConn.setConnectTimeout(60000);
					
					if(httpConn.getResponseCode()==404){
						return "";
					}
					is = httpConn.getInputStream();
					toTry = false;
				} catch (SocketTimeoutException e) {
					tryCount++;
					if (tryCount >= 3) {
						toTry = false;
						throw e;
					}
				}
			}
		}
		File save = new File(storagePath, saveFile);
		Log.i("WLL", saveFile);
		if (!save.getParentFile().exists()) {
			if (!save.getParentFile().mkdirs()) {
				throw new IOException("mkdirs [" + save.getParent()
						+ "] failed!");
			}
		}
		if (!save.exists()) {
			if (!save.createNewFile()) {
				save = new File(save.getParentFile(), UNTITLED + (count++));
			}
		} else {
			delFile(save);
			// int i = 1;
			// String name = save.getAbsolutePath();
			// int end = name.indexOf('.');
			// String pre = name.substring(0, end), aft = name.substring(end +
			// 1);
			// do {
			// if (end > 0) {
			// save = new File(pre + (i++) + "." + aft);
			// } else {
			// save = new File(name + (i++));
			// }
			// } while (save.exists());
		}
		saveFile(is, save);
		Log.i("WLL", "save.getName()_" + save.getName());
		return save.getName();
	}

	private static void saveFile(InputStream is, File saveFile)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(saveFile);
		byte[] buff = new byte[4096];
		int length = -1;
		while ((length = is.read(buff)) != -1) {
			fos.write(buff, 0, length);
		}
		if (is != null) {
			is.close();
		}
		if (fos != null) {
			fos.close();
		}
	}

}
