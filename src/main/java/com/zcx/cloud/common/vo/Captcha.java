package com.zcx.cloud.common.vo;

import lombok.Data;

/**
 * 验证码对象
 * @author ZCX
 *
 */
@Data
public class Captcha {
	/**
	 * 字符串代码
	 */
	private String text;
	/**
	 * base64格式的图片
	 */
	private String img;
}
