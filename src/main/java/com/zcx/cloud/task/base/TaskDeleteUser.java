package com.zcx.cloud.task.base;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.system.fastdfs.helper.FDFSHelper;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.entity.UserRole;
import com.zcx.cloud.user.mapper.UserMapper;
import com.zcx.cloud.user.mapper.UserRoleMapper;
import com.zcx.cloud.user.service.IUserService;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除用户信息定时任务
 * 
 * 1.删除标志为删除状态的用户基本信息
 * 2.删除用户与角色关联信息
 * 3.删除FastDFS头像文件
 * 
 * 涉及表：t_user、t_user_role
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteUser")
public class TaskDeleteUser {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private FDFSHelper fdfsHelper;
	
	public void deleteUser() {
		//1.查询所有无效的用户信息
		LambdaQueryWrapper<User> qwU = new LambdaQueryWrapper<User>();
		qwU.eq(User::getValid, Constant.VALID_DELETE);
		List<User> users = userMapper.selectList(qwU);
		if(CollectionUtil.isNullOrSizeLeZero(users))
			return ;
		List<Long> userIds = users.stream().map(user -> user.getUserId()).collect(Collectors.toList());

		//2.删除FastDFS头像文件
		users.forEach(user -> {
			String fdfsPath = fdfsHelper.getPath(user.getPhoto());
			fdfsHelper.deleteFile(fdfsPath);
		});
		
		//3.删除用户与角色关联信息
		LambdaQueryWrapper<UserRole> qwUR = new LambdaQueryWrapper<UserRole>();
		qwUR.in(UserRole::getUserId, userIds);
		userRoleMapper.delete(qwUR);
		
		//4.删除用户基本信息
		LambdaQueryWrapper<User> qwUDelete = new LambdaQueryWrapper<User>();
		qwUDelete.in(User::getUserId, userIds);
		userMapper.delete(qwUDelete);
	}
	
}
