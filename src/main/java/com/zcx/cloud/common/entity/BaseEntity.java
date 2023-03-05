package com.zcx.cloud.common.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zcx.cloud.common.constant.Constant;

import lombok.Data;


/**
 * 抽取所欲实体共用属性
 * @author Administrator
 *
 */
@Data
public class BaseEntity{
	/**
	 * 创建人
	 */
	protected String createBy;
	/**
	 * 创建时间 HH:mm:ss（24小时制） hh:mm:ss（12小时制） 可能会引起String转Date异常
	 * 指定时区为东八区，比默认的格林威治时间早8小时
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = Constant.GMT_8)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date createTime;
	/**
	 * 更新人
	 */
	protected String updateBy;
	/**
	 * 更新人
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = Constant.GMT_8)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date updateTime;
	/**
	 * 删除标志 （1:正常;0:删除）
	 */
	protected String valid;
}
