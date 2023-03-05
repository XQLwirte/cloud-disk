package com.zcx.cloud.garbage.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.common.vo.TimeData;
import com.zcx.cloud.garbage.entity.Garbage;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 垃圾箱 服务类
 * </p>
 *
 
 */
public interface IGarbageService extends IService<Garbage> {

	/**
	 * 分页查询垃圾信息
	 * @param garbage
	 * @param pageInfo
	 * @param sortInfo
	 * @return
	 */
	Result getGarbagesPage(Garbage garbage, PageInfo<Garbage> pageInfo, SortInfo sortInfo);

	/**
	 * 查询垃圾信息以及关联用户资源信息
	 * @param garbageId
	 * @return
	 */
	Garbage getGarbageById(Long garbageId);

	/**
	 * 恢复文件
	 * @param garbageId
	 */
	void garbageResume(Long garbageId);

	/**
	 * 逻辑删除垃圾信息
	 * @param garbageId
	 */
	void logicDelete(Long garbageId);

	/**
	 * 批量逻辑删除垃圾信息
	 * @param garbageIds
	 */
	void logicDeleteBatch(List<Long> garbageIds);

	/**
	 * 获取当前用户的垃圾数据，时间集合
	 * @return
	 */
	List<TimeData<Garbage>> getCurrentGarbagesTimeData();

}
