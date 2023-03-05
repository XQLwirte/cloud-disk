package com.zcx.cloud.system.fastdfs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

import com.github.tobato.fastdfs.FdfsClientConfig;

/**
 * FastDFS配置Bean
 * @author Administrator
 *
 */
@Configuration
//@Import(FdfsClientConfig.class)注解，就可以拥有带有连接池的FastDFS Java客户端了
@Import(FdfsClientConfig.class)
//@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)注解是为了解决JMX重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FDFSConfig {
	
}
