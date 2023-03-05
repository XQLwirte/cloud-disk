package com.zcx.cloud.system.exception;

/**
 * 用户已登录异常，用于当用户登录
 * @author Administrator
 *
 */
public class UserLoginedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserLoginedException(String msg) {
		super(msg);
	}
}
