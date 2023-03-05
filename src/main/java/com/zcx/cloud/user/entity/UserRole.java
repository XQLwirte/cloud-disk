package com.zcx.cloud.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户角色关联表
 * </p>
 *

 */
@Data
@Accessors(chain = true)
@TableName("t_user_role")
public class UserRole{

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long roleId;


}
