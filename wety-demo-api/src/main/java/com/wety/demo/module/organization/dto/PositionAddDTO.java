package com.wety.demo.module.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 新增岗位参数。
 */
@Schema(name = "PositionAddDTO", description = "新增岗位参数")
public class PositionAddDTO {

    /** 岗位标识编码（未删除记录中唯一）。 */
    @Schema(description = "岗位标识编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String postCode;

    /** 岗位名称（未删除记录中唯一）。 */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 排序号，默认 0。 */
    @Schema(description = "排序号，默认0")
    private Integer sortOrder;

    /** 状态：1-启用（默认），0-停用。 */
    @Schema(description = "状态：1-启用（默认），0-停用")
    private Integer status;

    /** 备注。 */
    @Schema(description = "备注")
    private String remark;

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

    @Override
    public String toString() {
        return "PositionAddDTO{" +
                "postCode='" + postCode + '\'' +
                ", name='" + name + '\'' +
                ", sortOrder=" + sortOrder +
                ", status=" + status +
                '}';
    }
}
