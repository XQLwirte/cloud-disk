package com.zcx.cloud.garbage.controller;


import java.util.List;

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
import com.zcx.cloud.common.vo.TimeData;
import com.zcx.cloud.garbage.entity.Garbage;
import com.zcx.cloud.garbage.service.IGarbageService;
import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 垃圾箱 前端控制器
 * </p>
 *
 
 */
@RestController
@RequestMapping("/garbage")
public class GarbageController extends BaseController {
	@Autowired
	private IGarbageService garbageService;
	
	@RequiresPermissions("garbage:view")
	@GetMapping("getGarbagesPage")
	public Result getGarbagesPage(Garbage garbage,PageInfo<Garbage> pageInfo,SortInfo sortInfo) {
		try {
			Result result = garbageService.getGarbagesPage(garbage, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取垃圾分页信息失败");
		}
	}
	
	@RequiresPermissions("garbage:view")
	@GetMapping("getGarbagesTimeData")
	public Result getGarbagesTimeData() {
		try {
			List<TimeData<Garbage>> timeData = garbageService.getCurrentGarbagesTimeData();
			return new Result().success().data(timeData);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取垃圾数据失败");
		}
	}

	@SysLog("恢复垃圾")
	@RequiresPermissions("garbage:update")
	@GetMapping("garbageResume/{garbageId}")
	public Result garbageResume(@PathVariable("garbageId")Long garbageId) {
		try {
			garbageService.garbageResume(garbageId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("恢复文件失败");
		}
	}
	
	@SysLog("删除垃圾")
	@RequiresPermissions("garbage:delete")
	@GetMapping("garbageDelete/{garbageId}")
	public Result garbageDelete(@PathVariable("garbageId")Long garbageId) {
		try {
			garbageService.logicDelete(garbageId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("删除文件失败");
		}
	}
	
	@SysLog("批量删除垃圾")
	@RequiresPermissions("garbage:delete")
	@GetMapping("garbageDeleteBatch/{garbageIdsStr}")
	public Result garbageDeleteBatch(@PathVariable("garbageIdsStr")String garbageIdsStr) {
		try {
			List<Long> garbageIds = StringUtil.stringToLongs(garbageIdsStr, Constant.COMMA);
			garbageService.logicDeleteBatch(garbageIds);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("批量删除文件失败");
		}
	}
}
