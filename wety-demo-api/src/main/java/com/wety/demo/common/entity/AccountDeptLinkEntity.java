package com.wety.demo.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 账号与部门关联实体。
 *
 * <p>通过 primaryFlag 与 leaderFlag 兼容一人多部门及部门负责人语义。</p>
 */
@Entity
@Table(name = "account_dept_link")
@ApiModel(description = "账号部门关联表")
public class AccountDeptLinkEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "数据库主键")
    private Long id;

    /** 关联记录唯一编码。 */
    @Column(name = "code", nullable = false, length = 40)
    @ApiModelProperty(value = "关联记录唯一编码")
    private String code;

    /** 账号业务编码。 */
    @Column(name = "account_code", nullable = false, length = 40)
    @ApiModelProperty(value = "账号业务编码")
    private String accountCode;

    /** 部门业务编码。 */
    @Column(name = "dept_code", nullable = false, length = 40)
    @ApiModelProperty(value = "部门业务编码")
    private String deptCode;

    /** 是否主部门：1-是，0-否。 */
    @Column(name = "primary_flag", nullable = false)
    @ApiModelProperty(value = "是否主部门：1-是，0-否")
    private Integer primaryFlag;

    /** 是否部门负责人：1-是，0-否。 */
    @Column(name = "leader_flag", nullable = false)
    @ApiModelProperty(value = "是否部门负责人：1-是，0-否")
    private Integer leaderFlag;

    /** 创建时间。 */
    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /** 更新时间。 */
    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /** 是否删除：0-否，1-是。 */
    @Column(name = "deleted", nullable = false)
    @ApiModelProperty(value = "是否删除：0-否，1-是")
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

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Integer getPrimaryFlag() {
        return primaryFlag;
    }

    public void setPrimaryFlag(Integer primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    public Integer getLeaderFlag() {
        return leaderFlag;
    }

    public void setLeaderFlag(Integer leaderFlag) {
        this.leaderFlag = leaderFlag;
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
        return "AccountDeptLinkEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", deptCode='" + deptCode + '\'' +
                ", primaryFlag=" + primaryFlag +
                ", leaderFlag=" + leaderFlag +
                ", deleted=" + deleted +
                '}';
    }
}
