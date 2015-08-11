package com.universe.galaxy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.qing.browser.ui.launcher.LauncherApplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TongJi {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static String FILE_PATH = "/data/data/com.qing.browser/files/tongji.txt";

	private static void AddAnalyticsToSDcard(String message) {
		message = message + "#" + sdf.format(new Date()) + "\r\n";
		RandomAccessFile raf = null;
		File file = null;
		try {
			file = new File("sdcard/tongji.log");
			raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(message.getBytes());
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

	public static void AddAnalyticsData(String message) {
		try {
			Context context = LauncherApplication.getInstance();
			message = message + "#" + sdf.format(new Date()) + "\r\n";
			FileOutputStream outStream = context.openFileOutput("tongji.txt",
					Context.MODE_APPEND);
			outStream.write(message.getBytes());
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void deleteTongJiFile() {
		File path = new File(FILE_PATH);
		if (path.exists()) {
			path.delete();
		}
	}

	public static void uploadTongJi(final Context context) {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					switch (msg.what) {
					case 0:
						// 提交成功 删除文件
						Log.v("LS","用户操作统计 提交成功 删除文件");
						deleteTongJiFile();
						break;
					case 1:
						// 提交失败 下次继续提交
						Log.v("LS","用户操作统计 提交失败");
						break;
					}
				}
			}
		};
		Thread thread = new Thread(TongJiUtil.getInstance(context, handler));
		thread.start();
	}
	
	
	
	
	
	
	
	/**
	 * 桌面轻浏览器启动
	 */
	public static String d_qingliulanqi = "1";
	
	/**
	 * 通知栏 更新
	 */
	public static String n_gengxin = "3_1";
	
	/**
	 * 通知栏 广告
	 */
	public static String n_ad_2 = "3_2";
	
	/**
	 * 左屏幕
	 */
	public static String m_zuopinggouwu = "4";
	
	/**
	 * 添加书签
	 */
	public static String m_tianjiashuqian = "5";
	
	/**
	 * 添加书签 热门
	 */
	public static String s_remen = "5_1";
	
	/**
	 * 添加书签 自定义
	 */
	public static String s_zidingyi = "5_2";
	
	/**
	 * 添加书签 历史
	 */
	public static String s_lishi = "5_3";
	
	/**
	 * 扫一扫   （调到这里了）
	 */
	public static String m_saoyidao = "6";
	
	/**
	 * 扫一扫 相册
	 */
	public static String s_saoyisao_xiangce = "6_1";
	
	/**
	 * 扫一扫 我的
	 */
	public static String s_saoyisao_wode = "6_2";
	
	/**
	 * 扫一扫 生成二维码
	 */
	public static String s_shengchengerweima = "6_3";
	
	/**
	 * 扫一扫 扫描历史
	 */
	public static String s_saomiaolishi = "6_4";
	
	/**
	 * 扫一扫 生成历史
	 */
	public static String s_shengchenglishi = "6_5";
	
	
	/**
	 * 顶部输入网址或关键字
	 */
	public static String m_wangzhi_guanjianzi = "7";
	
	/**
	 * 顶部选择搜索引擎
	 */
	public static String m_xuanze_sousuo_yinqing = "8";
	
	/**
	 * 顶部书签/历史
	 */
	public static String m_dingbu_shuqian_lishi = "9";
	
	/**
	 * 底部菜单栏
	 */
	public static String m_dibu_caidan = "10";
	
	/**
	 * 底部菜单栏 全屏
	 */
	public static String s_dibu_quanping = "10_1";
	
	/**
	 * 底部菜单栏 书签/历史
	 */
	public static String s_dibu_shuqian_lishi = "10_2";
	
	/**
	 * 桌底部菜单栏 加书签
	 */
	public static String s_dibu_jiashuqian = "10_3";
	
	/**
	 * 底部菜单栏 夜间
	 */
	public static String s_dibu_yejian = "10_4";
	
	/**
	 * 底部菜单栏 分享
	 */
	public static String s_dibu_fenxiang = "10_5";
	
	/**
	 * 底部菜单栏 下载
	 */
	public static String s_dibu_xiazai = "10_6";
	
	/**
	 * 底部菜单栏 更新
	 */
	public static String s_dibu_gengxin = "10_7";
	
	/**
	 * 底部菜单栏 退出
	 */
	public static String s_dibu_tuichu = "10_8";
	
	/**
	 * 底部菜单栏 设置
	 */
	public static String s_dibu_shezhi = "10_9";
	
	/**
	 * 底部菜单栏 屏幕
	 */
	public static String s_dibu_pingmu = "10_10";
	
	/**
	 * 底部菜单栏 翻页模式
	 */
	public static String s_dibu_yanyemoshi = "10_11";
	
	/**
	 * 底部菜单栏 复制链接
	 */
	public static String s_dibu_fuzhilianjie = "10_12";
	
	/**
	 * 底部菜单栏 无痕
	 */
	public static String s_dibu_wuheng = "10_13";
	
	/**
	 * 底部菜单栏 无图
	 */
	public static String s_dibu_wutu = "10_14";
	
	/**
	 * 底部菜单栏 页内查找
	 */
	public static String s_dibu_yeneichazhao = "10_15";
	
	/**
	 * 底部菜单栏设置 清除数据
	 */
	public static String s_dibu_qingchu_shuju = "10_16";
	
	/**
	 * 底部菜单栏设置 流量统计
	 */
	public static String s_dibu_liuliang_tongji = "10_17";
	
	/**
	 * 底部菜单栏设置 建议反馈
	 */
	public static String s_dibu_jianyi_fankui = "10_18";
	
	/**
	 * 底部菜单栏设置 新手指南
	 */
	public static String s_dibu_xinshouzhinan = "10_19";
	
	/**
	 * 底部菜单栏设置 帮助
	 */
	public static String s_dibu_bangzhu = "10_20";
	
	
	/**
	 * 广告 底部横屏广告
	 */
	public static String m_guanggao_dibuhengping = "11";
	
	/**
	 * 广告 中间插屏广告
	 */
	public static String m_guanggao_zhongjianchaping = "12";
	
	/**
	 * 关键字点击的次数
	 */
	public static String m_guanjianzi = "13";
	
}
