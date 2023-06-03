package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> getCourseTeacherInfo(String courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    public CourseTeacher saveCourseBase(CourseTeacher courseTeacher) {
        //课程教师id
        Long id = courseTeacher.getId();
        //修改课程教师
        if(id!=null){
            courseTeacherMapper.updateById(courseTeacher);
        }else{
            courseTeacherMapper.insert(courseTeacher);
        }
        return courseTeacher;
    }

    @Override
    public void removeCourseTeacher(String courseId, String courseTeacherId) {
        CourseTeacher courseTeacher = courseTeacherMapper.selectById(courseTeacherId);
        if(courseTeacher == null){
            XueChengPlusException.cast("教师信息不存在");
        }
        if(!courseTeacher.getCourseId().toString().equals(courseId)){
            XueChengPlusException.cast("要删除的教师信息不属于该课程");
        }
        courseTeacherMapper.deleteById(courseTeacherId);
    }
}
