package com.zcx.cloud.system.exception;

/**
 * Redisli连接异常
 * @author Administrator
 *
 */
public class RedisConnectException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RedisConnectException(String msg) {
		super(msg);
	}
}
