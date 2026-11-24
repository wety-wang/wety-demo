package com.wety.demo.module.user.dto;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.PageableDTO;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.common.jpa.ExpressionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 角色分页查询参数。
 *
 * @author wety
 */
@ApiModel(value = "RolePageQueryDTO", description = "角色分页查询参数")
public class RolePageQueryDTO extends PageableDTO implements CriteriaQueryObject {

    /** 角色名称（模糊搜索）。 */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @ApiModelProperty(value = "角色名称，模糊搜索")
    private String name;

    /** 软删除过滤标识，固定仅查询未删除角色。 */
    @CriteriaExpression(expressionType = ExpressionType.EQUAL)
    @ApiModelProperty(hidden = true)
    private Integer deleted = GlobalConst.NOT;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "RolePageQueryDTO{" +
                "name='" + name + '\'' +
                ", deleted=" + deleted +
                "} " + super.toString();
    }
}
