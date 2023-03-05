package com.zcx.cloud.job.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.job.entity.Job;

import java.util.List;

import org.quartz.SchedulerException;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 定时任务表 服务类
 * </p>
 *
 
 */
public interface IJobService extends IService<Job> {

	/**
	 * 分页条件查询定时任务信息
	 * @param job
	 * @param pageInfo
	 * @param sortInfo
	 * @return
	 */
	Result getJobsPage(Job job, PageInfo<Job> pageInfo, SortInfo sortInfo);

	/**
	 * 新增定时任务
	 * @param job
	 */
	void saveJob(Job job, Boolean isStart) throws SchedulerException;

	/**
	 * 逻辑删除定时任务
	 * @param jobId
	 */
	void logicDelete(Long jobId) throws SchedulerException ;

	/**
	 * 批量逻辑删除定时任务
	 * @param jobIds
	 */
	void logicDeleteBatch(List<Long> jobIds) throws SchedulerException;

	/**
	 * 开启定时任务
	 * @param jobId
	 */
	void startJob(Long jobId) throws SchedulerException;

	/**
	 * 暂停定时任务
	 * @param jobId
	 */
	void pauseJob(Long jobId) throws SchedulerException;

	/**
	 * 停止定时任务
	 * @param jobId
	 */
	void stopJob(Long jobId) throws SchedulerException;

	/**
	 * 修改定时任务，修改完成后，任务须手动重新启动
	 * @param job
	 * @throws SchedulerException
	 */
	void updateJob(Job job) throws SchedulerException;

}
