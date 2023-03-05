package com.zcx.cloud.dict.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.dict.entity.DictValue;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 数据字典值表 服务类
 * </p>
 *

 */
public interface IDictValueService extends IService<DictValue> {

	/**
	 * 数据表格获取字典键分页数据
	 * @param dictValue
	 * @param pageInfo
	 * @return
	 */
	Result getDictValuesPage(DictValue dictValue, PageInfo<DictValue> pageInfo);

	/**
	 * 添加字典值
	 * @param dictValue
	 * @return
	 */
	void saveDictValue(DictValue dictValue);

	/**
	 * 逻辑删除字典值
	 * @param valueId
	 */
	void logicDelete(Long valueId);

	/**
	 * 批量逻辑删除字典键
	 * @param valueIds
	 */
	void logicDeleteBatch(List<Long> valueIds);

	/**
	 * 修改字典值
	 * @param dictValue
	 */
	void updateDictValue(DictValue dictValue);

}
