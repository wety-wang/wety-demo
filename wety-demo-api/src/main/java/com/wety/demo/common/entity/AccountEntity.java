package com.wety.demo.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wety
 */
@Entity
@Table(name = "account")
@ApiModel(description = "账户信息表")
public class AccountEntity implements Serializable {

    private static final long serialVersionUID = 5821201569490544457L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "id")
    private Long id;

    @Column(name = "code")
    @ApiModelProperty(value = "账户唯一识别，修改操作时必须带着")
    private String code;

    @Column(name = "login_name")
    @ApiModelProperty(value = "登录名")
    private String loginName;

    @Column(name = "user_name")
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @Column(name = "password")
    @ApiModelProperty(value = "密码")
    private String password;

    @Column(name = "disabled")
    @ApiModelProperty(value = "是否禁用：0未禁用 1已禁用")
    private Integer disabled;

    @Column(name = "email")
    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @Column(name = "phone_number")
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    @Column(name = "type")
    @ApiModelProperty(value = "账户类型:admin表示系统管理员；normal表示普通账户")
    private String type;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    public AccountEntity() {
    }

    public AccountEntity(AccountEntity entity) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.loginName = entity.getLoginName();
        this.userName = entity.getUserName();
        this.password = entity.getPassword();
        this.disabled = entity.getDisabled();
        this.email = entity.getEmail();
        this.phoneNumber = entity.getPhoneNumber();
        this.type = entity.getType();
        this.createTime = entity.getCreateTime();
        this.updateTime = entity.getUpdateTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", disabled=" + disabled +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
