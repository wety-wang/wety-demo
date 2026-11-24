package com.wety.demo.module.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 角色数据权限详情返回参数。
 *
 * @author wety
 */
@Schema(name = "RoleDataScopeVO", description = "角色数据权限详情返回参数")
public class RoleDataScopeVO {

    /** 角色业务编码。 */
    @Schema(description = "角色业务编码")
    private String roleCode;

    /** 当前数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人。 */
    @Schema(description = "当前数据范围（1~5）")
    private Integer dataScope;

    /** 自定义部门编码列表（仅 dataScope == 2 时有值）。 */
    @Schema(description = "自定义部门编码列表（仅 dataScope == 2 时有值）")
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
        return "RoleDataScopeVO{" +
                "roleCode='" + roleCode + '\'' +
                ", dataScope=" + dataScope +
                ", deptCodes=" + deptCodes +
                '}';
    }
}
