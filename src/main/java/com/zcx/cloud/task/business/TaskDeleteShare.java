package com.zcx.cloud.task.business;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.share.entity.Share;
import com.zcx.cloud.share.entity.ShareUser;
import com.zcx.cloud.share.mapper.ShareMapper;
import com.zcx.cloud.share.mapper.ShareUserMapper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除共享信息定时任务
 * 
 * 1.删除共享基本信息
 * 2.删除共享与用户关联信息
 * 涉及表：t_share、t_share_user
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteShare")
public class TaskDeleteShare {
	@Autowired
	private ShareMapper shareMapper;
	@Autowired
	private ShareUserMapper shareUserMapper;
	
	public void deleteShare() {
		//1.查询无效的共享信息
		LambdaQueryWrapper<Share> qwS = new LambdaQueryWrapper<Share>();
		qwS.eq(Share::getValid, Constant.VALID_DELETE);
		List<Share> shares = shareMapper.selectList(qwS);
		if(CollectionUtil.isNullOrSizeLeZero(shares))
			return ;
		List<Long> shareIds = shares.stream().map(share -> share.getShareId()).collect(Collectors.toList());
		
		//2.删除无效的共享与用户关联信息
		LambdaQueryWrapper<ShareUser> qwSU = new LambdaQueryWrapper<ShareUser>();
		qwSU.in(ShareUser::getShareId, shareIds);
		shareUserMapper.delete(qwSU);
		
		//3.删除无效的共享信息
		LambdaQueryWrapper<Share> qwSDel = new LambdaQueryWrapper<Share>();
		qwSDel.eq(Share::getValid, Constant.VALID_DELETE);
		shareMapper.delete(qwSDel);
	}
	
}
