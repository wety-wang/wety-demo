package com.wety.demo.module.user.dto;

import com.wety.demo.common.dto.PageableDTO;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.common.jpa.ExpressionType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author wety
 */
@Schema(name = "UserPageQueryDTO", description = "用户分页查询参数")
public class UserPageQueryDTO extends PageableDTO implements CriteriaQueryObject {

    /**
     * 姓名
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "姓名,模糊搜索")
    private String userName;

    /**
     * 登录名
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "登录名,模糊搜索")
    private String loginName;

    /**
     * 邮箱
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "邮箱,模糊搜索")
    private String email;

    /**
     * 手机号
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @Schema(description = "手机号,模糊搜索")
    private String phoneNumber;

    /**
     * 类型
     */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    @Schema(description = "类型")
    private String type;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

    @Override
    public String toString() {
        return "UserPageQueryDTO{"+
                "userName='"+userName+'\''+
                ", loginName='"+loginName+'\''+
                ", email='"+email+'\''+
                ", phoneNumber='"+phoneNumber+'\''+
                ", type='"+type+'\''+
                "} "+super.toString();
    }
}
