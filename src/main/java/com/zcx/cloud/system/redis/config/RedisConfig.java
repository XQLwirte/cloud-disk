package com.zcx.cloud.system.redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zcx.cloud.system.exception.RedisConnectException;
import com.zcx.cloud.system.properties.ProjectProperties;
import com.zcx.cloud.system.properties.RedisProperties;
import com.zcx.cloud.system.redis.helper.RedisHelper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置Bean
 * @author Administrator
 *
 */
@Slf4j
@Configuration
public class RedisConfig {
	@Autowired
	private ProjectProperties properties;
	
	@Bean
	public RedisHelper getRedisHelper(JedisPool jedisPool) {
		RedisHelper redisHelper = new RedisHelper();
		redisHelper.setJedisPool(jedisPool);
		return redisHelper;
	}
	
	/**
	 * 创建redis连接池
	 */
	@Bean
	public JedisPool getJedisPool() {
		try {
			RedisProperties redisP = properties.getRedis();
			
			JedisPoolConfig config = new JedisPoolConfig();
	        config.setMaxIdle(redisP.getMaxIdle());
	        config.setMaxTotal(redisP.getMaxTotal());
	        config.setMaxWaitMillis(redisP.getMaxWait());
	        config.setTestOnBorrow(redisP.isTestOnBorrow());
			
	        JedisPool jedisPool = new JedisPool(config, redisP.getHost(), redisP.getPort(), 
	        		redisP.getTimeout(), redisP.getPassword(), 
	        		redisP.getDatabase(), redisP.isSsl());
	        //测试连接
	        jedisPool.getResource();
	        
	        return jedisPool;
		}catch (Exception e) {
			log.error("                                                                                                                                                      ");
			log.error("                          _|  _|                                                                              _|              _|_|            _|  _|  ");
			log.error("_|  _|_|    _|_|      _|_|_|        _|_|_|        _|_|_|    _|_|    _|_|_|    _|_|_|      _|_|      _|_|_|  _|_|_|_|        _|        _|_|_|      _|  ");
			log.error("_|_|      _|_|_|_|  _|    _|  _|  _|_|          _|        _|    _|  _|    _|  _|    _|  _|_|_|_|  _|          _|          _|_|_|_|  _|    _|  _|  _|  ");
			log.error("_|        _|        _|    _|  _|      _|_|      _|        _|    _|  _|    _|  _|    _|  _|        _|          _|            _|      _|    _|  _|  _|  ");
			log.error("_|          _|_|_|    _|_|_|  _|  _|_|_|          _|_|_|    _|_|    _|    _|  _|    _|    _|_|_|    _|_|_|      _|_|        _|        _|_|_|  _|  _|  ");
			log.error("                                                                                                                                                      ");
			throw new RedisConnectException("Redis连接失败");
		}
		
	}
	
}
