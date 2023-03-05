package com.zcx.cloud.log.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.log.entity.Log;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 日志表 服务类
 * </p>
 *

 */
public interface ILogService extends IService<Log> {

	/**
	 * 分页查询日志信息
	 * @param log
	 * @param pageInfo
	 * @return
	 */
	public Result getLogsPage(Log log,PageInfo<Log> pageInfo, SortInfo sortInfo);
	
	/**
	 * 逻辑删除
	 * @param logId
	 * @return 返回删除对象
	 */
	public Log logicDelete(Long logId);
	
	/**
	 * 批量逻辑删除
	 * @param logIds
	 * @return 返回删除记录数
	 */
	public int logicDeleteBatch(List<Long> logIds);
}
