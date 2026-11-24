package com.wety.demo.module.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.entity.RoleEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author wety
 */
@ApiModel(value = "RoleInfoVO", description = "角色列表返回参数")
public class RoleInfoVO {

    @ApiModelProperty(value = "角色内部用code，修改操作时必须带着", example = "4601b43274e64b45a95cfcd15fcd9660")
    private String code;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public RoleInfoVO(RoleEntity entity) {
        this.code = entity.getCode();
        this.name = entity.getName();
        this.createTime = entity.getCreateTime();
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "RoleInfoVO{"+
                "code='"+code+'\''+
                ", name='"+name+'\''+
                ", createTime="+createTime+
                '}';
    }
}
