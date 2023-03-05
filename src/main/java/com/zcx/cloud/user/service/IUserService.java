package com.zcx.cloud.user.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.user.entity.User;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *

 */
public interface IUserService extends IService<User> {
	

	/**
	 * 分页查询用户信息
	 * @param user
	 * @param pageInfo
	 * @return
	 */
	public Result getUsersPage(User user,PageInfo<User> pageInfo, SortInfo sortInfo);
	
	/**
	 * 后端登陆
	 * @param user
	 */
	public void loginBack(User user,Boolean rememberMe);

	/**
	 * 前端登陆
	 * @param user
	 * @param rememberMe
	 */
	public void loginFront(User user, Boolean rememberMe);
	
	/**
	 * 注销
	 */
	public void logout();
	
	/**
	 * 注册
	 * @param user
	 */
	public void register(User user);
	
	/**
	 * 根据用户名查询用户信息
	 * @param username
	 * @return
	 */
	public User getUserByUsername(String username);
	
	/**
	 * 逻辑删除用户信息
	 * @param userId
	 */
	public void logicDelete(Long userId);
	
	/**
	 * 批量逻辑删除用户信息
	 * @param userIds
	 * @return
	 */
	public int logicDeleteBatch(List<Long> userIds);
	
	/**
	 * 更新用户信息通过ID
	 * @param user
	 */
	public void updateUserById(User user);

	/**
	 * 修改密码
	 * @param user
	 */
	public void updatePassword(User user) throws Exception;

	/**
	 * 修改用户基本信息
	 * @param user
	 */
	public void updateUserInfo(User user);
}
