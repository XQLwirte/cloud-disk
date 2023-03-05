package com.zcx.cloud.file.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.TimeData;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.file.service.IFileService;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.folder.service.IFolderService;
import com.zcx.cloud.folder.service.IFolderUserService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.OutOfStorageException;
import com.zcx.cloud.util.DateUtil;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 文件表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {
	@Autowired
	private IFileService fileService;
	@Autowired
	private IFolderService folderService;
	@Autowired
	private IFolderUserService folderUserService;

	@RequiresPermissions("file:view")
	@GetMapping("getCurrentFile/{currentId}")
	public Result getCurrentFile(@PathVariable("currentId")Long currentId) {
		try {
			//转换成当前文件夹ID
			Folder currentfolder = folderUserService.getCurrentFolder(currentId);
			List<File> files = fileService.getChildFiles(currentfolder.getFolderId());
			List<Folder> folders = folderService.getChildFolders(currentfolder.getFolderId());
			return new Result().success().put("files", files).put("folders", folders).put("currentfolder", currentfolder);
		}catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("获取文件信息失败");
		}
	}

	@SysLog("上传文件")
	@RequiresPermissions("file:add")
	@PostMapping("upload")
	public Result upload(MultipartFile file, @RequestParam("folderId")Long folderId) {
		try {
			fileService.upload(file, folderId);
			return new Result().success();
		} catch(OutOfStorageException e) {
			return new Result().error().msg("存储空间不足");
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("上传文件失败");
		}
	}

	@SysLog("文件重命名")
	@RequiresPermissions("file:update")
	@GetMapping("rename/{fileId}/{newName}")
	public Result rename(@PathVariable("fileId")Long fileId, @PathVariable("newName")String newName) {
		try {
			fileService.rename(fileId, newName);
			return new Result().success();
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件重命名失败");
		}
	}

	@SysLog("文件移入回收站")
	@RequiresPermissions("file:delete")
	@GetMapping("moveGarbage/{fileId}")
	public Result moveGarbage(@PathVariable("fileId")Long fileId) {
		try {
			fileService.moveGarbage(fileId);
			return new Result().success();
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件移入回收站失败");
		}
	}

	@SysLog("文件批量移入回收站")
	@RequiresPermissions("file:delete")
	@GetMapping("moveGarbageBat/{fileIdsStr}")
	public Result moveGarbageBat(@PathVariable("fileIdsStr")String fileIdsStr) {
		try {
			List<Long> fileIds = StringUtil.stringToLongs(fileIdsStr, Constant.COMMA);
			fileService.moveGarbageBat(fileIds);
			return new Result().success();
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件批量移入回收站失败");
		}
	}

	@SysLog("文件移动")
	@RequiresPermissions("file:update")
	@GetMapping("move/{fileId}/{targetId}")
	public Result move(@PathVariable("fileId")Long fileId, @PathVariable("targetId")Long targetId) {
		try {
			fileService.move(fileId, targetId);
			return new Result().success();
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件移动失败");
		}
	}

	@RequiresPermissions("file:view")
	@GetMapping("getTimeDataByType/{fileType}")
	public Result getTimeDataByType(@PathVariable("fileType")String fileType) {
		try {
			List<TimeData<File>> timeData = fileService.getTimeDataByType(fileType);
			return new Result().success().data(timeData);
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("文件移动失败");
		}
	}

	@RequiresPermissions("file:view")
	@GetMapping("searchByName/{fileName}")
	public Result searchByName(@PathVariable("fileName")String fileName) {
		try {
			long startTime = DateUtil.now().getTime();
			List<File> files = fileService.searchByName(fileName);
			long endTime = DateUtil.now().getTime();
			return new Result().success().data(files).put("useTime", endTime - startTime);
		} catch(Exception e) {
			e.printStackTrace();
			return new Result().error().msg("查询文件失败");
		}
	}
}
