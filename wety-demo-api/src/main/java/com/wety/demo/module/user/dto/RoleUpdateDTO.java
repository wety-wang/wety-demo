package com.wety.demo.module.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wety
 */
@ApiModel(value = "RoleUpdateDTO", description = "修改角色参数")
public class RoleUpdateDTO {

    @ApiModelProperty(value = "角色内部用code，修改操作时必须带着", example = "4601b43274e64b45a95cfcd15fcd9660")
    private String code;

    @ApiModelProperty(value = "角色名称")
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
        return "RoleUpdateDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
