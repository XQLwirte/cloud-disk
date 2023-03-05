package com.zcx.cloud;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.zcx.cloud.system.fastdfs.helper.FDFSHelper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.EncryptionUtil;
import com.zcx.cloud.util.QRCodeUtil;
import com.zcx.cloud.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@RunWith(value = SpringRunner.class)
public class ApplicationTest {
	@Autowired
	private FDFSHelper fdfsHelper;
	
	/**
	 * MD5加密
	 */
	@Test
	public void md5Encryption() {
		String password = "123456";//要加密的字符串
        String salt = "ff9b8a85-a7e3-42db-9302-f55b32199f3e";
        String md5 = EncryptionUtil.md5(password, salt);
        log.info("原文本："+password);
        log.info("盐值："+salt);
        log.info("加密文本："+md5);
        
	}
	
	/**
	 * 测试FastDFS文件上传
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testFdfsUpload() throws Exception {
		//1.文件准备
		FileInputStream inputStream = new FileInputStream("D:\\PersonalFile\\ResourceFile\\picture\\girl\\mm14.jpg");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] temp = new byte[1024];
		int size = 0;
		while((size = inputStream.read(temp)) != -1) {
			outputStream.write(temp, 0, size);
		}
		byte[] bytes = outputStream.toByteArray();
		inputStream.close();
		outputStream.close();
		
		//2。fdfs工具上传
		String path = fdfsHelper.uploadFile(bytes, bytes.length, "jpg");
		log.info("文件上传成功："+path);
	}
	
	/**
	 * 测试FastDFS文件下载
	 */
	@Test
	public void testFdfsDownload() {
		String group = "group1";
		String path = "M00/00/00/rBI8Jl48zMyAP_L2AAJr-ChOJeE893.jpg";
		byte[] bs = fdfsHelper.downloadFile(group, path);
		if(Objects.nonNull(bs)) {
			log.info("文件读取成功："+bs.length);
		}else {
			log.info("文件读取失败");
		}
	}
	
	/**
	 * 测试FastDFS文件删除
	 */
	@Test 
	public void testFdfsDelete() {
		String group = "group1";
		String path = "M00/00/00/rBI8Jl48zMyAP_L2AAJr-ChOJeE893.jpg";
		fdfsHelper.deleteFile(group, path);
		log.info("文件删除成功");
	}
	
	/**
	 * 测试FastDFS获取虚拟路径
	 */
	@Test 
	public void testFdfsGetPath() {
		String url = "http://39.108.122.170:8081/group1/M00/00/00/rBI8Jl5TQLaAScPfAAAeSeoqIzE395.jpg";
		String path = fdfsHelper.getPath(url);
		log.info("URL:"+url);
		log.info("PATH:"+path);
	}
	
	
	/**
	 * 测试分隔文件拓展名
	 */
	@Test
	public void testGetExtension() {
		String fileName = "aaasds.sdsds.jpg";
		String extension = StringUtil.getExtension(fileName);
		log.info("文件拓展名："+extension);
	}
	
	/**
	 * 测试QRCode功能
	 */
	@Test
	public void testQRCode() {
		String content = "http://39.108.122.170:8081/group1/M00/00/00/rBI8Jl5XjtiAOPsTAAJr-ChOJeE269.jpg"; 
		String base64 = QRCodeUtil.toBase64(content);
		
		log.info("原内容："+content);
		log.info("Base64位内容："+base64);
		
//		QRCodeUtil.toFile(content, "D:\\code.png");
	}
	
	@Test
	public void testUnderlineToCamelCase() {
		String str = "db_yan_cloud_disk";
		String strCamelCase = AppUtil.underLineToCamelCase(str);
		log.info("源字符串：{}",str);
		log.info("驼峰字符串：{}",strCamelCase);
	}
}
