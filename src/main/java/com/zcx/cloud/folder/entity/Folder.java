package com.zcx.cloud.folder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.zcx.cloud.common.entity.BaseEntity;
import com.zcx.cloud.file.entity.File;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 目录表
 * </p>
 *

 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_folder")
public class Folder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "folder_id", type = IdType.AUTO)
    private Long folderId;

    private String folderName;

    private Long parentId;

    @TableField(exist = false)
    private String username;

    /**
     * 子目录集合
     */
    @TableField(exist = false)
    List<Folder> folders = new ArrayList<Folder>();

    /**
     * 挂载的文件
     */
    @TableField(exist = false)
    List<File> files = new ArrayList<File>();
}
