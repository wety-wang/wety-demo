package com.wety.demo.module.common.vo;

import com.wety.demo.common.entity.AccountEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wety
 */
@ApiModel(description = "账户信息vo")
public class AccountVo {

    @ApiModelProperty(value = "账号code")
    private String accountCode;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "token")
    private String token;

    public AccountVo(AccountEntity accountEntity, String token) {
        this.accountCode = accountEntity.getCode();
        this.loginName = accountEntity.getLoginName();
        this.userName = accountEntity.getUserName();
        this.token = token;
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

    @Override
    public String toString() {
        return "AccountVo{"+
                "accountCode='"+accountCode+'\''+
                ", loginName='"+loginName+'\''+
                ", userName='"+userName+'\''+
                ", token='"+token+'\''+
                '}';
    }
}
