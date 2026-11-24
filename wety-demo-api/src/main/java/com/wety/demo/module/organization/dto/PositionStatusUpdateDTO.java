package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 启停岗位参数。
 */
@ApiModel(value = "PositionStatusUpdateDTO", description = "启停岗位参数")
public class PositionStatusUpdateDTO {

    /** 岗位业务编码。 */
    @ApiModelProperty(value = "岗位业务编码", required = true)
    private String code;

    /** 目标状态：1-启用，0-停用。 */
    @ApiModelProperty(value = "目标状态：1-启用，0-停用", required = true)
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
        return "PositionStatusUpdateDTO{" +
                "code='" + code + '\'' +
                ", status=" + status +
                '}';
    }
}
