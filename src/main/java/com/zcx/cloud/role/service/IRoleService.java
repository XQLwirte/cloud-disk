package com.zcx.cloud.role.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.role.entity.Role;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *

 */
public interface IRoleService extends IService<Role> {
	
	/**
	 * 通过UserId查询角色名称集合
	 * @param userId 用户ID
	 * @return
	 */
	public List<Role> getRolesByUserId(Long userId);

	/**
	 * 听过userId查询到用户的所有角色ID
	 * @param userId
	 * @return
	 */
	public List<Long> getRoleIdsByUserId(Long userId);
	
	/**
	 * 获取所有有效的角色信息
	 * @return
	 */
	public List<Role> getAllRoles();

	/**
	 * 分页获取权限信息
	 * @param role
	 * @param pageInfo
	 * @param sortInfo
	 * @return
	 */
	public Result getRolesPage(Role role, PageInfo<Role> pageInfo, SortInfo sortInfo);

	/**
	 * 添加角色信息
	 * @param role
	 */
	public void saveRole(Role role);

	/**
	 * 修改角色信息
	 * @param role
	 */
	public void updateRole(Role role);

	/**
	 * 逻辑删除角色信息
	 * @param roleId
	 */
	public void logicDelete(Long roleId);

	/**
	 * 批量逻辑删除角色信息
	 * @param roleIds
	 */
	public void logicDeleteBatch(List<Long> roleIds);

	/**
	 * 获取到角色所赋予的所有菜单ID
	 * @param roleId
	 * @return
	 */
	public List<Long> getGrantMenuIds(Long roleId);

	/**
	 * 修改角色与菜单关联信息
	 * @param roleId
	 * @param menuIds
	 */
	public void updateRoleMenuIds(Long roleId, List<Long> menuIds);

	/**
	 * 修改角色与权限关联信息
	 * @param roleId
	 * @param permissionIds
	 */
	public void updateRolePermissionIds(Long roleId, List<Long> permissionIds);
}
