package com.zcx.cloud.folder.service.impl;

import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.entity.FolderUser;
import com.zcx.cloud.folder.mapper.FolderMapper;
import com.zcx.cloud.folder.mapper.FolderUserMapper;
import com.zcx.cloud.folder.service.IFolderUserService;
import com.zcx.cloud.util.AppUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 目录与用户关联表 服务实现类
 * </p>
 *

 */
@Service
public class FolderUserServiceImpl extends ServiceImpl<FolderUserMapper, FolderUser> implements IFolderUserService {
	@Autowired
	private FolderUserMapper folderUserMapper;
	@Autowired
	private FolderMapper folderMapper;
	
	@Override
	public Folder getCurrentFolder(Long currentId) {
		//当前文件夹ID为负数，则查询当前用户根目录
		if(Objects.isNull(currentId)||currentId<=0) {
			LambdaQueryWrapper<FolderUser> queryWrapper = new LambdaQueryWrapper<FolderUser>();
			queryWrapper.eq(FolderUser::getUserId, AppUtil.getCurrentUser().getUserId());
			FolderUser folderUser = folderUserMapper.selectOne(queryWrapper);
			currentId = folderUser.getFolderId();
		}
		Folder folder = folderMapper.selectById(currentId);
		
		return folder;
	}

}
