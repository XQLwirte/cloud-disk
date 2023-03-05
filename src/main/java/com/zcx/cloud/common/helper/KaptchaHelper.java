package com.zcx.cloud.common.helper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.zcx.cloud.common.vo.Captcha;

import lombok.extern.slf4j.Slf4j;

/**
 * 验证码工具
 * @author ZCX
 *
 */
@Slf4j
@Component
public class KaptchaHelper {
 
    @Autowired
    private DefaultKaptcha defaultKaptcha;
 
    /**
     * 生成验证码.
     * @param request request
     * @param response response
     * @return CaptchaDTO
     */
    public Captcha kaptcha() {
    	Captcha captcha = new Captcha();
        // 生产验证码字符串
        String createText = this.defaultKaptcha.createText();
        captcha.setText(createText);
        // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage bufferedImage = this.defaultKaptcha.createImage(createText);
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            // 使用生产的验证码字符串返回一个BufferedImage
            ImageIO.write(bufferedImage, "jpeg", jpegOutputStream);
            Base64.Encoder encoder = Base64.getEncoder();
            String base64 = encoder.encodeToString(jpegOutputStream.toByteArray());
            String captchaBase64 = "data:image/jpeg;base64,"
                    + base64.replaceAll("\r\n", "");
            captcha.setImg(captchaBase64);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (jpegOutputStream != null) {
                    jpegOutputStream.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return captcha;
    }
 
}
