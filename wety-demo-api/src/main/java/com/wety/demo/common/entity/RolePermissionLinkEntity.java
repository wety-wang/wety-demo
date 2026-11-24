package com.wety.demo.common.entity;

import jakarta.persistence.*;

/**
 * @author wety
 */
@Entity
@Table(name = "role_permission_link")
public class RolePermissionLinkEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "permission_code")
    private String permissionCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    @Override
    public String toString() {
        return "RolePermissionLinkEntity{"+
                "id="+id+
                ", roleCode='"+roleCode+'\''+
                ", permissionCode='"+permissionCode+'\''+
                '}';
    }
}
