package com.zcx.cloud.folder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 目录与用户关联表
 * </p>
 *

 */
@Data
@Accessors(chain = true)
@TableName("t_folder_user")
public class FolderUser{

    private static final long serialVersionUID = 1L;

    private Long folderId;

    private Long userId;


}
