package com.zcx.cloud.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zcx.cloud.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据字典键表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_dict_key")
public class DictKey extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "key_id", type = IdType.AUTO)
    private Long keyId;

    private String keyCode;

    private String keyLabel;
}
