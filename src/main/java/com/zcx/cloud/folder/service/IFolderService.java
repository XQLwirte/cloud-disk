package com.zcx.cloud.folder.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.folder.entity.Folder;

/**
 * <p>
 * 目录表 服务类
 * </p>
 *

 */
public interface IFolderService extends IService<Folder> {

	/*
	 * 条件查询分页数据,顶级目录
	 */
	Result getFoldersPage(Folder folder, PageInfo<Folder> pageInfo, SortInfo sortInfo);

	/**
	 * 获取当前目录下挂载的所有子目录以及文件
	 * @param folderId
	 * @return
	 */
	Folder getFolderTree(Long folderId);

	/**
	 * 获取子目录
	 * @param currentId
	 * @return
	 */
	List<Folder> getChildFolders(Long currentId);

	/**
	 * 添加文件夹
	 * @param parentId
	 * @param folderName
	 */
	void saveFolder(Long parentId, String folderName);

	/**
	 * 文件夹重命名
	 * @param folderId
	 * @param newName
	 */
	void rename(Long folderId, String newName);

	/**
	 * 文件夹移入回收站
	 * @param folderId
	 */
	void moveGarbage(Long folderId);

	/**
	 * 文件夹批量移入回收站
	 * @param folderIds
	 */
	void moveGarbageBat(List<Long> folderIds);

	/**
	 * 获取当前用户的目录树
	 * @return
	 */
	List<Folder> getCurrentFolderTree();

	/**
	 * 文件夹移动
	 * @param folderId 要移动的文件夹
	 * @param targetId 目标文件夹
	 */
	void move(Long folderId, Long targetId);

}
