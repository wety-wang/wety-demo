package com.wety.demo.module.organization.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 组织架构用户信息返回模型。
 */
@ApiModel(value = "OrgUserInfoVO", description = "组织架构用户信息")
public class OrgUserInfoVO {

    /** 账号业务编码。 */
    @ApiModelProperty(value = "账号业务编码")
    private String code;

    /** 登录名。 */
    @ApiModelProperty(value = "登录名")
    private String loginName;

    /** 用户姓名。 */
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    /** 邮箱。 */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /** 手机号。 */
    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    /** 是否禁用：0-启用，1-禁用。 */
    @ApiModelProperty(value = "是否禁用：0-启用，1-禁用")
    private Integer disabled;

    /** 是否内置超管：1-是，0-否。 */
    @ApiModelProperty(value = "是否内置超管：1-是，0-否")
    private Integer superAdmin;

    /** 主部门编码。 */
    @ApiModelProperty(value = "主部门编码")
    private String primaryDeptCode;

    /** 主部门名称。 */
    @ApiModelProperty(value = "主部门名称")
    private String primaryDeptName;

    /** 岗位编码列表。 */
    @ApiModelProperty(value = "岗位编码列表")
    private List<String> positionCodes = new ArrayList<>();

    /** 岗位名称列表。 */
    @ApiModelProperty(value = "岗位名称列表")
    private List<String> positionNames = new ArrayList<>();

    /** 角色编码列表。 */
    @ApiModelProperty(value = "角色编码列表")
    private List<String> roleCodes = new ArrayList<>();

    /** 角色名称列表。 */
    @ApiModelProperty(value = "角色名称列表")
    private List<String> roleNames = new ArrayList<>();

    /** 创建时间。 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public OrgUserInfoVO() {
    }

    /**
     * 基于账号实体构造基础展示信息。
     *
     * @param entity 账号实体
     */
    public OrgUserInfoVO(AccountEntity entity) {
        this.code = entity.getCode();
        this.loginName = entity.getLoginName();
        this.userName = entity.getUserName();
        this.email = entity.getEmail();
        this.phoneNumber = entity.getPhoneNumber();
        this.disabled = entity.getDisabled() == null ? GlobalConst.NOT : entity.getDisabled();
        this.superAdmin = entity.getSuperAdmin() == null ? GlobalConst.NOT : entity.getSuperAdmin();
        this.primaryDeptCode = entity.getPrimaryDeptCode();
        this.createTime = entity.getCreateTime();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Integer getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Integer superAdmin) {
        this.superAdmin = superAdmin;
    }

    public String getPrimaryDeptCode() {
        return primaryDeptCode;
    }

    public void setPrimaryDeptCode(String primaryDeptCode) {
        this.primaryDeptCode = primaryDeptCode;
    }

    public String getPrimaryDeptName() {
        return primaryDeptName;
    }

    public void setPrimaryDeptName(String primaryDeptName) {
        this.primaryDeptName = primaryDeptName;
    }

    public List<String> getPositionCodes() {
        return positionCodes;
    }

    public void setPositionCodes(List<String> positionCodes) {
        this.positionCodes = positionCodes;
    }

    public List<String> getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(List<String> positionNames) {
        this.positionNames = positionNames;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
