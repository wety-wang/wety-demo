package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 重置用户密码参数。
 */
@ApiModel(value = "OrgUserResetPasswordDTO", description = "重置用户密码参数")
public class OrgUserResetPasswordDTO {

    /** 账号业务编码。 */
    @ApiModelProperty(value = "账号业务编码", required = true)
    private String code;

    /** 新密码（前端 SM2 加密传入，后端解密后 BCrypt 存储）。 */
    @ApiModelProperty(value = "新密码（SM2加密）", required = true)
    private String newPassword;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        // 不输出 newPassword，避免日志泄露敏感信息
        return "OrgUserResetPasswordDTO{" +
                "code='" + code + '\'' +
                '}';
    }
}
