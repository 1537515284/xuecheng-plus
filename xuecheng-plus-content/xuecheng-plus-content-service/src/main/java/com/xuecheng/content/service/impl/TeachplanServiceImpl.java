package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    private TeachplanMapper teachplanMapper;

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
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);

            teachplanMapper.insert(teachplanNew);
        }
    }

    /**
     * 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

}
