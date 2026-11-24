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
 * 部门信息实体。
 *
 * <p>通过 parentCode 与 ancestors 物化路径共同维护树形结构，便于高效查询子孙部门。</p>
 */
@Entity
@Table(name = "dept")
@Schema(description = "部门信息表")
public class DeptEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "数据库主键")
    private Long id;

    /** 部门唯一业务编码。 */
    @Column(name = "code", nullable = false, length = 40)
    @Schema(description = "部门唯一业务编码")
    private String code;

    /** 父部门业务编码，根部门为 0。 */
    @Column(name = "parent_code", nullable = false, length = 40)
    @Schema(description = "父部门业务编码，根部门为0")
    private String parentCode;

    /** 祖级物化路径，逗号分隔，如 0, 或 0,dept-root,。 */
    @Column(name = "ancestors", nullable = false, length = 500)
    @Schema(description = "祖级物化路径")
    private String ancestors;

    /** 部门名称。 */
    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "部门名称")
    private String name;

    /** 同级排序号，升序排列。 */
    @Column(name = "sort_order", nullable = false)
    @Schema(description = "同级排序号")
    private Integer sortOrder;

    /** 部门负责人账号编码。 */
    @Column(name = "leader_account_code", length = 40)
    @Schema(description = "部门负责人账号编码")
    private String leaderAccountCode;

    /** 联系电话。 */
    @Column(name = "phone", length = 32)
    @Schema(description = "联系电话")
    private String phone;

    /** 联系邮箱。 */
    @Column(name = "email", length = 100)
    @Schema(description = "联系邮箱")
    private String email;

    /** 状态：1-启用，0-停用。 */
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    @Schema(description = "状态：1-启用，0-停用")
    private Integer status;

    /** 创建人业务编码。 */
    @Column(name = "create_user", length = 40)
    @Schema(description = "创建人业务编码")
    private String createUser;

    /** 更新人业务编码。 */
    @Column(name = "update_user", length = 40)
    @Schema(description = "更新人业务编码")
    private String updateUser;

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

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getLeaderAccountCode() {
        return leaderAccountCode;
    }

    public void setLeaderAccountCode(String leaderAccountCode) {
        this.leaderAccountCode = leaderAccountCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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
        return "DeptEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", ancestors='" + ancestors + '\'' +
                ", name='" + name + '\'' +
                ", sortOrder=" + sortOrder +
                ", leaderAccountCode='" + leaderAccountCode + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                '}';
    }
}
