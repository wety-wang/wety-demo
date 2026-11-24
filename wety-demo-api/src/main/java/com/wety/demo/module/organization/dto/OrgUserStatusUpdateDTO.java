package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 启停用户参数。
 */
@Schema(name = "OrgUserStatusUpdateDTO", description = "启停用户参数")
public class OrgUserStatusUpdateDTO {

    /** 账号业务编码。 */
    @Schema(description = "账号业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 目标状态：0-启用，1-禁用。 */
    @Schema(description = "目标状态：0-启用，1-禁用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer disabled;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "OrgUserStatusUpdateDTO{" +
                "code='" + code + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
