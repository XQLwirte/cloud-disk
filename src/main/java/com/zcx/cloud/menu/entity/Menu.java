package com.zcx.cloud.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单表
 * </p>
 *
 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_menu")
public class Menu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;
    
    private Long parentId;

    private String menuName;

    private String menuIcon;

    private String menuUrl;
    
    private Integer menuSort;
    
    @TableField(exist = false)
    private Menu parentMenu;
    
    @TableField(exist = false)
    private List<Menu> children = new ArrayList<Menu>();
    
    /**
     * 此菜单所需的角色，以逗号分割
     */
    @TableField(exist = false)
    private String roleCodes;
}
