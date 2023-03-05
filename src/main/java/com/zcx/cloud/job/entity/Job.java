package com.zcx.cloud.job.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 定时任务表
 * </p>
 *
 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_job")
public class Job extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_id", type = IdType.AUTO)
    private Long jobId;

    private String jobName;

    private String jobGroup;

    private String beanName;

    private String beanMethod;

    private String beanParam;

    private String cron;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = Constant.GMT_8)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = Constant.GMT_8)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String remark;

    /**
     * 00：停止
       01：运行
       02：暂停
     */
    private String status;
}
