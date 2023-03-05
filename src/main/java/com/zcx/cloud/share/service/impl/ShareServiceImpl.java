package com.zcx.cloud.share.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.file.mapper.FileMapper;
import com.zcx.cloud.share.entity.Share;
import com.zcx.cloud.share.entity.ShareUser;
import com.zcx.cloud.share.mapper.ShareMapper;
import com.zcx.cloud.share.mapper.ShareUserMapper;
import com.zcx.cloud.share.service.IShareService;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.mapper.UserMapper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;

/**
 * <p>
 * 共享信息表 服务实现类
 * </p>
 *

 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements IShareService {
	@Autowired
	private ShareMapper shareMapper;
	@Autowired
	private ShareUserMapper shareUserMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private FileMapper fileMapper;
	
	@Override
	public Result getSharesPage(Share share, PageInfo<Share> pageInfo, SortInfo sortInfo) {
		//排序信息
		Page<Share> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Share> selectPage = shareMapper.selectSharePage(page, share);
		long count = selectPage.getTotal();
		List<Share> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public Share saveShare(Share share) {
		//设置基本信息
		if(Objects.isNull(share.getShareCode())||share.getShareCode().trim()=="") {
			share.setShareCode(UUID.randomUUID().toString().substring(0, 4));
		}
		share.setCreateBy(AppUtil.getCurrentUser().getUsername());
		share.setCreateTime(DateUtil.now());
		
		//添加共享信息
		shareMapper.insert(share);
		
		//添加共享与用户关联信息
		ShareUser shareUser = new ShareUser();
		shareUser.setShareId(share.getShareId());
		shareUser.setUserId(AppUtil.getCurrentUser().getUserId());
		shareUserMapper.insert(shareUser);
		
		return share;
	}

	@Override
	public Share getShareById(Long shareId) {
		//1.查询共享基本信息
		Share share = shareMapper.selectById(shareId);
		
		//2.查询用户信息
		LambdaQueryWrapper<ShareUser> qwSU = new LambdaQueryWrapper<ShareUser>();
		qwSU.eq(ShareUser::getShareId, share.getShareId());
		ShareUser shareUser = shareUserMapper.selectOne(qwSU);
		LambdaQueryWrapper<User> qwU = new LambdaQueryWrapper<User>();
		qwU.eq(User::getUserId, shareUser.getUserId());
		qwU.eq(User::getValid, Constant.VALID_NORMAL);
		User user = userMapper.selectOne(qwU);
		share.setUsername(Objects.isNull(user)?null:user.getUsername());
		
		//3.查询文件信息
		LambdaQueryWrapper<File> qwF = new LambdaQueryWrapper<File>();
		qwF.eq(File::getFileId, share.getFileId());
		File file = fileMapper.selectOne(qwF);
		share.setFileName(Objects.isNull(file)?null:file.getFileName());
	
		return share;
	}

	@Override
	public List<Share> getCurrentShares(PageInfo<Share> pageInfo) {
		List<Share> result = new ArrayList<Share>();
		//1.查询当前用户的共享关联信息
		User currentUser = AppUtil.getCurrentUser();
		LambdaQueryWrapper<ShareUser> qwSU = new LambdaQueryWrapper<ShareUser>();
		qwSU.eq(ShareUser::getUserId, currentUser.getUserId());
		List<ShareUser> shareUsers = shareUserMapper.selectList(qwSU);
		List<Long> shareIds = new ArrayList<Long>();
		shareUsers.forEach(shareUser -> {
			shareIds.add(shareUser.getShareId());
		});
		
		//2.查询共享信息
		if(CollectionUtil.noNullAndSizeGtZero(shareIds)) {
			//查询总记录数
			LambdaQueryWrapper<Share> qwShare = new LambdaQueryWrapper<Share>();
			qwShare.in(Share::getShareId, shareIds);
			qwShare.orderByDesc(Share::getCreateTime);
			qwShare.eq(Share::getValid, Constant.VALID_NORMAL);
			int total = shareMapper.selectCount(qwShare);
			pageInfo.setTotal(total);
			
			//查询分页信息
			result = shareMapper.selectSharePage(pageInfo.getPage(), new Share()).getRecords();
		}
		
		//3.查询关联文件信息
		result.forEach(share -> {
			LambdaQueryWrapper<File> qwF = new LambdaQueryWrapper<File>();
			qwF.eq(File::getFileId, share.getFileId());
			File file = fileMapper.selectOne(qwF);
			share.setFile(file);
		});
		
		return result;
	}

	@Override
	public void cancelShare(Long shareId) {
		Share share = new Share();
		share.setShareId(shareId);
		share.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		share.setUpdateTime(DateUtil.now());
		share.setValid(Constant.VALID_DELETE);
		
		shareMapper.updateById(share);
	}

	@Override
	public boolean checkShareCode(Share share) {
		LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<Share>();
		queryWrapper.eq(Share::getShareId, share.getShareId());
		queryWrapper.eq(Share::getValid, Constant.VALID_NORMAL);
		Share shareDB = shareMapper.selectOne(queryWrapper);
		if(Objects.isNull(shareDB))
			return false;
		
		if(share.getShareCode().equals(shareDB.getShareCode()))
			return true;
		
		return false;
	}

	@Override
	public File getShareFile(Long shareId) {
		//1.查询共享信息
		LambdaQueryWrapper<Share> qwS = new LambdaQueryWrapper<Share>();
		qwS.eq(Share::getShareId, shareId);
		qwS.eq(Share::getValid, Constant.VALID_NORMAL);
		Share share = shareMapper.selectOne(qwS);
		if(Objects.isNull(share))
			return null;
		
		//2.查询文件信息
		LambdaQueryWrapper<File> qwF = new LambdaQueryWrapper<File>();
		qwF.eq(File::getFileId, share.getFileId());
		qwF.eq(File::getValid, Constant.VALID_NORMAL);
		File file = fileMapper.selectOne(qwF);
		
		return file;
	}

	@Override
	public void download(Long shareId) {
		LambdaQueryWrapper<Share> queryWrapper = new LambdaQueryWrapper<Share>();
		queryWrapper.eq(Share::getShareId, shareId);
		queryWrapper.eq(Share::getValid, Constant.VALID_NORMAL);
		Share share = shareMapper.selectOne(queryWrapper);
		
		share.setDownload(share.getDownload() + 1);
		
		shareMapper.updateById(share);
	}

}
