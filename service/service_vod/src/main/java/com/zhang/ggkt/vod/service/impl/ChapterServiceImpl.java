package com.zhang.ggkt.vod.service.impl;


import com.atguigu.ggkt.model.vod.Chapter;
import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.ggkt.vo.vod.ChapterVo;
import com.atguigu.ggkt.vo.vod.VideoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhang.ggkt.vod.mapper.ChapterMapper;
import com.zhang.ggkt.vod.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.ggkt.vod.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-12
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoService videoService;

    // 大纲列表
    @Override
    public List<ChapterVo> getTreeList(Long courseId) {
        //定义最终数据list集合
        List<ChapterVo> finalChapterVoList = new ArrayList<>();

        //根据courseId获取课程里面的所有章节
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getCourseId, courseId);
        List<Chapter> chapterList = baseMapper.selectList(queryWrapper);

        //根据courseId获取课程里面的所有小节
        LambdaQueryWrapper<Video> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Video::getCourseId,courseId);
        List<Video> videoList = videoService.list(lambdaQueryWrapper);

        //封装章节
        chapterList.stream().forEach(item ->{
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(item,chapterVo);
            finalChapterVoList.add(chapterVo);

            //封装里面的小节
            List<VideoVo> videoVoList = new ArrayList<>();
            videoList.stream().forEach(video ->{
                //判断小节是哪个章节
                if (item.getId().equals(video.getChapterId())){
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video,videoVo);
                    videoVoList.add(videoVo);
                }
            });
            //把章节里面的所有小节放入章节
            chapterVo.setChildren(videoVoList);
        });

        return finalChapterVoList;
    }

    //根据课程id删除章节
    @Override
    public void removeChapterByCourseId(Long id) {
        LambdaQueryWrapper<Chapter> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Chapter::getCourseId,id);
        baseMapper.delete(lambdaQueryWrapper);
    }
}
