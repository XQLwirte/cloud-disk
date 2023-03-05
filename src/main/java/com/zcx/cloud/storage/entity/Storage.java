package com.zcx.cloud.storage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储信息表
 * </p>
 *

 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_storage")
public class Storage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "storage_id", type = IdType.AUTO)
    private Long storageId;

    /**
     * 单位：B（字节）
     */
    private Long capacityTotal;

    /**
     * 单位：B（字节）
     */
    private Long capacityUse;

    /**
     * 所属用户
     */
    @TableField(exist = false)
    private String username;
}
