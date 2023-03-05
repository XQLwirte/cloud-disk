package com.zcx.cloud.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.zcx.cloud.common.constant.Constant;
import com.zcx.cloud.common.entity.BaseEntity;
import com.zcx.cloud.util.AppUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *

 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user")
public class User extends BaseEntity implements Serializable{
	/**
	 * 注意：Shiro Session存储在Redis中，Session会被序列化，那么伴随的用户都会被序列化
	 * 所以此UID值不能与其它相同，否则会序列化失败
	 */
    private static final long serialVersionUID = 4359709211352400087L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private String username;

    /**
     * 后期需要md5加密
     */
    private String password;

    private String photo;
    
    /**
     * 00：女;01：男
     */
    private String gender;
    
    private String qq;
    
    private String email;

	/**
	 * 生日 HH:mm:ss（24小时制） hh:mm:ss（12小时制） 可能会引起String转Date异常
	 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = Constant.GMT_8)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = DateSerializer.class)//日期对象序列化
	@JsonDeserialize(using = DateDeserializer.class)//日期对象反序列化序列化
    private Date birthday;
    
    private String salt;
    
    @TableField(exist = false)
    private String roleIdsStr;
    
    @TableField(exist = false)
    private Boolean online;

    @TableField(exist = false)
    private String oldPassword;
    
    @TableField(exist = false)
    private String newPassword;
    
    /**
     * 拷贝基本信息
     * @param target
     */
    public void copyTo(User target) {
    	target.setUserId(this.getUserId());
    	target.setUsername(this.getUsername());
    	target.setPassword(this.getPassword());
    	target.setPhoto(this.getPhoto());
    	target.setGender(this.getGender());
    	target.setQq(this.getQq());
    	target.setEmail(this.getEmail());
    	target.setBirthday(this.getBirthday());
    	target.setSalt(this.getSalt());
    	target.setCreateBy(this.getCreateBy());
    	target.setCreateTime(this.getCreateTime());
    	target.setUpdateBy(this.getUpdateBy());
    	target.setUpdateTime(this.getUpdateTime());
    	target.setValid(this.getValid());
    }
}
