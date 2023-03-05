package com.zcx.cloud.task.base;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.entity.DictValue;
import com.zcx.cloud.dict.mapper.DictKeyMapper;
import com.zcx.cloud.dict.mapper.DictValueMapper;
import com.zcx.cloud.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除数据字典定时任务
 * 
 * 1.删除数据字典键
 * 2.删除对应的所有值信息
 * 涉及表：t_dict_key、t_dict_value
 * @author ZCX
 *
 */
@Slf4j
@Component("taskDeleteDict")
public class TaskDeleteDict {
	@Autowired
	private DictKeyMapper dictKeyMapper;
	@Autowired
	private DictValueMapper dictValueMapper;
	
	public void deleteDict() {
		//1.查询所有无效键
		LambdaQueryWrapper<DictKey> qwDK = new LambdaQueryWrapper<DictKey>();
		qwDK.eq(DictKey::getValid, Constant.VALID_DELETE);
		List<DictKey> dictKeys = dictKeyMapper.selectList(qwDK);
		if(CollectionUtil.noNullAndSizeGtZero(dictKeys)) {
			List<Long> keyIds = dictKeys.stream().map(dictKey -> dictKey.getKeyId()).collect(Collectors.toList());
			List<String> keyCodes = dictKeys.stream().map(dictKey -> dictKey.getKeyCode()).collect(Collectors.toList());
			//2.删除无效键对应的值
			LambdaQueryWrapper<DictValue> qwDV = new LambdaQueryWrapper<DictValue>();
			qwDV.in(DictValue::getKeyCode, keyCodes);
			dictValueMapper.delete(qwDV);
			
			//3.删除无效键
			LambdaQueryWrapper<DictKey> qwDK2 = new LambdaQueryWrapper<DictKey>();
			qwDK2.in(DictKey::getKeyId, keyIds);
			dictKeyMapper.delete(qwDK2);
		}

		//4.删除散列的无效值
		LambdaQueryWrapper<DictValue> qwDV2 = new LambdaQueryWrapper<DictValue>();
		qwDV2.eq(DictValue::getValid, Constant.VALID_DELETE);
		dictValueMapper.delete(qwDV2);
	}
	
}
