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
						// �ύ�ɹ� ɾ���ļ�
						Log.v("LS","�û�����ͳ�� �ύ�ɹ� ɾ���ļ�");
						deleteTongJiFile();
						break;
					case 1:
						// �ύʧ�� �´μ����ύ
						Log.v("LS","�û�����ͳ�� �ύʧ��");
						break;
					}
				}
			}
		};
		Thread thread = new Thread(TongJiUtil.getInstance(context, handler));
		thread.start();
	}
	
	
	
	
	
	
	
	/**
	 * ���������������
	 */
	public static String d_qingliulanqi = "1";
	
	/**
	 * ֪ͨ�� ����
	 */
	public static String n_gengxin = "3_1";
	
	/**
	 * ֪ͨ�� ���
	 */
	public static String n_ad_2 = "3_2";
	
	/**
	 * ����Ļ
	 */
	public static String m_zuopinggouwu = "4";
	
	/**
	 * �����ǩ
	 */
	public static String m_tianjiashuqian = "5";
	
	/**
	 * �����ǩ ����
	 */
	public static String s_remen = "5_1";
	
	/**
	 * �����ǩ �Զ���
	 */
	public static String s_zidingyi = "5_2";
	
	/**
	 * �����ǩ ��ʷ
	 */
	public static String s_lishi = "5_3";
	
	/**
	 * ɨһɨ   �����������ˣ�
	 */
	public static String m_saoyidao = "6";
	
	/**
	 * ɨһɨ ���
	 */
	public static String s_saoyisao_xiangce = "6_1";
	
	/**
	 * ɨһɨ �ҵ�
	 */
	public static String s_saoyisao_wode = "6_2";
	
	/**
	 * ɨһɨ ���ɶ�ά��
	 */
	public static String s_shengchengerweima = "6_3";
	
	/**
	 * ɨһɨ ɨ����ʷ
	 */
	public static String s_saomiaolishi = "6_4";
	
	/**
	 * ɨһɨ ������ʷ
	 */
	public static String s_shengchenglishi = "6_5";
	
	
	/**
	 * ����������ַ��ؼ���
	 */
	public static String m_wangzhi_guanjianzi = "7";
	
	/**
	 * ����ѡ����������
	 */
	public static String m_xuanze_sousuo_yinqing = "8";
	
	/**
	 * ������ǩ/��ʷ
	 */
	public static String m_dingbu_shuqian_lishi = "9";
	
	/**
	 * �ײ��˵���
	 */
	public static String m_dibu_caidan = "10";
	
	/**
	 * �ײ��˵��� ȫ��
	 */
	public static String s_dibu_quanping = "10_1";
	
	/**
	 * �ײ��˵��� ��ǩ/��ʷ
	 */
	public static String s_dibu_shuqian_lishi = "10_2";
	
	/**
	 * ���ײ��˵��� ����ǩ
	 */
	public static String s_dibu_jiashuqian = "10_3";
	
	/**
	 * �ײ��˵��� ҹ��
	 */
	public static String s_dibu_yejian = "10_4";
	
	/**
	 * �ײ��˵��� ����
	 */
	public static String s_dibu_fenxiang = "10_5";
	
	/**
	 * �ײ��˵��� ����
	 */
	public static String s_dibu_xiazai = "10_6";
	
	/**
	 * �ײ��˵��� ����
	 */
	public static String s_dibu_gengxin = "10_7";
	
	/**
	 * �ײ��˵��� �˳�
	 */
	public static String s_dibu_tuichu = "10_8";
	
	/**
	 * �ײ��˵��� ����
	 */
	public static String s_dibu_shezhi = "10_9";
	
	/**
	 * �ײ��˵��� ��Ļ
	 */
	public static String s_dibu_pingmu = "10_10";
	
	/**
	 * �ײ��˵��� ��ҳģʽ
	 */
	public static String s_dibu_yanyemoshi = "10_11";
	
	/**
	 * �ײ��˵��� ��������
	 */
	public static String s_dibu_fuzhilianjie = "10_12";
	
	/**
	 * �ײ��˵��� �޺�
	 */
	public static String s_dibu_wuheng = "10_13";
	
	/**
	 * �ײ��˵��� ��ͼ
	 */
	public static String s_dibu_wutu = "10_14";
	
	/**
	 * �ײ��˵��� ҳ�ڲ���
	 */
	public static String s_dibu_yeneichazhao = "10_15";
	
	/**
	 * �ײ��˵������� �������
	 */
	public static String s_dibu_qingchu_shuju = "10_16";
	
	/**
	 * �ײ��˵������� ����ͳ��
	 */
	public static String s_dibu_liuliang_tongji = "10_17";
	
	/**
	 * �ײ��˵������� ���鷴��
	 */
	public static String s_dibu_jianyi_fankui = "10_18";
	
	/**
	 * �ײ��˵������� ����ָ��
	 */
	public static String s_dibu_xinshouzhinan = "10_19";
	
	/**
	 * �ײ��˵������� ����
	 */
	public static String s_dibu_bangzhu = "10_20";
	
	
	/**
	 * ��� �ײ��������
	 */
	public static String m_guanggao_dibuhengping = "11";
	
	/**
	 * ��� �м�������
	 */
	public static String m_guanggao_zhongjianchaping = "12";
	
	/**
	 * �ؼ��ֵ���Ĵ���
	 */
	public static String m_guanjianzi = "13";
	
}
