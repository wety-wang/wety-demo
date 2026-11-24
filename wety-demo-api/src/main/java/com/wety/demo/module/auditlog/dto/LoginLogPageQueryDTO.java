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
 * 登录日志分页查询参数
 *
 * @author wety
 */
@Schema(name = "LoginLogPageQueryDTO", description = "登录日志分页查询参数")
public class LoginLogPageQueryDTO extends PageableDTO implements CriteriaQueryObject {

    /**
     * 登录名，模糊查询
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "登录名, 模糊搜索")
    private String accountLoginName;

    /**
     * 用户姓名，模糊查询
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "用户名, 模糊搜索")
    private String accountName;

    /**
     * 操作结果，精确匹配
     */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    @Schema(description = "操作结果")
    private String result;

    /**
     * 客户端IP，模糊查询
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "客户端IP, 模糊搜索")
    private String clientIp;

    /**
     * 操作时间，模糊查询
     */
    @CriteriaExpression(expressionType = ExpressionType.BEWTEEN)
    @Schema(description = "操作时间, 模糊搜索")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private List<Date> operationTime;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public List<Date> getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(List<Date> operationTime) {
        this.operationTime = operationTime;
    }

    @Override
    public String toString() {
        return "LoginLogPageQueryDTO{"+
                "accountLoginName='"+accountLoginName+'\''+
                ", accountName='"+accountName+'\''+
                ", result='"+result+'\''+
                ", clientIp='"+clientIp+'\''+
                ", operationTime="+operationTime+
                "} "+super.toString();
    }
}
