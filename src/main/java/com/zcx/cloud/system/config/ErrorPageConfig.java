package com.zcx.cloud.system.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.zcx.cloud.util.ViewUtil;


/**
 * 错误页面自定义
 * @author Administrator
 *
 */
@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
    	ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND,"/error/404");
        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500");
        registry.addErrorPages(error404Page,error500Page);
    }
}
