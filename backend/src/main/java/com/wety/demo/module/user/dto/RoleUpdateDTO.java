package com.wety.demo.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 修改角色请求参数。
 *
 * @author wety
 */
@Schema(name = "RoleUpdateDTO", description = "修改角色参数")
public class RoleUpdateDTO {

    /** 角色业务编码。 */
    @Schema(description = "角色内部用code，修改操作时必须带着", example = "4601b43274e64b45a95cfcd15fcd9660", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    /** 角色名称。 */
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 数据权限范围（可选，传入时同步更新）。 */
    @Schema(description = "数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人")
    private Integer dataScope;

    /** 自定义部门编码列表（当 dataScope == 2 时必填）。 */
    @Schema(description = "自定义部门编码列表（当 dataScope == 2 时必填）")
    private List<String> deptCodes;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public List<String> getDeptCodes() {
        return deptCodes;
    }

    public void setDeptCodes(List<String> deptCodes) {
        this.deptCodes = deptCodes;
    }

    @Override
    public String toString() {
        return "RoleUpdateDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dataScope=" + dataScope +
                ", deptCodes=" + deptCodes +
                '}';
    }
}
