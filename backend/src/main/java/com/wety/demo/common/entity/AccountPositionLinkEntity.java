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
 * 账号与岗位关联实体。
 */
@Entity
@Table(name = "account_position_link")
@Schema(description = "账号岗位关联表")
public class AccountPositionLinkEntity implements Serializable {

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

    /** 账号业务编码。 */
    @Column(name = "account_code", nullable = false, length = 40)
    @Schema(description = "账号业务编码")
    private String accountCode;

    /** 岗位业务编码。 */
    @Column(name = "position_code", nullable = false, length = 40)
    @Schema(description = "岗位业务编码")
    private String positionCode;

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

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
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
        return "AccountPositionLinkEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", accountCode='" + accountCode + '\'' +
                ", positionCode='" + positionCode + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
