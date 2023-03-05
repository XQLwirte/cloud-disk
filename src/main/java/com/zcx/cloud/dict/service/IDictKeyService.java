package com.zcx.cloud.dict.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.dict.entity.DictKey;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 数据字典键表 服务类
 * </p>
 *

 */
public interface IDictKeyService extends IService<DictKey> {

	/**
	 * 获取字典键分页表格数据
	 * @param dictKey
	 * @param pageInfo
	 * @return
	 */
	Result getDictKeysPage(DictKey dictKey, PageInfo<DictKey> pageInfo);

	/**
	 * 持久化字典键信息
	 * @param dictKey
	 */
	void saveDictKey(DictKey dictKey);

	/**
	 * 逻辑删除字典键
	 * @param keyId
	 */
	void logicDelete(Long keyId);

	/**
	 * 批量逻辑删除字典键
	 * @param dictKeyIds
	 */
	void logicDeleteBatch(List<Long> dictKeyIds);

	/**
	 * 修改字典键信息
	 * @param dictKey
	 */
	void updateDictKey(DictKey dictKey);

}
