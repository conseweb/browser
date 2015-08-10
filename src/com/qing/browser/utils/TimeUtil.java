package com.qing.browser.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
	private static final String[] WEEK = { "��", "һ", "��", "��", "��", "��", "��" };
	public static final String XING_QI = "����";
	public static final String ZHOU = "��";

	public static String getWeek(int num, String format) {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int weekNum = c.get(Calendar.DAY_OF_WEEK) + num;
		if (weekNum > 7)
			weekNum = weekNum - 7;
		return format + WEEK[weekNum - 1];
	}

	public static String getZhouWeek() {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd");
		return format.format(new Date(System.currentTimeMillis())) + " "
				+ getWeek(0, ZHOU);
	}

	public static String getDay(long timesamp) {
		if(timesamp == 0L)
			return "δ";
		String result = "δ";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "����" + getTime(timesamp);
			break;
		case 1:
			result = "����"+ getTime(timesamp);
			break;
		case 2:
			result = "ǰ��"+ getTime(timesamp);
			break;

		default:
			result = temp + "��ǰ"+ getTime(timesamp);
			break;
		}

		return result;
	}

	public static long getLongTime(String time) {
		try {
			time = time.substring(0, time.indexOf('.'));
			Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
					.parse(time);
			return date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public static String getTime(long time) {
		return new SimpleDateFormat("HH:mm").format(new Date(time));
	}

}
