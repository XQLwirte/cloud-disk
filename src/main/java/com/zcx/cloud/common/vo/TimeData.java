package com.zcx.cloud.common.vo;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zcx.cloud.common.constant.Constant;

import lombok.Data;

/**
 * 在某一个时间点的数据
 * @author ZCX
 *
 */
@Data
public class TimeData<T> {
	/**
	 * 时间点
	 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = Constant.GMT_8)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date time;
	/**
	 * 数据
	 */
	private List<T> data;
}
