package com.wety.demo.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 登录审计日志实体
 *
 * @author wety
 */
@ApiModel(description = "登录审计日志记录")
@Entity(name = "login_audit_log")
public class LoginAuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("登录日志唯一编码")
    @Column(name = "code")
    private String code;

    @ApiModelProperty(value = "登录账号的code", example = "admin")
    @Column(name = "account_code")
    private String accountCode;

    @ApiModelProperty("登录账号登录名")
    @Column(name = "account_login_name")
    private String accountLoginName;

    @ApiModelProperty("登录账号用户名")
    @Column(name = "account_name")
    private String accountName;

    @ApiModelProperty("登录/登出时间")
    @Column(name = "operation_time")
    private Date operationTime;

    @ApiModelProperty(value = "操作结果", example = "success/fail")
    @Column(name = "result")
    private String result;

    @ApiModelProperty(value = "操作描述")
    @Column(name = "description")
    private String description;

    @ApiModelProperty("客户端IP")
    @Column(name = "client_ip")
    private String clientIp;

    @ApiModelProperty("最后一次操作时间，用于会话活跃度记录")
    @Column(name = "last_operation_time")
    private Date lastOperationTime;

    @ApiModelProperty("主动退出登录时间")
    @Column(name = "logout_time")
    private Date logoutTime;

    @ApiModelProperty("User-Agent信息")
    @Column(name = "user_agent")
    private String userAgent;

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

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountLoginName() {
        return accountLoginName;
    }

    public void setAccountLoginName(String accountLoginName) {
        this.accountLoginName = accountLoginName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Date getLastOperationTime() {
        return lastOperationTime;
    }

    public void setLastOperationTime(Date lastOperationTime) {
        this.lastOperationTime = lastOperationTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "LoginAuditLogEntity{"+
                "id="+id+
                ", code='"+code+'\''+
                ", accountCode='"+accountCode+'\''+
                ", accountLoginName='"+accountLoginName+'\''+
                ", accountName='"+accountName+'\''+
                ", operationTime="+operationTime+
                ", result='"+result+'\''+
                ", description='"+description+'\''+
                ", clientIp='"+clientIp+'\''+
                ", lastOperationTime="+lastOperationTime+
                ", logoutTime="+logoutTime+
                ", userAgent='"+userAgent+'\''+
                '}';
    }
}
