package com.zcx.cloud.task.base;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.entity.RoleMenu;
import com.zcx.cloud.role.entity.RolePermission;
import com.zcx.cloud.role.mapper.RoleMapper;
import com.zcx.cloud.role.mapper.RoleMenuMapper;
import com.zcx.cloud.role.mapper.RolePermissionMapper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除用户信息定时任务
 * 
 * 1.删除角色基本信息
 * 2.删除角色与菜单
 * 3.删除角色与权限关联信息
 * 涉及表：t_role、t_role_menu、t_role_permission
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteRole")
public class TaskDeleteRole {
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	
	public void deleteRole() {
		//1.查询所有无效角色信息
		LambdaQueryWrapper<Role> qwRole = new LambdaQueryWrapper<Role>();
		qwRole.eq(Role::getValid, Constant.VALID_DELETE);
		List<Role> roles = roleMapper.selectList(qwRole);
		if(CollectionUtil.isNullOrSizeLeZero(roles))
			return ;
		List<Long> roleIds = roles.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
		
		//2.删除角色权限关联信息
		LambdaQueryWrapper<RolePermission> qwRP = new LambdaQueryWrapper<RolePermission>();
		qwRP.in(RolePermission::getRoleId, roleIds);
		rolePermissionMapper.delete(qwRP);
		
		//3.删除角色菜单关联信息
		LambdaQueryWrapper<RoleMenu> qwRM = new LambdaQueryWrapper<RoleMenu>();
		qwRM.in(RoleMenu::getRoleId, roleIds);
		roleMenuMapper.delete(qwRM);
		
		
		//4.删除角色信息
		LambdaQueryWrapper<Role> qwRoleDel = new LambdaQueryWrapper<Role>();
		qwRoleDel.in(Role::getRoleId, roleIds);
		roleMapper.delete(qwRoleDel);
	}
	
}
