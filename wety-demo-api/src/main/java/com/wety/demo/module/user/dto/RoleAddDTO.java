package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 新增角色请求参数。
 *
 * @author wety
 */
@Schema(name = "RoleAddDTO", description = "新增角色参数")
public class RoleAddDTO {

    /** 角色名称。 */
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 数据权限范围（可选，默认 5-仅本人）。 */
    @Schema(description = "数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人")
    private Integer dataScope;

    /** 自定义部门编码列表（当 dataScope == 2 时生效）。 */
    @Schema(description = "自定义部门编码列表（当 dataScope == 2 时必填）")
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
