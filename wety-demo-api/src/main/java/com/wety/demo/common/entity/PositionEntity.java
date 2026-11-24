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
 * 岗位信息实体。
 */
@Entity
@Table(name = "position")
@ApiModel(description = "岗位信息表")
public class PositionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "数据库主键")
    private Long id;

    /** 岗位唯一业务编码。 */
    @Column(name = "code", nullable = false, length = 40)
    @ApiModelProperty(value = "岗位唯一业务编码")
    private String code;

    /** 岗位标识编码（如 DEV、PM）。 */
    @Column(name = "post_code", nullable = false, length = 64)
    @ApiModelProperty(value = "岗位标识编码")
    private String postCode;

    /** 岗位名称。 */
    @Column(name = "name", nullable = false, length = 100)
    @ApiModelProperty(value = "岗位名称")
    private String name;

    /** 排序号，升序排列。 */
    @Column(name = "sort_order", nullable = false)
    @ApiModelProperty(value = "排序号")
    private Integer sortOrder;

    /** 状态：1-启用，0-停用。 */
    @Column(name = "status", nullable = false)
    @ApiModelProperty(value = "状态：1-启用，0-停用")
    private Integer status;

    /** 岗位备注。 */
    @Column(name = "remark", length = 500)
    @ApiModelProperty(value = "岗位备注")
    private String remark;

    /** 创建人业务编码。 */
    @Column(name = "create_user", length = 40)
    @ApiModelProperty(value = "创建人业务编码")
    private String createUser;

    /** 更新人业务编码。 */
    @Column(name = "update_user", length = 40)
    @ApiModelProperty(value = "更新人业务编码")
    private String updateUser;

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

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return "PositionEntity{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", postCode='" + postCode + '\'' +
                ", name='" + name + '\'' +
                ", sortOrder=" + sortOrder +
                ", status=" + status +
                ", deleted=" + deleted +
                '}';
    }
}
