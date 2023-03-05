package com.zcx.cloud.system.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.menu.service.IMenuService;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.StringUtil;
import com.zcx.cloud.util.ViewUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ViewController {
	@Autowired
	private IMenuService menuService;
	@Autowired
	private IRoleService roleService;
	
	/**
	 * 登录页面
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return ViewUtil.view("login");
	}
	
	/**
	 * 后台首页
	 * @return
	 */
	@GetMapping("/")
	public String redirectIndex() {
		return "redirect:/index";
	}
	
	@GetMapping("/index")
	public String index(Model model) {
		//获取菜单数据
		List<Menu> menuTree = menuService.getMenuTree();
		model.addAttribute("menuTree", menuTree);
		//用户信息
		model.addAttribute("user", AppUtil.getCurrentUser());
		return ViewUtil.view("index");
	}
	
	/**
	 * 主页
	 */
	@GetMapping("/home/home")
	public String home(Model model) {
		User currentUser = AppUtil.getCurrentUser();
		List<Role> roles = roleService.getRolesByUserId(currentUser.getUserId());
		List<String> roleNames = roles.stream().map(role -> role.getRoleName()).collect(Collectors.toList());
		String roleNamesStr = StringUtil.objsToString(roleNames, " ");
		
		model.addAttribute("currentUser", AppUtil.getCurrentUser());
		model.addAttribute("roleNamesStr", roleNamesStr);
		
		return ViewUtil.view("home/home");
	}
	
	
	/**
	 * 404页面
	 */
	@GetMapping("/error/404")
	public String notFound() {
		return ViewUtil.view("error/404");
	}
	/**
	 * 500页面
	 */
	@GetMapping("/error/500")
	public String internalServerError() {
		return ViewUtil.view("error/500");
	}
	/**
	 * 403页面
	 */
	@GetMapping("/error/403")
	public String noPermissions() {
		return ViewUtil.view("error/403");
	}
}
