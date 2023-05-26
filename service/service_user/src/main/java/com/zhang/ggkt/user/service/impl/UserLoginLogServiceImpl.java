package com.zhang.ggkt.user.service.impl;


import com.atguigu.ggkt.model.user.UserLoginLog;
import com.zhang.ggkt.user.mapper.UserLoginLogMapper;
import com.zhang.ggkt.user.service.UserLoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登陆记录表 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-13
 */
@Service
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, UserLoginLog> implements UserLoginLogService {

}
