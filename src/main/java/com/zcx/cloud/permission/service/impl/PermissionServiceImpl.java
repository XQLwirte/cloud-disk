package com.zcx.cloud.permission.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.permission.mapper.PermissionMapper;
import com.zcx.cloud.permission.service.IPermissionService;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.entity.RolePermission;
import com.zcx.cloud.role.mapper.RolePermissionMapper;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *

 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	@Autowired
	private PermissionMapper permissionMapper;
	
	@Override
	public List<Permission> getPermissionsByRoles(List<Role> roles) {
		List<Permission> permissions = new ArrayList<Permission>();
		//1.查询角色权限关联信息
		if(CollectionUtil.isNullOrSizeLeZero(roles))
			return permissions;
		List<Long> roleIds = roles.stream().map(role -> role.getRoleId()).collect(Collectors.toList());
		LambdaQueryWrapper<RolePermission> qwRP = new LambdaQueryWrapper<RolePermission>();
		qwRP.in(RolePermission::getRoleId, roleIds);
		List<RolePermission> rolePermissions = rolePermissionMapper.selectList(qwRP);
		if(CollectionUtil.isNullOrSizeLeZero(rolePermissions))
			return permissions;
		
		//2.查询权限信息
		List<Long> permissionIds = rolePermissions.stream()
				.map(rolePermission -> rolePermission.getPermissionId())
				.distinct()
				.collect(Collectors.toList());
		LambdaQueryWrapper<Permission> qwPermission = new LambdaQueryWrapper<Permission>();
		qwPermission.in(Permission::getPermissionId, permissionIds);
		qwPermission.eq(Permission::getValid, Constant.VALID_NORMAL);
		permissions = permissionMapper.selectList(qwPermission);
		//3.返回结果
		return permissions;
	}

	@Override
	public List<Permission> getAllPermGroupByAndSort() {
		LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<Permission>();
		queryWrapper.groupBy(Permission::getPermissionCode);
		queryWrapper.orderByAsc(Permission::getPermissionCode);
		queryWrapper.eq(Permission::getValid, Constant.VALID_NORMAL);
		List<Permission> selectList = permissionMapper.selectList(queryWrapper);
		return selectList;
	}

	@Override
	public Result getPermissionsPage(Permission permission, PageInfo<Permission> pageInfo, SortInfo sortInfo) {
		LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<Permission>();
		//设置查询条件
		if(Objects.nonNull(permission)) {
			if(!Strings.isNullOrEmpty(permission.getPermissionName()))
				queryWrapper.like(Permission::getPermissionName, permission.getPermissionName());
			if(!Strings.isNullOrEmpty(permission.getPermissionCode()))
				queryWrapper.like(Permission::getPermissionCode, permission.getPermissionCode());
			if(!Strings.isNullOrEmpty(permission.getPermissionDescribe()))
				queryWrapper.like(Permission::getPermissionDescribe, permission.getPermissionDescribe());
			if(!Strings.isNullOrEmpty(permission.getCreateBy()))
				queryWrapper.like(Permission::getCreateBy, permission.getCreateBy());
			if(Objects.nonNull(permission.getCreateTime()))
				queryWrapper.gt(Permission::getCreateTime, permission.getCreateTime());
		}
		queryWrapper.eq(Permission::getValid, Constant.VALID_NORMAL);
		
		//排序信息
		Page<Permission> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Permission> selectPage = permissionMapper.selectPage(page, queryWrapper);
		long count = selectPage.getTotal();
		List<Permission> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public void savePermission(Permission permission) {
		//1.检查权限码是否重复
		String permissionCode = permission.getPermissionCode();
		LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<Permission>();
		queryWrapper.eq(Permission::getPermissionCode,permissionCode);
		queryWrapper.eq(Permission::getValid, Constant.VALID_NORMAL);
		List<Permission> selectList = permissionMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("权限码已存在");
		
		//2.持久化权限信息
		permission.setCreateBy(AppUtil.getCurrentUser().getUsername());
		permission.setCreateTime(DateUtil.now());
		permissionMapper.insert(permission);
	}

	@Override
	public void logicDelete(Long permissionId) {
		Permission permission = new Permission();
		permission.setPermissionId(permissionId);
		permission.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		permission.setUpdateTime(DateUtil.now());
		permission.setValid(Constant.VALID_DELETE);
		
		permissionMapper.updateById(permission);
	}

	@Override
	public void logicDeleteBatch(List<Long> permissionIds) {
		Permission permission = new Permission();
		permission.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		permission.setUpdateTime(DateUtil.now());
		permission.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<Permission> updateWrapper = new LambdaUpdateWrapper<Permission>();
		updateWrapper.in(Permission::getPermissionId, permissionIds);
		updateWrapper.eq(Permission::getValid, Constant.VALID_NORMAL);
		permissionMapper.update(permission, updateWrapper);
	}

	@Override
	public void updatePermission(Permission permission) {
		//1.查询是否存在相同的权限码
		LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<Permission>();
		queryWrapper.notIn(Permission::getPermissionId, permission.getPermissionId());
		queryWrapper.eq(Permission::getPermissionCode, permission.getPermissionCode());
		queryWrapper.eq(Permission::getValid,Constant.VALID_NORMAL);
		List<Permission> selectList = permissionMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("权限码已存在");
		
		//2.更新权限信息
		permission.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		permission.setUpdateTime(DateUtil.now());
		permissionMapper.updateById(permission);
	}

}
