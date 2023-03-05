package com.zcx.cloud.common.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring工具类
 * 实现ApplicationContextAware能捕捉到SpringIOC容器实例
 * @author Administrator
 */
@Component
public class SpringHelper implements ApplicationContextAware{
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 通过名称获取Bean
	 * @param beanName
	 * @return
	 */
	public Object getBean(String beanName) {
		return applicationContext.getBean(beanName);
	}
	
	/**
	 * 根据类型获取Bean
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
}
