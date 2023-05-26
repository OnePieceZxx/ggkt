package com.zhang.ggkt.vod.controller;


import com.atguigu.ggkt.model.vod.Teacher;
import com.atguigu.ggkt.vo.vod.TeacherQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.ggkt.exception.GgktException;
import com.zhang.ggkt.result.Result;
import com.zhang.ggkt.vod.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author zhang
 * @since 2023-05-07
 */
@Api(tags = "讲师管理")
@RestController
@RequestMapping("/admin/vod/teacher")
//@CrossOrigin//跨域
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    //http://localhost:8301/admin/vod/teacher/findAll
    //1.查询所有讲师
//    @ApiOperation("查询所有讲师")
//    @GetMapping("findAll")
//    public List<Teacher> findAll(){
//        List<Teacher> teacherList = teacherService.list();
//        return teacherList;
//    }
    @ApiOperation("查询所有讲师")
    @GetMapping("findAll")
    public Result findAllTeacher(){
        //测试异常处理
//        try {
//            int i = 10 / 0;
//        } catch (Exception e) {
//            throw  new GgktException(201,"执行了自定义异常处理");
//        }
        List<Teacher> teacherList = teacherService.list();
        return Result.ok(teacherList);
    }

    //2.逻辑删除讲师
//    @ApiOperation("根据id逻辑删除讲师")
//    @DeleteMapping("remove/{id}")
//    public boolean removeById(@PathVariable String id){
//        return teacherService.removeById(id);
//    }
    @ApiOperation("根据id逻辑删除讲师")
    @DeleteMapping("remove/{id}")
    public Result removeById(@ApiParam(name = "id", value = "ID", required = true,example = "1")
                                  @PathVariable Long id){

        boolean isSuccess = teacherService.removeById(id);
        if (isSuccess){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //3 条件查询分页
    @ApiOperation("条件查询分页")
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findPage(@PathVariable long current,
                           @PathVariable long limit,
                           @RequestBody(required = false) TeacherQueryVo teacherQueryVo){
        //创建page
        Page<Teacher> pageParam = new Page<>(current,limit);
        //判断teacherQueryVo是否为空
        if (teacherQueryVo == null){
            //查询全部
            Page<Teacher> teacherPage = teacherService.page(pageParam);
            return Result.ok(teacherPage);
        }else {
            //获取条件值
            String name = teacherQueryVo.getName();
            Integer level = teacherQueryVo.getLevel();
            String joinDateBegin = teacherQueryVo.getJoinDateBegin();
            String joinDateEnd = teacherQueryVo.getJoinDateEnd();
            //进行非空判断，条件封装
            QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
            if (!StringUtils.isEmpty(name)){
                wrapper.like("name",name);
            }
            if (!StringUtils.isEmpty(level)){
                wrapper.eq("level",level);
            }
            if (!StringUtils.isEmpty(joinDateBegin)){
                wrapper.ge("join_date",joinDateBegin);
            }
            if (!StringUtils.isEmpty(joinDateEnd)){
                wrapper.le("join_date",joinDateEnd);
            }
            //调用方法分页查询
            Page<Teacher> teacherPage = teacherService.page(pageParam, wrapper);
            return Result.ok(teacherPage);
        }
    }

    //4 添加讲师
    @ApiOperation("添加讲师")
    @PostMapping("saveTeacher")
    public Result saveTeacher(@RequestBody Teacher teacher){
        boolean isSuccess = teacherService.save(teacher);
        if (isSuccess){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    //5 根据id查询讲师
    @ApiOperation("根据id查询讲师")
    @GetMapping("getTeacher/{id}")
    public Result getTeacher(@PathVariable Long id){
        Teacher teacher = teacherService.getById(id);
        return Result.ok(teacher);
    }

    //6 修改讲师
    @ApiOperation("修改讲师")
    @PostMapping("updateTeacher")
    public Result updateTeacher(@RequestBody Teacher teacher){
        boolean isSuccess = teacherService.updateById(teacher);
        if (isSuccess){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    
    //7 批量删除讲师,json数组
    @ApiOperation("批量删除讲师")
    @DeleteMapping("removeBatch")
    public Result removeBatch(@RequestBody List<Long> idList){
        boolean isSuccess = teacherService.removeByIds(idList);
        if (isSuccess){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 根据id查询讲师 远程调用
    @ApiOperation("根据id查询讲师")
    @GetMapping("inner/getTeacher/{id}")
    public Teacher getTeacherInfo(@PathVariable Long id){
        Teacher teacher = teacherService.getById(id);
        return teacher;
    }

}

