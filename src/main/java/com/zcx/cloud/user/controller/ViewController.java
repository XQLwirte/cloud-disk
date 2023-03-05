package com.zcx.cloud.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.service.IUserService;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.ViewUtil;

/**
 * User模块视图跳转
 * @author Administrator
 *
 */
@Component("userViewController")
@RequestMapping("/base/user")
public class ViewController {
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	
	/**
	 * 用户信息页
	 * @return
	 */
	@GetMapping("user")
	public String user(Model model) {
		model.addAttribute("currentUser", AppUtil.getCurrentUser());
		return ViewUtil.view("base/user/user");
	}
	
	/**
	 * 用户详情
	 * @param userId
	 * @param model
	 * @return
	 */
	@GetMapping("userDetail/{userId}")
	public String userDetail(@PathVariable("userId")Long userId,Model model) {
		User user = userService.getById(userId);
		model.addAttribute("user",user);
		return ViewUtil.view("base/user/userDetail");
	}
	
	/**
	 * 添加用户
	 * @return
	 */
	@GetMapping("userAdd")
	public String userAdd() {
		return ViewUtil.view("base/user/userAdd");
	}
	
	/**
	 * 编辑用户
	 * @return
	 */
	@GetMapping("userEdit/{userId}")
	public String userEdit(@PathVariable("userId")Long userId,Model model) {
		User user = userService.getById(userId);
		List<Long> roleIds = roleService.getRoleIdsByUserId(userId);
		model.addAttribute("user",user);
		model.addAttribute("roleIds",roleIds);
		return ViewUtil.view("base/user/userEdit");
	}
	
	/**
	 * 修改密码
	 * @return
	 */
	@GetMapping("userEditPassword")
	public String userEditPassword(Model model) {
		User currentUser = AppUtil.getCurrentUser();
		model.addAttribute("currentUser", currentUser);
		return ViewUtil.view("base/user/userEditPassword");
	}
}
