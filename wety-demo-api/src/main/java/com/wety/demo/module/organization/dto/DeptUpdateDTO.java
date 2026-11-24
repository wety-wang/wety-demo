package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 修改部门参数。
 */
@Schema(name = "DeptUpdateDTO", description = "修改部门参数")
public class DeptUpdateDTO {

    /** 部门业务编码。 */
    @Schema(description = "部门业务编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 父部门业务编码（不可设为自身或自身子孙节点）。 */
    @Schema(description = "父部门编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String parentCode;

    /** 部门名称。 */
    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 排序号。 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 负责人账号编码。 */
    @Schema(description = "负责人账号编码")
    private String leaderAccountCode;

    /** 联系电话。 */
    @Schema(description = "联系电话")
    private String phone;

    /** 联系邮箱。 */
    @Schema(description = "联系邮箱")
    private String email;

    /** 状态：1-启用，0-停用。 */
    @Schema(description = "状态：1-启用，0-停用")
    private Integer status;

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
        return "DeptUpdateDTO{" +
                "code='" + code + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", name='" + name + '\'' +
                ", sortOrder=" + sortOrder +
                ", leaderAccountCode='" + leaderAccountCode + '\'' +
                ", status=" + status +
                '}';
    }
}
