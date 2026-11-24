package com.wety.demo.module.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author wety
 */
@ApiModel(value = "RoleUserAddDTO", description = "添加角色用户参数")
public class RoleUserAddDTO {

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "账户编码")
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
