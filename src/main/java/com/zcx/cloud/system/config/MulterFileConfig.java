package com.zcx.cloud.system.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringBoot文件上传格式配置
 * @author ZCX
 *
 */
@Configuration
public class MulterFileConfig {

	/**
	 * 文件上传配置
	 * @return
	 */
	@Bean  
    public MultipartConfigElement multipartConfigElement() {  
        MultipartConfigFactory factory = new MultipartConfigFactory();  
        //文件最大  
        factory.setMaxFileSize("50MB"); //KB,MB
        /// 设置总上传数据总大小  
        factory.setMaxRequestSize("50MB");  
        return factory.createMultipartConfig();  
	}
}
