package com.zcx.cloud.system.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 在配置文件中的项目配置信息
 * @author Administrator
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
	private ShiroProperties shiro;
	private RedisProperties redis;
	/**
	 * 后端登陆角色限制
	 */
	private List<String> backRoles = new ArrayList<String>();
	/**
	 * 前端登陆角色控制
	 */
	private List<String> frontRoles = new ArrayList<String>();
}
