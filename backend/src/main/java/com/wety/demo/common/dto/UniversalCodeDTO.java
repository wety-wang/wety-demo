package com.wety.demo.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
@Schema(description = "通用Code")
public class UniversalCodeDTO {

    @Schema(description = "编号")
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
