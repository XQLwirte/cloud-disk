package com.zcx.cloud.share.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import com.zcx.cloud.file.entity.File;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 共享信息表
 * </p>
 *

 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_share")
public class Share extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "share_id", type = IdType.AUTO)
    private Long shareId;

    private Long fileId;

    /**
     * 单位（天）
            0：代表永久有效
     */
    private Long activeDuration;

    /**
     * 01:有效;00:失效
     */
    private String activeFlag;

    private Long download;

    /**
     * 无共享码则直接共享
     */
    private String shareCode;

    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String fileName;
    
    @TableField(exist = false)
    private File file;

}
