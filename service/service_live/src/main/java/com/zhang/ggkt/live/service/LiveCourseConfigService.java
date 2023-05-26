package com.zhang.ggkt.live.service;


import com.atguigu.ggkt.model.live.LiveCourseConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 直播课程配置表 服务类
 * </p>
 *
 * @author zhang
 * @since 2023-05-16
 */
public interface LiveCourseConfigService extends IService<LiveCourseConfig> {

    LiveCourseConfig getByLiveCourseId(Long id);
}
