package com.zcx.cloud.system.scheduler.factory;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * jobRunner实例都是由quartz的JobFactory创建的,默认情况下我们在job实现类中注入spring对象都是无效的。因为对象并没有被spring纳入管理。
 * 这时我可以通过AutowireCapableBeanFactory来帮助我们实现
 * @author Administrator
 */
public class JobFactory extends AdaptableJobFactory{
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		//实例化Job实例
		Object jobInstance = super.createJobInstance(bundle);
		//将JobDetail实例装配到IOC容器中
		capableBeanFactory.autowireBean(jobInstance);
		//返回
		return jobInstance;
		
	}
    
    
}
