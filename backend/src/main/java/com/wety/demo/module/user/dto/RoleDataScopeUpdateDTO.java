package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 修改角色数据权限请求参数。
 *
 * @author wety
 */
@Schema(name = "RoleDataScopeUpdateDTO", description = "修改角色数据权限参数")
public class RoleDataScopeUpdateDTO {

    /** 角色业务编码。 */
    @Schema(description = "角色业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    /** 目标数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人。 */
    @Schema(description = "目标数据范围（1~5）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dataScope;

    /** 自定义部门编码列表（当 dataScope == 2 时必填）。 */
    @Schema(description = "自定义部门编码列表（当 dataScope == 2 时必填）")
    private List<String> deptCodes;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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
        return "RoleDataScopeUpdateDTO{" +
                "roleCode='" + roleCode + '\'' +
                ", dataScope=" + dataScope +
                ", deptCodes=" + deptCodes +
                '}';
    }
}
