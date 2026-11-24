package com.wety.demo.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;
import java.util.Date;

/**
 * @author wety
 */
@Entity(name = "role")
public class RoleEntity {

    @Schema(description = "数据主键，修改操作时必须带着", example = "1")
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "角色内部用code，修改操作时必须带着", example = "4601b43274e64b45a95cfcd15fcd9660")
    @Column(name = "code")
    private String code;

    @Schema(description = "角色名称")
    @Column(name = "name")
    private String name;

    /** 数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人。 */
    @Schema(description = "数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人")
    @Column(name = "data_scope", nullable = false, columnDefinition = "TINYINT")
    private Integer dataScope;

    @Schema(description = "是否删除 1已删除，0未删除", example = "")
    @Column(name = "deleted", nullable = false)
    private Integer deleted;

    @Schema(hidden = true, description = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @Schema(hidden = true, description = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
