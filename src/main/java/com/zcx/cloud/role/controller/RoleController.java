package com.zcx.cloud.role.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {
	@Autowired
	private IRoleService roleService;

	@RequiresPermissions("role:view")
	@GetMapping("getRolesPage")
	public Result getRolesPage(Role role,PageInfo<Role> pageInfo,SortInfo sortInfo) {
		try {
			Result result = roleService.getRolesPage(role, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取角色分页信息失败");
		}
	}
	
	/**
	 * 获取所有的角色信息
	 * @return
	 */
	@RequiresPermissions("role:view")
	@GetMapping("getAllRoles")
	public Result getAllRoles() {
		try {
			List<Role> roles = roleService.getAllRoles();			
			return new Result().success().data(roles);
		}catch(Exception e) {
			throw new BusException("获取角色信息失败");
		}
	}
	
	@SysLog("添加角色")
	@RequiresPermissions("role:add")
	@PostMapping("roleAdd")
	public Result roleAdd(Role role) {
		try {
			roleService.saveRole(role);
			return new Result().success();
		} catch(ExistsException e) {
			return new Result().error().msg("角色码已存在");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("添加角色失败");
		}
	}

	@SysLog("修改角色")
	@RequiresPermissions("role:update")
	@PostMapping("roleUpdate")
	public Result roleUpdate(Role role) {
		try {
			roleService.updateRole(role);
			return new Result().success();
		} catch(ExistsException e) {
			return new Result().error().msg("角色码已存在");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("修改角色失败");
		}
	}
	
	@SysLog("删除角色")
	@RequiresPermissions("role:delete")
	@GetMapping("roleDelete/{roleId}")
	public Result roleDelete(@PathVariable("roleId")Long roleId) {
		try {
			roleService.logicDelete(roleId);
			return new Result().success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("删除角色失败");
		}
	}
	
	@SysLog("批量删除角色")
	@RequiresPermissions("role:delete")
	@GetMapping("roleDeleteBatch/{roleIdsStr}")
	public Result roleDeleteBatch(@PathVariable("roleIdsStr")String roleIdsStr) {
		try {
			List<Long> roleIds = StringUtil.stringToLongs(roleIdsStr, Constant.COMMA);
			roleService.logicDeleteBatch(roleIds);
			return new Result().success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("批量删除角色失败");
		}
	}
	
	@SysLog("修改角色关联菜单")
	@RequiresPermissions("role:update")
	@PostMapping("roleUpdateMenu")
	public Result roleUpdateMenu(@RequestParam("roleId")Long roleId,
				@RequestParam("menuIdsStr")String menuIdsStr) {
		try {
			List<Long> menuIds = StringUtil.stringToLongs(menuIdsStr, Constant.COMMA);
			roleService.updateRoleMenuIds(roleId, menuIds);
			return new Result().success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("修改角色关联菜单失败");
		}
	}
	
	
	@SysLog("修改角色关联权限")
	@RequiresPermissions("role:update")
	@PostMapping("roleUpdatePermission")
	public Result roleUpdatePermission(@RequestParam("roleId")Long roleId,
				@RequestParam("permissionIdsStr")String permissionIdsStr) {
		try {
			List<Long> permissionIds = StringUtil.stringToLongs(permissionIdsStr, Constant.COMMA);
			roleService.updateRolePermissionIds(roleId, permissionIds);
			return new Result().success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("修改角色关联权限失败");
		}
	}
}
