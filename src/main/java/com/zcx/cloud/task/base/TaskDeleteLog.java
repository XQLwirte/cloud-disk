package com.zcx.cloud.task.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.log.mapper.LogMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除日志定时任务
 * 
 * 1.删除定时任务基本信息
 * 涉及表：t_log
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteLog")
public class TaskDeleteLog {
	@Autowired
	private LogMapper logMapper;

	public void deleteLog() {
		//1.删除无效日志信息
		LambdaQueryWrapper<Log> qwL = new LambdaQueryWrapper<Log>();
		qwL.eq(Log::getValid, Constant.VALID_DELETE);
		logMapper.delete(qwL);
	}
	
}
