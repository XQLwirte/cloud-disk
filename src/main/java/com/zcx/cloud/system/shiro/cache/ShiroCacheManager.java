package com.zcx.cloud.system.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Shiro缓存管理器
 * @author Administrator
 */
@Data
public class ShiroCacheManager implements CacheManager{
	
	private ShiroCache shiroCache;
	
	@Override
	public <K, V> Cache<K, V> getCache(String arg0) throws CacheException {
		return shiroCache;
	}

}
