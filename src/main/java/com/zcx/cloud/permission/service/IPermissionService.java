package com.zcx.cloud.permission.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.role.entity.Role;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *

 */
public interface IPermissionService extends IService<Permission> {

	/**
	 * 通过角色列表查询到所有权限信息
	 * @param roles
	 * @return
	 */
	public List<Permission> getPermissionsByRoles(List<Role> roles);
	
	/**
	 * 获取所有的权限信息
	 * 按permissionCode分组并排序
	 * @return
	 */
	public List<Permission> getAllPermGroupByAndSort();

	/**
	 * 分页查询所有权限信息
	 * @param permission
	 * @param pageInfo
	 * @param sortInfo
	 * @return
	 */
	public Result getPermissionsPage(Permission permission, PageInfo<Permission> pageInfo, SortInfo sortInfo);

	/**
	 * 持久化权限信息
	 * @param permission
	 */
	public void savePermission(Permission permission);

	/**
	 * 逻辑删除权限信息
	 * @param permissionId
	 */
	public void logicDelete(Long permissionId);

	/**
	 * 批量删除权限信息
	 * @param permissionIds
	 */
	public void logicDeleteBatch(List<Long> permissionIds);

	/**
	 * 修改权限信息
	 * @param permission
	 */
	public void updatePermission(Permission permission);
}
