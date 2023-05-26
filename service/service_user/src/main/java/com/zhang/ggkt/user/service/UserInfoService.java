package com.zhang.ggkt.user.service;


import com.atguigu.ggkt.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zhang
 * @since 2023-05-13
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo getByOpenid(String openId);
}
