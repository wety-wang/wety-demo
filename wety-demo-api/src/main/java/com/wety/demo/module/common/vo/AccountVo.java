package com.wety.demo.module.common.vo;

import com.wety.demo.common.entity.AccountEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 登录返回账户信息 VO。
 *
 * @author wety
 */
@ApiModel(description = "账户信息vo")
public class AccountVo {

    /** 账号业务编码。 */
    @ApiModelProperty(value = "账号code")
    private String accountCode;

    /** 登录名。 */
    @ApiModelProperty(value = "登录名")
    private String loginName;

    /** 用户姓名。 */
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    /** 登录会话 Token。 */
    @ApiModelProperty(value = "token")
    private String token;

    /** 是否内置超级管理员：1-是，0-否。 */
    @ApiModelProperty(value = "是否内置超级管理员：1-是，0-否")
    private Integer superAdmin;

    /** 主部门业务编码。 */
    @ApiModelProperty(value = "主部门编码")
    private String primaryDeptCode;

    /** 主部门名称。 */
    @ApiModelProperty(value = "主部门名称")
    private String primaryDeptName;

    public AccountVo(AccountEntity accountEntity, String token) {
        this.accountCode = accountEntity.getCode();
        this.loginName = accountEntity.getLoginName();
        this.userName = accountEntity.getUserName();
        this.token = token;
        this.superAdmin = accountEntity.getSuperAdmin();
        this.primaryDeptCode = accountEntity.getPrimaryDeptCode();
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getPrimaryDeptCode() {
        return primaryDeptCode;
    }

    public void setPrimaryDeptCode(String primaryDeptCode) {
        this.primaryDeptCode = primaryDeptCode;
    }

    public String getPrimaryDeptName() {
        return primaryDeptName;
    }

    public void setPrimaryDeptName(String primaryDeptName) {
        this.primaryDeptName = primaryDeptName;
    }

    @Override
    public String toString() {
        return "AccountVo{" +
                "accountCode='" + accountCode + '\'' +
                ", loginName='" + loginName + '\'' +
                ", userName='" + userName + '\'' +
                ", token='" + token + '\'' +
                ", superAdmin=" + superAdmin +
                ", primaryDeptCode='" + primaryDeptCode + '\'' +
                ", primaryDeptName='" + primaryDeptName + '\'' +
                '}';
    }
}
