package com.zhang.ggkt.vod.service.impl;


import com.alibaba.excel.EasyExcel;
import com.atguigu.ggkt.model.vod.Subject;
import com.atguigu.ggkt.vo.vod.SubjectEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhang.ggkt.exception.GgktException;
import com.zhang.ggkt.vod.listener.SubjectListener;
import com.zhang.ggkt.vod.mapper.SubjectMapper;
import com.zhang.ggkt.vod.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author zhang
 * @since 2023-05-11
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Autowired
    private SubjectListener subjectListener;

    @Override
    public List<Subject> selectSubjectList(Long id) {
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Subject> subjectsList = baseMapper.selectList(wrapper);
        for (Subject subject : subjectsList) {
            //获取subject的id值
            Long subjectId = subject.getId();
            //查询
            boolean isChild = this.isChildren(subjectId);
            //封装到对象里面
            subject.setHasChildren(isChild);
        }
        return subjectsList;
    }

    //课程分类导出
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            //设置下载
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("课程分类", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            //查询课程分类数据
            List<Subject> subjectList = baseMapper.selectList(null);
            List<SubjectEeVo> subjectEeVoList = new ArrayList<>();
            for (Subject subjext : subjectList) {
                SubjectEeVo subjectEeVo = new SubjectEeVo();
//                subjectEeVo.setId(subjext.getId());
//                subjectEeVo.setParentId(subjext.getParentId());
                BeanUtils.copyProperties(subjext,subjectEeVo);
                subjectEeVoList.add(subjectEeVo);
            }

            //easyExcel写操作
            EasyExcel.write(response.getOutputStream(), SubjectEeVo.class)
                    .sheet("课程分类").doWrite(subjectEeVoList);
        } catch (Exception e) {
            throw  new GgktException(20001,"导出失败！");
        }
    }

    //课程分类导入
    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),SubjectEeVo.class,subjectListener).sheet().doRead();
        } catch (IOException e) {
            throw new GgktException(20001,"导入失败！");
        }
    }

    //判断是否有下一层数据
    private boolean isChildren(Long subjectId) {
        QueryWrapper<Subject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",subjectId);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
