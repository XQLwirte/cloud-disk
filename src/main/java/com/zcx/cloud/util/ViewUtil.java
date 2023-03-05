package com.zcx.cloud.util;

import org.assertj.core.util.Strings;

import com.zcx.cloud.common.constant.Constant;

public interface ViewUtil {
	
	/**
	 * 获取视图完整路径
	 * @param path
	 * @return
	 */
	public static String view(String path) {
		return Strings.concat(Constant.VIEW_PREFIX,path,Constant.VIEW_SUFFIX);
	}
	
	/**
	 * 重定向请求
	 * @param path 重定向路径
	 */
	public static String redirect(String path) {
		return "redirect:/" + path;
	}
}
