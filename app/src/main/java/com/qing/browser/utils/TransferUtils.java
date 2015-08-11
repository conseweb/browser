package com.qing.browser.utils;

/**
 * 命令传输时工具类，参考《j2meRss通讯协议.doc》
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Modim</p>
 * @author Derek Wang
 * @version 1.0
 */
public class TransferUtils {
  /**
   * Comment for <code>trans</code>
   * 字符串转义数组
   */
  private static String[][] trans = new String[][] {
      // this should be replace last when decoding,
      // but replace first when encoding, be careful
      {
      "%", "%25"}
      , {
    	  "&", "%26"}
      , {
    	  ",", "%2C"}
      , {
    	  ";", "%3B"}
      , {
    	  "\\|", "%7C"}
      , {
    	  "=", "%3D"}
      , {
    	  "\\[", "%5B"}
      , {
          "\\]", "%5D"}
  };

  private static String[][] transFor14 = new String[][]{
      {
    	  "%", "%25"}
      , {
    	  "&", "%26"}
      , {
    	  ",", "%2C"}
      , {
    	  ";", "%3B"}
      , {
    	  "\\|", "%7C"}
      , {
    	  "#", "%6C"}
      , {
    	  "=", "%3D"}
      , {
    	  "\\[", "%5B"}
      , {
          "\\]", "%5D"}
  };

  private static String[][] transForTmp = new String[][] {
      {
      "%", "％"}
      , {
      ",", "，"}
      , {
      ";", "；"}
      , {
      "#", "井"}
      , {
      "&", "\\$"}
      , {
      "\\|", "1"}
      , {
      "\\[", "【"}
      , {
      "]", "】"}
  };



  /**
   * 虚拟机编码
   */
  /*private static String enc;
  static {
    Properties prop = new Properties();
    try {
      prop.load(TransferUtils.class
                .getResourceAsStream(Constants.PLATFORM_BASE));
      enc = prop.getProperty("encoding").trim();
    }
    catch (Exception e) {
      enc = "GBK";
    }
  }

  public static String getPlatformEncoding() {
    return enc;
  }*/

  public static String replaceString(String str) {
    if (null == str) {
      return "";
    }
    str = decodeString(str);
    for (int i = 0; i < transForTmp.length; ++i) {
      str = str.replaceAll(transForTmp[i][0], transForTmp[i][1]);
    }
    return str;
  }

  /**
   * 信息编码
   * @param str
   * @return
   */
  public static String encodeString(String str) {
    if (null == str) {
      return "";
    }
    for (int i = 0; i < trans.length; ++i) {
      str = str.replaceAll(trans[i][0], trans[i][1]);
    }
    return str;
  }

  public static String encodeString(String str, String pro) {
    if (null == str) {
      return "";
    }
    if (pro == null || pro.equals("")){
      return encodeString(str);
    }
    if (pro.equals("14") || pro.substring(0,2).equals("14")){
      for (int i = 0; i < transFor14.length; ++i) {
        str = str.replaceAll(transFor14[i][0], transFor14[i][1]);
      }
      return str;
    }
    return  encodeString(str);
  }

  /**
   * 信息解码
   * @param str
   * @return
   */
  public static String decodeString(String str) {
    if (str == null) {
      return null;
    }
    for (int i = trans.length - 1; i >= 0; --i) {
      str = str.replaceAll(trans[i][1], trans[i][0]);
    }
    return str;
  }
  public static String decodeString(String str,String pro) {
    if (str == null) {
      return null;
    }
    if (pro == null || pro.equals("")){
      return decodeString(str);
    }
    if (pro.startsWith("14")) {
      for (int i = transFor14.length - 1; i >= 0; --i) {
        str = str.replaceAll(transFor14[i][1], transFor14[i][0]);
      }
      return str;
    }
    return decodeString(str);
  }
  /**
   * 根据http参数获取整形数值
   * @param value 值
   * @param default_val 默认值
   * @param name 参数名称
   * @return
   * @throws IllegalParameterException
   */
  /*public static int getIntValue(String value, int default_val, String name) throws
      IllegalParameterException {
    if (null == value) {
      return default_val;
    }
    int val = default_val;
    try {
      val = Integer.parseInt(value);
    }
    catch (NullPointerException e) {
    }
    catch (NumberFormatException e) {
      throw new IllegalParameterException(name);
    }
    if (val <= 0) {
      val = default_val;
    }
    return val;
  }
*/
  /**
   * 根据http参数获取浮点型数值
   * @param value 值
   * @param default_val 默认值
   * @param name 参数名称
   * @return
   * @throws IllegalParameterException
   */
  /*public static float getFloatValue(String value, float default_val,
                                    String name) throws
      IllegalParameterException {
    if (null == value) {
      return default_val;
    }
    float val = default_val;
    try {
      val = Float.parseFloat(value);
    }
    catch (NullPointerException e) {
    }
    catch (NumberFormatException e) {
      throw new IllegalParameterException(name);
    }
    if (val <= 0) {
      val = default_val;
    }
    return val;

  }
*/
  /**
   * html内容进行编码过滤
   * @param ml
   * @return
   * @throws LTException
   */
  /*public static String HTMLFilter(String ml){
    return HTMLFilter(ml, 0);
  }*/

  /**
   * html内容进行编码过滤，截尾
   * @param ml
   * @param truncLen 保留长度，byte为参考
   * @return
   * @throws LTException
   */
  /*public static String HTMLFilter(String ml, int truncLen) {
    try {
      byte[] buf = ml.getBytes(getPlatformEncoding());
      byte[] res = new byte[buf.length];
      int idx = 0, i;
      boolean inTag = false, inComment = false;
      byte cmtCh = 0;
      if (truncLen == 0) {
        truncLen = buf.length;
      }
      for (i = 0; (i < buf.length) && (idx < truncLen); ++i) {
        if (!inComment) {
          if (inTag) {
            if (buf[i] == '>') {
              inTag = false;
            }
            else if (buf[i] == '\\') {
              i++;
            }
            else if ( (buf[i] == '\'') || (buf[i] == '"')) {
              cmtCh = buf[i];
              inComment = true;
            }
          }
          else {
            if (buf[i] == '<') {
              inTag = true;
            }
            else {
              res[idx++] = buf[i];
            }
          }
        }
        else {
          if (buf[i] == '\\') {
            i++;
          }
          else if (buf[i] == cmtCh) {
            inComment = false;
          }
        }
      }
//      String st = new String(res, 0, idx);
      String st = new String(res, 0, idx, getPlatformEncoding());
      if ( (i < buf.length) && (idx == truncLen)) {
        st = st + "...";
      }
      return st;
    }
    catch (UnsupportedEncodingException e) {
      //throw new HebeException(e.getMessage());
    }
  }*/
}