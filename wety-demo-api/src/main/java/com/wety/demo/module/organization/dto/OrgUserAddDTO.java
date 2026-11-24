package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 新增用户参数。
 */
@ApiModel(value = "OrgUserAddDTO", description = "新增用户参数")
public class OrgUserAddDTO {

    /** 登录名（全局唯一）。 */
    @ApiModelProperty(value = "登录名", required = true)
    private String loginName;

    /** 姓名。 */
    @ApiModelProperty(value = "用户姓名", required = true)
    private String userName;

    /** 密码（前端 SM2 加密传入，后端解密后 BCrypt 存储）。 */
    @ApiModelProperty(value = "密码（SM2加密）", required = true)
    private String password;

    /** 邮箱。 */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /** 手机号。 */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /** 主部门编码。 */
    @ApiModelProperty(value = "主部门编码", required = true)
    private String primaryDeptCode;

    /** 关联岗位编码列表。 */
    @ApiModelProperty(value = "关联岗位编码列表")
    private List<String> positionCodes;

    /** 关联角色编码列表。 */
    @ApiModelProperty(value = "关联角色编码列表")
    private List<String> roleCodes;

    /** 初始状态：0-启用（默认），1-禁用。 */
    @ApiModelProperty(value = "初始状态：0-启用（默认），1-禁用")
    private Integer disabled;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPrimaryDeptCode() {
        return primaryDeptCode;
    }

    public void setPrimaryDeptCode(String primaryDeptCode) {
        this.primaryDeptCode = primaryDeptCode;
    }

    public List<String> getPositionCodes() {
        return positionCodes;
    }

    public void setPositionCodes(List<String> positionCodes) {
        this.positionCodes = positionCodes;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        // 不输出 password，避免日志泄露敏感信息
        return "OrgUserAddDTO{" +
                "loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", primaryDeptCode='" + primaryDeptCode + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
