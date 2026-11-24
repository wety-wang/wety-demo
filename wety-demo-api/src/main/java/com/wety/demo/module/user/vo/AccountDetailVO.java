package com.wety.demo.module.user.vo;

import com.wety.demo.common.entity.AccountEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author wety
 */
@ApiModel(description = "账户详细信息")
public class AccountDetailVO extends AccountInfoVO {

    @ApiModelProperty(value = "用户角色名（用逗号分割）")
    private String roleNames;

    @ApiModelProperty(value = "角色Code列表")
    private List<String> roleCodes;

    @ApiModelProperty(value = "用户权限列表")
    private List<String> permissionCodes;

    public AccountDetailVO(AccountEntity entity) {
        super(entity);
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }

    @Override
    public String toString() {
        return "AccountDTO{"+
                "roleNames='"+roleNames+'\''+
                ", roleCodes="+roleCodes+
                ", permissionCodes="+permissionCodes+
                '}';
    }
}
