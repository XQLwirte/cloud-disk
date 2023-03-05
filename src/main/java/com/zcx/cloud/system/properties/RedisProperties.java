package com.zcx.cloud.system.properties;

import lombok.Data;

/**
 * RedisPool配置信息
 * @author Administrator
 *
 */
@Data
public class RedisProperties {
	private String host;
	private String password;
	private int port;
	private int timeout;
	private int database;
	private boolean ssl;
	private int maxIdle;
	private int maxTotal;
	private int maxWait;
	private boolean testOnBorrow;
}
