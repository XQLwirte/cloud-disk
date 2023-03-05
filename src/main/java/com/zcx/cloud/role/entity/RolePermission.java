package com.zcx.cloud.role.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色权限关联表
 * </p>
 *

 */
@Data
@Accessors(chain = true)
@TableName("t_role_permission")
public class RolePermission{

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long permissionId;


}
