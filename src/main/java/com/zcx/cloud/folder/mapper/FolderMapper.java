package com.zcx.cloud.folder.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.cloud.folder.entity.Folder;

/**
 * <p>
 * 目录表 Mapper 接口
 * </p>
 *

 */
public interface FolderMapper extends BaseMapper<Folder> {

	/**
	 * 分页查询顶级目录的目录信息
	 * @param page
	 * @param folder
	 * @return
	 */
	Page<Folder> selectFolderPage(Page<Folder> page, @Param("folder")Folder folder);
	
}
