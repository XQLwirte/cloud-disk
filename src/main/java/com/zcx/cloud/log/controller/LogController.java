package com.zcx.cloud.log.controller;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.log.service.ILogService;
import com.zcx.cloud.log.service.impl.LogServiceImpl;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 日志表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/log")
public class LogController extends BaseController {
	@Autowired
	private ILogService logService;
	
	@RequiresPermissions("log:view")
	@GetMapping("getLogsPage")
	public Result getLogsPage(Log log,PageInfo<Log> pageInfo,SortInfo sortInfo) {
		try {
			Result result = logService.getLogsPage(log, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取日志分页信息失败");
		}
	}
	
	/**
	 * 单个删除日志信息
	 * @param logId
	 * @return
	 */
	@SysLog("删除日志")
	@RequiresPermissions("log:delete")
	@GetMapping("logDelete/{logId}")
	public Result logDelete(@PathVariable("logId")Long logId) {
		try {
			logService.logicDelete(logId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("删除日志失败");
		}
	}
	
	
	/**
	 * 批量删除日志信息
	 * @param logIdsStr
	 * @return
	 */
	@SysLog("批量删除日志")
	@RequiresPermissions("log:delete")
	@GetMapping("logDeleteBatch/{logIdsStr}")
	public Result logDeleteBatch(@PathVariable("logIdsStr")String logIdsStr) {
		try {
			List<Long> logIds = StringUtil.stringToLongs(logIdsStr, Constant.COMMA);
			logService.logicDeleteBatch(logIds);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("批量删除日志失败");
		}
	}
}
