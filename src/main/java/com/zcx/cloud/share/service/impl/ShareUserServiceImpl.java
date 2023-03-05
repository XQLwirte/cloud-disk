package com.zcx.cloud.share.service.impl;

import com.zcx.cloud.share.entity.ShareUser;
import com.zcx.cloud.share.mapper.ShareUserMapper;
import com.zcx.cloud.share.service.IShareUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 共享用户关联表 服务实现类
 * </p>
 *

 */
@Service
public class ShareUserServiceImpl extends ServiceImpl<ShareUserMapper, ShareUser> implements IShareUserService {

}
