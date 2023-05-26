package com.zhang.ggkt.user.service.impl;


import com.atguigu.ggkt.model.user.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhang.ggkt.user.mapper.UserInfoMapper;
import com.zhang.ggkt.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-13
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public UserInfo getByOpenid(String openId) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id",openId);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }
}
