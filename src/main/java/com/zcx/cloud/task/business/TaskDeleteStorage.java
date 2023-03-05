package com.zcx.cloud.task.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.storage.entity.StorageUser;
import com.zcx.cloud.storage.mapper.StorageMapper;
import com.zcx.cloud.storage.mapper.StorageUserMapper;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.mapper.UserMapper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除存储信息定时任务
 * 
 * 1.删除存储基本信息
 * 2.删除存储于用户关联信息
 * 涉及表：t_storage、t_storage_user
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteStorage")
public class TaskDeleteStorage {
	@Autowired
	private StorageUserMapper storageUserMapper;
	@Autowired
	private StorageMapper storageMapper;
	@Autowired
	private UserMapper userMapper;

	public void deleteStorage() {
		//1.查询无效的存储与用户关联信息
		LambdaQueryWrapper<User> qwUser = new LambdaQueryWrapper<User>();
		qwUser.eq(User::getValid, Constant.VALID_NORMAL);
		qwUser.select(User::getUserId);
		List<User> users = userMapper.selectList(qwUser);
		List<Long> userIds = new ArrayList<Long>();
		if(CollectionUtil.noNullAndSizeGtZero(users)) {
			userIds = users.stream().map(user -> user.getUserId()).collect(Collectors.toList());
		}
		
		LambdaQueryWrapper<StorageUser> qwSU = new LambdaQueryWrapper<StorageUser>();
		if(CollectionUtil.noNullAndSizeGtZero(userIds))
			qwSU.notIn(StorageUser::getUserId, userIds);
		List<StorageUser> storageUsers = storageUserMapper.selectList(qwSU);
		List<Long> storageIds = new ArrayList<Long>();
		if(CollectionUtil.noNullAndSizeGtZero(storageUsers))
			storageIds = storageUsers.stream().map(storageUser -> storageUser.getStorageId()).collect(Collectors.toList());
		
		if(CollectionUtil.noNullAndSizeGtZero(storageIds)) {
			//2.删除无效的存储与用户关联信息
			LambdaQueryWrapper<StorageUser> qwStorageUser = new LambdaQueryWrapper<StorageUser>();
			qwStorageUser.in(StorageUser::getStorageId, storageIds);
			storageUserMapper.delete(qwStorageUser);
			
			//3.删除关联的存储信息
			LambdaQueryWrapper<Storage> qwStorage = new LambdaQueryWrapper<Storage>();
			qwStorage.in(Storage::getStorageId, storageIds);
			storageMapper.delete(qwStorage);
		}
	}
	
}
