package com.zcx.cloud.dict.controller;


import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.controller.BaseController;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.service.IDictKeyService;
import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 数据字典键表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/dictKey")
public class DictKeyController extends BaseController {
	@Autowired
	private IDictKeyService dictKeyService;

	/**
	 * 获取数据表数据
	 */
	@RequiresPermissions("dict:view")
	@GetMapping("getDictKeysPage")
	public Result getDictKeysPage(DictKey dictKey,PageInfo<DictKey> pageInfo) {
		try {
			Result result = dictKeyService.getDictKeysPage(dictKey,pageInfo);
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("获取字典键表格失败");
		}
	}

	@SysLog("字典键添加")
	@RequiresPermissions("dict:add")
	@PostMapping("dictKeyAdd")
	public Result dictKeyAdd(DictKey dictKey) {
		try {
			dictKeyService.saveDictKey(dictKey);
			return new Result().success();
		}catch(ExistsException e) {
			return new Result().error().msg("字典键码已存在");
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("字典键添加失败");
		}
	}

	@SysLog("删除字典键")
	@RequiresPermissions("dict:delete")
	@GetMapping("dictKeyDelete/{keyId}")
	public Result dictKeyDelete(@PathVariable("keyId")Long keyId) {
		try {
			dictKeyService.logicDelete(keyId);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("字典键删除失败");
		}
	}

	@SysLog("批量删除字典键")
	@RequiresPermissions("dict:delete")
	@GetMapping("dictKeyDeleteBatch/{dictKeyIdsStr}")
	public Result dictKeyDeleteBatch(@PathVariable("dictKeyIdsStr")String dictKeyIdsStr) {
		try {
			List<Long> dictKeyIds = StringUtil.stringToLongs(dictKeyIdsStr, Constant.COMMA);
			dictKeyService.logicDeleteBatch(dictKeyIds);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("批量字典键删除失败");
		}
	}

	@SysLog("修改字典键")
	@RequiresPermissions("dict:update")
	@PostMapping("dictKeyUpdate")
	public Result dictKeyUpdate(DictKey dictKey) {
		try {
			dictKeyService.updateDictKey(dictKey);
			return new Result().success();
		}catch(ExistsException e) {
			return new Result().error().msg("字典键已存在");
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("修改字典键失败");
		}
	}
}
