package com.zcx.cloud.system.shiro.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.util.AppUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 解决前后端分离，在Shiro中的options请求被拒绝
 * @author ZCX
 *
 */
@Slf4j
public class ShiroFilter extends FormAuthenticationFilter{
	@Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            setHeader(httpRequest,httpResponse);
            return true;
        }
        return super.preHandle(request,response);
    }

	/**
     * 该方法会在验证失败后调用，这里由于是前后端分离，后台不控制页面跳转
     * 因此重写改成传输JSON数据
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
    	String type = WebUtils.toHttp(request).getHeader("Request-Type");
    	if(Constant.FLAG_CLIENT.equals(type)) {//前端请求
			saveRequest(request);
            setHeader((HttpServletRequest) request,(HttpServletResponse) response);
            PrintWriter out = response.getWriter();
            Result result = new Result().code(HttpStatus.UNAUTHORIZED.value()).msg("用户未登陆").put(Constant.AUTHENTICATION_KEY, AppUtil.getSessionId());
            out.println(JSON.toJSONString(result));
            out.flush();
            out.close();
    	}else {//后端请求，返回页面
    		super.saveRequestAndRedirectToLogin(request, response);
    	}

    }

	/**
     * 为response设置header，实现跨域
     */
    private void setHeader(HttpServletRequest request,HttpServletResponse response){
        //跨域的header设置
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", request.getMethod());
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
        //防止乱码，适用于传输JSON数据
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
    }
}
