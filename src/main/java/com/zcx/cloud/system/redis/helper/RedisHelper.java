package com.zcx.cloud.system.redis.helper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zcx.cloud.system.exception.RedisConnectException;
import com.zcx.cloud.util.CollectionUtil;

import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis工具
 * @author Administrator
 *
 */
@Data
public class RedisHelper {
	
	private JedisPool jedisPool;
	
	/**
	 * Jedis获取字节数组value
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		Jedis jedis = null;
		byte[] value = null;
		try {
			jedis = jedisPool.getResource();
			value = jedis.get(key);
		}catch(Exception e) {
			throw new RedisConnectException("Redis连接失败");
		}finally {
			jedis.close();
		}
		return value;
	}
	
	/**
	 * Jedis存储直接数组键值对
	 * @param key
	 * @param value
	 */
	public void set(byte[] key,byte[] value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		}catch(Exception e) {
			throw new RedisConnectException("Redis连接失败");
		}finally {
			jedis.close();
		}
	}
	
	/**
	 * 删除指定key的数据
	 * @param key
	 */
	public void remove(byte[] key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
		}catch(Exception e) {
			throw new RedisConnectException("Redis连接失败");
		}finally {
			jedis.close();
		}
	}
	
	/**
	 * 通过key前缀清空指定Redis缓存数据
	 * @param prefix 前缀
	 */
	public void clear(String prefix) {
		//获取到所有相关的key集合
		Set<String> keys = keys(prefix + "*");
		if(Objects.isNull(keys)||keys.size()<=0)
			return;
		//依次删除所有数据
		keys.forEach(key -> {
			this.remove(key.getBytes());
		});
	}
	
	
	/**
	 * 获取指定前缀的所有key集合
	 * @param pattern 例：shiro_*
	 * @return
	 */
	public Set<String> keys(String pattern){
		Set<String> keys = new HashSet<String>();
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			keys = jedis.keys(pattern);
		}catch(Exception e) {
			keys.clear();
			throw new RedisConnectException("Redis连接失败");
		}finally {
			jedis.close();
		}
		return keys;
	}
}
