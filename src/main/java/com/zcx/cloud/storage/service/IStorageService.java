package com.zcx.cloud.storage.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.storage.entity.Storage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 存储信息表 服务类
 * </p>
 *

 */
public interface IStorageService extends IService<Storage> {

	/**
	 * 分页条件查询存储信息
	 * @param storage
	 * @param pageInfo
	 * @param sortInfo
	 * @return
	 */
	Result getStoragesPage(Storage storage, PageInfo<Storage> pageInfo, SortInfo sortInfo);

	/**
	 * 查询存储信息与所属用户名称
	 * @param storageId
	 * @return
	 */
	Storage getStorageWithUsername(Long storageId);

	/**
	 * 更新存储信息
	 * @param storage
	 */
	void updateStorage(Storage storage);

	/**
	 * 获取当前用户存储信息
	 * @return
	 */
	Storage getCurrentStorage();
}
