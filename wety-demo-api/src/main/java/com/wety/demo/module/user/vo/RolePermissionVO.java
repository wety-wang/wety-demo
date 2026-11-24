package com.wety.demo.module.user.vo;

import com.wety.demo.core.annotation.PermissionHolder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author wety
 */
@Schema(name = "RolePermissionVO", description = "角色权限返回参数")
public class RolePermissionVO {

    @Schema(description = "角色拥有的叶子权限编码")
    private List<String> codes;

    @Schema(description = "全部的权限结构")
    private List<PermissionHolder> hierarchicalList;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public List<PermissionHolder> getHierarchicalList() {
        return hierarchicalList;
    }

    public void setHierarchicalList(List<PermissionHolder> hierarchicalList) {
        this.hierarchicalList = hierarchicalList;
    }

    @Override
    public String toString() {
        return "RolePermissionVO{"+
                "codes="+codes+
                ", hierarchicalList="+hierarchicalList+
                '}';
    }
}
