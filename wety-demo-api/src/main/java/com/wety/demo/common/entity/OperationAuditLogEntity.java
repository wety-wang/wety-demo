package com.wety.demo.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * 业务操作审计日志实体
 *
 * @author wety
 */
@ApiModel(description = "操作审计日志记录")
@Entity(name = "operation_audit_log")
public class OperationAuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("操作日志唯一编码")
    @Column(name = "code")
    private String code;

    @ApiModelProperty(value = "模块编码", example = "user")
    @Column(name = "model_code")
    private String modelCode;

    @ApiModelProperty(value = "所属模块", example = "用户管理")
    @Column(name = "model_name")
    private String modelName;

    @ApiModelProperty("请求路径")
    @Column(name = "path")
    private String path;

    @ApiModelProperty("HTTP方法")
    @Column(name = "http_method")
    private String httpMethod;

    @ApiModelProperty("Query字符串")
    @Column(name = "query_string")
    private String queryString;

    @ApiModelProperty("User-Agent")
    @Column(name = "user_agent")
    private String userAgent;

    @ApiModelProperty("Referer")
    @Column(name = "referer")
    private String referer;

    @ApiModelProperty("请求ID")
    @Column(name = "request_id")
    private String requestId;

    @ApiModelProperty(value = "登录账号的code", example = "admin")
    @Column(name = "account_code")
    private String accountCode;

    @ApiModelProperty("登录账号登录名")
    @Column(name = "account_login_name")
    private String accountLoginName;

    @ApiModelProperty("登录账号用户名")
    @Column(name = "account_name")
    private String accountName;

    @ApiModelProperty("客户端IP")
    @Column(name = "client_ip")
    private String clientIp;

    @ApiModelProperty("操作时间")
    @Column(name = "operation_time")
    private Date operationTime;

    @ApiModelProperty(value = "操作类型", example = "create/update/delete/query")
    @Column(name = "operation_type")
    private String operationType;

    @ApiModelProperty(value = "操作结果", example = "success/fail")
    @Column(name = "result")
    private String result;

    @ApiModelProperty(value = "操作描述")
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @ApiModelProperty("处理耗时(ms)")
    @Column(name = "duration_ms")
    private Long durationMs;

    @ApiModelProperty("关联的登录日志编码")
    @Column(name = "login_log_code")
    private String loginLogCode;

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

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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
        return "OperationAuditLogEntity{"+
                "id="+id+
                ", code='"+code+'\''+
                ", modelCode='"+modelCode+'\''+
                ", modelName='"+modelName+'\''+
                ", path='"+path+'\''+
                ", httpMethod='"+httpMethod+'\''+
                ", queryString='"+queryString+'\''+
                ", userAgent='"+userAgent+'\''+
                ", referer='"+referer+'\''+
                ", requestId='"+requestId+'\''+
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
