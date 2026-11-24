package com.wety.demo.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
public class CodeNameDTO {

    /**
     * code
     */
    @Schema(description = "code")
    private String code;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CodeNameDTO{"+
                "code='"+code+'\''+
                ", name='"+name+'\''+
                '}';
    }
}
