package com.zcx.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 使用外置tomcat时，必须继承自SpringBootServletInitializer
 * 相对于传统的web程序，作用就在于弥补缺省的web.xml
 * 并重写configure方法，制定启动主类
 * @author ZCX
 *
 */
@SpringBootApplication
@MapperScan("com.zcx.cloud.*.mapper")
public class Application extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);//指向主类
	}
}
