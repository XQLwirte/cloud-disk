package com.zcx.cloud.task.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.job.entity.Job;
import com.zcx.cloud.job.mapper.JobMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除无效定时任务定时任务
 * 
 * 1.删除无效的定时任务基本信息
 * 涉及表：t_job
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteJob")
public class TaskDeleteJob {
	@Autowired
	private JobMapper jobMapper;
	
	public void deleteJob() {
		//1.删除无效定时任务信息
		LambdaQueryWrapper<Job> qwJ = new LambdaQueryWrapper<Job>();
		qwJ.eq(Job::getValid, Constant.VALID_DELETE);
		jobMapper.delete(qwJ);
	}
	
}
