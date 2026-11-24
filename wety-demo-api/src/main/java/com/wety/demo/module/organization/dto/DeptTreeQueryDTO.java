package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 部门树查询参数。
 */
@ApiModel(value = "DeptTreeQueryDTO", description = "部门树查询参数")
public class DeptTreeQueryDTO {

    /** 部门名称模糊搜索。 */
    @ApiModelProperty(value = "部门名称模糊搜索")
    private String name;

    /** 状态筛选：1-启用，0-停用。 */
    @ApiModelProperty(value = "状态筛选：1-启用，0-停用")
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeptTreeQueryDTO{" +
                "name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
