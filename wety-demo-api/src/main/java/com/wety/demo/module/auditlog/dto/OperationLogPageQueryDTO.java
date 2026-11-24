package com.wety.demo.module.auditlog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.dto.PageableDTO;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.common.jpa.ExpressionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

/**
 * 操作日志分页查询参数
 *
 * @author wety
 */
@Schema(name = "OperationLogPageQueryDTO", description = "操作日志分页查询参数")
public class OperationLogPageQueryDTO extends PageableDTO implements CriteriaQueryObject {

    /**
     * 模块编码
     */
    @CriteriaExpression(propertyName = "modelCode", expressionType = ExpressionType.IN)
    @Schema(description = "模块编码")
    private List<String> modelCodes;

    /**
     * 操作人（多选）
     */
    @CriteriaExpression(propertyName = "accountCode", expressionType = ExpressionType.IN)
    @Schema(description = "操作人")
    private List<String> accountCodes;

    /**
     * 操作类型（多选）
     */
    @CriteriaExpression(propertyName = "operationType", expressionType = ExpressionType.IN)
    @Schema(description = "操作类型")
    private List<String> operationTypes;

    /**
     * 操作结果，精确匹配
     */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    @Schema(description = "操作结果")
    private String result;

    /**
     * 操作时间范围，区间查询
     */
    @CriteriaExpression(expressionType = ExpressionType.BEWTEEN)
    @Schema(description = "操作时间范围")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private List<Date> operationTime;

    /**
     * 客户端IP，模糊查询
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "客户端IP, 模糊搜索")
    private String clientIp;

    public List<String> getModelCodes() {
        return modelCodes;
    }

    public void setModelCodes(List<String> modelCodes) {
        this.modelCodes = modelCodes;
    }

    public List<String> getAccountCodes() {
        return accountCodes;
    }

    public void setAccountCodes(List<String> accountCodes) {
        this.accountCodes = accountCodes;
    }

    public List<String> getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(List<String> operationTypes) {
        this.operationTypes = operationTypes;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Date> getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(List<Date> operationTime) {
        this.operationTime = operationTime;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        return "OperationLogPageQueryDTO{"+
                "modelCodes="+modelCodes+
                ", accountCodes="+accountCodes+
                ", operationTypes="+operationTypes+
                ", result='"+result+'\''+
                ", operationTime="+operationTime+
                ", clientIp='"+clientIp+'\''+
                "} "+super.toString();
    }
}
