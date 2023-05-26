package com.zhang.ggkt.live.service.impl;


import com.atguigu.ggkt.model.live.LiveCourseDescription;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.ggkt.live.mapper.LiveCourseDescriptionMapper;
import com.zhang.ggkt.live.service.LiveCourseDescriptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.lang.invoke.LambdaConversionException;

/**
 * <p>
 * 课程简介 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-16
 */
@Service
public class LiveCourseDescriptionServiceImpl extends ServiceImpl<LiveCourseDescriptionMapper, LiveCourseDescription> implements LiveCourseDescriptionService {

    @Override
    public LiveCourseDescription getByLiveCourseId(Long id) {
        LambdaQueryWrapper<LiveCourseDescription> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LiveCourseDescription::getLiveCourseId,id);
        LiveCourseDescription liveCourseDescription = baseMapper.selectOne(lambdaQueryWrapper);
        return liveCourseDescription;
    }
}
