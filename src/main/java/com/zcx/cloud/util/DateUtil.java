package com.zcx.cloud.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface DateUtil {

	public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	public static String YYYY_MM_DD = "yyyy-MM-dd";
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static Date now() {
		return new Date();
	}
	
	/**
	 * 字符串按指定格式转换成Date对象
	 * @param source
	 * @param pattern
	 * @return
	 */
	public static Date toDate(String source, String pattern) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.parse(source);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Date对象按指定格式转换成字符串
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(Date date, String pattern) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.format(date);
		} catch(Exception e) {
			return null;
		}
	}
}
