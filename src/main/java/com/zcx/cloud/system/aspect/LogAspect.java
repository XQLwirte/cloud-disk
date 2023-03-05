package com.zcx.cloud.system.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.log.service.ILogService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAspect {
	@Autowired
	private ILogService LogService;
	
	/**
	 * 重用切点表达式
	 */
	@Pointcut("@annotation(com.zcx.cloud.system.annotaion.SysLog)")
	public void sysLog() {} //方法名后续使用，代表切点表达式
	
	/**
	 * 环绕通知
	 * @throws Throwable 
	 */
	@Around("sysLog()")
	public Object logAround(ProceedingJoinPoint  joinPoint) throws Throwable {
		//前置
		
		//方法执行
		Object result = joinPoint.proceed();
		
		//后置，保存日志信息
		saveSysLog(joinPoint);
		
		return result;
	}
	
	/**
	 * 保存系统日志信息
	 * @param joinPoint
	 */
	private void saveSysLog(ProceedingJoinPoint joinPoint) {
		try {
			//1.获取方法上的注解参数
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			SysLog sysLog = method.getAnnotation(SysLog.class);
			String content = sysLog.value();
			
			//2.构建日志对象
			Log log = new Log();
			log.setLogContent(content);
			log.setIp(AppUtil.getCurrentIp());
			log.setCreateBy(AppUtil.getCurrentUser().getUsername());
			log.setCreateTime(DateUtil.now());
			
			//3.保存日志信息
			LogService.save(log);
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("保存日志出错");
		}
	}
}
