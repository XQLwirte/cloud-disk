package com.zcx.cloud.role.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.permission.service.IPermissionService;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.util.ViewUtil;

@Controller("roleViewController")
@RequestMapping("/base/role")
public class ViewController {
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IPermissionService permissionService;
	
	/**
	 * 权限首页
	 * @return
	 */
	@GetMapping("role")
	public String role() {
		return ViewUtil.view("base/role/role");
	}
	
	@GetMapping("roleDetail/{roleId}")
	public String roleDetail(@PathVariable("roleId")Long roleId, Model model) {
		Role role = roleService.getById(roleId);
		model.addAttribute("role", role);
		return ViewUtil.view("base/role/roleDetail"); 
	}
	
	@GetMapping("roleAdd")
	public String roleAdd() {
		return ViewUtil.view("base/role/roleAdd"); 
	}
	
	@GetMapping("roleEdit/{roleId}")
	public String roleEdit(@PathVariable("roleId")Long roleId, Model model) {
		Role role = roleService.getById(roleId);
		model.addAttribute("role", role);
		return ViewUtil.view("base/role/roleEdit"); 
	}
	
	@GetMapping("roleEditMenu/{roleId}")
	public String roleEditMenu(@PathVariable("roleId")Long roleId, Model model) {
		Role role = roleService.getById(roleId);
		List<Long> menuIds = roleService.getGrantMenuIds(roleId);
		model.addAttribute("menuIds", menuIds);
		model.addAttribute("role", role);
		return ViewUtil.view("base/role/roleEditMenu"); 
	}
	
	@GetMapping("roleEditPermission/{roleId}")
	public String roleEditPermission(@PathVariable("roleId")Long roleId, Model model) {
		//1.查询当前角色信息
		Role role = roleService.getById(roleId);
		//2.查询关联权限ID列表
		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		List<Permission> permissions = permissionService.getPermissionsByRoles(roles);
		List<Long> permissionIds = new ArrayList<Long>();
		permissions.forEach(permission -> permissionIds.add(permission.getPermissionId()));
		
		model.addAttribute("permissionIds", permissionIds);
		model.addAttribute("role", role);
		return ViewUtil.view("base/role/roleEditPermission"); 
	}
}
