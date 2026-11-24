package com.wety.demo.module.organization.dto;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.common.jpa.ExpressionType;

import java.util.Collection;

/**
 * 账号动态条件查询对象。
 *
 * <p>配合 {@code BaseRepositoryImpl} 执行 JPA Criteria 动态查询，并自动衔接 {@code @DataScope} 数据权限过滤。</p>
 */
public class AccountCriteriaQuery implements CriteriaQueryObject {

    /** 软删除标记（固定过滤未删除记录）。 */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    private Integer deleted = GlobalConst.NOT;

    /** 姓名模糊搜索。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    private String userName;

    /** 登录名模糊搜索。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    private String loginName;

    /** 手机号模糊搜索。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    private String phoneNumber;

    /** 邮箱模糊搜索。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    private String email;

    /** 状态筛选：0-启用，1-禁用。 */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    private Integer disabled;

    /** 主部门编码集合（部门树节点及其所有子孙部门）。 */
    @CriteriaExpression(propertyName = "primaryDeptCode", expressionType = ExpressionType.IN)
    private Collection<String> deptCodes;

    /** 账号编码集合（按岗位或角色筛选后的交集）。 */
    @CriteriaExpression(propertyName = "code", expressionType = ExpressionType.IN)
    private Collection<String> accountCodes;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Collection<String> getDeptCodes() {
        return deptCodes;
    }

    public void setDeptCodes(Collection<String> deptCodes) {
        this.deptCodes = deptCodes;
    }

    public Collection<String> getAccountCodes() {
        return accountCodes;
    }

    public void setAccountCodes(Collection<String> accountCodes) {
        this.accountCodes = accountCodes;
    }
}
