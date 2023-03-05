package com.zcx.cloud.garbage.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 垃圾箱与用户关联表
 * </p>
 *
 
 */
@Data
@Accessors(chain = true)
@TableName("t_garbage_user")
public class GarbageUser{

    private static final long serialVersionUID = 1L;

    private Long garbageId;

    private Long userId;


}
