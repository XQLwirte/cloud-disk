package com.zcx.cloud.system.controller;

import java.util.Set;

import javax.servlet.ServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.helper.KaptchaHelper;
import com.zcx.cloud.common.vo.Captcha;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.UnAllowLoginException;
import com.zcx.cloud.system.exception.UserLoginedException;
import com.zcx.cloud.system.fastdfs.helper.FDFSHelper;
import com.zcx.cloud.system.shiro.helper.ShiroHelper;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.service.IUserService;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.QRCodeUtil;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@RestController
public class SysController {
	@Autowired
	private IUserService userService;
	@Autowired
	private FDFSHelper fdfsHelper;
	@Autowired
	private KaptchaHelper kaptchaHelper;
	@Autowired
	private ShiroHelper shiroHelper;
	/**
	 * 后端认证
	 * @return
	 */
	@PostMapping("/authenticationBack")
	public Result authenticationBack(User user,
			@RequestParam(value = "rememberMe",defaultValue = "0")Boolean rememberMe) {
		try {
			userService.loginBack(user,rememberMe);
			return new Result().success();
		} catch (IncorrectCredentialsException e) {
            return new Result().error().msg("密码错误");
        } catch (UnknownAccountException e){
            return new Result().error().msg("用户不存在");
        } catch (UserLoginedException e){
            return new Result().error().msg("此用户已登录");
        } catch(UnAllowLoginException e) {
        	return new Result().error().msg("此用户不允许登入");
        } catch(Exception e) {
			e.printStackTrace();
			throw new BusException("认证失败");
		}
	}
	
	/**
	 * 前端认证
	 * @return
	 */
	@PostMapping("/authenticationFront")
	public Result authenticationFront(User user,
			@RequestParam(value = "rememberMe",defaultValue = "0")Boolean rememberMe,
			ServletRequest request) {
		try {
			userService.loginFront(user,rememberMe);
			return new Result().success().put(Constant.AUTHENTICATION_KEY, AppUtil.getSessionId());
		} catch (IncorrectCredentialsException e) {
            return new Result().error().msg("密码错误");
        } catch (UnknownAccountException e){
            return new Result().error().msg("用户不存在");
        } catch (UserLoginedException e){
            return new Result().error().msg("此用户已登录");
        } catch(UnAllowLoginException e) {
        	return new Result().error().msg("此用户不允许登入");
        } catch(Exception e) {
			e.printStackTrace();
			throw new BusException("认证失败");
		}
	}
	
	/**
	 * 验证码
	 * @return
	 */
	@GetMapping("/kaptcha")
	public Result kaptcha() {
		try {
			Captcha kaptcha = kaptchaHelper.kaptcha();
			return new Result().success().data(kaptcha);
		} catch(Exception e) {
			return new Result().error().msg("生成验证码失败");
		}
	}
	
	/**
	 * 获取base64位二维码图片
	 * @return
	 */
	@PostMapping("/getQRCode")
	public Result getQRCode(@RequestParam("content")String content) {
		try {
			String base64 = QRCodeUtil.toBase64(content);
			return new Result().success().data(base64);
		} catch(Exception e) {
			return new Result().error().msg("生成二维码失败");
		}
	}
	
	
	
	/**
	 * 注销
	 * @return
	 */
	@GetMapping("/logout")
	public Result logout() {
		try {
			//注销操作
			userService.logout();
			return new Result().success();
		} catch(Exception e) {
			e.printStackTrace();
			throw new BusException("注销失败");
		}
	}
	
	/**
	 * 清空Shiro缓存
	 * @return
	 */
	@GetMapping("/clearCache")
	public Result clearCache() {
		try {
			shiroHelper.clearCache();
			return new Result().success();
		} catch(Exception e) {
			e.printStackTrace();
			throw new BusException("清空Redis失败");
		}
	}
	
	/**
	 * 文件统一上传接口
	 * @param file
	 * @return 结果result的data为fasddfs+nginx访问路径
	 */
	@PostMapping("/sys/upload")
	public Result sysUpload(MultipartFile file) {
		try {
			String path = fdfsHelper.uploadFile(file);
			return new Result().success().data(path);
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件上传失败");
		}
	}
}
