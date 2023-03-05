package com.zcx.cloud.system.scheduler.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.impl.matchers.EverythingMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.zcx.cloud.system.scheduler.factory.JobFactory;
import com.zcx.cloud.system.scheduler.listener.QuartzJobListener;
import com.zcx.cloud.system.scheduler.listener.QuartzTriggerListener;

@Configuration
public class QuartzConfig {
	/**
	 * 数据源名称
	 */
	public static final String DS_NAME = "quartz";
	
	@Autowired
	private DynamicRoutingDataSource dynamicRoutingDataSource;//取出动态数据源中的quartz数据源
	
	/**
	 * 调度器工厂
	 * @return
	 * @throws Exception 
	 */
	public SchedulerFactoryBean getSchedulerFactoryBean(JobFactory jobFactory) throws Exception {
		//加载properties配置属性
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        //初始化配置属性以及注册各种监听器以及初始化定时任务
        propertiesFactoryBean.afterPropertiesSet();
        
        //创建SchedulerFactoryBean
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        //配置信息
        DataSource quartzDataSource = dynamicRoutingDataSource.getDataSource(DS_NAME);
        factory.setDataSource(quartzDataSource);
        factory.setQuartzProperties(propertiesFactoryBean.getObject());
        factory.setJobFactory(jobFactory);
        factory.setWaitForJobsToCompleteOnShutdown(true);//这样当spring关闭时，会等待所有已经启动的quartz job结束后spring才能完全shutdown。
        factory.setOverwriteExistingJobs(true); 
        factory.setStartupDelay(0);
        factory.afterPropertiesSet();
        return factory;
	}
	
	/**
	 * 创建JobDetail工厂
	 * @return
	 */
	@Bean("jobFactory")
	public JobFactory jobFactory() {
		return new JobFactory();
	}
	
	
	/**
	 * 所有Job监听器
	 * @return
	 */
	@Bean("jobListener")
	public QuartzJobListener jobListener() {
		return new QuartzJobListener();
	}
	
	
	/**
	 * 所有Trigger监听器
	 * @return
	 */
	@Bean("triggerListener")
	public QuartzTriggerListener triggerListener() {
		return new QuartzTriggerListener();
	}
	
	
	/**
	 * 创建调度器实例
	 * @return
	 * @throws Exception 
	 */
    @Bean(name="scheduler")
    public Scheduler scheduler(
    		@Qualifier("jobFactory")JobFactory jobFactory,
    		@Qualifier("jobListener")QuartzJobListener jobListener,
    		@Qualifier("triggerListener")QuartzTriggerListener triggerListener) throws Exception {
        Scheduler scheduler = getSchedulerFactoryBean(jobFactory).getScheduler();
        //添加全局监听器
        ListenerManager listenerManager = scheduler.getListenerManager();
        listenerManager.addJobListener(jobListener, EverythingMatcher.allJobs());
        listenerManager.addTriggerListener(triggerListener);
        
        //调度器启动（切记）
        scheduler.start();
        
        return scheduler;
    }
	
}
