package com.zcx.cloud.util;

import com.swetake.util.Qrcode;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 二维码工具
 * @author ZCX
 *
 */
public interface QRCodeUtil {


	public static BufferedImage getImage(String content) {
		try {
	        //计算二维码图片的高宽比
	        // API文档规定计算图片宽高的方式 ，v是本次测试的版本号
	        int v =6;
	        int width = 67 + 12 * (v - 1);
	        int height = 67 + 12 * (v - 1);


			Qrcode qrcode = new Qrcode();           // 创建Qrcode对象
	        // 排错率可选(%)-L(7):M(15):Q(25):H(30)
	        qrcode.setQrcodeErrorCorrect('M');
	        // 编码模式-Numeric(M-数字)：Binary(B-二进制):KanJi(K-汉字):Alphanumeric(A-英文字母)
	        qrcode.setQrcodeEncodeMode('B');
	        qrcode.setQrcodeVersion(v);             // 设置版本（可选）

	        // 创建画布和画图设备
	        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
	        Graphics2D draw = img.createGraphics();
	        draw.setBackground(Color.WHITE);        // 设置背景色
	        draw.clearRect(0, 0, width, height);    // 清空原始内容
	        draw.setColor(Color.BLACK);             // 设置前景色

	        int posOff = 2;     // 设置偏移量，避免输出点重叠
	        // 设置内容编码
	        byte[] codeContent = content.getBytes("utf-8");
	        // 生成二维数组,300是内容大小，根据自己的内容大小进行设定
	        if (codeContent.length > 0 && codeContent.length < 300) {
	            boolean[][] qrcodeOut = qrcode.calQrcode(codeContent);
	            // 将内容写入到图片中
	            for (int i = 0; i < qrcodeOut.length; i++) {
	                for (int j = 0; j < qrcodeOut.length; j++) {
	                    // 如果当前位置有像素点
	                    if (qrcodeOut[j][i]){
	                       // 写入图片
	                       draw.fillRect(j * 3 + posOff, i * 3 + posOff, 3, 3);
	                   }
	                }
	            }
	        }

	        draw.dispose();                                // 关闭画图设备
	        img.flush();                                   // 刷新缓冲区

	        return img;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将content转换成base64位的图片字符串
	 * @param content
	 * @return
	 */
	public static String toBase64(String content) {
		try {
			BufferedImage image = getImage(content);
	        //输出流
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", stream);
			//加码
	        byte[] bytes = stream.toByteArray();//转换成字节
	        BASE64Encoder encoder = new BASE64Encoder();
	        String png_base64 =  encoder.encodeBuffer(bytes).trim();//转换成base64串
	        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
	        //加上前端识别的base64位图片前缀
	        png_base64 = "data:image/jpeg;base64," + png_base64;

	        return png_base64;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void toFile(String content, String filePath) {
		try {
			BufferedImage image = getImage(content);

			//保存图片
			File file = new File(filePath);
            ImageIO.write(image, "png", file);    // 保存图片

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
