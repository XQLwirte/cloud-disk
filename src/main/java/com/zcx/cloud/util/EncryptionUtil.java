package com.zcx.cloud.util;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 加密工具
 * @author Administrator
 *
 */
public interface EncryptionUtil {
	/**
	 * 加密散列次数
	 */
	public static final int HASH_ITERATIONS = 6;
	
	/**
	 * MD5加密
	 * @param password 加密内容
	 * @param salt md5加密盐值
	 * @return
	 */
	public static String md5(String password,String salt) {
		SimpleHash hash = new SimpleHash("md5", password, salt,EncryptionUtil.HASH_ITERATIONS);
		return hash.toString();
	}
	
}
