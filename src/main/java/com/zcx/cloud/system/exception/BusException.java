package com.zcx.cloud.system.exception;


/**
 * Business 业务异常
 * @author Administrator
 *
 */
public class BusException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusException(String msg) {
		super(msg);
	}
}
