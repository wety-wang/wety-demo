package com.wety.demo.module.organization.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.entity.PositionEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 岗位信息返回模型。
 */
@ApiModel(value = "PositionInfoVO", description = "岗位信息")
public class PositionInfoVO {

    /** 岗位业务编码。 */
    @ApiModelProperty(value = "岗位业务编码")
    private String code;

    /** 岗位标识编码。 */
    @ApiModelProperty(value = "岗位标识编码")
    private String postCode;

    /** 岗位名称。 */
    @ApiModelProperty(value = "岗位名称")
    private String name;

    /** 排序号。 */
    @ApiModelProperty(value = "排序号")
    private Integer sortOrder;

    /** 状态：1-启用，0-停用。 */
    @ApiModelProperty(value = "状态：1-启用，0-停用")
    private Integer status;

    /** 备注。 */
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public PositionInfoVO() {
    }

    /**
     * 基于岗位实体构造展示模型。
     *
     * @param entity 岗位实体
     */
    public PositionInfoVO(PositionEntity entity) {
        this.code = entity.getCode();
        this.postCode = entity.getPostCode();
        this.name = entity.getName();
        this.sortOrder = entity.getSortOrder();
        this.status = entity.getStatus();
        this.remark = entity.getRemark();
        this.createTime = entity.getCreateTime();
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
