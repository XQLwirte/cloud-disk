package com.zcx.cloud.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.zcx.cloud.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件表
 * </p>

 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_file")
public class File extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    private String fileName;

    private String fileType;

    private String fileIcon;

    /**
     * 为不暴露FastDFS服务器路径，可只保存关键数据
     */
    private String fileUrl;

    /**
     * KB（千字节）
     */
    private Long fileSize;

    /**
     * 挂载在哪一个目录下，目录跟随用户
     */
    private Long folderId;

}
