package com.zcx.cloud.storage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储与用户关联表
 * </p>
 *

 */
@Data
@Accessors(chain = true)
@TableName("t_storage_user")
public class StorageUser{

    private static final long serialVersionUID = 1L;

    private Long storageId;

    private Long userId;


}
