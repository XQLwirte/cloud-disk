package com.zcx.cloud.folder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.entity.FolderUser;

/**
 * <p>
 * 目录与用户关联表 服务类
 * </p>
 *

 */
public interface IFolderUserService extends IService<FolderUser> {
	
	/**
	 * 转换当前用户当前ID
	 * 为null或<=0，则查找当前用户根目录
	 * 否则原样返回
	 * @param currentId
	 * @return
	 */
	Folder getCurrentFolder(Long currentId);
	
}
