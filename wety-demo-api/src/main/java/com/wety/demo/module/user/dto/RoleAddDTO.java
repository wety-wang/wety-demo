package com.wety.demo.module.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wety
 */
@ApiModel(value = "RoleAddDTO", description = "新增角色参数")
public class RoleAddDTO {

    @ApiModelProperty(value = "角色名称")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RoleAddDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
