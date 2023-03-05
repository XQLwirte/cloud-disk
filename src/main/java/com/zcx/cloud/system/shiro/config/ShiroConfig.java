package com.zcx.cloud.system.shiro.config;

import java.util.Map;

import javax.servlet.Filter;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zcx.cloud.system.properties.ProjectProperties;
import com.zcx.cloud.system.properties.ShiroProperties;
import com.zcx.cloud.system.redis.helper.RedisHelper;
import com.zcx.cloud.system.shiro.cache.ShiroCache;
import com.zcx.cloud.system.shiro.cache.ShiroCacheManager;
import com.zcx.cloud.system.shiro.filter.ShiroFilter;
import com.zcx.cloud.system.shiro.realm.UserRealm;
import com.zcx.cloud.system.shiro.session.ShiroSessionDAO;
import com.zcx.cloud.system.shiro.session.ShiroSessionManager;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

/**
 * Shiro配置Bean
 * @author Administrator
 *
 */
@Configuration
public class ShiroConfig {
	private static final String COOKIE_NAME = "shiro_cookie";
	
	@Autowired
	private ProjectProperties properties;
	@Autowired
	private RedisHelper redisHelper;
	
	/**
	 * 创建SecurityManager安全管理器Bean
	 * @return
	 */
	@Bean
	public SecurityManager getSecurityManager(@Qualifier("userRealm")UserRealm userRealm,
			CookieRememberMeManager rememberMeManager) {
		//新建安全管理器对象
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		//设置realm
		securityManager.setRealm(userRealm);
		//设置自定义的Session管理器
		securityManager.setSessionManager(getSessionManager());
		//设置自定义的Shiro缓存管理器
		securityManager.setCacheManager(getShiroCacheManager());
		//设置RememberMeManager
		securityManager.setRememberMeManager(rememberMeManager);
		//返回
		return securityManager;
	}
	
	/**
	 * 自定义Session管理器
	 */
	public ShiroSessionManager getSessionManager() {
		ShiroSessionManager sessionManager = new ShiroSessionManager();
		sessionManager.setSessionDAO(getSessionDAO());
		return sessionManager;
	}
	public ShiroSessionDAO getSessionDAO() {
		ShiroSessionDAO sessionDAO = new ShiroSessionDAO();
		sessionDAO.setRedisHelper(redisHelper);
		return sessionDAO;
	}
	
	/**
	 * Shiro自定义缓存管理
	 */
	public ShiroCacheManager getShiroCacheManager() {
		ShiroCacheManager shiroCacheManager = new ShiroCacheManager();
		shiroCacheManager.setShiroCache(getShiroCache());
		return shiroCacheManager;
	}
	public ShiroCache<Object,Object> getShiroCache(){
		ShiroCache<Object, Object> shiroCache =  new ShiroCache<Object, Object>();
		shiroCache.setRedisHelper(redisHelper);
		return shiroCache;
	}
	
	 
	
	/**
	 * 创建shiro过滤器r工厂bean
	 * @param securityManager
	 * @return
	 */
	@Bean
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {
		//新建shiro过滤器工厂bean
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		
		//设置安全管理器对象
		factoryBean.setSecurityManager(securityManager);
		
		/**
		 * 内置拦截器
		 * 	anon:无需认证（注意anon配置必须在authc之前）
		 *  authc:需要认证的URl
		 *  user:使用rememberMe功能的用户能直接访问
		 *  perms:某些权限可以访问
		 *  role:角色可以访问
		 */
		//配置Url过滤
		ShiroProperties shiro = properties.getShiro();
		Map<String,String> filterMap = new LinkedMap();
		//设置无需认证的URL
		shiro.getAnons().forEach(anon -> {
			filterMap.put(anon,"anon");
		});
		//设置需要认证的url
		filterMap.put(shiro.getAuthc(),"authc");
		//设置登录URL
		factoryBean.setLoginUrl(shiro.getLogin());
		//设置登录成功的URL
		factoryBean.setSuccessUrl(shiro.getSuccess());
		//设置拦截链
		factoryBean.setFilterChainDefinitionMap(filterMap);
		
		///添加自定义过滤器
		Map<String, Filter> filters = factoryBean.getFilters();
		filters.put("authc", new ShiroFilter());
		factoryBean.setFilters(filters);
		
		//返回
		return factoryBean;
	}
	
	/**
	 * 自定义realm
	 * @return
	 */
	@Bean("userRealm")
	public UserRealm getUserRealm() {
		return new UserRealm();
	}

	
	/**
	 * 使用Shiro RememberMe功能（Cookie存储）
	 * @param cookie
	 * @return
	 */
	@Bean
    public CookieRememberMeManager getCookieRememberMeManager(SimpleCookie cookie) {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(cookie);
        return cookieRememberMeManager;
    }
	@Bean
    public SimpleCookie sessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        //cookie的name,对应的默认是 JSESSIONID
        simpleCookie.setName(COOKIE_NAME);
        simpleCookie.setMaxAge(60*30);//设置Cookie有效时常30分钟
        simpleCookie.setPath("/");
        simpleCookie.setHttpOnly(false);//只支持http
        return simpleCookie;
	}
	
	

	/**
	 * 开启Shiro注解,@RequirePermissions等注解可用
	 */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    
    /**
     ** 开启Shiro html权限标签 hasPermission等可用
     */
	@Bean
	public ShiroDialect shiroDialect() {
	   return new ShiroDialect();
	}
}
