package com.zcx.cloud.system.shiro.realm;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.permission.service.IPermissionService;
import com.zcx.cloud.role.entity.Role;
import com.zcx.cloud.role.service.IRoleService;
import com.zcx.cloud.system.shiro.session.ShiroSessionDAO;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.service.IUserService;


/**
 * 自定义Realm，处理认证、授权信息
 * @author Administrator
 *
 */
public class UserRealm extends AuthorizingRealm {
	private static String REALM_NAME = "AUTHORIZING_REALM";
	
	@Autowired
	private IUserService userService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IPermissionService permissionService;
	
	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//1.获取操作用户信息
		User user = (User) principals.getPrimaryPrincipal();
		
		//2.获取角色信息
		List<Role> roles = roleService.getRolesByUserId(user.getUserId());
		Set<String> roleCodes = roles.stream().map(role -> role.getRoleCode()).collect(Collectors.toSet());
		
		//3.获取权限信息
		List<Permission> permissions = permissionService.getPermissionsByRoles(roles);
		Set<String> permissionCodes = permissions.stream().map(permission -> permission.getPermissionCode()).collect(Collectors.toSet());
		
		//4.创建授权对象
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.addRoles(roleCodes);
		authorizationInfo.addStringPermissions(permissionCodes);
		
		//5.返回
		return authorizationInfo;
	}

	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//1.获取token用户信息
		String username = (String) token.getPrincipal();
		//2.查询对应用户
		User user = userService.getUserByUsername(username);
		//3.返回成功认证信息
		if(Objects.isNull(user)) 
			return null;
		//将user对象作为principle,全局获取的principle就是当前登录用户
		//SimpleAuthenticationInfo（三个参数：当前用户，认证凭证，realmName）
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user,user.getPassword(),REALM_NAME);
		
		return authenticationInfo;
	}

}
