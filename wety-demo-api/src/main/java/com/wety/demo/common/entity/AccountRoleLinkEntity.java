package com.wety.demo.common.entity;

import javax.persistence.*;

/**
 * @author wety
 */
@Entity
@Table(name = "account_role_link")
public class AccountRoleLinkEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_code")
    private String accountCode;

    @Column(name = "role_code")
    private String roleCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public String toString() {
        return "AccountRoleLinkEntity{" +
                "id=" + id +
                ", accountCode='" + accountCode + '\'' +
                ", roleCode='" + roleCode + '\'' +
                '}';
    }
}
