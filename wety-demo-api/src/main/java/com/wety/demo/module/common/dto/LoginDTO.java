package com.wety.demo.module.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 登录请求参数载体
 *
 * @author wety
 */
@ApiModel(description = "登录参数对象")
public class LoginDTO {
    /**
     * 用户登录名
     */
    @ApiModelProperty(value = "登录名", example = "admin")
    private String loginName;

    /**
     * 用户登录密码
     */
    @ApiModelProperty(value = "登录密码", example = "1fed0533d4a52b1e2cdd53155a9a27ed")
    private String password;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码", example = "1234")
    private String checkCode;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    @Override
    public String toString() {
        return "LoginDTO{"+
                "loginName='"+loginName+'\''+
                ", password='"+password+'\''+
                ", checkCode='"+checkCode+'\''+
                '}';
    }
}
