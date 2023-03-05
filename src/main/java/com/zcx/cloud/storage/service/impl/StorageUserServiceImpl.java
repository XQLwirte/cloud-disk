package com.zcx.cloud.storage.service.impl;

import com.zcx.cloud.storage.entity.StorageUser;
import com.zcx.cloud.storage.mapper.StorageUserMapper;
import com.zcx.cloud.storage.service.IStorageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 存储与用户关联表 服务实现类
 * </p>
 *

 */
@Service
public class StorageUserServiceImpl extends ServiceImpl<StorageUserMapper, StorageUser> implements IStorageUserService {

}
