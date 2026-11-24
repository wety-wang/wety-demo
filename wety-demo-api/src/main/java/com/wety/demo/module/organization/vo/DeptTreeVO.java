package com.wety.demo.module.organization.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.entity.DeptEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门树节点返回模型。
 */
@Schema(name = "DeptTreeVO", description = "部门树节点信息")
public class DeptTreeVO {

    /** 部门业务编码。 */
    @Schema(description = "部门业务编码")
    private String code;

    /** 父部门编码（根为 "0"）。 */
    @Schema(description = "父部门编码（根为0）")
    private String parentCode;

    /** 祖级路径。 */
    @Schema(description = "祖级路径")
    private String ancestors;

    /** 部门名称。 */
    @Schema(description = "部门名称")
    private String name;

    /** 排序号。 */
    @Schema(description = "排序号")
    private Integer sortOrder;

    /** 负责人账号编码。 */
    @Schema(description = "负责人账号编码")
    private String leaderAccountCode;

    /** 负责人姓名。 */
    @Schema(description = "负责人姓名")
    private String leaderAccountName;

    /** 联系电话。 */
    @Schema(description = "联系电话")
    private String phone;

    /** 联系邮箱。 */
    @Schema(description = "联系邮箱")
    private String email;

    /** 状态：1-启用，0-停用。 */
    @Schema(description = "状态：1-启用，0-停用")
    private Integer status;

    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    /** 子部门列表。 */
    @Schema(description = "子部门列表")
    private List<DeptTreeVO> children = new ArrayList<>();

    public DeptTreeVO() {
    }

    /**
     * 基于部门实体构造树节点。
     *
     * @param entity 部门实体
     */
    public DeptTreeVO(DeptEntity entity) {
        this.code = entity.getCode();
        this.parentCode = entity.getParentCode();
        this.ancestors = entity.getAncestors();
        this.name = entity.getName();
        this.sortOrder = entity.getSortOrder();
        this.leaderAccountCode = entity.getLeaderAccountCode();
        this.phone = entity.getPhone();
        this.email = entity.getEmail();
        this.status = entity.getStatus();
        this.createTime = entity.getCreateTime();
    }

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

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
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

    public String getLeaderAccountName() {
        return leaderAccountName;
    }

    public void setLeaderAccountName(String leaderAccountName) {
        this.leaderAccountName = leaderAccountName;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<DeptTreeVO> getChildren() {
        return children;
    }

    public void setChildren(List<DeptTreeVO> children) {
        this.children = children;
    }
}
