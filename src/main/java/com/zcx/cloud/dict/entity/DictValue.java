package com.zcx.cloud.dict.entity;

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
 * 数据字典值表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_dict_value")
public class DictValue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "value_id", type = IdType.AUTO)
    private Long valueId;

    private String keyCode;

    private String valueCode;

    private String valueLabel;

    private Integer valueSort;
}
