package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 启停用户参数。
 */
@ApiModel(value = "OrgUserStatusUpdateDTO", description = "启停用户参数")
public class OrgUserStatusUpdateDTO {

    /** 账号业务编码。 */
    @ApiModelProperty(value = "账号业务编码", required = true)
    private String code;

    /** 目标状态：0-启用，1-禁用。 */
    @ApiModelProperty(value = "目标状态：0-启用，1-禁用", required = true)
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
