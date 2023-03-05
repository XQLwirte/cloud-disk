package com.zcx.cloud.job.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.job.entity.Job;
import com.zcx.cloud.job.service.IJobService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.scheduler.helper.SchedulerHelper;
import com.zcx.cloud.util.DateUtil;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 定时任务表 前端控制器
 * </p>
 *
 
 */
@RestController
@RequestMapping("/job")
public class JobController extends BaseController {
	@Autowired
	private IJobService jobService;
	@Autowired
	private SchedulerHelper schedulerHelper;
	
	@RequiresPermissions("job:view")
	@GetMapping("getJobsPage")
	public Result getJobsPage(Job job,PageInfo<Job> pageInfo,SortInfo sortInfo) {
		try {
			Result result = jobService.getJobsPage(job, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取定时任务分页信息失败");
		}
	}

	@SysLog("添加定时任务")
	@RequiresPermissions("job:add")
	@PostMapping("jobAdd")
	public Result jobAdd(Job job, @RequestParam(value = "isStart" ,defaultValue = "0")Boolean isStart) {
		try {
			jobService.saveJob(job, isStart);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("添加定时任务失败");
		}
	}
	
	@SysLog("删除定时任务")
	@RequiresPermissions("job:delete")
	@GetMapping("jobDelete/{jobId}")
	public Result jobDelete(@PathVariable("jobId")Long jobId) {
		try {
			jobService.logicDelete(jobId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("删除定时任务失败");
		}
	}
	
	@SysLog("批量删除定时任务")
	@RequiresPermissions("job:delete")
	@GetMapping("jobDeleteBatch/{jobIdsStr}")
	public Result jobDeleteBatch(@PathVariable("jobIdsStr")String jobIdsStr) {
		try {
			List<Long> jobIds = StringUtil.stringToLongs(jobIdsStr, Constant.COMMA);
			jobService.logicDeleteBatch(jobIds);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("批量删除定时任务失败");
		}
	}
	
	@SysLog("开启定时任务")
	@RequiresPermissions("job:update")
	@GetMapping("jobStart/{jobId}")
	public Result jobStart(@PathVariable("jobId")Long jobId) {
		try {
			jobService.startJob(jobId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("开启定时任务失败");
		}
	}
	
	@SysLog("暂停定时任务")
	@RequiresPermissions("job:update")
	@GetMapping("jobPause/{jobId}")
	public Result jobPause(@PathVariable("jobId")Long jobId) {
		try {
			jobService.pauseJob(jobId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("暂停定时任务失败");
		}
	}
	
	@SysLog("停止定时任务")
	@RequiresPermissions("job:update")
	@GetMapping("jobStop/{jobId}")
	public Result jobStop(@PathVariable("jobId")Long jobId) {
		try {
			jobService.stopJob(jobId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("停止定时任务失败");
		}
	}
	
	@SysLog("修改定时任务")
	@RequiresPermissions("job:update")
	@PostMapping("jobUpdate")
	public Result jobUpdate(Job job) {
		try {
			jobService.updateJob(job);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("修改定时任务失败");
		}
	}
	
}
