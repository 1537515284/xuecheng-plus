package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description 分页查询结果模型类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    @ApiModelProperty("数据列表")
    private List<T> items;

    @ApiModelProperty("总记录数")
    private long counts;

    @ApiModelProperty("当前页码")
    private long page;

    @ApiModelProperty("每页记录数")
    private long pageSize;

}
