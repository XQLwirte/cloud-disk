package com.zcx.cloud.user.controller;


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
import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.service.IUserService;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
	@Autowired
	private IUserService userService;
	
	@RequiresPermissions("user:view")
	@GetMapping("getUsersPage")
	public Result getUsersPage(User user,PageInfo<User> pageInfo,SortInfo sortInfo) {
		try {
			Result result = userService.getUsersPage(user, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取用户分页信息失败");
		}
	}
	
	
	@RequiresPermissions("user:view")
	@GetMapping("getCurrentUser")
	public Result getCurrentUser() {
		try {
			User user = AppUtil.getCurrentUser();
			return new Result().success().data(user);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取当前用户失败");
		}
	}
	
	/**
	 * 注册
	 * @param user
	 * @return
	 */
	@SysLog("添加用户")
	@RequiresPermissions("user:add")
	@PostMapping("add")
	public Result add(User user) {
		try {
			userService.register(user);
			return new Result().success();
		} catch(ExistsException e) {
			return new Result().error().msg("用户名已存在");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("用户注册失败");
		}
	}
	
	
	/**
	 * 删除用户
	 * @param userId
	 * @return
	 */
	@SysLog("删除用户")
	@RequiresPermissions("user:delete")
	@GetMapping("userDelete/{userId}")
	public Result userDelete(@PathVariable("userId")Long userId) {
		try {
			userService.logicDelete(userId);
			return new Result().success();
		} catch(Exception e) {
			return new Result().error();
		}
	}
	
	@SysLog("批量删除用户")
	@RequiresPermissions("user:delete")
	@GetMapping("userDeleteBatch/{userIdsStr}")
	public Result userDeleteBatch(@PathVariable("userIdsStr")String userIdsStr) {
		try {
			List<Long> userIds = StringUtil.stringToLongs(userIdsStr, Constant.COMMA);
			userService.logicDeleteBatch(userIds);
			return new Result().success();
		} catch(Exception e) {
			return new Result().error();
		}
	}
	
	/**
	 * 修改用户
	 * @param user
	 * @return
	 */
	@SysLog("修改用户")
	@RequiresPermissions("user:update")
	@PostMapping("update")
	public Result update(User user) {
		try {
			userService.updateUserById(user);
			return new Result().success();
		} catch(ExistsException e) {
			return new Result().error().msg("用户名已存在");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("用户修改失败");
		}
	}
	
	/**
	 * 修改基本信息
	 * @param user
	 * @return
	 */
	@SysLog("修改用户")
	@RequiresPermissions("user:update")
	@PostMapping("updateInfo")
	public Result updateInfo(User user) {
		try {
			userService.updateUserInfo(user);
			return new Result().success();
		} catch(ExistsException e) {
			return new Result().error().msg("用户名已存在");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("用户修改失败");
		}
	}
	
	/**
	 * 修改密码
	 * @param user
	 * @return
	 */
	@SysLog("修改密码")
	@RequiresPermissions("user:update")
	@PostMapping("userUpdatePassword")
	public Result userUpdatePassword(User user) {
		try {
			userService.updatePassword(user);
			return new Result().success();
		} catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg(e.getMessage());
		}
	}
}
