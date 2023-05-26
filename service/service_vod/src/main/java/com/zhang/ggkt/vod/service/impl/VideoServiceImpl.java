package com.zhang.ggkt.vod.service.impl;


import com.atguigu.ggkt.model.vod.Course;
import com.atguigu.ggkt.model.vod.Video;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.ggkt.vod.mapper.VideoMapper;
import com.zhang.ggkt.vod.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.ggkt.vod.service.VodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-12
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private VodService vodService;



    //根据课程id删除小节
    @Override
    public void removeVideoByCourseId(Long id) {
        //根据课程id查询课程所有小节
        LambdaQueryWrapper<Video> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Video::getCourseId,id);
        List<Video> videoList = baseMapper.selectList(lambdaQueryWrapper);
        //遍历得到集合中每个小节，获取每个小节视频id
        videoList.stream().forEach(item ->{
            String videoSourceId = item.getVideoSourceId();
            //判断视频id是否为空，不为空，删除视频
            if (!StringUtils.isEmpty(videoSourceId)){
                vodService.removeVideo(videoSourceId);
            }

        });
        //根据课程id删除课程所有小节
        baseMapper.delete(lambdaQueryWrapper);
    }

    //删除小节中的视频
    @Override
    public void removeVideoById(Long id) {
        //id查询
        Video video = baseMapper.selectById(id);
        //获取视频id
        String videoSourceId = video.getVideoSourceId();
        //判断视频id是否为空
        if (!StringUtils.isEmpty(videoSourceId)){
            vodService.removeVideo(videoSourceId);
        }
        //根据id删除小节
        baseMapper.deleteById(id);
    }
}
