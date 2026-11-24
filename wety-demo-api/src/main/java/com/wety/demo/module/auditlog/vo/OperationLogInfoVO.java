package com.wety.demo.module.auditlog.vo;

import com.wety.demo.common.entity.OperationAuditLogEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 操作日志返回VO（与登录日志分离的数据结构）
 *
 * @author wety
 */
@ApiModel(description = "操作日志信息")
public class OperationLogInfoVO {

    @ApiModelProperty(value = "模块编码", example = "user")
    private String modelCode;

    @ApiModelProperty(value = "所属模块", example = "用户管理")
    private String modelName;

    @ApiModelProperty("请求路径")
    private String path;

    @ApiModelProperty(value = "登录账号的code", example = "admin")
    private String accountCode;

    @ApiModelProperty("登录账号登录名")
    private String accountLoginName;

    @ApiModelProperty("登录账号用户名")
    private String accountName;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("操作时间")
    private Date operationTime;

    @ApiModelProperty(value = "操作类型", example = "create/update/delete/query")
    private String operationType;

    @ApiModelProperty(value = "操作结果", example = "success/fail")
    private String result;

    @ApiModelProperty(value = "操作描述")
    private String description;

    @ApiModelProperty("处理耗时(ms)")
    private Long durationMs;

    @ApiModelProperty("关联的登录日志编码")
    private String loginLogCode;

    public OperationLogInfoVO(OperationAuditLogEntity entity) {
        this.modelCode = entity.getModelCode();
        this.modelName = entity.getModelName();
        this.path = entity.getPath();
        this.accountCode = entity.getAccountCode();
        this.accountLoginName = entity.getAccountLoginName();
        this.accountName = entity.getAccountName();
        this.clientIp = entity.getClientIp();
        this.operationTime = entity.getOperationTime();
        this.operationType = entity.getOperationType();
        this.result = entity.getResult();
        this.description = entity.getDescription();
        this.durationMs = entity.getDurationMs();
        this.loginLogCode = entity.getLoginLogCode();
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getLoginLogCode() {
        return loginLogCode;
    }

    public void setLoginLogCode(String loginLogCode) {
        this.loginLogCode = loginLogCode;
    }

    @Override
    public String toString() {
        return "OperationLogInfoVO{"+
                "modelCode='"+modelCode+'\''+
                ", modelName='"+modelName+'\''+
                ", path='"+path+'\''+
                ", accountCode='"+accountCode+'\''+
                ", accountLoginName='"+accountLoginName+'\''+
                ", accountName='"+accountName+'\''+
                ", clientIp='"+clientIp+'\''+
                ", operationTime="+operationTime+
                ", operationType='"+operationType+'\''+
                ", result='"+result+'\''+
                ", description='"+description+'\''+
                ", durationMs="+durationMs+
                ", loginLogCode='"+loginLogCode+'\''+
                '}';
    }
}
