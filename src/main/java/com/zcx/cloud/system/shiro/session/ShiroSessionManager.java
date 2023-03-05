package com.zcx.cloud.system.shiro.session;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Objects;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.assertj.core.util.Strings;

import com.zcx.cloud.common.constant.Constant;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义SessionManager
 * 解决一个网页多次授权等等
 * 每次均需访问session而频繁访问redis的情况
 * @author Administrator
 *
 */
@Slf4j
@Data
public class ShiroSessionManager extends DefaultWebSessionManager{
	
	
	/**
	 * 自定义Session的获取逻辑
	 */
	@Override
	protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
		//1.获取SessionID
		Serializable sessionId = getSessionId(sessionKey);
		
		//2.内存中的请求对象中如果存在Session，则直接从内存中获取，无需读取Session
		//获取请求对象
		ServletRequest request = null;
		if(sessionKey instanceof WebSessionKey) {
			request = ((WebSessionKey)sessionKey).getServletRequest();
		}
		//从请求对象中获取Session
		if(Objects.nonNull(request)&&Objects.nonNull(sessionId)) {
			Session session = (Session) request.getAttribute(sessionId.toString());
			if(Objects.nonNull(session))
				return session;
		}
		
		//3.内存中获取不到Session对象，再尝试原生获取
		Session session = super.retrieveSession(sessionKey);
		//将session存放在内存的请求对象中
		if(Objects.nonNull(request)&&Objects.nonNull(sessionId)) {
			request.setAttribute(sessionId.toString(), session);
		}
		return session;
	}
	
	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
		//1.从前端获取token参数
		String Authorization = WebUtils.toHttp(request).getHeader(Constant.AUTHENTICATION_KEY);//如果请求头中有 Authorization 则其值为sessionId
		//2.根据前端传来的token设置相应session的状态
        if (!Strings.isNullOrEmpty(Authorization)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "cookie");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, Authorization);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return Authorization;
        } else {
            //否则按默认规则从cookie取sessionId
            return super.getSessionId(request, response);
        }
	}
}
