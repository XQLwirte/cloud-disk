package com.zcx.cloud.system.exception;

/**
 * 已存在异常（用户名唯一,角色吗唯一等等）
 * @author Administrator
 *
 */
public class ExistsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ExistsException(String msg) {
		super(msg);
	}
}
