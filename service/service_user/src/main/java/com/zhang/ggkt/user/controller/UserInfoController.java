package com.zhang.ggkt.user.controller;


import com.atguigu.ggkt.model.user.UserInfo;
import com.zhang.ggkt.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zhang
 * @since 2023-05-13
 */
@RestController
@RequestMapping("/admin/user/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    //根据id查询用户信息
    @GetMapping("inner/getById/{id}")
    public UserInfo getById(@PathVariable Long id){
        return userInfoService.getById(id);
    }

}

