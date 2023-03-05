package com.zcx.cloud.folder.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.file.mapper.FileMapper;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.entity.FolderUser;
import com.zcx.cloud.folder.mapper.FolderMapper;
import com.zcx.cloud.folder.mapper.FolderUserMapper;
import com.zcx.cloud.folder.service.IFolderService;
import com.zcx.cloud.garbage.entity.Garbage;
import com.zcx.cloud.garbage.entity.GarbageUser;
import com.zcx.cloud.garbage.mapper.GarbageMapper;
import com.zcx.cloud.garbage.mapper.GarbageUserMapper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;

/**
 * <p>
 * 目录表 服务实现类
 * </p>
 *

 */
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements IFolderService {
	@Autowired
	private FolderMapper folderMapper;
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	private FolderUserMapper folderUserMapper;
	@Autowired
	private GarbageMapper garbageMapper;
	@Autowired
	private GarbageUserMapper garbageUserMapper;
	
	@Override
	public Result getFoldersPage(Folder folder, PageInfo<Folder> pageInfo, SortInfo sortInfo) {
		//排序信息
		Page<Folder> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Folder> selectPage = folderMapper.selectFolderPage(page, folder);
		long count = selectPage.getTotal();
		List<Folder> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public Folder getFolderTree(Long folderId) {
		Folder folder = folderMapper.selectById(folderId);
		folder.setFiles(getMountFiles(folder.getFolderId()));
		folder.setFolders(getMountFolders(folder.getFolderId()));
		return folder;
	}
	
	/**
	 * 获取目录下挂载的所有文件
	 * @param folderId
	 * @return
	 */
	private List<File> getMountFiles(Long folderId){
		LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>();
		queryWrapper.eq(File::getFolderId, folderId);
		queryWrapper.eq(File::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByAsc(File::getFileName);
		List<File> files = fileMapper.selectList(queryWrapper);
		return files;
	}
	
	/**
	 * 获取目录下挂载的所有目录
	 * @param folderId
	 * @return
	 */
	private List<Folder> getMountFolders(Long folderId){
		LambdaQueryWrapper<Folder> queryWrapper = new LambdaQueryWrapper<Folder>();
		queryWrapper.eq(Folder::getParentId, folderId);
		queryWrapper.eq(Folder::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByAsc(Folder::getFolderName);
		List<Folder> folders = folderMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(folders)) {
			folders.forEach(folder -> {
				folder.setFiles(getMountFiles(folder.getFolderId()));
				folder.setFolders(getMountFolders(folder.getFolderId()));
			});
		}
		return folders;
	}

	@Override
	public List<Folder> getChildFolders(Long currentId) {
		//当前文件夹ID为负数，则查询当前用户根目录
		if(Objects.isNull(currentId)||currentId<=0) {
			LambdaQueryWrapper<FolderUser> queryWrapper = new LambdaQueryWrapper<FolderUser>();
			queryWrapper.eq(FolderUser::getUserId, AppUtil.getCurrentUser().getUserId());
			FolderUser folderUser = folderUserMapper.selectOne(queryWrapper);
			currentId = folderUser.getFolderId();
		}
		//查询子目录
		LambdaQueryWrapper<Folder> queryWrapper = new LambdaQueryWrapper<Folder>();
		queryWrapper.eq(Folder::getParentId, currentId);
		queryWrapper.eq(Folder::getValid, Constant.VALID_NORMAL);
		queryWrapper.orderByAsc(Folder::getFolderName);
		List<Folder> folders = folderMapper.selectList(queryWrapper);
		
		return folders;
	}

	@Override
	public void saveFolder(Long parentId, String folderName) {
		Folder folder = new Folder();
		folder.setFolderName(folderName);
		folder.setParentId(parentId);
		folder.setCreateBy(AppUtil.getCurrentUser().getUsername());
		folder.setCreateTime(DateUtil.now());
		
		folderMapper.insert(folder);
	}

	@Override
	public void rename(Long folderId, String newName) {
		Folder folder = new Folder();
		folder.setFolderId(folderId);
		folder.setFolderName(newName);
		folder.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		folder.setUpdateTime(DateUtil.now());
		
		folderMapper.updateById(folder);
	}

	@Override
	public void moveGarbage(Long folderId) {
		//1.修改文件夹可用标志
		Folder folder = new Folder();
		folder.setFolderId(folderId);
		folder.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		folder.setUpdateTime(DateUtil.now());
		folder.setValid(Constant.VALID_DELETE);
		folderMapper.updateById(folder);
		
		//2.添加文件夹对应垃圾信息
		Garbage garbage = new Garbage();
		garbage.setResourceId(folderId);
		garbage.setResourceType(Constant.GARBAGE_FOLDER);
		garbage.setActive(Constant.GARBAGE_DEFAULT_ACTIVE);
		garbage.setCreateBy(AppUtil.getCurrentUser().getUsername());
		garbage.setCreateTime(DateUtil.now());
		garbageMapper.insert(garbage);
		
		
		//3.添加垃圾与用户的关联信息
		GarbageUser garbageUser = new GarbageUser();
		garbageUser.setUserId(AppUtil.getCurrentUser().getUserId());
		garbageUser.setGarbageId(garbage.getGarbageId());
		garbageUserMapper.insert(garbageUser);
	}

	@Override
	public void moveGarbageBat(List<Long> folderIds) {
		//1.修改文件夹可用标志
		Folder folder = new Folder();
		folder.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		folder.setUpdateTime(DateUtil.now());
		folder.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<Folder> uwFolder = new LambdaUpdateWrapper<Folder>();
		uwFolder.in(Folder::getFolderId, folderIds);
		
		folderMapper.update(folder, uwFolder);
		
		//2.添加文件夹对应垃圾信息
		List<Garbage> garbages = new ArrayList<Garbage>();
		folderIds.forEach(folderId -> {
			Garbage garbage = new Garbage();
			garbage.setResourceId(folderId);
			garbage.setResourceType(Constant.GARBAGE_FOLDER);
			garbage.setActive(Constant.GARBAGE_DEFAULT_ACTIVE);
			garbage.setCreateBy(AppUtil.getCurrentUser().getUsername());
			garbage.setCreateTime(DateUtil.now());
			garbageMapper.insert(garbage);
			
			garbages.add(garbage);
		});
		
		
		//3.添加垃圾与用户的关联信息
		garbages.forEach(garbage -> {
			GarbageUser garbageUser = new GarbageUser();
			garbageUser.setUserId(AppUtil.getCurrentUser().getUserId());
			garbageUser.setGarbageId(garbage.getGarbageId());
			garbageUserMapper.insert(garbageUser);
		});
	}

	@Override
	public List<Folder> getCurrentFolderTree() {
		List<Folder> folderTree = new ArrayList<Folder>();
		//1.查询当前用户根目录ID
		LambdaQueryWrapper<FolderUser> queryWrapper = new LambdaQueryWrapper<FolderUser>();
		queryWrapper.eq(FolderUser::getUserId, AppUtil.getCurrentUser().getUserId());
		FolderUser folderUser = folderUserMapper.selectOne(queryWrapper);
		Long folderId = folderUser.getFolderId();
		
		//2.查询根目录以及子目录
		Folder rootFolder = folderMapper.selectById(folderId);
		rootFolder.setFolders(getMountFolders(rootFolder.getFolderId()));
		folderTree.add(rootFolder);
		
		return folderTree;
	}

	@Override
	public void move(Long folderId, Long targetId) {
		Folder folder = new Folder();
		folder.setFolderId(folderId);
		folder.setParentId(targetId);
		folder.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		folder.setUpdateTime(DateUtil.now());
		
		folderMapper.updateById(folder);
	}

}
