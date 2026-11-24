package com.wety.demo.module.auditlog.vo;

import com.wety.demo.common.entity.OperationAuditLogEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * 操作日志返回VO（与登录日志分离的数据结构）
 *
 * @author wety
 */
@Schema(description = "操作日志信息")
public class OperationLogInfoVO {

    @Schema(description = "模块编码", example = "user")
    private String modelCode;

    @Schema(description = "所属模块", example = "用户管理")
    private String modelName;

    @Schema(description = "请求路径")
    private String path;

    @Schema(description = "登录账号的code", example = "admin")
    private String accountCode;

    @Schema(description = "登录账号登录名")
    private String accountLoginName;

    @Schema(description = "登录账号用户名")
    private String accountName;

    /** 操作人所属主部门编码。 */
    @Schema(description = "操作人所属主部门编码")
    private String deptCode;

    /** 操作人所属主部门名称。 */
    @Schema(description = "操作人所属主部门名称")
    private String deptName;

    @Schema(description = "客户端IP")
    private String clientIp;

    @Schema(description = "操作时间")
    private Date operationTime;

    @Schema(description = "操作类型", example = "create/update/delete/query")
    private String operationType;

    @Schema(description = "操作结果", example = "success/fail")
    private String result;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "处理耗时(ms)")
    private Long durationMs;

    @Schema(description = "关联的登录日志编码")
    private String loginLogCode;

    public OperationLogInfoVO(OperationAuditLogEntity entity) {
        this.modelCode = entity.getModelCode();
        this.modelName = entity.getModelName();
        this.path = entity.getPath();
        this.accountCode = entity.getAccountCode();
        this.accountLoginName = entity.getAccountLoginName();
        this.accountName = entity.getAccountName();
        this.deptCode = entity.getDeptCode();
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
