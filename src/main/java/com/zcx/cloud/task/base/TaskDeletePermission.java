package com.zcx.cloud.task.base;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.permission.mapper.PermissionMapper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除权限定时任务
 * 
 * 1.删除权限基本信息
 * 涉及表:t_permission
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeletePermission")
public class TaskDeletePermission {
	@Autowired
	private PermissionMapper permissionMapper;

	public void deletePermission() {
		//1.删除无效权限信息
		LambdaQueryWrapper<Permission> qwP = new LambdaQueryWrapper<Permission>();
		qwP.eq(Permission::getValid, Constant.VALID_DELETE);
		permissionMapper.delete(qwP);

	}
	
}
