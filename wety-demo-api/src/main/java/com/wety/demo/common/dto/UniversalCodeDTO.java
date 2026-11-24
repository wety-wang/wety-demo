package com.wety.demo.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wety
 */
@ApiModel(description = "通用Code")
public class UniversalCodeDTO {

    @ApiModelProperty(value = "编号")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UniversalCodeDTO{" + "code='" + code + '\'' + '}';
    }
}
