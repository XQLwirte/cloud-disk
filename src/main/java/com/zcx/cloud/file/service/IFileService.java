package com.zcx.cloud.file.service;

import com.zcx.cloud.common.vo.TimeData;
import com.zcx.cloud.file.entity.File;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文件表 服务类
 * </p>
 *

 */
public interface IFileService extends IService<File> {

	/**
	 * 获取挂载文件
	 * @param 当前文件夹ID
	 * @return
	 */
	List<File> getChildFiles(Long currentId);

	/**
	 * 文件上传到FastDFS服务器中，并在数据库中插入一条文件记录
	 * @param file
	 * @param folderId
	 */
	void upload(MultipartFile file, Long folderId);

	/**
	 * 文件重命名
	 * @param fileId
	 * @param newName
	 */
	void rename(Long fileId, String newName);

	/**
	 * 文件移入回收站
	 * @param fileId
	 */
	void moveGarbage(Long fileId);

	/**
	 * 文件批量移入回收站
	 * @param fileIds
	 */
	void moveGarbageBat(List<Long> fileIds);

	/**
	 * 文件移动
	 * @param fileId 文件ID
	 * @param targetId 目标目录ID
	 */
	void move(Long fileId, Long targetId);

	/**
	 * 根据类型获取时间分序数据
	 * @param fileType
	 * @return
	 */
	List<TimeData<File>> getTimeDataByType(String fileType);

	/**
	 * 通过文件名查找当前用户的文件
	 * @param fileName
	 * @return
	 */
	List<File> searchByName(String fileName);

}
