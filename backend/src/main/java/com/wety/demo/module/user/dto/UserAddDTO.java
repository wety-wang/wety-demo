package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
@Schema(name = "UserAddDTO", description = "新增用户参数")
public class UserAddDTO {

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "登录名")
    private String loginName;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "手机号")
    private String phoneNumber;

    @Schema(description = "账户类型:admin表示系统管理员；normal表示普通账户")
    private String type;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserUpdateDTO{" +
                "userName='" + userName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
