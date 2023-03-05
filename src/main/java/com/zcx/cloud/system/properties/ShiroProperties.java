package com.zcx.cloud.system.properties;

import java.util.List;

import lombok.Data;

@Data
public class ShiroProperties {
	private String authc; //需要认证的URL
	private String login; //登录URL
	private String success; //登录成功跳转的URL
	private List<String> anons; //无需认证的URL列表
}
