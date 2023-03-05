package com.zcx.cloud.share.mapper;

import com.zcx.cloud.share.entity.Share;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 共享信息表 Mapper 接口
 * </p>
 *

 */
public interface ShareMapper extends BaseMapper<Share> {

	/**
	 * 分页条件查询共享信息
	 * @param page
	 * @param share
	 * @return
	 */
	Page<Share> selectSharePage(Page<Share> page, @Param("share")Share share);

}
