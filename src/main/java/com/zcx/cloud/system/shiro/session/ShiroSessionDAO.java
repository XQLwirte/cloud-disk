package com.zcx.cloud.system.shiro.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.zcx.cloud.system.redis.helper.RedisHelper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Shiro会话管理
 * @author Administrator
 *
 */
@Slf4j
@Data
public class ShiroSessionDAO extends AbstractSessionDAO{
	/**
	 * Shiro在Redis中的Session缓存前缀
	 */
	private static final String SHIRO_SESSION_PREFIX = "shiro_session_prefix_";

	private RedisHelper redisHelper;

	/**
	 * 根据SessionID获取到带有前缀的Key
	 * @param sessionId
	 * @return
	 */
	public byte[] getKey(Serializable sessionId) {
		if(Objects.nonNull(sessionId)) {
			return (SHIRO_SESSION_PREFIX + sessionId).getBytes();
		}
		return SHIRO_SESSION_PREFIX.getBytes();
	}
	
	/**
	 * Session被创建的回调
	 */
	@Override
	protected Serializable doCreate(Session session) {
		if(Objects.isNull(session))
			return null;
		//1.生成SessionID
		Serializable sessionId = this.generateSessionId(session);
		//2.分配SessionID，将Session与SessionId绑定
		assignSessionId(session, sessionId);
		//3.存放至Redis中
		byte[] key = this.getKey(sessionId);
		byte[] value = SerializationUtils.serialize(session);
		redisHelper.set(key, value);
		
		return sessionId;
	}
	
	/**
	 * 读取每次会话信息回调
	 */
	@Override
	protected Session doReadSession(Serializable sessionId) {
		if(Objects.isNull(sessionId))
			return null;
		//1.从Redis缓存中查询Session信息
		byte[] key = this.getKey(sessionId);
		byte[] value = redisHelper.get(key);
		if(Objects.isNull(value))
			return null;
		//2.返回Session对象
		Session session = (Session)SerializationUtils.deserialize(value);
		return session;
	}
	
	/**
	 * 会话更新回调
	 */
	@Override
	public void update(Session session) throws UnknownSessionException {
		if(Objects.isNull(session)||Objects.isNull(session.getId()))
			return;
		//1.获取当前Session的Id
		Serializable sessionId = session.getId();
		//2.更新Redis中对应的Session对象
		byte[] key = getKey(sessionId);
		byte[] value = SerializationUtils.serialize(session);
		redisHelper.set(key, value);
	}

	/**
	 * 移除会话
	 */
	@Override
	public void delete(Session session) {
		if(Objects.isNull(session)||Objects.isNull(session.getId()))
			return;
		//1.获取当前Session的Id
		Serializable sessionId = session.getId();
		//2.从Redis中移除相应的Session
		byte[] key = getKey(sessionId);
		redisHelper.remove(key);
	}

	/**
	 * 获取所有活跃的Session对象
	 */
	@Override
	public Collection<Session> getActiveSessions() {
		List<Session> sessions = new ArrayList<Session>();
		//1.获取所有有效的session key
		Set<String> keys = redisHelper.keys(SHIRO_SESSION_PREFIX + "*");
		if(CollectionUtil.isNullOrSizeLeZero(keys))
			return sessions;
		//2.获取所有key对应的Session
		keys.forEach(key -> {
			byte[] value = redisHelper.get(key.getBytes());
			Session session = (Session)SerializationUtils.deserialize(value);
			sessions.add(session);
		});
		
		return sessions;
	}

}
