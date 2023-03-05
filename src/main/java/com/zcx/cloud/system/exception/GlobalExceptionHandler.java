package com.zcx.cloud.system.exception;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.util.ViewUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 * @author Administrator
 *
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * SysExeception异常处理
	 * @param e
	 */
	@ExceptionHandler(value = BusException.class)
	public String handleSysException(BusException e) {
		return ViewUtil.redirect("error/500");
	}
	
	/**
	 * AuthorizationException无权限异常处理
	 * @param e
	 */
	@ExceptionHandler(value = AuthorizationException.class)
	@ResponseBody
	public Result handleAuthorizationException(AuthorizationException e) {
		return new Result().code(HttpStatus.FORBIDDEN.value()).msg("权限不足");
	}
	
}
