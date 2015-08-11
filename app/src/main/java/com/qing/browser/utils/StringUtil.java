package com.qing.browser.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public final static String CMCC_NUMBERS = ",134,135,136,137,138,139,150,151,152,157,158,159,187,188,147,182,183,184,";
	public final static String UNICOM_NUMBERS = ",130,131,132,155,156,185,186,145,";
	public final static String CDMA_NUMBERS = ",133,153,180,189,";
	private static long lastClickTime;
	private static HashMap<String, String> contentTypeMap = new HashMap<String, String>();
	private static HashMap<String, String> directoryTypeMap = new HashMap<String, String>();
	
	static{
		contentTypeMap.put(".apk", "application/vnd.android.package-archive");
		contentTypeMap.put(".ai", "application/postscript");
		contentTypeMap.put(".aif", "audio/x-aiff");
		contentTypeMap.put(".aifc", "audio/x-aiff");
		contentTypeMap.put(".aiff", "audio/x-aiff");
		contentTypeMap.put(".asc", "text/plain");
		contentTypeMap.put(".au", "audio/basic");
		contentTypeMap.put(".avi", "video/x-msvideo");
		contentTypeMap.put(".bcpio", "application/x-bcpio");
		contentTypeMap.put(".bin", "application/octet-stream");
		contentTypeMap.put(".c", "text/plain");
		contentTypeMap.put(".cc", "text/plain");
		contentTypeMap.put(".ccad", "application/clariscad");
		contentTypeMap.put(".cdf", "application/x-netcdf");
		contentTypeMap.put(".class", "application/octet-stream");
		contentTypeMap.put(".cpio", "application/x-cpio");
		contentTypeMap.put(".cpt", "application/mac-compactpro");
		contentTypeMap.put(".csh", "application/x-csh");
		contentTypeMap.put(".css", "text/css");
		contentTypeMap.put(".dcr", "application/x-director");
		contentTypeMap.put(".dir", "application/x-director");
		contentTypeMap.put(".dms", "application/octet-stream");
		contentTypeMap.put(".doc", "application/msword");
		contentTypeMap.put(".drw", "application/drafting");
		contentTypeMap.put(".dvi", "application/x-dvi");
		contentTypeMap.put(".dwg", "application/acad");
		contentTypeMap.put(".dxf", "application/dxf");
		contentTypeMap.put(".dxr", "application/x-director");
		contentTypeMap.put(".eps", "application/postscript");
		contentTypeMap.put(".etx", "text/x-setext");
		contentTypeMap.put(".exe", "application/octet-stream");
		contentTypeMap.put(".ez", "application/andrew-inset");
		contentTypeMap.put(".f", "text/plain");
		contentTypeMap.put(".f90", "text/plain");
		contentTypeMap.put(".fli", "video/x-fli");
		contentTypeMap.put(".gif", "image/gif");
		contentTypeMap.put(".gtar", "application/x-gtar");
		contentTypeMap.put(".gz", "application/x-gzip");
		contentTypeMap.put(".h", "text/plain");
		contentTypeMap.put(".hdf", "application/x-hdf");
		contentTypeMap.put(".hh", "text/plain");
		contentTypeMap.put(".hqx", "application/mac-binhex40");
		contentTypeMap.put(".htm", "text/html");
		contentTypeMap.put(".html", "text/html");
		contentTypeMap.put(".ice", "x-conference/x-cooltalk");
		contentTypeMap.put(".ief", "image/ief");
		contentTypeMap.put(".iges", "model/iges");
		contentTypeMap.put(".igs", "model/iges");
		contentTypeMap.put(".ips", "application/x-ipscript");
		contentTypeMap.put(".ipx", "application/x-ipix");
		contentTypeMap.put(".jpe", "image/jpeg");
		contentTypeMap.put(".jpeg", "image/jpeg");
		contentTypeMap.put(".jpg", "image/jpeg");
		contentTypeMap.put(".js", "application/x-javascript");
		contentTypeMap.put(".kar", "audio/midi");
		contentTypeMap.put(".latex", "application/x-latex");
		contentTypeMap.put(".lha", "application/octet-stream");
		contentTypeMap.put(".lsp", "application/x-lisp");
		contentTypeMap.put(".lzh", "application/octet-stream");
		contentTypeMap.put(".m", "text/plain");
		contentTypeMap.put(".man", "application/x-troff-man");
		contentTypeMap.put(".me", "application/x-troff-me");
		contentTypeMap.put(".mesh", "model/mesh");
		contentTypeMap.put(".mid", "audio/midi");
		contentTypeMap.put(".midi", "audio/midi");
		contentTypeMap.put(".mif", "application/vnd.mif");
		contentTypeMap.put(".mime", "www/mime");
		contentTypeMap.put(".mov", "video/quicktime");
		contentTypeMap.put(".movie", "video/x-sgi-movie");
		contentTypeMap.put(".mp2", "audio/mpeg");
		contentTypeMap.put(".mp3", "audio/mpeg");
		contentTypeMap.put(".mpe", "video/mpeg");
		contentTypeMap.put(".mpeg", "video/mpeg");
		contentTypeMap.put(".mpg", "video/mpeg");
		contentTypeMap.put(".mpga", "audio/mpeg");
		contentTypeMap.put(".ms", "application/x-troff-ms");
		contentTypeMap.put(".msh", "model/mesh");
		contentTypeMap.put(".nc", "application/x-netcdf");
		contentTypeMap.put(".oda", "application/oda");
		contentTypeMap.put(".pbm", "image/x-portable-bitmap");
		contentTypeMap.put(".pdb", "chemical/x-pdb");
		contentTypeMap.put(".pdf", "application/pdf");
		contentTypeMap.put(".pgm", "image/x-portable-graymap");
		contentTypeMap.put(".pgn", "application/x-chess-pgn");
		contentTypeMap.put(".png", "image/png");
		contentTypeMap.put(".pnm", "image/x-portable-anymap");
		contentTypeMap.put(".pot", "application/mspowerpoint");
		contentTypeMap.put(".ppm", "image/x-portable-pixmap");
		contentTypeMap.put(".pps", "application/mspowerpoint");
		contentTypeMap.put(".ppt", "application/mspowerpoint");
		contentTypeMap.put(".ppz", "application/mspowerpoint");
		contentTypeMap.put(".pre", "application/x-freelance");
		contentTypeMap.put(".prt", "application/pro_eng");
		contentTypeMap.put(".ps", "application/postscript");
		contentTypeMap.put(".qt", "video/quicktime");
		contentTypeMap.put(".ra", "audio/x-realaudio");
		contentTypeMap.put(".ram", "audio/x-pn-realaudio");
		contentTypeMap.put(".ras", "image/cmu-raster");
		contentTypeMap.put(".rgb", "image/x-rgb");
		contentTypeMap.put(".rm", "audio/x-pn-realaudio");
		contentTypeMap.put(".roff", "application/x-troff");
		contentTypeMap.put(".rpm", "audio/x-pn-realaudio-plugin");
		contentTypeMap.put(".rtf", "text/rtf");
		contentTypeMap.put(".rtx", "text/richtext");
		contentTypeMap.put(".scm", "application/x-lotusscreencam");
		contentTypeMap.put(".set", "application/set");
		contentTypeMap.put(".sgm", "text/sgml");
		contentTypeMap.put(".sgml", "text/sgml");
		contentTypeMap.put(".sh", "application/x-sh");
		contentTypeMap.put(".shar", "application/x-shar");
		contentTypeMap.put(".silo", "model/mesh");
		contentTypeMap.put(".sit", "application/x-stuffit");
		contentTypeMap.put(".skd", "application/x-koan");
		contentTypeMap.put(".skm", "application/x-koan");
		contentTypeMap.put(".skp", "application/x-koan");
		contentTypeMap.put(".skt", "application/x-koan");
		contentTypeMap.put(".smi", "application/smil");
		contentTypeMap.put(".smil", "application/smil");
		contentTypeMap.put(".snd", "audio/basic");
		contentTypeMap.put(".sol", "application/solids");
		contentTypeMap.put(".spl", "application/x-futuresplash");
		contentTypeMap.put(".src", "application/x-wais-source");
		contentTypeMap.put(".step", "application/STEP");
		contentTypeMap.put(".stl", "application/SLA");
		contentTypeMap.put(".stp", "application/STEP");
		contentTypeMap.put(".sv4cpio", "application/x-sv4cpio");
		contentTypeMap.put(".sv4crc", "application/x-sv4crc");
		contentTypeMap.put(".swf", "application/x-shockwave-flash");
		contentTypeMap.put(".t", "application/x-troff");
		contentTypeMap.put(".tar", "application/x-tar");
		contentTypeMap.put(".tcl", "application/x-tcl");
		contentTypeMap.put(".tex", "application/x-tex");
		contentTypeMap.put(".texi", "application/x-texinfo");
		contentTypeMap.put(".texinfo", "application/x-texinfo");
		contentTypeMap.put(".tif", "image/tiff");
		contentTypeMap.put(".tiff", "image/tiff");
		contentTypeMap.put(".tr", "application/x-troff");
		contentTypeMap.put(".tsi", "audio/TSP-audio");
		contentTypeMap.put(".tsp", "application/dsptype");
		contentTypeMap.put(".tsv", "text/tab-separated-values");
		contentTypeMap.put(".txt", "text/plain");
		contentTypeMap.put(".unv", "application/i-deas");
		contentTypeMap.put(".ustar", "application/x-ustar");
		contentTypeMap.put(".vcd", "application/x-cdlink");
		contentTypeMap.put(".vda", "application/vda");
		contentTypeMap.put(".viv", "video/vnd.vivo");
		contentTypeMap.put(".vivo", "video/vnd.vivo");
		contentTypeMap.put(".vrml", "model/vrml");
		contentTypeMap.put(".wav", "audio/x-wav");
		contentTypeMap.put(".wrl", "model/vrml");
		contentTypeMap.put(".xbm", "image/x-xbitmap");
		contentTypeMap.put(".xlc", "application/vnd.ms-excel");
		contentTypeMap.put(".xll", "application/vnd.ms-excel");
		contentTypeMap.put(".xlm", "application/vnd.ms-excel");
		contentTypeMap.put(".xls", "application/vnd.ms-excel");
		contentTypeMap.put(".xlw", "application/vnd.ms-excel");
		contentTypeMap.put(".xml", "text/xml");
		contentTypeMap.put(".xpm", "image/x-xpixmap");
		contentTypeMap.put(".xwd", "image/x-xwindowdump");
		contentTypeMap.put(".xyz", "chemical/x-pdb");
		contentTypeMap.put(".zip", "application/zip");
		
		directoryTypeMap.put(".apk", "apk");
		directoryTypeMap.put(".mp3", "music");
		directoryTypeMap.put(".jpg", "picture");
		directoryTypeMap.put(".gif", "picture");
		directoryTypeMap.put(".png", "picture");
		directoryTypeMap.put(".jpeg", "picture");
	}
	
	public static String getPath(String suffix){
		String path = directoryTypeMap.get(suffix);		
		return isNull(path) ? IOUtils.APPLICATION_FOLDER : IOUtils.APPLICATION_FOLDER + path + "/";
	}
	
	/**
	 * 获取contentType
	 * @param suffix 文件后缀
	 * @return
	 */
	public static String getHttpContentType(String suffix){
		return contentTypeMap.get(suffix);
	}
	
	public static String getSuffixOfFileName(String fileName){
		String suffix = "";
		if(!isNull(fileName)){
			int len = fileName.lastIndexOf(".");			
			if(len != -1)
				suffix = fileName.substring(len, fileName.length());
		}
		return suffix;
	}
	
	/**
	 * 替换非法的文件名符号
	 * @param str
	 * @return
	 */
	public static String replaceIllegalFileSymbol(String str){
		if(isNull(str))
			return "";
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
        Pattern p = Pattern.compile(regEx);     
        Matcher m = p.matcher(str);     
        return m.replaceAll("").trim();  
	}

	/**
	 * 字符串为空返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null)
			return true;
		if ("".equals(str.trim()))
			return true;
		if ("null".equalsIgnoreCase(str))
			return true;
		return false;
	}

	/**
	 * 根据手机号码查找运营商<br/>
	 * 手机号错误时返回0
	 * 
	 * @param mobile
	 * @return 1移动,2联通,3电信,4未知运营商
	 */
	public static int getOperator(String mobile) {
		if (isNull(mobile))
			return 0;
		mobile = mobile.replace("+", "");
		if (mobile.startsWith("86"))
			mobile = mobile.replaceFirst("86", "");
		if (!isNumeric(mobile))
			return 0;
		if (mobile.length() < 7 || mobile.length() > 12) // 手机号码必须大于7位小于12位
			return 0;
		if (mobile.startsWith("1349")) // 1349是电信
			return 3;
		mobile = "," + mobile.substring(0, 3) + ",";
		if (CMCC_NUMBERS.indexOf(mobile) != -1) // 中移动
			return 1;
		if (UNICOM_NUMBERS.indexOf(mobile) != -1) // 联通
			return 2;
		if (CDMA_NUMBERS.indexOf(mobile) != -1) // 电信
			return 3;
		return 0;
	}

	/**
	 * 判断是否为正整数<br/>
	 * 是否为正小数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (isNull(str))
			return false;
		if (str.matches("^[0-9]*$")) {
			return true;
		}
		if (str.matches("^\\d+\\.\\d+$"))
			return true;
		return false;
	}

	/**
	 * 正常点击返回false 频繁点击返回true
	 * 
	 * @param seconds
	 * @return
	 */
	public static boolean frequentClick(int seconds) {
		long currentClickTime = System.currentTimeMillis();
		long time = currentClickTime - lastClickTime;
		lastClickTime = currentClickTime;
		if (time < (seconds * 1000))
			return true;
		return false;
	}

	public static boolean isUrl(String urlstring) {
		try {
			String str = "(http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?";
			Pattern p = Pattern.compile(str);
			Matcher m = p.matcher(urlstring);
			return m.matches();
		} catch (Exception e) {
		}
		return false;
	}
}
