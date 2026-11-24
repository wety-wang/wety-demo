package com.wety.demo.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wety
 */
@ApiModel(description = "审计日志记录")
@Entity(name = "audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "登录账号的code", example = "admin")
    @Column(name = "account_code")
    private String accountCode;

    @ApiModelProperty("登录账号登录名")
    @Column(name = "account_login_name")
    private String accountLoginName;

    @ApiModelProperty("登录账号用户名")
    @Column(name = "account_name")
    private String accountName;

    @ApiModelProperty(hidden = true)
    @Column(name = "audit_type")
    private String auditType;

    @ApiModelProperty("时间")
    @Column(name = "operation_time")
    private Date operationTime;

    @ApiModelProperty(value = "操作结果", example = "登录成功")
    @Column(name = "result")
    private String result;

    @ApiModelProperty(value = "操作记录", example = "登录成功")
    @Column(name = "description")
    private String description;

    @ApiModelProperty(value = "操作数据")
    @Column(name = "operation_data")
    private String operationData;

    @ApiModelProperty("最后一次操作时间，登陆日志中会记录")
    @Column(name = "last_operation_time")
    private Date lastOperationTime;

    @ApiModelProperty("IP")
    @Column(name = "client_ip")
    private String clientIp;

    @ApiModelProperty("主动退出登陆时间，登陆日志中会记录")
    @Column(name = "logout_time")
    private Date logoutTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
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

    public String getOperationData() {
        return operationData;
    }

    public void setOperationData(String operationData) {
        this.operationData = operationData;
    }

    public Date getLastOperationTime() {
        return lastOperationTime;
    }

    public void setLastOperationTime(Date lastOperationTime) {
        this.lastOperationTime = lastOperationTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }
}
