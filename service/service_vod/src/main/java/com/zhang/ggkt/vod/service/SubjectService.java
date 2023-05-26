package com.zhang.ggkt.vod.service;


import com.atguigu.ggkt.model.vod.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author zhang
 * @since 2023-05-11
 */
public interface SubjectService extends IService<Subject> {

    //查询下一层课程分类
    //根据parent_id
    List<Subject> selectSubjectList(Long id);

    //课程分类导出
    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);
}
