package com.wety.demo.module.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.entity.RoleEntity;
import com.wety.demo.core.datascope.DataScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * 角色列表返回参数。
 *
 * @author wety
 */
@Schema(name = "RoleInfoVO", description = "角色列表返回参数")
public class RoleInfoVO {

    /** 角色业务编码。 */
    @Schema(description = "角色内部用code，修改操作时必须带着", example = "4601b43274e64b45a95cfcd15fcd9660")
    private String code;

    /** 角色名称。 */
    @Schema(description = "角色名称")
    private String name;

    /** 数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人。 */
    @Schema(description = "数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人")
    private Integer dataScope;

    /** 数据权限范围中文名称。 */
    @Schema(description = "数据权限范围中文名称")
    private String dataScopeName;

    /** 创建时间。 */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public RoleInfoVO(RoleEntity entity) {
        this.code = entity.getCode();
        this.name = entity.getName();
        DataScopeEnum scopeEnum = DataScopeEnum.fromCode(entity.getDataScope());
        this.dataScope = scopeEnum.getCode();
        this.dataScopeName = scopeEnum.getLabel();
        this.createTime = entity.getCreateTime();
    }

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

    public String getDataScopeName() {
        return dataScopeName;
    }

    public void setDataScopeName(String dataScopeName) {
        this.dataScopeName = dataScopeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "RoleInfoVO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dataScope=" + dataScope +
                ", dataScopeName='" + dataScopeName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
