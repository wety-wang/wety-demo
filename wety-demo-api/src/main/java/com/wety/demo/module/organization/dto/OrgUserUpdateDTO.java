package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 修改用户参数。
 */
@ApiModel(value = "OrgUserUpdateDTO", description = "修改用户参数")
public class OrgUserUpdateDTO {

    /** 账号业务编码。 */
    @ApiModelProperty(value = "账号业务编码", required = true)
    private String code;

    /** 姓名。 */
    @ApiModelProperty(value = "用户姓名", required = true)
    private String userName;

    /** 邮箱。 */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /** 手机号。 */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /** 主部门编码。 */
    @ApiModelProperty(value = "主部门编码", required = true)
    private String primaryDeptCode;

    /** 关联岗位编码列表（全量覆盖）。 */
    @ApiModelProperty(value = "关联岗位编码列表（全量覆盖）")
    private List<String> positionCodes;

    /** 关联角色编码列表（全量覆盖）。 */
    @ApiModelProperty(value = "关联角色编码列表（全量覆盖）")
    private List<String> roleCodes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public String toString() {
        return "OrgUserUpdateDTO{" +
                "code='" + code + '\'' +
                ", userName='" + userName + '\'' +
                ", primaryDeptCode='" + primaryDeptCode + '\'' +
                '}';
    }
}
