package com.zcx.cloud.user.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.entity.FolderUser;
import com.zcx.cloud.folder.mapper.FolderMapper;
import com.zcx.cloud.folder.mapper.FolderUserMapper;
import com.zcx.cloud.log.entity.Log;
import com.zcx.cloud.log.mapper.LogMapper;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.mapper.RoleMapper;
import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.storage.entity.StorageUser;
import com.zcx.cloud.storage.mapper.StorageMapper;
import com.zcx.cloud.storage.mapper.StorageUserMapper;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.system.exception.UnAllowLoginException;
import com.zcx.cloud.system.exception.UserLoginedException;
import com.zcx.cloud.system.properties.ProjectProperties;
import com.zcx.cloud.system.shiro.helper.ShiroHelper;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.entity.UserRole;
import com.zcx.cloud.user.mapper.UserMapper;
import com.zcx.cloud.user.mapper.UserRoleMapper;
import com.zcx.cloud.user.service.IUserService;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.zcx.cloud.util.EncryptionUtil;
import com.zcx.cloud.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.shiro.authc.UnknownAccountException;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *

 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private ProjectProperties properties;
	@Autowired
	private ShiroHelper shiroHelper;
	@Autowired
	private FolderMapper folderMapper;
	@Autowired
	private FolderUserMapper folderUserMapper;
	@Autowired
	private StorageMapper storageMapper;
	@Autowired
	private StorageUserMapper storageUserMapper;
	@Autowired
	private LogMapper logMapper;


	@Override
	public void loginBack(User user,Boolean rememberMe) {
		//1.用户检测
		User userDB = this.getUserByUsername(user.getUsername());
		if(Objects.isNull(userDB))
			throw new UnknownAccountException();
		//2.角色检测
		List<Role> roles = roleService.getRolesByUserId(userDB.getUserId());
		List<String> backRoleCodes = properties.getBackRoles();
		List<String> roleCodes = new ArrayList<String>();
		roles.forEach(role -> {
			roleCodes.add(role.getRoleCode());
		});
		Collection<String> intersection = CollectionUtil.intersection(roleCodes, backRoleCodes);
		if(CollectionUtil.isNullOrSizeLeZero(intersection))
			throw new UnAllowLoginException("此用户不允许登入");

		//3.获取盐值对密码进行MD5加密
		String md5Password = EncryptionUtil.md5(user.getPassword(), userDB.getSalt());
		user.setPassword(md5Password);

//		//4.单用户登录，检测用户是否已经登录
//		boolean logined = shiroHelper.checkLogined(user.getUsername());
//		if(logined)
//			throw new UserLoginedException("此用户已登录");

		//5.登录
		shiroHelper.login(user,rememberMe);

		//6.写入日志
		saveLoginLog(AppUtil.getCurrentUser().getUsername() + "登入后端");
	}


	@Override
	public void loginFront(User user, Boolean rememberMe) {
		//1.用户检测
		User userDB = this.getUserByUsername(user.getUsername());
		if(Objects.isNull(userDB))
			throw new UnknownAccountException();
		//2.角色检测
		List<Role> roles = roleService.getRolesByUserId(userDB.getUserId());
		List<String> frontRoleCodes = properties.getFrontRoles();
		List<String> roleCodes = new ArrayList<String>();
		roles.forEach(role -> {
			roleCodes.add(role.getRoleCode());
		});
		Collection<String> intersection = CollectionUtil.intersection(roleCodes, frontRoleCodes);
		if(CollectionUtil.isNullOrSizeLeZero(intersection))
			throw new UnAllowLoginException("此用户不允许登入");

		//3.获取盐值对密码进行MD5加密
		String md5Password = EncryptionUtil.md5(user.getPassword(), userDB.getSalt());
		user.setPassword(md5Password);

		//4.单用户登录，检测用户是否已经登录
		boolean logined = shiroHelper.checkLogined(user.getUsername());
		if(logined)
			throw new UserLoginedException("此用户已登录");

		//5.登录
		shiroHelper.login(user,rememberMe);

		//6.写入日志
		saveLoginLog(AppUtil.getCurrentUser().getUsername() + "登入前端");
	}

	/**
	 * 写入登录日志
	 * @param content
	 */
	private void saveLoginLog(String content) {
		Log log = new Log();
		log.setLogContent(content);
		log.setIp(AppUtil.getCurrentIp());
		log.setCreateBy(AppUtil.getCurrentUser().getUsername());
		log.setCreateTime(DateUtil.now());
		log.setValid(Constant.VALID_NORMAL);

		logMapper.insert(log);
	}

	public User getUserByUsername(String username) {
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
		queryWrapper.eq(User::getUsername, username);
		queryWrapper.eq(User::getValid, Constant.VALID_NORMAL);
		User user = userMapper.selectOne(queryWrapper);
		return user;
	}

	@Override
	public Result getUsersPage(User user, PageInfo<User> pageInfo, SortInfo sortInfo) {
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
		//设置查询条件
		if(Objects.nonNull(user)) {
			if(!Strings.isNullOrEmpty(user.getUsername()))
				queryWrapper.like(User::getUsername, user.getUsername());
			if(!Strings.isNullOrEmpty(user.getGender()))
				queryWrapper.eq(User::getGender, user.getGender());
			if(!Strings.isNullOrEmpty(user.getQq()))
				queryWrapper.like(User::getQq, user.getQq());
			if(!Strings.isNullOrEmpty(user.getEmail()))
				queryWrapper.like(User::getEmail, user.getEmail());
			if(Objects.nonNull(user.getCreateTime()))
				queryWrapper.gt(User::getCreateTime, user.getCreateTime());
		}
		queryWrapper.eq(User::getValid, Constant.VALID_NORMAL);

		//排序信息
		Page<User> page = pageInfo.getPage();
		sortInfo.toPageSort(page);

		//查询数据
		Page<User> selectPage = userMapper.selectPage(page, queryWrapper);
		long count = selectPage.getTotal();
		List<User> data = selectPage.getRecords();
		//注入登录状态
		data.forEach(u -> {
			u.setOnline(shiroHelper.checkLogined(u.getUsername()));
		});
		//在线靠前
		data = data.stream().sorted((d1,d2) -> {
			int t1 = d1.getOnline()?1:0;
			int t2 = d2.getOnline()?1:0;
			return t2 - t1;
		}).collect(Collectors.toList());
		return new Result().success().count(count).data(data);
	}

	@Override
	public void register(User user) {
		//1.检查是否存在相同名称的用户
		String username = user.getUsername();
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
		queryWrapper.eq(User::getUsername, username);
		queryWrapper.eq(User::getValid, Constant.VALID_NORMAL);
		List<User> users = userMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(users))
			throw new ExistsException("用户已存在");

		//2.注册用户
		String salt = UUID.randomUUID().toString();
		user.setPassword(EncryptionUtil.md5(user.getPassword(), salt));
		user.setCreateBy(AppUtil.getCurrentUser().getUsername());
		user.setCreateTime(DateUtil.now());
		user.setSalt(salt);
		if(Strings.isNullOrEmpty(user.getPhoto()))
			user.setPhoto(Constant.USER_DEFAULT_PHOTO);
		userMapper.insert(user);

		//3.持久化用户角色关联信息
		String roleIdsStr = user.getRoleIdsStr();
		List<Long> roleIds = StringUtil.stringToLongs(roleIdsStr, Constant.COMMA);
		roleIds.forEach(roleId -> {
			UserRole userRole = new UserRole();
			userRole.setUserId(user.getUserId());
			userRole.setRoleId(roleId);
			userRoleMapper.insert(userRole);
		});

		//4.为用户创建顶级目录
		Folder folder = new Folder();
		folder.setFolderName(Constant.ROOT_FOLDER_NAME);
		folder.setParentId(Constant.ROOT_FOLDER_PARENT_ID);
		folder.setCreateBy(AppUtil.getCurrentUser().getUsername());
		folder.setCreateTime(DateUtil.now());
		folderMapper.insert(folder);
		FolderUser folderUser = new FolderUser();
		folderUser.setFolderId(folder.getFolderId());
		folderUser.setUserId(user.getUserId());
		folderUserMapper.insert(folderUser);

		//5.初始化用户存储信息
		Storage storage = new Storage();
		storage.setCapacityTotal(Constant.INIT_TOTAL_SIZE);
		storage.setCapacityUse(Constant.INIT_USE_SIZE);
		storage.setCreateBy(AppUtil.getCurrentUser().getUsername());
		storage.setCreateTime(DateUtil.now());
		storageMapper.insert(storage);
		StorageUser storageUser = new StorageUser();
		storageUser.setStorageId(storage.getStorageId());
		storageUser.setUserId(user.getUserId());
		storageUserMapper.insert(storageUser);
	}

	@Override
	public void logicDelete(Long userId) {
		User user = new User();
		user.setUserId(userId);
		user.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		user.setUpdateTime(DateUtil.now());
		user.setValid(Constant.VALID_DELETE);
		userMapper.updateById(user);
	}

	@Override
	public int logicDeleteBatch(List<Long> userIds) {
		User user = new User();
		user.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		user.setUpdateTime(DateUtil.now());
		user.setValid(Constant.VALID_DELETE);

		LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>();
		updateWrapper.in(User::getUserId, userIds);

		int count = userMapper.update(user, updateWrapper);
		return count;
	}

	@Override
	public void updateUserById(User user) {
		//1.查询用户名是否重复
		String username = user.getUsername();
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
		queryWrapper.notIn(User::getUserId, user.getUserId());
		queryWrapper.eq(User::getUsername, username);
		queryWrapper.eq(User::getValid, Constant.VALID_NORMAL);
		User selectOne = userMapper.selectOne(queryWrapper);
		if(Objects.nonNull(selectOne))
			throw new ExistsException("用户名已存在");

		//2.更新用户信息
		user.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		user.setUpdateTime(DateUtil.now());
		userMapper.updateById(user);

		//3.处理用户与角色关联信息
		List<Long> roleIds = StringUtil.stringToLongs(user.getRoleIdsStr(), Constant.COMMA);
		//删除掉不存在集合中的用户角色关联信息
		LambdaQueryWrapper<UserRole> qwUR = new LambdaQueryWrapper<UserRole>();
		qwUR.eq(UserRole::getUserId, user.getUserId());
		if(CollectionUtil.noNullAndSizeGtZero(roleIds))
			qwUR.notIn(UserRole::getRoleId, roleIds);
		userRoleMapper.delete(qwUR);
		//查询出现有的用户角色关联信息
		LambdaQueryWrapper<UserRole> qwUR2 = new LambdaQueryWrapper<UserRole>();
		qwUR2.eq(UserRole::getUserId, user.getUserId());
		List<UserRole> userRoles = userRoleMapper.selectList(qwUR2);
		List<Long> roleIdsExists = new ArrayList<Long>();
		if(CollectionUtil.noNullAndSizeGtZero(userRoles))
			userRoles.forEach(userRole -> roleIdsExists.add(userRole.getRoleId()));
		//添加新增的用户角色关联信息
		roleIds.removeAll(roleIdsExists);
		roleIds.forEach(roleId -> {
			UserRole userRole = new UserRole();
			userRole.setUserId(user.getUserId());
			userRole.setRoleId(roleId);
			userRoleMapper.insert(userRole);
		});
	}

	@Override
	public void logout() {
		shiroHelper.logout();
	}

	@Override
	public void updatePassword(User user) throws Exception {
		//1.密码加密转换
		User userDB = userMapper.selectById(user.getUserId());
		String password = userDB.getPassword();
		String oldPasswordMd5 = EncryptionUtil.md5(user.getOldPassword(), userDB.getSalt());
		String newPasswordMd5 = EncryptionUtil.md5(user.getNewPassword(), userDB.getSalt());

		//2.密码比对
		if(!password.equals(oldPasswordMd5))
			throw new Exception("原密码不正确");

		//3.修改密码信息
		userDB.setPassword(newPasswordMd5);
		userDB.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		userDB.setUpdateTime(DateUtil.now());
		userMapper.updateById(userDB);
	}


	@Override
	public void updateUserInfo(User user) {
		//1.查询用户名是否重复
		String username = user.getUsername();
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
		queryWrapper.notIn(User::getUserId, user.getUserId());
		queryWrapper.eq(User::getUsername, username);
		queryWrapper.eq(User::getValid, Constant.VALID_NORMAL);
		User selectOne = userMapper.selectOne(queryWrapper);
		if(Objects.nonNull(selectOne))
			throw new ExistsException("用户名已存在");

		//2.更新用户信息
		user.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		user.setUpdateTime(DateUtil.now());
		user.setValid(Constant.VALID_NORMAL);
		userMapper.updateById(user);

		//3.更新Shiro缓存中的当前用户信息
		user.copyTo(AppUtil.getCurrentUser());
	}

}
