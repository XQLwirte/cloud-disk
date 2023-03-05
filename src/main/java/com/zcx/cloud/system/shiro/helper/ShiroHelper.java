package com.zcx.cloud.system.shiro.helper;


import java.util.Collection;
import java.util.Objects;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.stereotype.Component;

import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.UserLoginedException;
import com.zcx.cloud.system.shiro.cache.ShiroCache;
import com.zcx.cloud.system.shiro.cache.ShiroCacheManager;
import com.zcx.cloud.system.shiro.session.ShiroSessionDAO;
import com.zcx.cloud.system.shiro.session.ShiroSessionManager;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Shiro工具
 * @author Administrator
 *
 */
@Slf4j
@Component
public class ShiroHelper {
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	public void login(User user,Boolean rememberMe) {
		//1.获取当前安全操作对象
		Subject subject = SecurityUtils.getSubject();
		//2.创建Token
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword());
		//3.设置是否记住我
		token.setRememberMe(rememberMe);
		//4.登录
		subject.login(token);
	}
	
	
	/**
	 * 检测用户是否登录
	 * @param username
	 * @return true:已经登录
	 */
	public boolean checkLogined(String username) {
		//1.获取所有活跃的Session
		Collection<Session> activeSessions = ((ShiroSessionManager)((DefaultWebSecurityManager)SecurityUtils.getSecurityManager()).getSessionManager()).getSessionDAO().getActiveSessions();
		//2.检测Session中是否包含当前登录进行
		if(CollectionUtil.noNullAndSizeGtZero(activeSessions)) {
			for(Session session:activeSessions) {
				SimplePrincipalCollection attribute = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
				if(Objects.nonNull(attribute)) {
					User user = (User) attribute.getPrimaryPrincipal();
					if(Objects.nonNull(user)) {
						if(username.equals(user.getUsername()))
							return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 清空Shiro缓存
	 */
	public void clearCache() {
		//1.清空所有缓存
		ShiroCache cache = (ShiroCache) ((DefaultWebSecurityManager)SecurityUtils.getSecurityManager()).getCacheManager().getCache(ShiroCache.CACHE_NAME);
		cache.clear();
		
		//2.清空所有Session
		SessionDAO sessionDAO = ((ShiroSessionManager)((DefaultWebSecurityManager)SecurityUtils.getSecurityManager()).getSessionManager()).getSessionDAO();
		Collection<Session> activeSessions = sessionDAO.getActiveSessions();
		if(CollectionUtil.noNullAndSizeGtZero(activeSessions)) {
			activeSessions.forEach(session -> {
				sessionDAO.delete(session);
			});
		}
	}
	
	/**
	 * 注销
	 */
	public void logout() {
		//1.获取当前用户安全操作对象
		Subject subject = SecurityUtils.getSubject();
		//2.注销
		subject.logout();
	}
}
