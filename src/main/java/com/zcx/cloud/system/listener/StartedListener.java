package com.zcx.cloud.system.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.zcx.cloud.util.IpAddressUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * SpringBoot项目启动监听器
 * @author Administrator
 *	ApplicationStartedEvent ：启动完毕监听
 *	ApplicationStartingEvent：开始启动
 */
@Slf4j
@Component("springBootStartedListener")
public class StartedListener implements ApplicationListener<ApplicationStartedEvent>{
	/**
	 * Tomcat服务器端口
	 */
	@Value("${server.port}")
	private int port;
	
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		
		//打印项目地址
		String url = IpAddressUtil.getUrl(port);
		log.info("项目启动完毕："+url);
		
	}
}
