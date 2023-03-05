package com.zcx.cloud.system.listener;

import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.zcx.cloud.system.shiro.session.ShiroSessionManager;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * SpringBoot项目关闭监听器
 * @author Administrator
 *
 */
@Slf4j
@Component("springBootClosedListener")
public class ClosedListener implements ApplicationListener<ContextClosedEvent>{
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		//手动从Redis中清空所有Session
		SessionDAO sessionDAO = ((ShiroSessionManager)((DefaultWebSecurityManager)SecurityUtils.getSecurityManager()).getSessionManager()).getSessionDAO();
		Collection<Session> activeSessions = sessionDAO.getActiveSessions();
		if(CollectionUtil.noNullAndSizeGtZero(activeSessions)) {
			activeSessions.forEach(session -> {
				sessionDAO.delete(session);
			});
		}
		
		log.info("系统已关闭");
	}

}
