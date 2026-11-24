package com.wety.demo.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色自定义数据权限部门关联实体。
 *
 * <p>仅当角色数据权限范围为“自定义部门（CUSTOM）”时生效。</p>
 */
@Entity
@Table(name = "role_dept_link")
@Schema(description = "角色自定义数据权限部门关联表")
public class RoleDeptLinkEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "数据库主键")
    private Long id;

    /** 关联记录唯一编码。 */
    @Column(name = "code", nullable = false, length = 40)
    @Schema(description = "关联记录唯一编码")
    private String code;

    /** 角色业务编码。 */
    @Column(name = "role_code", nullable = false, length = 40)
    @Schema(description = "角色业务编码")
    private String roleCode;

    /** 部门业务编码。 */
    @Column(name = "dept_code", nullable = false, length = 40)
    @Schema(description = "部门业务编码")
    private String deptCode;

    /** 创建时间。 */
    @Column(name = "create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新时间。 */
    @Column(name = "update_time")
    @Schema(description = "更新时间")
    private Date updateTime;

    /** 是否删除：0-否，1-是。 */
    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT")
    @Schema(description = "是否删除：0-否，1-是")
    private Integer deleted;

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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "RoleDeptLinkEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", deptCode='" + deptCode + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
