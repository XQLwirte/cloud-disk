package com.zcx.cloud.garbage.mapper;

import com.zcx.cloud.garbage.entity.Garbage;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 垃圾箱 Mapper 接口
 * </p>
 */
public interface GarbageMapper extends BaseMapper<Garbage> {

	/**
	 * 分页查询垃圾信息
	 * @param page
	 * @param garbage
	 * @return
	 */
	Page<Garbage> selectGarbagePage(Page<Garbage> page, @Param("garbage")Garbage garbage);

}
