package com.zcx.cloud.folder.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.service.IFolderService;
import com.zcx.cloud.permission.entity.Permission;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 目录表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/folder")
public class FolderController extends BaseController {
	@Autowired
	private IFolderService folderService;
	
	@RequiresPermissions("folder:view")
	@GetMapping("getFoldersPage")
	public Result getFoldersPage(Folder folder,PageInfo<Folder> pageInfo,SortInfo sortInfo) {
		try {
			Result result = folderService.getFoldersPage(folder, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取目录分页信息失败");
		}
	}
	
	@RequiresPermissions("folder:view")
	@GetMapping("getFolderTree/{folderId}")
	public Result getFolderTree(@PathVariable("folderId")Long folderId) {
		try {
			Folder folder = folderService.getFolderTree(folderId);
			return new Result().success().data(folder);
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("获取目录树失败");
		}
	}
	
	@RequiresPermissions("folder:view")
	@GetMapping("getCurrentFolderTree")
	public Result getCurrentFolderTree() {
		try {
			List<Folder> folderTree = folderService.getCurrentFolderTree();
			return new Result().success().data(folderTree);
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("获取当前用户目录树失败");
		}
	}
	
	@SysLog("添加文件夹")
	@RequiresPermissions("folder:add")
	@GetMapping("create/{parentId}/{folderName}")
	public Result create(@PathVariable("parentId")Long parentId, @PathVariable("folderName")String folderName) {
		try {
			folderService.saveFolder(parentId,folderName);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("添加文件夹失败");
		}
	}
	
	@SysLog("文件夹重命名")
	@RequiresPermissions("folder:update")
	@GetMapping("rename/{folderId}/{newName}")
	public Result rename(@PathVariable("folderId")Long folderId, @PathVariable("newName")String newName) {
		try {
			folderService.rename(folderId,newName);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件夹重命名失败");
		}
	}
	
	@SysLog("文件夹移入回收站")
	@RequiresPermissions("folder:delete")
	@GetMapping("moveGarbage/{folderId}")
	public Result moveGarbage(@PathVariable("folderId")Long folderId) {
		try {
			folderService.moveGarbage(folderId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件夹移入回收站失败");
		}
	}
	
	@SysLog("文件夹批量移入回收站")
	@RequiresPermissions("folder:delete")
	@GetMapping("moveGarbageBat/{folderIdsStr}")
	public Result moveGarbageBat(@PathVariable("folderIdsStr")String folderIdsStr) {
		try {
			List<Long> folderIds = StringUtil.stringToLongs(folderIdsStr, Constant.COMMA);
			folderService.moveGarbageBat(folderIds);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件夹批量移入回收站失败");
		}
	}
	
	@SysLog("文件夹移动")
	@RequiresPermissions("folder:update")
	@GetMapping("move/{folderId}/{targetId}")
	public Result move(@PathVariable("folderId")Long folderId, @PathVariable("targetId")Long targetId) {
		try {
			folderService.move(folderId, targetId);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件夹移动失败");
		}
	}
}
