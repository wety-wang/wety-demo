package com.wety.demo.core.datascope;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 当前线程生效的数据权限上下文。
 *
 * <p>由 {@link DataScopeResolver} 解析生成，并绑定在 {@link com.wety.demo.core.misc.AppThreadLocal} 中。</p>
 */
public class DataScopeContext {

    /** 是否拥有全部数据权限。 */
    private boolean all;

    /** 允许访问的部门业务编码集合。 */
    private Set<String> allowedDeptCodes;

    /** 是否允许访问本人数据。 */
    private boolean includeSelf;

    /** 当前登录账号业务编码。 */
    private String currentUserCode;

    /** 目标实体的部门编码属性名。 */
    private String deptField;

    /** 目标实体的用户编码属性名。 */
    private String userField;

    public DataScopeContext() {
        this.allowedDeptCodes = new LinkedHashSet<>();
    }

    /**
     * 构造全部数据权限上下文。
     *
     * @param currentUserCode 当前用户编码
     * @param deptField       部门属性名
     * @param userField       用户属性名
     * @return 全部权限上下文
     */
    public static DataScopeContext all(String currentUserCode, String deptField, String userField) {
        DataScopeContext context = new DataScopeContext();
        context.setAll(true);
        context.setAllowedDeptCodes(Collections.emptySet());
        context.setIncludeSelf(true);
        context.setCurrentUserCode(currentUserCode);
        context.setDeptField(deptField);
        context.setUserField(userField);
        return context;
    }

    /**
     * 构造无任何数据权限的空上下文（用于未登录等异常兜底场景）。
     *
     * @param currentUserCode 当前用户编码
     * @param deptField       部门属性名
     * @param userField       用户属性名
     * @return 空权限上下文
     */
    public static DataScopeContext empty(String currentUserCode, String deptField, String userField) {
        DataScopeContext context = new DataScopeContext();
        context.setAll(false);
        context.setAllowedDeptCodes(Collections.emptySet());
        context.setIncludeSelf(false);
        context.setCurrentUserCode(currentUserCode);
        context.setDeptField(deptField);
        context.setUserField(userField);
        return context;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public Set<String> getAllowedDeptCodes() {
        return allowedDeptCodes;
    }

    public void setAllowedDeptCodes(Set<String> allowedDeptCodes) {
        this.allowedDeptCodes = allowedDeptCodes == null ? new LinkedHashSet<>() : allowedDeptCodes;
    }

    public boolean isIncludeSelf() {
        return includeSelf;
    }

    public void setIncludeSelf(boolean includeSelf) {
        this.includeSelf = includeSelf;
    }

    public String getCurrentUserCode() {
        return currentUserCode;
    }

    public void setCurrentUserCode(String currentUserCode) {
        this.currentUserCode = currentUserCode;
    }

    public String getDeptField() {
        return deptField;
    }

    public void setDeptField(String deptField) {
        this.deptField = deptField;
    }

    public String getUserField() {
        return userField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }

    @Override
    public String toString() {
        return "DataScopeContext{" +
                "all=" + all +
                ", allowedDeptCodes=" + allowedDeptCodes +
                ", includeSelf=" + includeSelf +
                ", currentUserCode='" + currentUserCode + '\'' +
                ", deptField='" + deptField + '\'' +
                ", userField='" + userField + '\'' +
                '}';
    }
}
