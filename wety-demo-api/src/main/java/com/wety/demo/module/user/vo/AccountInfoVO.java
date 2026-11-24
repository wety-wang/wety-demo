package com.wety.demo.module.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.entity.AccountEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author wety
 */
@ApiModel(description = "账户信息")
public class AccountInfoVO {

    @ApiModelProperty(value = "账户唯一识别，修改操作时必须带着")
    private String code;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @ApiModelProperty(value = "账户类型:admin表示系统管理员；normal表示普通账户")
    private String type;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public AccountInfoVO(AccountEntity entity) {
        this.code = entity.getCode();
        this.loginName = entity.getLoginName();
        this.userName = entity.getUserName();
        this.email = entity.getEmail();
        this.phoneNumber = entity.getPhoneNumber();
        this.type = entity.getType();
        this.createTime = entity.getCreateTime();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AccountInfoVO{"+
                "code='"+code+'\''+
                ", loginName='"+loginName+'\''+
                ", userName='"+userName+'\''+
                ", email='"+email+'\''+
                ", phoneNumber='"+phoneNumber+'\''+
                ", type='"+type+'\''+
                ", createTime="+createTime+
                '}';
    }
}
