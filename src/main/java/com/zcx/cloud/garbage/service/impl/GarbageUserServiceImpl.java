package com.zcx.cloud.garbage.service.impl;

import com.zcx.cloud.garbage.entity.GarbageUser;
import com.zcx.cloud.garbage.mapper.GarbageUserMapper;
import com.zcx.cloud.garbage.service.IGarbageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 垃圾箱与用户关联表 服务实现类
 * </p>
 *
 
 */
@Service
public class GarbageUserServiceImpl extends ServiceImpl<GarbageUserMapper, GarbageUser> implements IGarbageUserService {

}
