package com.zcx.cloud.system.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.zcx.cloud.system.redis.helper.RedisHelper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Shiro缓存
 * @author Administrator
 *
 * @param <K>
 * @param <V>
 */
@Slf4j
@Data
public class ShiroCache<K, V> implements Cache<K, V>{
	public static final String CACHE_NAME = "SHIRO_CACHE";
	
	/**
	 * Shiro缓存前缀，便于在Redis中查找到shiro相关的缓存内容
	 */
	private static final String SHIRO_CACHE_PREFIX = "shiro_cache_prefix_";
	
	private RedisHelper redisHelper;

	/**
	 * 获取到加shiro缓存前缀的key字节数组
	 * 加上username作为表示，防止不同用户权限紊乱
	 * @param k
	 * @return
	 */
	private byte[] getKey(K k) {
		if(k instanceof String) {
			return (SHIRO_CACHE_PREFIX + AppUtil.getCurrentUser().getUsername()+ "_" + (String)k).getBytes();
		}
		return (SHIRO_CACHE_PREFIX + AppUtil.getCurrentUser().getUsername()+ "_").getBytes();
	}
	
	/**
	 * 清空shiro缓存
	 */
	@Override
	public void clear() throws CacheException {
		redisHelper.clear(SHIRO_CACHE_PREFIX);
	}

	/**
	 * 获取Shiro缓存数据
	 */
	@Override
	public V get(K arg0) throws CacheException {
		byte[] key = getKey(arg0);
		byte[] value = redisHelper.get(key);
		if(Objects.nonNull(value)) {
			return (V)SerializationUtils.deserialize(value);
		}else {
			return null;
		}
	}

	/**
	 * 获取Shiro所有缓存的key集合
	 */
	@Override
	public Set<K> keys() {
		Set<K> keys = redisHelper.keys(SHIRO_CACHE_PREFIX + "*").stream()
				.map(key -> (K)key).collect(Collectors.toSet());
		return keys;
	}

	/**
	 * 存入缓存数据
	 */
	@Override
	public V put(K arg0, V arg1) throws CacheException {
		byte[] key = getKey(arg0);
		byte[] value = SerializationUtils.serialize(arg1);
		redisHelper.set(key, value);
		return arg1;
	}

	/**
	 * 移除某个缓存数据
	 */
	@Override
	public V remove(K arg0) throws CacheException {
		byte[] key = getKey(arg0);
		byte[] value = redisHelper.get(key);
		if(Objects.isNull(value))
			return null;
		V v = (V)SerializationUtils.deserialize(value);
		redisHelper.remove(key);
		return v;
	}

	/**
	 * Shiro缓存数据种记录数
	 */
	@Override
	public int size() {
		Set<String> keys = redisHelper.keys(SHIRO_CACHE_PREFIX + "*");
		return Objects.isNull(keys)?0:keys.size();
	}

	/**
	 * 获取Shiro缓存的所有value集合
	 */
	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<V>();
		//1.获取Shiro相关的所有key
		Set<String> keys = redisHelper.keys(SHIRO_CACHE_PREFIX + "*");
		if(CollectionUtil.isNullOrSizeLeZero(keys))
			return values;
		//2.获取所有key对应的value
		keys.stream().forEach(key -> {
			byte[] value = redisHelper.get(key.getBytes());
			V v = (V) SerializationUtils.deserialize(value);
			values.add(v);
		});
		
		return values;
	}

}
