package com.wety.demo.module.user.dto;

import com.wety.demo.common.dto.PageableDTO;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
@Schema(name = "RoleUserPageQueryDTO", description = "角色用户分页查询参数")
public class RoleUserPageQueryDTO extends PageableDTO {

    @Schema(description = "姓名,模糊搜索")
    private String userName;

    /**
     * 角色code
     */
    @Schema(description = "角色code")
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
