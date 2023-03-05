package com.zcx.cloud.permission.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.permission.service.IPermissionService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/permission")
public class PermissionController extends BaseController {
	@Autowired
	private IPermissionService permissionService;
	
	@RequiresPermissions("permission:view")
	@GetMapping("getPermissionsPage")
	public Result getPermissionsPage(Permission permission,PageInfo<Permission> pageInfo,SortInfo sortInfo) {
		try {
			Result result = permissionService.getPermissionsPage(permission, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取权限分页信息失败");
		}
	}
	
	@RequiresPermissions("permission:view")
	@GetMapping("getAllPermGroupByAndSort")
	public Result getAllPermGroupByAndSort() {
		try {
			List<Permission> permissions = permissionService.getAllPermGroupByAndSort();
			return new Result().success().data(permissions);
		} catch(Exception e) {
			e.printStackTrace();
			throw new BusException("获取权限列表失败");
		}
	}
	
	@SysLog("添加权限")
	@RequiresPermissions("permission:add")
	@PostMapping("permissionAdd")
	public Result permissionAdd(Permission permission) {
		try {
			permissionService.savePermission(permission);
			return new Result().success();
		}catch (ExistsException e) {
			return new Result().error().msg("权限码已存在");
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("添加权限信息失败");
		}
	}
	
	
	@SysLog("删除权限")
	@RequiresPermissions("permission:delete")
	@GetMapping("permissionDelete/{permissionId}")
	public Result permissionDelete(@PathVariable("permissionId")Long permissionId) {
		try {
			permissionService.logicDelete(permissionId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("删除权限信息失败");
		}
	}
	
	@SysLog("批量删除权限")
	@RequiresPermissions("permission:delete")
	@GetMapping("permissionDeleteBatch/{permissionIdsStr}")
	public Result permissionDeleteBatch(@PathVariable("permissionIdsStr")String permissionIdsStr) {
		try {
			List<Long> permissionIds = StringUtil.stringToLongs(permissionIdsStr, Constant.COMMA);
			permissionService.logicDeleteBatch(permissionIds);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("批量删除权限信息失败");
		}
	}
	
	@SysLog("修改权限")
	@RequiresPermissions("permission:update")
	@PostMapping("permissionUpdate")
	public Result roleUpdate(Permission permission) {
		try {
			permissionService.updatePermission(permission);
			return new Result().success();
		} catch(ExistsException e) {
			return new Result().error().msg("权限码已存在");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusException("修改权限失败");
		}
	}
}
