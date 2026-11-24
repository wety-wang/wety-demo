package com.wety.demo.module.user.vo;

import com.wety.demo.common.entity.AccountRoleLinkEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
@Schema(name = "RoleUserInfoVO", description = "角色用户列表返回参数")
public class RoleUserInfoVO {

    @Schema(description = "角色code")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "用户code")
    private String accountCode;

    @Schema(description = "用户姓名")
    private String accountName;

    public RoleUserInfoVO(AccountRoleLinkEntity linkEntity) {
        this.roleCode = linkEntity.getRoleCode();
        this.accountCode = linkEntity.getAccountCode();
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "RoleUserInfoVO{"+
                "roleCode='"+roleCode+'\''+
                ", roleName='"+roleName+'\''+
                ", accountCode='"+accountCode+'\''+
                ", accountName='"+accountName+'\''+
                '}';
    }
}
