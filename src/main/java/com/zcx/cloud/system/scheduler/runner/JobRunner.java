package com.zcx.cloud.system.scheduler.runner;

import java.lang.reflect.Method;

import org.assertj.core.util.Strings;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.helper.SpringHelper;
import com.zcx.cloud.job.entity.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务执行器
 * @author Administrator
 *
 */
@Slf4j
public class JobRunner implements org.quartz.Job{
	@Autowired
	private SpringHelper springHelper;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//1.获取到Job实例
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		Job job = (Job) dataMap.get(Constant.JOB_ENTITY_KEY);
		String beanName = job.getBeanName();
		String beanMethod = job.getBeanMethod();
		String beanParam = job.getBeanParam();
		try {
			//2.找到IOC容器中的bean对象
			Object bean = springHelper.getBean(beanName);
			//3.执行bean的方法
			if(Strings.isNullOrEmpty(beanParam)) {//无参数
				Method method = bean.getClass().getDeclaredMethod(beanMethod);
				method.invoke(bean);
			} else {//有参数
				Method method = bean.getClass().getDeclaredMethod(beanMethod, String.class);
				method.invoke(bean, beanParam);
			}
		} catch(Exception e) {
			e.printStackTrace();
			log.info("执行失败：Bean({})-Method({})-Param({})",beanName,beanMethod,beanParam);
		}
		
		
	}

}
