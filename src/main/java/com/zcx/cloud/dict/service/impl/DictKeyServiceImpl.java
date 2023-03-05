package com.zcx.cloud.dict.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.mapper.DictKeyMapper;
import com.zcx.cloud.dict.service.IDictKeyService;
import com.zcx.cloud.menu.entity.Menu;
import com.zcx.cloud.system.exception.ExistsException;
import com.zcx.cloud.util.AppUtil;
import com.zcx.cloud.util.CollectionUtil;
import com.zcx.cloud.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Objects;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据字典键表 服务实现类
 * </p>
 *

 */
@Service
public class DictKeyServiceImpl extends ServiceImpl<DictKeyMapper, DictKey> implements IDictKeyService {
	@Autowired
	private DictKeyMapper dictKeyMapper;
	
	@Override
	public Result getDictKeysPage(DictKey dictKey, PageInfo<DictKey> pageInfo) {
		LambdaQueryWrapper<DictKey> queryWrapper = new LambdaQueryWrapper<DictKey>();
		//设置查询条件
		if(Objects.nonNull(dictKey)) {
			if(!Strings.isNullOrEmpty(dictKey.getKeyCode()))
				queryWrapper.like(DictKey::getKeyCode, dictKey.getKeyCode());
			if(!Strings.isNullOrEmpty(dictKey.getKeyLabel()))
				queryWrapper.like(DictKey::getKeyLabel, dictKey.getKeyLabel());
			if(!Strings.isNullOrEmpty(dictKey.getCreateBy()))
				queryWrapper.like(DictKey::getCreateBy, dictKey.getCreateBy());
			if(Objects.nonNull(dictKey.getCreateTime()))
				queryWrapper.gt(DictKey::getCreateTime, dictKey.getCreateTime());
		}
		queryWrapper.eq(DictKey::getValid, Constant.VALID_NORMAL);
		
		Page selectPage = dictKeyMapper.selectPage(pageInfo.getPage(), queryWrapper);
		long count = selectPage.getTotal();
		List<DictKey> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public void saveDictKey(DictKey dictKey) {
		//1.检查是否存在相同的键码
		String keyCode = dictKey.getKeyCode();
		LambdaQueryWrapper<DictKey> queryWrapper = new LambdaQueryWrapper<DictKey>();
		queryWrapper.eq(DictKey::getKeyCode, keyCode);
		queryWrapper.eq(DictKey::getValid, Constant.VALID_NORMAL);
		List<DictKey> selectList = dictKeyMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("字典键码已存在");
		
		//2.持久化字典键信息
		dictKey.setCreateBy(AppUtil.getCurrentUser().getUsername());
		dictKey.setCreateTime(DateUtil.now());
		dictKeyMapper.insert(dictKey);
	}

	@Override
	public void logicDelete(Long keyId) {
		DictKey dictKey = new DictKey();
		dictKey.setKeyId(keyId);
		dictKey.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		dictKey.setUpdateTime(DateUtil.now());
		dictKey.setValid(Constant.VALID_DELETE);
		
		dictKeyMapper.updateById(dictKey);
	}

	@Override
	public void logicDeleteBatch(List<Long> dictKeyIds) {
		DictKey dictKey = new DictKey();
		dictKey.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		dictKey.setUpdateTime(DateUtil.now());
		dictKey.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<DictKey> updateWrapper = new LambdaUpdateWrapper<DictKey>();
		updateWrapper.in(DictKey::getKeyId, dictKeyIds);
		updateWrapper.eq(DictKey::getValid, Constant.VALID_NORMAL);
		
		dictKeyMapper.update(dictKey, updateWrapper);
	}

	@Override
	public void updateDictKey(DictKey dictKey) {
		//1.检查是否存在相同的键码
		LambdaQueryWrapper<DictKey> queryWrapper = new LambdaQueryWrapper<DictKey>();
		queryWrapper.notIn(DictKey::getKeyId, dictKey.getKeyId());
		queryWrapper.eq(DictKey::getKeyCode, dictKey.getKeyCode());
		queryWrapper.eq(DictKey::getValid, Constant.VALID_NORMAL);
		List<DictKey> selectList = dictKeyMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("字典键码已存在");
		
		//2.更改字典键信息
		dictKey.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		dictKey.setUpdateTime(DateUtil.now());
		dictKeyMapper.updateById(dictKey);
	}

}
