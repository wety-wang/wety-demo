package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author wety
 */
@Schema(name = "RolePermissionUpdateDTO", description = "角色权限修改参数")
public class RolePermissionUpdateDTO {

    @Schema(description = "角色的code", example = "3fa9b8243efb4fa7a6a313248b32177b")
    private String roleCode;

    /**
     * 所用有的权限编码的数组
     */
    @Schema(description = "用户/账户维护的权限的code列表，权限修改时用")
    private List<String> permissionCodeList;

    @Schema(description = "用户的code列表")
    private List<String> accountCodeList;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public List<String> getPermissionCodeList() {
        return permissionCodeList;
    }

    public void setPermissionCodeList(List<String> permissionCodeList) {
        this.permissionCodeList = permissionCodeList;
    }

    public List<String> getAccountCodeList() {
        return accountCodeList;
    }

    public void setAccountCodeList(List<String> accountCodeList) {
        this.accountCodeList = accountCodeList;
    }

    @Override
    public String toString() {
        return "RolePermissionUpdateDTO{"+
                "roleCode='"+roleCode+'\''+
                ", permissionCodeList="+permissionCodeList+
                ", accountCodeList="+accountCodeList+
                '}';
    }
}
