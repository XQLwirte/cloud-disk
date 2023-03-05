package com.zcx.cloud.role.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色菜单关联表
 * </p>
 *
 
 */
@Data
@Accessors(chain = true)
@TableName("t_role_menu")
public class RoleMenu{

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long menuId;


}
