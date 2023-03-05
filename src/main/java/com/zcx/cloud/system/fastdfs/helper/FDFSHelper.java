package com.zcx.cloud.system.fastdfs.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.StringUtil;

/**
 * FastDFS工具
 * @author Administrator
 *
 */
@Component("fdfsHelper")
public class FDFSHelper {
	@Value("${fdfs.nginx}")
	private String nginx;
	@Autowired
    private FastFileStorageClient fastFileStorageClient;
	
	/**
     * 文件上传
     *
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @return fastDfs路径，带组名，例如：group1/M00/00/00/zxcscdad.jpg
     */
    public String uploadFile(byte[] bytes, long fileSize, String extension) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        StorePath storePath = fastFileStorageClient.uploadFile(byteArrayInputStream, fileSize, extension, null);
        return storePath.getFullPath();
    }
    
    /**
     * 上传文件
     * @param file
     * @return nginx代理+fastDFS虚拟路径
     */
    public String uploadFile(MultipartFile file) {
		try {
			byte[] bytes = file.getBytes();
	    	long fileSize = file.getSize();
	    	String extension = StringUtil.getExtension(file.getOriginalFilename());
	        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
	        StorePath storePath = fastFileStorageClient.uploadFile(byteArrayInputStream, fileSize, extension, null);
	        String fullPath = storePath.getFullPath();
	        return Strings.concat(Constant.HTTP_PREFIX, nginx, "/", fullPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 下载文件
     * @param group 组名，例如：group1
     * @param path 文件虚拟路径，例如：M00/00/00/zxcscdad.jpg
     * @return
     */
    public byte[] downloadFile(String group, String path){
        DownloadByteArray downloadByteArray = new DownloadByteArray();
        byte[] bytes = fastFileStorageClient.downloadFile(group, path, downloadByteArray);
        return bytes;
    }
    
    /**
     * 删除文件
     * @param group 组名，例如：group1
     * @param path 文件虚拟路径，例如：M00/00/00/zxcscdad.jpg
     */
    public void deleteFile(String group, String path) {
    	fastFileStorageClient.deleteFile(group, path);
    }
    
    /**
     * 删除文件
     * @param filePath 文件完整路径，例如：group1/M00/00/00/zxcscdad.jpg
     */
    public void deleteFile(String filePath) {
    	fastFileStorageClient.deleteFile(filePath);
    }
    
    /**
     * 从完整的url路径中解析出FastDFS文件虚拟路径
     * @param url
     * @return
     */
    public String getPath(String url) {
    	try {
    		String[] split = url.split(nginx + "/");
    		return split[split.length-1];
    	} catch(Exception e) {
    		return null;
    	}
    }
}
