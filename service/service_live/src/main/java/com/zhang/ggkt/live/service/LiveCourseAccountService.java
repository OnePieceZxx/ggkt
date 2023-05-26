package com.zhang.ggkt.live.service;


import com.atguigu.ggkt.model.live.LiveCourseAccount;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 直播课程账号表（受保护信息） 服务类
 * </p>
 *
 * @author zhang
 * @since 2023-05-16
 */
public interface LiveCourseAccountService extends IService<LiveCourseAccount> {

    LiveCourseAccount getByLiveCourseId(Long id);
}
