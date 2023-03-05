package com.zcx.cloud.common.helper;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.dict.entity.DictKey;
import com.zcx.cloud.dict.entity.DictValue;
import com.zcx.cloud.dict.mapper.DictKeyMapper;
import com.zcx.cloud.dict.mapper.DictValueMapper;

/**
 * 字典工具对象
 * @author Administrator
 *
 */
@Component("dict")
public class DictHelper {
	@Autowired
	private DictKeyMapper dictKeyMapper;
	@Autowired
	private DictValueMapper dictValueMapper;
	
	
	/**
	 * 根据key代码获取到字典数据对应的所有值
	 * @param code
	 * @return
	 */
	public List<DictValue> getValues(String code){
		LambdaQueryWrapper<DictValue> queryWrapper = new LambdaQueryWrapper<DictValue>();
		queryWrapper.eq(DictValue::getKeyCode, code);
		queryWrapper.orderByAsc(DictValue::getValueSort);
		queryWrapper.eq(DictValue::getValid, Constant.VALID_NORMAL);
		List<DictValue> result = dictValueMapper.selectList(queryWrapper);
		return result;
	}
	
	/**获取带前缀的key集合
	 * @param codePrefix
	 * @return
	 */
	public List<DictKey> keys(String codePrefix){
		LambdaQueryWrapper<DictKey> queryWrapper = new LambdaQueryWrapper<DictKey>();
		queryWrapper.like(DictKey::getKeyCode, codePrefix);
		queryWrapper.eq(DictKey::getValid, Constant.VALID_NORMAL);
		List<DictKey> result = dictKeyMapper.selectList(queryWrapper);
		return result;
	}
	
	/**
	 * 根据值显示值对应的标签
	 * @param keyCode
	 * @param valueCode
	 * @return
	 */
	public String getValueLabel(String keyCode, String valueCode) {
		LambdaQueryWrapper<DictValue> queryWrapper = new LambdaQueryWrapper<DictValue>();
		queryWrapper.eq(DictValue::getKeyCode, keyCode);
		queryWrapper.eq(DictValue::getValueCode, valueCode);
		queryWrapper.eq(DictValue::getValid, Constant.VALID_NORMAL);
		DictValue dictValue = dictValueMapper.selectOne(queryWrapper);
		return Objects.isNull(dictValue)?null:dictValue.getValueLabel();
	}
}
