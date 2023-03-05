package com.zcx.cloud.role.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.entity.RoleMenu;
import com.zcx.cloud.role.entity.RolePermission;
import com.zcx.cloud.role.mapper.RoleMapper;
import com.zcx.cloud.role.mapper.RoleMenuMapper;
import com.zcx.cloud.role.mapper.RolePermissionMapper;
import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.entity.UserRole;
import com.zcx.cloud.user.mapper.UserRoleMapper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *

 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	@Autowired
	private RolePermissionMapper rolePermissionMapper;
	
	@Override
	public List<Role> getRolesByUserId(Long userId) {
		List<Role> roles = new ArrayList<Role>();
		//1.查询用户角色关联信息
		LambdaQueryWrapper<UserRole> qwUserRole = new LambdaQueryWrapper<UserRole>();
		qwUserRole.eq(UserRole::getUserId, userId);
		List<UserRole> userRoles = userRoleMapper.selectList(qwUserRole);
		if(CollectionUtil.isNullOrSizeLeZero(userRoles))
			return roles;
		
		//2.查询角色信息
		List<Long> roleIds = userRoles.stream().map(userRole -> userRole.getRoleId()).collect(Collectors.toList());
		LambdaQueryWrapper<Role> qwRole = new LambdaQueryWrapper<Role>();
		qwRole.in(Role::getRoleId, roleIds);
		qwRole.eq(Role::getValid, Constant.VALID_NORMAL);
		roles = roleMapper.selectList(qwRole);
		
		return roles;
	}

	@Override
	public List<Role> getAllRoles() {
		LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>();
		queryWrapper.eq(Role::getValid, Constant.VALID_NORMAL);
		List<Role> roles = roleMapper.selectList(queryWrapper);
		return roles;
	}

	@Override
	public List<Long> getRoleIdsByUserId(Long userId) {
		List<Long> roleIds = new ArrayList<Long>();
		//1.查询角色信息
		List<Role> roles = this.getRolesByUserId(userId);
		if(CollectionUtil.isNullOrSizeLeZero(roles))
			return roleIds;
		//2.分离角色ID
		roles.forEach(role -> {
			roleIds.add(role.getRoleId());
		});
		return roleIds;
	}

	@Override
	public Result getRolesPage(Role role, PageInfo<Role> pageInfo, SortInfo sortInfo) {
		LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>();
		//设置查询条件
		if(Objects.nonNull(role)) {
			if(!Strings.isNullOrEmpty(role.getRoleName()))
				queryWrapper.like(Role::getRoleName, role.getRoleName());
			if(!Strings.isNullOrEmpty(role.getRoleCode()))
				queryWrapper.like(Role::getRoleCode, role.getRoleCode());
			if(!Strings.isNullOrEmpty(role.getRoleDescribe()))
				queryWrapper.like(Role::getRoleDescribe, role.getRoleDescribe());
			if(!Strings.isNullOrEmpty(role.getCreateBy()))
				queryWrapper.eq(Role::getCreateBy, role.getCreateBy());
			if(Objects.nonNull(role.getCreateTime()))
				queryWrapper.gt(Role::getCreateTime, role.getCreateTime());
		}
		queryWrapper.eq(Role::getValid, Constant.VALID_NORMAL);
		
		//排序信息
		Page<Role> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Role> selectPage = roleMapper.selectPage(page, queryWrapper);
		long count = selectPage.getTotal();
		List<Role> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public void saveRole(Role role) {
		//1.查询是否存在相同的角色码
		String roleCode = role.getRoleCode();
		LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>();
		queryWrapper.eq(Role::getRoleCode, roleCode);
		queryWrapper.eq(Role::getValid,Constant.VALID_NORMAL);
		List<Role> selectList = roleMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("角色码已存在");
		
		//2.持久化角色信息
		role.setCreateBy(AppUtil.getCurrentUser().getUsername());
		role.setCreateTime(DateUtil.now());
		roleMapper.insert(role);
	}

	@Override
	public void updateRole(Role role) {
		//1.查询是否存在相同的角色码
		LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<Role>();
		queryWrapper.notIn(Role::getRoleId, role.getRoleId());
		queryWrapper.eq(Role::getRoleCode, role.getRoleCode());
		queryWrapper.eq(Role::getValid,Constant.VALID_NORMAL);
		List<Role> selectList = roleMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("角色码已存在");
		
		//2.更新化角色信息
		role.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		role.setUpdateTime(DateUtil.now());
		roleMapper.updateById(role);
	}

	@Override
	public void logicDelete(Long roleId) {
		Role role = new Role();
		role.setRoleId(roleId);
		role.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		role.setUpdateTime(DateUtil.now());
		role.setValid(Constant.VALID_DELETE);
		
		roleMapper.updateById(role);
	}

	@Override
	public void logicDeleteBatch(List<Long> roleIds) {
		Role role = new Role();
		role.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		role.setUpdateTime(DateUtil.now());
		role.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<Role>();
		updateWrapper.in(Role::getRoleId, roleIds);
		updateWrapper.eq(Role::getValid, Constant.VALID_NORMAL);
		roleMapper.update(role, updateWrapper);
	}

	@Override
	public List<Long> getGrantMenuIds(Long roleId) {
		List<Long> menuIds = new ArrayList<Long>();
		if(Objects.isNull(roleId))
			return menuIds;
		
		//1.查询角色菜单关联信息
		LambdaQueryWrapper<RoleMenu> qwRM = new LambdaQueryWrapper<RoleMenu>();
		qwRM.eq(RoleMenu::getRoleId, roleId);
		List<RoleMenu> roleMenus = roleMenuMapper.selectList(qwRM);
		if(CollectionUtil.isNullOrSizeLeZero(roleMenus))
			return menuIds;
		
		//2.提取菜单ID
		roleMenus.forEach(roleMenu -> {
			menuIds.add(roleMenu.getMenuId());
		});
		
		return menuIds;
	}

	@Override
	public void updateRoleMenuIds(Long roleId, List<Long> menuIds) {
		//1.去除不在集合中的角色菜单信息
		LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<RoleMenu>();
		queryWrapper.eq(RoleMenu::getRoleId, roleId);
		if(CollectionUtil.noNullAndSizeGtZero(menuIds))
			queryWrapper.notIn(RoleMenu::getMenuId, menuIds);
		roleMenuMapper.delete(queryWrapper);
		
		//2.分割出新增的菜单ID
		//查询现有的角色菜单信息
		LambdaQueryWrapper<RoleMenu> queryWrapper2 = new LambdaQueryWrapper<RoleMenu>();
		queryWrapper2.eq(RoleMenu::getRoleId, roleId);
		List<RoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper2);
		List<Long> menuIdsExists = new ArrayList<Long>();
		if(CollectionUtil.noNullAndSizeGtZero(roleMenus))
			roleMenus.forEach(roleMenu -> menuIdsExists.add(roleMenu.getMenuId()));
		//分离出新增的菜单ID
		menuIds.removeAll(menuIdsExists);
		
		//3.持久化新增的角色菜单信息
		menuIds.forEach(menuId -> {
			RoleMenu roleMenu = new RoleMenu();
			roleMenu.setRoleId(roleId);
			roleMenu.setMenuId(menuId);
			roleMenuMapper.insert(roleMenu);
		});
	}

	@Override
	public void updateRolePermissionIds(Long roleId, List<Long> permissionIds) {
		//1.去除不在集合中的角色权限信息
		LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<RolePermission>();
		queryWrapper.eq(RolePermission::getRoleId, roleId);
		if(CollectionUtil.noNullAndSizeGtZero(permissionIds))
			queryWrapper.notIn(RolePermission::getPermissionId, permissionIds);
		rolePermissionMapper.delete(queryWrapper);
		
		//2.分割出新增的权限ID
		//查询现有的角色权限信息
		LambdaQueryWrapper<RolePermission> queryWrapper2 = new LambdaQueryWrapper<RolePermission>();
		queryWrapper2.eq(RolePermission::getRoleId, roleId);
		List<RolePermission> rolePermissions = rolePermissionMapper.selectList(queryWrapper2);
		List<Long> permissionIdsExists = new ArrayList<Long>();
		if(CollectionUtil.noNullAndSizeGtZero(rolePermissions))
			rolePermissions.forEach(rolePermission -> permissionIdsExists.add(rolePermission.getPermissionId()));
		//分离出新增的权限ID
		permissionIds.removeAll(permissionIdsExists);
		
		//3.持久化新增的角色权限信息
		permissionIds.forEach(permissionId -> {
			RolePermission rolePermission = new RolePermission();
			rolePermission.setRoleId(roleId);
			rolePermission.setPermissionId(permissionId);
			rolePermissionMapper.insert(rolePermission);
		});
	}

}
