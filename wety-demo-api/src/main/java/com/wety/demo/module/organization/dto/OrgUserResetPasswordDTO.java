package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 重置用户密码参数。
 */
@Schema(name = "OrgUserResetPasswordDTO", description = "重置用户密码参数")
public class OrgUserResetPasswordDTO {

    /** 账号业务编码。 */
    @Schema(description = "账号业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 新密码（前端 SM2 加密传入，后端解密后 BCrypt 存储）。 */
    @Schema(description = "新密码（SM2加密）", requiredMode = Schema.RequiredMode.REQUIRED)
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
