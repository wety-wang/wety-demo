package com.wety.demo.module.organization.dto;

import com.wety.demo.common.dto.PageableDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 组织架构用户分页查询参数。
 */
@ApiModel(value = "OrgUserPageQueryDTO", description = "用户分页查询参数")
public class OrgUserPageQueryDTO extends PageableDTO {

    /** 左侧部门树点击筛选：查询该部门及其所有子孙部门下的用户。 */
    @ApiModelProperty(value = "部门编码（包含该部门及其所有子孙部门）")
    private String deptCode;

    /** 岗位编码筛选（可选）。 */
    @ApiModelProperty(value = "岗位编码筛选")
    private String positionCode;

    /** 角色编码筛选（可选）。 */
    @ApiModelProperty(value = "角色编码筛选")
    private String roleCode;

    /** 姓名模糊搜索。 */
    @ApiModelProperty(value = "姓名模糊搜索")
    private String userName;

    /** 登录名模糊搜索。 */
    @ApiModelProperty(value = "登录名模糊搜索")
    private String loginName;

    /** 手机号模糊搜索。 */
    @ApiModelProperty(value = "手机号模糊搜索")
    private String phoneNumber;

    /** 邮箱模糊搜索。 */
    @ApiModelProperty(value = "邮箱模糊搜索")
    private String email;

    /** 状态筛选：0-启用，1-禁用。 */
    @ApiModelProperty(value = "状态筛选：0-启用，1-禁用")
    private Integer disabled;

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "OrgUserPageQueryDTO{" +
                "deptCode='" + deptCode + '\'' +
                ", positionCode='" + positionCode + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", userName='" + userName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", disabled=" + disabled +
                "} " + super.toString();
    }
}
