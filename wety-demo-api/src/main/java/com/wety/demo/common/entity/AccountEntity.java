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

    /** 主部门业务编码（冗余字段，便于列表展示与数据权限过滤）。 */
    @Column(name = "primary_dept_code", length = 40)
    @ApiModelProperty(value = "主部门业务编码")
    private String primaryDeptCode;

    /** 是否内置超级管理员：0-否，1-是。 */
    @Column(name = "super_admin")
    @ApiModelProperty(value = "是否内置超级管理员：0-否，1-是")
    private Integer superAdmin;

    /** 创建人业务编码。 */
    @Column(name = "create_user", length = 40)
    @ApiModelProperty(value = "创建人业务编码")
    private String createUser;

    /** 是否删除：0-否，1-是。 */
    @Column(name = "deleted")
    @ApiModelProperty(value = "是否删除：0-否，1-是")
    private Integer deleted;

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
        this.primaryDeptCode = entity.getPrimaryDeptCode();
        this.superAdmin = entity.getSuperAdmin();
        this.createUser = entity.getCreateUser();
        this.deleted = entity.getDeleted();
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

    public String getPrimaryDeptCode() {
        return primaryDeptCode;
    }

    public void setPrimaryDeptCode(String primaryDeptCode) {
        this.primaryDeptCode = primaryDeptCode;
    }

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
                ", disabled=" + disabled +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", primaryDeptCode='" + primaryDeptCode + '\'' +
                ", superAdmin=" + superAdmin +
                ", createUser='" + createUser + '\'' +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
