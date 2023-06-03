package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-05-15
 */
public interface TeachplanService extends IService<Teachplan> {

    /**
     * 查询课程计划树型结构
     * @param courseId  课程id
     * @return List<TeachplanDto>
     */
    List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * 保存课程计划
     * @param teachplanDto  课程计划信息
     */
    void saveTeachplan(SaveTeachplanDto teachplanDto);

    /**
     * 保存课程计划
     * @param teachPlanId  课程计划编号
     */
    void removeTeachplan(String teachPlanId);

    /**
     * 移动课程计划
     * @param movementDirection 移动方向
     * @param teachPlanId   课程计划编号
     */
    void moveTeachplan(String movementDirection, String teachPlanId);
}
