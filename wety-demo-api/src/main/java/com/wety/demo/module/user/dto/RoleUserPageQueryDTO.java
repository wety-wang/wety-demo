package com.wety.demo.module.user.dto;

import com.wety.demo.common.dto.PageableDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wety
 */
@ApiModel(value = "RoleUserPageQueryDTO", description = "角色用户分页查询参数")
public class RoleUserPageQueryDTO extends PageableDTO {

    @ApiModelProperty(value = "姓名,模糊搜索")
    private String userName;

    /**
     * 角色code
     */
    @ApiModelProperty(value = "角色code")
    private String roleCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public String toString() {
        return "RoleUserPageQueryDTO{"+
                "userName='"+userName+'\''+
                ", roleCode='"+roleCode+'\''+
                "} "+super.toString();
    }
}
