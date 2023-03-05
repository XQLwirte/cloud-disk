package com.zcx.cloud.task.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.file.mapper.FileMapper;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.mapper.FolderMapper;
import com.zcx.cloud.garbage.entity.Garbage;
import com.zcx.cloud.garbage.entity.GarbageUser;
import com.zcx.cloud.garbage.mapper.GarbageMapper;
import com.zcx.cloud.garbage.mapper.GarbageUserMapper;
import com.zcx.cloud.system.fastdfs.helper.FDFSHelper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除垃圾文件定时任务
 * 
 * 1.删除垃圾基本信息
 * 2.删除垃圾与用户关联信息
 * 3.删除关联文件信息或
 * 4.删除关联目录信息
 * 5.删除FastDFS服务器文件
 * 涉及表：t_garbage、t_garbage_user、t_file、t_folder、t_folder_user
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteGarbage")
public class TaskDeleteGarbage {
	@Autowired
	private GarbageMapper garbageMapper;
	@Autowired
	private GarbageUserMapper garbageUserMapper;
	@Autowired
	private FDFSHelper fdfsHelper;
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	private FolderMapper folderMapper;

	public void deleteGarbage() {
		//1.查询无效的垃圾信息
		LambdaQueryWrapper<Garbage> qwG = new LambdaQueryWrapper<Garbage>();
		qwG.eq(Garbage::getValid, Constant.VALID_DELETE);
		List<Garbage> garbages = garbageMapper.selectList(qwG);
		if(CollectionUtil.isNullOrSizeLeZero(garbages))
			return ;
		List<Long> garbageIds = garbages.stream().map(garbage -> garbage.getGarbageId()).collect(Collectors.toList());
		
		//2.删除垃圾与用户关联信息
		this.deleteGarbageUser(garbageIds);
		
		//3.删除关联资源信息
		this.deleteResources(garbages);
		
		//4.删除无效的垃圾信息
		this.deleteGarbageInfo(garbageIds);
	}
	
	/**
	 * 删除垃圾与用户关联信息
	 * @param garbageIds
	 */
	public void deleteGarbageUser(List<Long> garbageIds) {
		LambdaQueryWrapper<GarbageUser> queryWrapper = new LambdaQueryWrapper<GarbageUser>();
		queryWrapper.in(GarbageUser::getGarbageId, garbageIds);
		garbageUserMapper.delete(queryWrapper);
	}
	
	/**
	 * 删除垃圾关联的资源信息
	 * @param garbageIds
	 */
	public void deleteResources(List<Garbage> garbages) {
		garbages.forEach(garbage -> {
			if(Constant.GARBAGE_FILE.equals(garbage.getResourceType())) { //删除文件
				this.deleteFile(garbage.getResourceId());
			} else if(Constant.GARBAGE_FOLDER.equals(garbage.getResourceType())) { //删除文件夹
				this.deleteFolder(garbage.getResourceId());
			}
		});
	}
	
	/**
	 * 删除文件
	 * @param fileId
	 */
	public void deleteFile(Long fileId) {
		File file = fileMapper.selectById(fileId);
		//1.删除FastDFS文件
		fdfsHelper.deleteFile(fdfsHelper.getPath(file.getFileUrl()));
		
		//2.删除文件记录
		fileMapper.deleteById(fileId);
	}
	
	/**
	 * 删除文件夹
	 * @param folderId
	 */
	public void deleteFolder(Long folderId) {
		//1.分离出文件夹中的所有文件、文件夹集合
		List<Long> fileIds = new ArrayList<Long>();
		List<Long> folderIds = new ArrayList<Long>();
		mountFileAndFolder(folderId, fileIds, folderIds);
		
		//2.删除文件
		if(CollectionUtil.noNullAndSizeGtZero(fileIds)) {
			fileIds.forEach(fileId -> {
				this.deleteFile(fileId);
			});	
		}
		
		//3.删除目录
		if(CollectionUtil.noNullAndSizeGtZero(folderIds)) {
			LambdaQueryWrapper<Folder> qwFDel = new LambdaQueryWrapper<Folder>();
			qwFDel.in(Folder::getFolderId, folderIds);
			folderMapper.delete(qwFDel);
		}
	}
	
	private void mountFileAndFolder(Long folderId, List<Long> fileIds, List<Long> folderIds) {
		//1.将当前的目录id加到集合中去
		folderIds.add(folderId);
		
		//2.将当前目录下挂载的文件加到集合中去
		LambdaQueryWrapper<File> qwFile = new LambdaQueryWrapper<File>();
		qwFile.eq(File::getFolderId, folderId);
		List<File> files = fileMapper.selectList(qwFile);
		if(CollectionUtil.noNullAndSizeGtZero(files))
			files.forEach(file -> fileIds.add(file.getFileId()));
		
		//3.查询子目录，递归调用
		LambdaQueryWrapper<Folder> qwFolder = new LambdaQueryWrapper<Folder>();
		qwFolder.eq(Folder::getParentId, folderId);
		List<Folder> folders = folderMapper.selectList(qwFolder);
		if(CollectionUtil.noNullAndSizeGtZero(folders))
			folders.forEach(folder -> this.mountFileAndFolder(folder.getFolderId(), fileIds, folderIds));
	}
	
	
	/**
	 * 删除无效的垃圾信息
	 * @param garbageIds
	 */
	public void deleteGarbageInfo(List<Long> garbageIds) {
		LambdaQueryWrapper<Garbage> queryWrapper = new LambdaQueryWrapper<Garbage>();
		queryWrapper.in(Garbage::getGarbageId, garbageIds);
		garbageMapper.delete(queryWrapper);
	}
}
