package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 部门树查询参数。
 */
@Schema(name = "DeptTreeQueryDTO", description = "部门树查询参数")
public class DeptTreeQueryDTO {

    /** 部门名称模糊搜索。 */
    @Schema(description = "部门名称模糊搜索")
    private String name;

    /** 状态筛选：1-启用，0-停用。 */
    @Schema(description = "状态筛选：1-启用，0-停用")
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
