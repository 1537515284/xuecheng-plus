package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-05-15
 */
public interface CourseTeacherService extends IService<CourseTeacher> {

    List<CourseTeacher> getCourseTeacherInfo(String courseId);

    CourseTeacher saveCourseBase(CourseTeacher courseTeacher);

    void removeCourseTeacher(String courseId, String courseTeacherId);
}
