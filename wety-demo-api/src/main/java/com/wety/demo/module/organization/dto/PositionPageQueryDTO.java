package com.wety.demo.module.organization.dto;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.PageableDTO;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.common.jpa.ExpressionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 岗位分页查询参数。
 */
@ApiModel(value = "PositionPageQueryDTO", description = "岗位分页查询参数")
public class PositionPageQueryDTO extends PageableDTO implements CriteriaQueryObject {

    /** 岗位名称模糊搜索。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @ApiModelProperty(value = "岗位名称模糊搜索")
    private String name;

    /** 岗位标识编码模糊搜索。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @ApiModelProperty(value = "岗位标识编码模糊搜索")
    private String postCode;

    /** 状态筛选：1-启用，0-停用。 */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    @ApiModelProperty(value = "状态：1-启用，0-停用")
    private Integer status;

    /** 软删除过滤条件（服务层强制设为未删除）。 */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    @ApiModelProperty(hidden = true)
    private Integer deleted = GlobalConst.NOT;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "PositionPageQueryDTO{" +
                "name='" + name + '\'' +
                ", postCode='" + postCode + '\'' +
                ", status=" + status +
                "} " + super.toString();
    }
}
