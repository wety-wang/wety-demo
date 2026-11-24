package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author wety
 */
@Schema(name = "RoleUserAddDTO", description = "添加角色用户参数")
public class RoleUserAddDTO {

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "账户编码")
    private List<String> accountCodes;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public List<String> getAccountCodes() {
        return accountCodes;
    }

    public void setAccountCodes(List<String> accountCodes) {
        this.accountCodes = accountCodes;
    }

    public void setAccountCode(List<String> accountCodes) {
        this.accountCodes = accountCodes;
    }

    @Override
    public String toString() {
        return "RoleUserAddDTO{"+
                "roleCode='"+roleCode+'\''+
                ", accountCodes="+accountCodes+
                '}';
    }
}
