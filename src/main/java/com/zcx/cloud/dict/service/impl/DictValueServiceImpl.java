package com.zcx.cloud.dict.service.impl;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.vo.PageInfo;
import com.zcx.cloud.common.vo.Result;
import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.entity.DictValue;
import com.zcx.cloud.dict.mapper.DictValueMapper;
import com.zcx.cloud.dict.service.IDictValueService;
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
 * 数据字典值表 服务实现类
 * </p>
 *

 */
@Service
public class DictValueServiceImpl extends ServiceImpl<DictValueMapper, DictValue> implements IDictValueService {
	@Autowired
	private DictValueMapper dictValueMapper;
	
	@Override
	public Result getDictValuesPage(DictValue dictValue, PageInfo<DictValue> pageInfo) {
		LambdaQueryWrapper<DictValue> queryWrapper = new LambdaQueryWrapper<DictValue>();
		//设置查询条件
		if(Objects.nonNull(dictValue)) {
			if(!Strings.isNullOrEmpty(dictValue.getKeyCode()))
				queryWrapper.eq(DictValue::getKeyCode, dictValue.getKeyCode());
			if(!Strings.isNullOrEmpty(dictValue.getValueCode()))
				queryWrapper.like(DictValue::getValueCode, dictValue.getValueCode());
			if(!Strings.isNullOrEmpty(dictValue.getValueLabel()))
				queryWrapper.like(DictValue::getValueLabel, dictValue.getValueLabel());
			if(!Strings.isNullOrEmpty(dictValue.getCreateBy()))
				queryWrapper.like(DictValue::getCreateBy, dictValue.getCreateBy());
			if(Objects.nonNull(dictValue.getCreateTime()))
				queryWrapper.gt(DictValue::getCreateTime, dictValue.getCreateTime());
		}
		queryWrapper.eq(DictValue::getValid, Constant.VALID_NORMAL);
		
		Page selectPage = dictValueMapper.selectPage(pageInfo.getPage(), queryWrapper);
		long count = selectPage.getTotal();
		List<DictValue> data = selectPage.getRecords();
		return new Result().success().count(count).data(data);
	}

	@Override
	public void saveDictValue(DictValue dictValue) {
		//1.检测是否存在相同的字典值
		LambdaQueryWrapper<DictValue> queryWrapper = new LambdaQueryWrapper<DictValue>();
		queryWrapper.eq(DictValue::getKeyCode, dictValue.getKeyCode());
		queryWrapper.eq(DictValue::getValueCode, dictValue.getValueCode());
		queryWrapper.eq(DictValue::getValid, Constant.VALID_NORMAL);
		List<DictValue> selectList = dictValueMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("键码已存在");
		
		//2.持久化字典值数据
		dictValue.setCreateBy(AppUtil.getCurrentUser().getUsername());
		dictValue.setCreateTime(DateUtil.now());
		dictValueMapper.insert(dictValue);
		
	}

	@Override
	public void logicDelete(Long valueId) {
		DictValue dictValue = new DictValue();
		dictValue.setValueId(valueId);
		dictValue.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		dictValue.setUpdateTime(DateUtil.now());
		dictValue.setValid(Constant.VALID_DELETE);
		
		dictValueMapper.updateById(dictValue);
	}

	@Override
	public void logicDeleteBatch(List<Long> valueIds) {
		DictValue dictValue = new DictValue();
		dictValue.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		dictValue.setUpdateTime(DateUtil.now());
		dictValue.setValid(Constant.VALID_DELETE);
		
		LambdaUpdateWrapper<DictValue> updateWrapper = new LambdaUpdateWrapper<DictValue>();
		updateWrapper.in(DictValue::getValueId, valueIds);
		updateWrapper.eq(DictValue::getValid, Constant.VALID_NORMAL);
		
		dictValueMapper.update(dictValue, updateWrapper);
	}

	@Override
	public void updateDictValue(DictValue dictValue) {
		//1.查询是否存在相同的值码
		LambdaQueryWrapper<DictValue> queryWrapper = new LambdaQueryWrapper<DictValue>();
		queryWrapper.notIn(DictValue::getValueId, dictValue.getValueId());
		queryWrapper.eq(DictValue::getValueCode, dictValue.getValueCode());
		queryWrapper.eq(DictValue::getKeyCode, dictValue.getKeyCode());
		queryWrapper.eq(DictValue::getValid, Constant.VALID_NORMAL);
		List<DictValue> selectList = dictValueMapper.selectList(queryWrapper);
		if(CollectionUtil.noNullAndSizeGtZero(selectList))
			throw new ExistsException("值码已存在");
		
		//2.更新字典值信息
		dictValue.setUpdateBy(AppUtil.getCurrentUser().getUsername());
		dictValue.setUpdateTime(DateUtil.now());
		dictValueMapper.updateById(dictValue);
	}

}
