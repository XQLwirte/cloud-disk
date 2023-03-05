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
import com.zcx.cloud.dict.entity.DictValue;
import com.zcx.cloud.dict.service.IDictValueService;
import com.zcx.cloud.system.annotaion.SysLog;
import com.zcx.cloud.system.exception.BusException;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.util.StringUtil;

/**
 * <p>
 * 数据字典值表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/dictValue")
public class DictValueController extends BaseController {
	@Autowired
	private IDictValueService dictValueService;


	/**
	 * 获取数据表数据
	 */
	@RequiresPermissions("dict:view")
	@GetMapping("getDictValuesPage")
	public Result getDictValuesPage(DictValue dictValue,PageInfo<DictValue> pageInfo) {
		try {
			Result result = dictValueService.getDictValuesPage(dictValue,pageInfo);
			return result;
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("获取字典值表格失败");
		}
	}

	/**
	 * 添加字典值
	 */
	@SysLog("添加字典值")
	@RequiresPermissions("dict:add")
	@PostMapping("dictValueAdd")
	public Result dictValueAdd(DictValue dictValue) {
		try {
			dictValueService.saveDictValue(dictValue);
			return new Result().success();
		}catch(ExistsException e) {
			return new Result().error().msg("值码已存在");
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("添加字典值失败");
		}
	}


	/**
	 * 删除字典值
	 */
	@SysLog("删除字典值")
	@RequiresPermissions("dict:delete")
	@GetMapping("dictValueDelete/{valueId}")
	public Result dictValueDelete(@PathVariable("valueId")Long valueId) {
		try {
			dictValueService.logicDelete(valueId);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("删除字典值失败");
		}
	}

	/**
	 * 批量删除字典值
	 */
	@SysLog("批量删除字典值")
	@RequiresPermissions("dict:delete")
	@GetMapping("dictValueDeleteBatch/{valueIdsStr}")
	public Result dictValueDeleteBatch(@PathVariable("valueIdsStr")String valueIdsStr) {
		try {
			List<Long> valueIds = StringUtil.stringToLongs(valueIdsStr, Constant.COMMA);
			dictValueService.logicDeleteBatch(valueIds);
			return new Result().success();
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("批量删除字典值");
		}
	}

	/**
	 * 修改字典值
	 */
	@SysLog("修改字典值")
	@RequiresPermissions("dict:update")
	@PostMapping("dictValueUpdate")
	public Result dictValueUpdate(DictValue dictValue) {
		try {
			dictValueService.updateDictValue(dictValue);
			return new Result().success();
		}catch(ExistsException e) {
			return new Result().error().msg("值码已存在");
		}catch(Exception e) {
			e.printStackTrace();
			throw new BusException("修改字典值失败");
		}
	}
}
