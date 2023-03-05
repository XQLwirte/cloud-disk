package com.zcx.cloud.system.exception;

/**
 * 存储空间不足
 * @author ZCX
 *
 */
public class OutOfStorageException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutOfStorageException(String msg) {
		super(msg);
	}
	
}
