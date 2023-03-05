package com.zcx.cloud.common.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;

/**
 * 分页信息
 * @author Administrator
 *
 */
@Data
public class PageInfo<T> {
	/**
	 * 当前页（1开始）
	 */
	private long page;
	
	/**
	 * 每页最大记录数
	 */
	private long limit;
	
	/**
	 * 总数
	 */
	private long total;
	
	/**
	 * 获取分页对象
	 */
	public Page<T> getPage(){
		Page<T> page = new Page<T>();
		page.setCurrent(this.page);
		page.setSize(limit);
		return page;
	}
}
