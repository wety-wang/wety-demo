package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
@Schema(name = "RoleUserDeleteDTO", description = "删除角色用户参数")
public class RoleUserDeleteDTO {

    @Schema(description = "角色code")
    private String roleCode;

    @Schema(description = "用户code")
    private String accountCode;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Override
    public String toString() {
        return "RoleUserDeleteDTO{"+
                "roleCode='"+roleCode+'\''+
                ", accountCode='"+accountCode+'\''+
                '}';
    }
}
