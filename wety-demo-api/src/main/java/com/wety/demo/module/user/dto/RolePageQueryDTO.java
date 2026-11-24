package com.wety.demo.module.user.dto;

import com.wety.demo.common.dto.PageableDTO;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.common.jpa.ExpressionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wety
 */
@ApiModel(value = "RolePageQueryDTO", description = "角色分页查询参数")
public class RolePageQueryDTO extends PageableDTO implements CriteriaQueryObject {

    /**
     * 姓名
     */
    @CriteriaExpression(expressionType = ExpressionType.LIKE)
    @ApiModelProperty(value = "姓名,模糊搜索")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PageQueryDTO{"+
                "name='"+name+'\''+
                "} "+super.toString();
    }
}
