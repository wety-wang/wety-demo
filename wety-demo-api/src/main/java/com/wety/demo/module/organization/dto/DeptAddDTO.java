package com.wety.demo.module.organization.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 新增部门参数。
 */
@ApiModel(value = "DeptAddDTO", description = "新增部门参数")
public class DeptAddDTO {

    /** 父部门业务编码（不可为空，且父部门必须存在且未删除）。 */
    @ApiModelProperty(value = "父部门编码", required = true)
    private String parentCode;

    /** 部门名称（同父级下不可重名，最大 100 字符）。 */
    @ApiModelProperty(value = "部门名称", required = true)
    private String name;

    /** 排序号，默认 0。 */
    @ApiModelProperty(value = "排序号，默认0")
    private Integer sortOrder;

    /** 部门负责人账号编码。 */
    @ApiModelProperty(value = "部门负责人账号编码")
    private String leaderAccountCode;

    /** 联系电话。 */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /** 联系邮箱。 */
    @ApiModelProperty(value = "联系邮箱")
    private String email;

    /** 状态：1-启用（默认），0-停用。 */
    @ApiModelProperty(value = "状态：1-启用（默认），0-停用")
    private Integer status;

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
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

    @Override
    public String toString() {
        return "DeptAddDTO{" +
                "parentCode='" + parentCode + '\'' +
                ", name='" + name + '\'' +
                ", sortOrder=" + sortOrder +
                ", leaderAccountCode='" + leaderAccountCode + '\'' +
                ", status=" + status +
                '}';
    }
}
