package com.wety.demo.module.auditlog.vo;

import com.wety.demo.common.entity.LoginAuditLogEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 登录日志返回VO
 *
 * @author wety
 */
@ApiModel(description = "登录日志信息")
public class LoginLogInfoVO {

    @ApiModelProperty("账户code")
    private String accountCode;

    @ApiModelProperty("账户登录名")
    private String accountLoginName;

    @ApiModelProperty("账户姓名")
    private String accountName;

    /** 登录账号所属主部门编码。 */
    @ApiModelProperty("登录账号所属主部门编码")
    private String deptCode;

    /** 登录账号所属主部门名称。 */
    @ApiModelProperty("登录账号所属主部门名称")
    private String deptName;

    @ApiModelProperty("登录时间")
    private Date operationTime;

    @ApiModelProperty("登录结果")
    private String result;

    @ApiModelProperty("登录描述")
    private String description;

    @ApiModelProperty("最后一次操作时间")
    private Date lastOperationTime;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("退出登录时间")
    private Date logoutTime;

    @ApiModelProperty("User-Agent")
    private String userAgent;

    /**
     * 通过登录日志实体构造VO
     */
    public LoginLogInfoVO(LoginAuditLogEntity entity) {
        this.accountCode = entity.getAccountCode();
        this.accountLoginName = entity.getAccountLoginName();
        this.accountName = entity.getAccountName();
        this.deptCode = entity.getDeptCode();
        this.operationTime = entity.getOperationTime();
        this.result = entity.getResult();
        this.description = entity.getDescription();
        this.lastOperationTime = entity.getLastOperationTime();
        this.clientIp = entity.getClientIp();
        this.logoutTime = entity.getLogoutTime();
        this.userAgent = entity.getUserAgent();
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

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "LoginLogInfoVO{"+
                "accountCode='"+accountCode+'\''+
                ", accountLoginName='"+accountLoginName+'\''+
                ", accountName='"+accountName+'\''+
                ", operationTime="+operationTime+
                ", result='"+result+'\''+
                ", description='"+description+'\''+
                ", lastOperationTime="+lastOperationTime+
                ", clientIp='"+clientIp+'\''+
                ", logoutTime="+logoutTime+
                ", userAgent='"+userAgent+'\''+
                '}';
    }
}
