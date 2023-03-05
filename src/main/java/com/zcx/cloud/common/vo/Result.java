package com.zcx.cloud.common.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.http.HttpStatus;

/**
 * VO表现层所需对象,主要用于数据表格
 * @author Administrator
 *
 */
public class Result extends HashMap<String, Object>{
	public static final Integer TABLE_SUCCESS_CODE = 0;
	public static final String TABLE_DEFAULT_MSG = "";
	
	public Result code(int code) {
		this.put("code", code);
		return this;
	}
	
	public Result msg(String msg) {
		this.put("msg", msg);
		return this;
	}
	
	public Result count(long count) {
		this.put("count", count);
		return this;
	}
	
	public Result data(Object data) {
		this.put("data", data);
		return this;
	}
	
	/**
	 * 成功码
	 */
	public Result success() {
		this.code(HttpStatus.OK.value());
		return this;
	}
	
	/**
	 * 错误码
	 */
	public Result error() {
		this.code(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return this;
	}
	
	public Result put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
