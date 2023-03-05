package com.zcx.cloud.log.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.log.mapper.LogMapper;
import com.zcx.cloud.log.service.ILogService;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *

 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {
	@Autowired
	private LogMapper logMapper;
	
	@Override
	public Result getLogsPage(Log log, PageInfo<Log> pageInfo, SortInfo sortInfo) {
		LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<Log>();
		//设置查询条件
		if(Objects.nonNull(log)) {
			if(!Strings.isNullOrEmpty(log.getLogContent()))
				queryWrapper.like(Log::getLogContent, log.getLogContent());
			if(!Strings.isNullOrEmpty(log.getIp()))
				queryWrapper.like(Log::getIp, log.getIp());
			if(!Strings.isNullOrEmpty(log.getCreateBy()))
				queryWrapper.like(Log::getCreateBy, log.getCreateBy());
			if(Objects.nonNull(log.getCreateTime()))
				queryWrapper.gt(Log::getCreateTime, log.getCreateTime());
		}
		queryWrapper.eq(Log::getValid, Constant.VALID_NORMAL);
		
		//排序信息
		Page<Log> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Log> selectPage = logMapper.selectPage(page, queryWrapper);
		long count = selectPage.getTotal();
		List<Log> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public Log logicDelete(Long logId) {
		//更改信息
		Log log = new Log();
		log.setValid(Constant.VALID_DELETE);
		log.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		log.setCreateTime(DateUtil.now());
		
		//查询条件
		LambdaUpdateWrapper<Log> updateWrapper = new LambdaUpdateWrapper<Log>();
		updateWrapper.eq(Log::getLogId, logId);
		
		logMapper.update(log, updateWrapper);
		
		return log;
	}

	@Override
	public int logicDeleteBatch(List<Long> logIds) {
		//更改信息
		Log log = new Log();
		log.setValid(Constant.VALID_DELETE);
		log.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		log.setUpdateTime(DateUtil.now());
		
		//查询条件
		LambdaUpdateWrapper<Log> updateWrapper = new LambdaUpdateWrapper<Log>();
		updateWrapper.in(Log::getLogId, logIds);
		
		int count = logMapper.update(log, updateWrapper);
		
		return count;
	}

}
