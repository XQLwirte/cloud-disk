package com.zcx.cloud.system.exception;

/**
 * 不允许登陆异常，用于控制前后台角色登陆控制
 * @author ZCX
 *
 */
public class UnAllowLoginException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnAllowLoginException(String msg) {
		super(msg);
	}
	
}
