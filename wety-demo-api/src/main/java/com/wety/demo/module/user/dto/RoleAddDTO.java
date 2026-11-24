package com.wety.demo.module.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 新增角色请求参数。
 *
 * @author wety
 */
@ApiModel(value = "RoleAddDTO", description = "新增角色参数")
public class RoleAddDTO {

    /** 角色名称。 */
    @ApiModelProperty(value = "角色名称", required = true)
    private String name;

    /** 数据权限范围（可选，默认 5-仅本人）。 */
    @ApiModelProperty(value = "数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人")
    private Integer dataScope;

    /** 自定义部门编码列表（当 dataScope == 2 时生效）。 */
    @ApiModelProperty(value = "自定义部门编码列表（当 dataScope == 2 时必填）")
    private List<String> deptCodes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public List<String> getDeptCodes() {
        return deptCodes;
    }

    public void setDeptCodes(List<String> deptCodes) {
        this.deptCodes = deptCodes;
    }

    @Override
    public String toString() {
        return "RoleAddDTO{" +
                "name='" + name + '\'' +
                ", dataScope=" + dataScope +
                ", deptCodes=" + deptCodes +
                '}';
    }
}
