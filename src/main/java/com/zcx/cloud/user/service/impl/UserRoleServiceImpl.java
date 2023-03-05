package com.zcx.cloud.user.service.impl;

import com.zcx.cloud.user.entity.UserRole;
import com.zcx.cloud.user.mapper.UserRoleMapper;
import com.zcx.cloud.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *

 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
