package com.zcx.cloud.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface IpAddressUtil {
	public static final String HTTP_PREFIX = "http://";
	
	/**
	 * 获取主机IP地址
	 * @return
	 */
	public static String getAddress() {
		String address = null;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			address = inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			address = null;
			e.printStackTrace();
		}
		return address;
	}
	
	/**
	 * 获取项目启动地址
	 * @return
	 */
	public static String getUrl(int port) {
		String url = "";
		try {
			url += HTTP_PREFIX;
			url += getAddress();
			url += ":"+port;
		}catch(Exception e) {
			e.printStackTrace();
			url = "";
		}
		return url;
	}
}
