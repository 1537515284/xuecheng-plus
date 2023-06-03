package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程教师信息
 */
@Slf4j
@RestController
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    @ApiOperation("课程教师信息查询接口")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacherByCourseId(@PathVariable String courseId){
        return courseTeacherService.getCourseTeacherInfo(courseId);
    }

    @ApiOperation("课程教师信息新增或修改")
    @PostMapping("/courseTeacher")
    public CourseTeacher createCourseBase(@RequestBody CourseTeacher CourseTeacher){
        return courseTeacherService.saveCourseBase(CourseTeacher);
    }

    @ApiOperation("课程教师删除")
    @DeleteMapping("/courseTeacher/course/{courseId}/{courseTeacherId}")
    public void removeCourseTeacher(@PathVariable String courseId, @PathVariable String courseTeacherId){
        courseTeacherService.removeCourseTeacher(courseId,courseTeacherId);
    }
}
