package com.zcx.cloud.common.vo;

import java.util.Objects;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.util.AppUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 表格排序信息
 * @author Administrator
 *
 */
@Slf4j
@Data
public class SortInfo {
	/**
	 * 排序字段
	 */
	private String field;
	
	/**
	 * 排序顺序 asc升序 desc降序
	 */
	private String type;
	
	/**
	 * 将排序信息注入到page中
	 * @param page
	 */
	public void toPageSort(Page page) {
		String string = AppUtil.camelCaseToUnderLine(field);
		if(Objects.nonNull(page)) {
			if(Constant.SORT_ASC.equals(type))
				page.setAsc(string);
			else if(Constant.SORT_DESC.equals(type))
				page.setDesc(string);
		}
	}
}
