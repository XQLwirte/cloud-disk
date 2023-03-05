package com.zcx.cloud.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.assertj.core.util.Strings;

import com.zcx.cloud.common.constant.Constant;

/**
 * 字符串操作工具
 * @author Administrator
 *
 */
public interface StringUtil {

	/**
	 * 将字符串分割称Long集合
	 * @param src 源字符串
	 * @param separator 分隔符
	 * @return
	 */
	public static List<Long> stringToLongs(String src,String separator){
		List<Long> result = new ArrayList<Long>();
		if(Objects.isNull(src)||Objects.isNull(separator))
			return result;
		String[] longStrs = src.split(separator);
		for(String longStr:longStrs) {
			try {
				Long value = Long.valueOf(longStr);
				result.add(value);
			}catch (Exception e) {
			}
		}
		return result;
	}
	
	/**
	 * 将对象列表按分隔符间隔为字符串
	 * @param list
	 * @param separator
	 * @return
	 */
	public static String objsToString(List list, String separator) {
		String result = "";
		if(CollectionUtil.isNullOrSizeLeZero(list)||Objects.isNull(separator))
			return result;
		
		for(int i = 0;i<list.size();i++) {
			if(i == 0) {
				result += list.get(i);
			}else {
				result += separator + list.get(i);
			}
		}
		
		return result;
	}
	
	/**
	 * 获取文件名的拓展名
	 * @param fileName
	 * @return
	 */
	public static String getExtension(String fileName) {
		if(Strings.isNullOrEmpty(fileName))
			return null;
		String[] strings = fileName.split("\\.");
		return strings[strings.length-1];
	}
	
	
	/**
	 * 去除文件名的拓展名
	 * @param fileName
	 * @return
	 */
	public static String excludeExtension(String fileName) {
		try {
			String extension = StringUtil.getExtension(fileName);
			return fileName.substring(0, fileName.lastIndexOf(extension)-1);
		}catch(Exception e) {
			return null;
		}
		
	}
}
