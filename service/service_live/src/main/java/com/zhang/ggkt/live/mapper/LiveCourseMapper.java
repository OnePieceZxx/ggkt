package com.zhang.ggkt.live.mapper;


import com.atguigu.ggkt.model.live.LiveCourse;
import com.atguigu.ggkt.vo.live.LiveCourseVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 直播课程表 Mapper 接口
 * </p>
 *
 * @author zhang
 * @since 2023-05-16
 */
public interface LiveCourseMapper extends BaseMapper<LiveCourse> {

    List<LiveCourseVo> findLatelyList();
}
