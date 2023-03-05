package com.zcx.cloud.storage.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.common.vo.SortInfo;
import com.zcx.cloud.folder.entity.Folder;
import com.zcx.cloud.storage.entity.Storage;
import com.zcx.cloud.storage.entity.StorageUser;
import com.zcx.cloud.storage.mapper.StorageMapper;
import com.zcx.cloud.storage.mapper.StorageUserMapper;
import com.zcx.cloud.storage.service.IStorageService;
import com.zcx.cloud.user.entity.User;
import com.zcx.cloud.user.mapper.UserMapper;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储信息表 服务实现类
 * </p>
 *

 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements IStorageService {
	@Autowired
	private StorageMapper storageMapper;
	@Autowired
	private StorageUserMapper storageUserMapper;
	@Autowired
	private UserMapper userMapper;
		
	@Override
	public Result getStoragesPage(Storage storage, PageInfo<Storage> pageInfo, SortInfo sortInfo) {
		//排序信息
		Page<Storage> page = pageInfo.getPage();
		sortInfo.toPageSort(page);
		
		//查询数据
		Page<Storage> selectPage = storageMapper.selectStoragePage(page, storage);
		long count = selectPage.getTotal();
		List<Storage> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public Storage getStorageWithUsername(Long storageId) {
		//1.查询存储信息
		LambdaQueryWrapper<Storage> queryWrapper = new LambdaQueryWrapper<Storage>();
		queryWrapper.eq(Storage::getStorageId, storageId);
		queryWrapper.eq(Storage::getValid, Constant.VALID_NORMAL);
		Storage storage = storageMapper.selectOne(queryWrapper);
		
		//2.查询关联的用户信息
		LambdaQueryWrapper<StorageUser> qwSU = new LambdaQueryWrapper<StorageUser>();
		qwSU.eq(StorageUser::getStorageId, storageId);
		StorageUser storageUser = storageUserMapper.selectOne(qwSU);
		if(Objects.nonNull(storageUser)) {
			LambdaQueryWrapper<User> qwU = new LambdaQueryWrapper<User>();
			qwU.eq(User::getUserId, storageUser.getUserId());
			qwU.eq(User::getValid, Constant.VALID_NORMAL);
			User user = userMapper.selectOne(qwU);
			storage.setUsername(Objects.nonNull(user)?user.getUsername():null);
		}
		
		return storage;
	}

	@Override
	public void updateStorage(Storage storage) {
		storage.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		storage.setUpdateTime(DateUtil.now());
		storageMapper.updateById(storage);
	}

	@Override
	public Storage getCurrentStorage() {
		//获取当前用户信息
		User currentUser = AppUtil.getCurrentUser();
		//获取关联存储信息
		LambdaQueryWrapper<StorageUser> queryWrapper = new LambdaQueryWrapper<StorageUser>();
		queryWrapper.eq(StorageUser::getUserId, currentUser.getUserId());
		StorageUser storageUser = storageUserMapper.selectOne(queryWrapper);
		if(Objects.isNull(storageUser))
			return null;
		Storage storage = storageMapper.selectById(storageUser.getStorageId());
		
		return storage;
	}

}
