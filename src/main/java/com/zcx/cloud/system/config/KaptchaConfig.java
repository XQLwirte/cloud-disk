package com.zcx.cloud.system.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

/**
 * Kaptcha验证码配置
 * @author ZCX
 *
 */
@Configuration
public class KaptchaConfig {
 
    private final static String CODE_LENGTH = "4";
 
    private final static String SESSION_KEY = "handsome_yang";
 
    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 设置边框，合法值：yes , no
        properties.setProperty("kaptcha.border", "yes");
        // 设置边框颜色，合法值： r,g,b (and optional alpha) 或者 white,
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 设置字体颜色， r,g,b 或者 white,black,blue.
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 设置图片宽度
        properties.setProperty("kaptcha.image.width", "118");
        // 设置图片高度
        properties.setProperty("kaptcha.image.height", "40");
        // 设置字体尺寸
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // 设置session key
        properties.setProperty("kaptcha.session.key", SESSION_KEY);
        // 设置验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", CODE_LENGTH);
        // 设置字体
        // 注意：设置的字体可能在Linux上没有，从而验证码图片不可用
        // 例如在Android上也就是Linux内核，所以使用的windows的字体将验证码图片无法显示
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
 
}