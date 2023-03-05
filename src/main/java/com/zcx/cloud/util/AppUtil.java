package com.zcx.cloud.util;

import java.io.Serializable;
import java.util.Objects;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.zcx.cloud.user.entity.User;


public interface AppUtil {

	/**
	 * 检查是否认证
	 * @return
	 */
	public static boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		return subject.isAuthenticated();
	}
	
	/**
	 * 获取Shiro在ThreadContext中绑定的当前用户对象
	 * @return 当前用户对象
	 */
	public static User getCurrentUser() {
		Subject subject = SecurityUtils.getSubject();
		Object principal = subject.getPrincipal();
		if(principal instanceof User)
			return (User)principal;
		return null;
	}
	
	/**
	 * 获取当前用户IP地址
	 * @return
	 */
	public static String getCurrentIp() {
		Session session = SecurityUtils.getSubject().getSession();
		return Objects.isNull(session)?null:session.getHost();
	}
	
	/**
	 * 获取当前SessionID
	 * @return
	 */
	public static String getSessionId() {
		Serializable sessionId = SecurityUtils.getSubject().getSession().getId();
		return sessionId instanceof String?(String)sessionId:null;
	}
	
	/**
	 * 下划线转驼峰
	 * @param str
	 * @return
	 */
	public static String underLineToCamelCase(String str) {
		StringBuilder stringBuilder = new StringBuilder();
		if(Objects.isNull(str))
			return null;
		String upperCase = str.toUpperCase();
		char[] charArray = str.toCharArray();
		boolean isUnderline = false;
		for(int i=0;i<charArray.length;i++) {
			if(charArray[i] == '_') {
				isUnderline = true;
				continue;
			}
			if(isUnderline) {
				stringBuilder.append(upperCase.charAt(i));
				isUnderline = false;
			}
			else {
				stringBuilder.append(charArray[i]);
			}
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * 驼峰转下划线
	 * @param str
	 * @return
	 */
	public static String camelCaseToUnderLine(String str) {
		String result = "";
		if(Objects.isNull(str))
			return null;
		
		char[] arr = str.toCharArray();
		for(int i=0;i<arr.length;i++) {
			if(arr[i]>=65&&arr[i]<=90) {
				result+="_"+arr[i];
			}else {
				result+=arr[i];
			}
		}
		
		return result.toLowerCase();
	}
}
