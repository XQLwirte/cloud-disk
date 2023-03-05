package com.zcx.cloud.system.scheduler.listener;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.job.entity.Job;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuartzJobListener implements JobListener{
	public static final String ALL_JOB_LISTENER = "all_job_listener";
	
	/**
	 * 获取Job Listener名称
	 */
	@Override
	public String getName() {
		return QuartzJobListener.ALL_JOB_LISTENER;
	}

	/**
	 * JobDetail将要被执行
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		
	}

	/**
	 * Trigger被终止时执行，Job执行终止
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		
	}

	/**
	 * JobDetail执行完毕
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Job job = (Job) dataMap.get(Constant.JOB_ENTITY_KEY);
		
		log.info("定时任务：{}(执行完毕)",job.getJobName());
	}

}
