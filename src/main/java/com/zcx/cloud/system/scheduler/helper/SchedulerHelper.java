package com.zcx.cloud.system.scheduler.helper;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.job.entity.Job;
import com.zcx.cloud.system.scheduler.runner.JobRunner;

import lombok.extern.slf4j.Slf4j;


/**
 * 定时任务工具
 * @author Administrator
 *
 */
@Slf4j
@Component
public class SchedulerHelper {
	@Autowired
	private Scheduler scheduler;
	
	/**
	 * 开启定时任务
	 * @param job
	 * @throws SchedulerException 
	 */
	public void startJob(Job job) throws SchedulerException {
		//检查定时任务是否已启动
		JobKey jobKey = getJobKey(job);
		boolean exists = scheduler.checkExists(jobKey);
		if(!exists) {
			//1.装载Job实例数据
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(Constant.JOB_ENTITY_KEY, job);
			
			//2.创建JobDetail
			JobDetail jobDetail = JobBuilder.newJob(JobRunner.class)
					.withIdentity(jobKey)
					.setJobData(dataMap)
					.build();
			
			//3.创建Trigger
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(job.getJobName(), job.getJobGroup())
					.forJob(jobDetail)
					.withSchedule(CronScheduleBuilder.cronSchedule(job.getCron()))
					.startAt(job.getStartTime())
					.endAt(job.getEndTime())
					.startNow()
					.build();
			
			//4.启动定时任务
			scheduler.scheduleJob(jobDetail, trigger);
			log.info("定时任务[{}]已启动", job.getJobName());
		}
	}
	
	/**
	 * 暂停定时任务
	 * @throws SchedulerException 
	 */
	public void pauseJob(Job job) throws SchedulerException {
		//检查定时任务是否已启动
		JobKey jobKey = getJobKey(job);
		boolean exists = scheduler.checkExists(jobKey);
		if(exists) {
			scheduler.pauseJob(jobKey);
			log.info("定时任务[{}]已暂停", job.getJobName());
		}
	}
	
	/**
	 * 恢复暂停的任务
	 * @param job
	 * @throws SchedulerException 
	 */
	public void resumeJob(Job job) throws SchedulerException {
		JobKey jobKey = getJobKey(job);
		boolean exists = scheduler.checkExists(jobKey);
		if(exists) {
			scheduler.resumeJob(jobKey);
			log.info("定时任务[{}]已恢复", job.getJobName());
		}
	}
	
	/**
	 * 停止定时任务
	 * @throws SchedulerException 
	 */
	public void stopJob(Job job) throws SchedulerException {
		//检查定时任务是否已启动
		JobKey jobKey = getJobKey(job);
		boolean exists = scheduler.checkExists(jobKey);
		if(exists) {
			scheduler.deleteJob(jobKey);
			log.info("定时任务[{}]已停止", job.getJobName());
		}
	}
	
	
	private JobKey getJobKey(Job job) {
		JobKey jobKey = new JobKey(job.getJobName() + job.getJobId(), job.getJobGroup());
		return jobKey;
	}
}
