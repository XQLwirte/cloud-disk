package com.zcx.cloud.storage.mapper;

import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.storage.entity.Storage;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 存储信息表 Mapper 接口
 * </p>
 *

 */
public interface StorageMapper extends BaseMapper<Storage> {

	Page<Storage> selectStoragePage(Page<Storage> page, @Param("storage")Storage storage);
	
}
