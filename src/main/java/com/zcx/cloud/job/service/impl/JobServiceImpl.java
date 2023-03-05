package com.zcx.cloud.job.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.job.entity.Job;
import com.zcx.cloud.job.mapper.JobMapper;
import com.zcx.cloud.job.service.IJobService;
import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.role.entity.RolePermission;
import com.zcx.cloud.system.scheduler.helper.SchedulerHelper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.assertj.core.util.Strings;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务表 服务实现类
 * </p>
 *
 
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements IJobService {
	@Autowired
	private JobMapper jobMapper;
	@Autowired
	private SchedulerHelper schedulerHelper;
	
	@Override
	public Result getJobsPage(Job job, PageInfo<Job> pageInfo, SortInfo sortInfo) {
		LambdaQueryWrapper<Job> queryWrapper = new LambdaQueryWrapper<Job>();
		//设置查询条件
		if(Objects.nonNull(job)) {
			if(!Strings.isNullOrEmpty(job.getJobName()))
				queryWrapper.like(Job::getJobName, job.getJobName());
			if(!Strings.isNullOrEmpty(job.getJobGroup()))
				queryWrapper.like(Job::getJobGroup, job.getJobGroup());
			if(!Strings.isNullOrEmpty(job.getBeanName()))
				queryWrapper.like(Job::getBeanName, job.getBeanName());
			if(!Strings.isNullOrEmpty(job.getBeanMethod()))
				queryWrapper.like(Job::getBeanMethod, job.getBeanMethod());
			if(Objects.nonNull(job.getStartTime()))
				queryWrapper.gt(Job::getStartTime, job.getStartTime());
			if(Objects.nonNull(job.getEndTime()))
				queryWrapper.gt(Job::getEndTime, job.getEndTime());
			if(!Strings.isNullOrEmpty(job.getCreateBy()))
				queryWrapper.like(Job::getCreateBy, job.getCreateBy());
			if(Objects.nonNull(job.getCreateTime()))
				queryWrapper.gt(Job::getCreateTime, job.getCreateTime());
		}
		queryWrapper.eq(Job::getValid, Constant.VALID_NORMAL);
		
		//排序信息
		Page<Job> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Job> selectPage = jobMapper.selectPage(page, queryWrapper);
		long count = selectPage.getTotal();
		List<Job> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	
	@Override
	public void saveJob(Job job, Boolean isStart) throws SchedulerException {
		//1.持久化定时任务信息
		job.setStatus(isStart?Constant.JOB_START:Constant.JOB_STOP);
		job.setCreateBy(AppUtil.getCurrentUser().getUsername());
		job.setCreateTime(DateUtil.now());
		jobMapper.insert(job);
		
		//2.开启定时任务
		if(isStart) {
			schedulerHelper.startJob(job);
		}
	}


	@Override
	public void logicDelete(Long jobId) throws SchedulerException {
		//1.停止定时任务
		Job job = jobMapper.selectById(jobId);
		schedulerHelper.stopJob(job);
		
		//2.逻辑删除定时任务
		job.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		job.setUpdateTime(DateUtil.now());
		job.setStatus(Constant.JOB_STOP);
		job.setValid(Constant.VALID_DELETE);
		jobMapper.updateById(job);
	}


	@Override
	public void logicDeleteBatch(List<Long> jobIds) throws SchedulerException {
		for(Long jobId:jobIds) {
			this.logicDelete(jobId);
		}
	}


	@Override
	public void startJob(Long jobId) throws SchedulerException {
		//1.开启定时任务
		Job job = jobMapper.selectById(jobId);
		switch(job.getStatus()) {
			case Constant.JOB_STOP:{//停止状态启动
				schedulerHelper.startJob(job);		
			}break;
			case Constant.JOB_PAUSE:{//暂停状态启动
				schedulerHelper.resumeJob(job);
			}break;
		}
		
		//2.修改定时任务状态
		job.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		job.setUpdateTime(DateUtil.now());
		job.setStatus(Constant.JOB_START);
		jobMapper.updateById(job);
	}


	@Override
	public void pauseJob(Long jobId) throws SchedulerException {
		//1.暂停定时任务
		Job job = jobMapper.selectById(jobId);
		schedulerHelper.pauseJob(job);
		
		//2.修改定时任务状态
		job.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		job.setUpdateTime(DateUtil.now());
		job.setStatus(Constant.JOB_PAUSE);
		jobMapper.updateById(job);
	}


	@Override
	public void stopJob(Long jobId) throws SchedulerException {
		//1.停止定时任务
		Job job = jobMapper.selectById(jobId);
		schedulerHelper.stopJob(job);
		
		//2.修改定时任务状态
		job.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		job.setUpdateTime(DateUtil.now());
		job.setStatus(Constant.JOB_STOP);
		jobMapper.updateById(job);
	}


	@Override
	public void updateJob(Job job) throws SchedulerException {
		//1.停止原先的定时任务
		Job jobOld = jobMapper.selectById(job.getJobId());
		schedulerHelper.stopJob(jobOld);
		
		//2.修改定时任务信息
		job.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		job.setUpdateTime(DateUtil.now());
		job.setStatus(Constant.JOB_STOP);
		jobMapper.updateById(job);
	}

}
