package com.zcx.cloud.share.service;

import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.share.entity.Share;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 共享信息表 服务类
 * </p>
 *

 */
public interface IShareService extends IService<Share> {

	/**
	 * 分页条件查询共享信息
	 * @param share
	 * @param pageInfo
	 * @param sortInfo
	 * @return
	 */
	Result getSharesPage(Share share, PageInfo<Share> pageInfo, SortInfo sortInfo);

	/**
	 * 保存共享信息
	 * @param share
	 * @return
	 */
	Share saveShare(Share share);

	/**
	 * 获取共享以及级联信息
	 * @param shareId
	 * @return
	 */
	Share getShareById(Long shareId);


	/**
	 *  获取当前用户的共享信息
	 * @param pageInfo
	 * @return
	 */
	List<Share> getCurrentShares(PageInfo<Share> pageInfo);

	/**
	 * 取消共享信息
	 * @param shareId
	 */
	void cancelShare(Long shareId);

	/**
	 * 验证共享码
	 * @param share
	 * @return
	 */
	boolean checkShareCode(Share share);

	/**
	 * 共享信息获取到共享文件
	 * @param shareId
	 * @return
	 */
	File getShareFile(Long shareId);

	/**
	 * 下载共享文件，增加下载量
	 * @param shareId
	 */
	void download(Long shareId);

}
