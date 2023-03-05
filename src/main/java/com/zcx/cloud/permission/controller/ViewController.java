package com.zcx.cloud.permission.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.permission.service.IPermissionService;
import com.zcx.cloud.util.ViewUtil;

@Controller("permissionViewController")
@RequestMapping("/base/permission")
public class ViewController {
	@Autowired
	private IPermissionService permissionService;
	
	@GetMapping("permission")
	public String permission() {
		return ViewUtil.view("base/permission/permission");
	}
	
	@GetMapping("permissionDetail/{permissionId}")
	public String permissionDetail(@PathVariable("permissionId")Long permissionId,Model model) {
		Permission permission = permissionService.getById(permissionId);
		model.addAttribute("permission", permission);
		return ViewUtil.view("base/permission/permissionDetail");
	}
	
	@GetMapping("permissionAdd")
	public String permissionAdd() {
		return ViewUtil.view("base/permission/permissionAdd");
	}
	
	@GetMapping("permissionEdit/{permissionId}")
	public String permissionEdit(@PathVariable("permissionId")Long permissionId, Model model) {
		Permission permission = permissionService.getById(permissionId);
		model.addAttribute(permission);
		return ViewUtil.view("base/permission/permissionEdit");
	}
}
