package com.zcx.cloud.storage.controller;


import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.storage.service.IStorageService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;

/**
 * <p>
 * 存储信息表 前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/storage")
public class StorageController extends BaseController {
	@Autowired
	private IStorageService storageService;
		
	@RequiresPermissions("storage:view")
	@GetMapping("getStoragesPage")
	public Result getStoragesPage(Storage storage,PageInfo<Storage> pageInfo,SortInfo sortInfo) {
		try {
			Result result = storageService.getStoragesPage(storage, pageInfo, sortInfo);
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取存储分页信息失败");
		}
	}
	
	@SysLog("修改存储信息")
	@RequiresPermissions("storage:update")
	@PostMapping("storageUpdate")
	public Result storageUpdate(Storage storage) {
		try {
			storageService.updateStorage(storage);
			return new Result().success();
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("修改存储信息失败");
		}
	}
	
	
	@RequiresPermissions("storage:view")
	@GetMapping("getCurrentStorage")
	public Result getCurrentStorage() {
		try {
			Storage currentStorage = storageService.getCurrentStorage();
			return new Result().success().data(currentStorage);
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusException("获取当前用户存储信息失败");
		}
	}
	
}
