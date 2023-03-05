package com.zcx.cloud.garbage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import com.zcx.cloud.file.entity.File;
import com.zcx.cloud.folder.entity.Folder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 垃圾箱
 * </p>
 *
 
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_garbage")
public class Garbage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "garbage_id", type = IdType.AUTO)
    private Long garbageId;

    private Long resourceId;

    /**
     * 01：文件 00：文件夹
     */
    private String resourceType;

    /**
     * 单位（天）
     */
    private Integer active;
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String resourceName;

    @TableField(exist = false)
    private String resourceUrl;
    
    @TableField(exist = false)
    private File file;
    
    @TableField(exist = false)
    private Folder folder;
}
