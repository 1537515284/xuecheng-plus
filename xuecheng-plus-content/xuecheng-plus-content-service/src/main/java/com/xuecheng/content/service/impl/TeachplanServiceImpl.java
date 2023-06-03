package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {

    /**
     * 第一级课程计划的父ID
     */
    private static final Long firstLevelParentId = 0L;
    /**
     * 第一级课程计划的等级
     */
    private static final Integer firstLevelGrade = 1;

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if(id!=null){
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
            //取出同父同级别的课程计划中最大的排序号
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);

            teachplanMapper.insert(teachplanNew);
        }
    }

    @Transactional
    @Override
    public void removeTeachplan(String teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if(Objects.isNull(teachplan)){
            XueChengPlusException.cast("没有查找到此课程计划信息");
        }
        if(isFirstLevelChapter(teachplan)){
            // 一级章节
            int count = getSubTeachplanCount(teachPlanId);
            if(count > 0){
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            }
        }else {
            // 二级章节
            removeTeachplanMedia(teachPlanId);
        }
        teachplanMapper.deleteById(teachPlanId);
    }

    @Transactional
    @Override
    public void moveTeachplan(String movementDirection, String teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if(Objects.isNull(teachplan)){
            XueChengPlusException.cast("没有查找到此课程计划信息");
        }

        // 获取相邻的章节
        Teachplan tarTeachplan  = getTarTeachplan(teachplan, movementDirection);
        if (tarTeachplan == null){
            return;
        }

        // 交换排序号并更新
        int originalOrderBy = teachplan.getOrderby();
        teachplan.setOrderby(tarTeachplan.getOrderby());
        tarTeachplan.setOrderby(originalOrderBy);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(tarTeachplan);
    }

    /**
     * 获取上/下方相邻的课程计划
     * @param teachplan 要调整的课程计划
     * @param movementDirection 移动方向
     * @return Teachplan
     */
    private Teachplan getTarTeachplan(Teachplan teachplan, String movementDirection) {
        String columns = "";
        if("moveup".equals(movementDirection)){
            columns = "id,MAX(orderby) AS orderby";
        } else if("movedown".equals(movementDirection)) {
            columns = "id,MIN(orderby) AS orderby";
        }else {
            return null;
        }
        QueryWrapper<Teachplan> queryWrapper = new QueryWrapper<>();
        if(isFirstLevelChapter(teachplan)){
            // 一级章节....
            Long courseId = teachplan.getCourseId();
            queryWrapper.select(columns);
            queryWrapper.eq("course_id",courseId);
            queryWrapper.eq("parentid",firstLevelParentId);
        }else {
            // 二级章节....
            Long parentId = teachplan.getParentid();
            queryWrapper.select(columns);
            queryWrapper.eq("parentid",parentId);
        }
        queryWrapper.lt("moveup".equals(movementDirection),"orderby",teachplan.getOrderby());
        queryWrapper.gt("movedown".equals(movementDirection),"orderby",teachplan.getOrderby());
        queryWrapper.groupBy("id");
        queryWrapper.orderByDesc("moveup".equals(movementDirection),"orderby");
        queryWrapper.orderByAsc("movedown".equals(movementDirection),"orderby");
        queryWrapper.last("limit 1");
        return teachplanMapper.selectOne(queryWrapper);
    }

    /**
     * 判断是否是第一级课程计划
     * @param teachplan 课程计划对象
     * @return boolean
     */
    private boolean isFirstLevelChapter(Teachplan teachplan) {
        return firstLevelParentId.equals(teachplan.getParentid()) && firstLevelGrade.equals(teachplan.getGrade());
    }

    /**
     * 删除章节视频
     * @param teachPlanId 章节编号
     */
    private void removeTeachplanMedia(String teachPlanId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
        teachplanMediaMapper.delete(queryWrapper);
    }

    /**
     * 获取子章节数量
     * @param teachPlanId 章节编号
     * @return int 子章节数量
     */
    private int getSubTeachplanCount(String teachPlanId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, teachPlanId);
        return teachplanMapper.selectCount(queryWrapper);
    }

    /**
     * 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     */
    private int getTeachplanCount(long courseId,long parentId){
        QueryWrapper<Teachplan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        queryWrapper.eq("parentid",parentId);
        queryWrapper.select("MAX(orderby) AS orderby");
        Teachplan teachplan = teachplanMapper.selectOne(queryWrapper);
        return teachplan != null ? teachplan.getOrderby() : 1;
    }

}
