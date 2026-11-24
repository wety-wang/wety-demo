package com.wety.demo.module.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.entity.AccountEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 账户基础信息 VO。
 *
 * @author wety
 */
@ApiModel(description = "账户信息")
public class AccountInfoVO {

    /** 账号业务编码。 */
    @ApiModelProperty(value = "账户唯一识别，修改操作时必须带着")
    private String code;

    /** 登录名。 */
    @ApiModelProperty(value = "登录名")
    private String loginName;

    /** 用户姓名。 */
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    /** 邮箱地址。 */
    @ApiModelProperty(value = "邮箱地址")
    private String email;

    /** 手机号。 */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /** 账户类型：admin 表示系统管理员，normal 表示普通账户。 */
    @ApiModelProperty(value = "账户类型:admin表示系统管理员；normal表示普通账户")
    private String type;

    /** 是否内置超级管理员：1-是，0-否。 */
    @ApiModelProperty(value = "是否内置超级管理员：1-是，0-否")
    private Integer superAdmin;

    /** 主部门业务编码。 */
    @ApiModelProperty(value = "主部门编码")
    private String primaryDeptCode;

    /** 主部门名称。 */
    @ApiModelProperty(value = "主部门名称")
    private String primaryDeptName;

    /** 创建时间。 */
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
        this.superAdmin = entity.getSuperAdmin();
        this.primaryDeptCode = entity.getPrimaryDeptCode();
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

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getPrimaryDeptCode() {
        return primaryDeptCode;
    }

    public void setPrimaryDeptCode(String primaryDeptCode) {
        this.primaryDeptCode = primaryDeptCode;
    }

    public String getPrimaryDeptName() {
        return primaryDeptName;
    }

    public void setPrimaryDeptName(String primaryDeptName) {
        this.primaryDeptName = primaryDeptName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AccountInfoVO{" +
                "code='" + code + '\'' +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", superAdmin=" + superAdmin +
                ", primaryDeptCode='" + primaryDeptCode + '\'' +
                ", primaryDeptName='" + primaryDeptName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
