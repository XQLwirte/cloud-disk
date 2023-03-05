package com.zcx.cloud.share.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 共享用户关联表
 * </p>
 *

 */
@Data
@Accessors(chain = true)
@TableName("t_share_user")
public class ShareUser{

    private static final long serialVersionUID = 1L;

    private Long shareId;

    private Long userId;


}
