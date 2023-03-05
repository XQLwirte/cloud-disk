package com.zcx.cloud.menu.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.menu.service.IMenuService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;

import lombok.extern.java.Log;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 
 */
@CrossOrigin
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {
	@Autowired
	private IMenuService menuService;
	
	/**
	 * 获取数据表数据
	 */
	@RequiresPermissions("menu:view")
	@GetMapping("getMenusPage")
	public Result getMenusPage(Menu menu,PageInfo<Menu> pageInfo) {
		try {
			Result result = menuService.getMenusPage(menu,pageInfo);
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("获取菜单表格失败");
		}
	}
	
	/**
	 * 获取菜单树
	 */
	@RequiresPermissions("menu:view")
	@GetMapping("getMenuTree")
	public Result getMenuTree(){
		try {
			List<Menu> menuTree = menuService.getMenuTree();
			return new Result().success().data(menuTree);	
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("获取菜单树失败");
		}
	}
	
	@RequiresPermissions("menu:view")
	@GetMapping("getAllMenu")
	public Result getAllMenu() {
		try {
			List<Menu> menus = menuService.getAllMenu();
			return new Result().success().data(menus);	
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("获取菜单失败");
		}
	}
	
	@SysLog("添加菜单")
	@RequiresPermissions("menu:add")
	@PostMapping("add")
	public Result menuAdd(Menu menu) {
		try {
			menuService.addMenu(menu);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("添加菜单失败");
		}
	}
	
	@SysLog("修改菜单")
	@RequiresPermissions("menu:update")
	@PostMapping("update")
	public Result updateMenu(Menu menu) {
		try {
			menuService.updateMenu(menu);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("更新菜单失败");
		}
	}
	
	@SysLog("删除菜单")
	@RequiresPermissions("menu:delete")
	@PostMapping("delete/{menuId}")
	public Result deleteMenu(@PathVariable("menuId")Long menuId) {
		try {
			menuService.logicDeleteMenu(menuId);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("删除菜单失败");
		}
	}
}
