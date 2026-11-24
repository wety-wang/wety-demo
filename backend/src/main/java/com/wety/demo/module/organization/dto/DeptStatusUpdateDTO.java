package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 启停部门参数。
 */
@Schema(name = "DeptStatusUpdateDTO", description = "启停部门参数")
public class DeptStatusUpdateDTO {

    /** 部门业务编码。 */
    @Schema(description = "部门业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 目标状态：1-启用，0-停用。 */
    @Schema(description = "目标状态：1-启用，0-停用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeptStatusUpdateDTO{" +
                "code='" + code + '\'' +
                ", status=" + status +
                '}';
    }
}
