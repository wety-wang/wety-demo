package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 修改用户参数。
 */
@Schema(name = "OrgUserUpdateDTO", description = "修改用户参数")
public class OrgUserUpdateDTO {

    /** 账号业务编码。 */
    @Schema(description = "账号业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 姓名。 */
    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    /** 邮箱。 */
    @Schema(description = "邮箱")
    private String email;

    /** 手机号。 */
    @Schema(description = "手机号")
    private String phoneNumber;

    /** 主部门编码。 */
    @Schema(description = "主部门编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String primaryDeptCode;

    /** 关联岗位编码列表（全量覆盖）。 */
    @Schema(description = "关联岗位编码列表（全量覆盖）")
    private List<String> positionCodes;

    /** 关联角色编码列表（全量覆盖）。 */
    @Schema(description = "关联角色编码列表（全量覆盖）")
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
