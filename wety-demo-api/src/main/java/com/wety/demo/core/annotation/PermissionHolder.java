package com.wety.demo.core.annotation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限注解Permission的一个包装，添加children属性，让权限可以有层级结构。
 *
 * @author wety
 */
@ApiModel(description = "权限定义")
public class PermissionHolder {
    @ApiModelProperty(hidden = true)
    private Permission permission;

    @ApiModelProperty(value = "下级权限数组")
    private List<PermissionHolder> children;

    @ApiModelProperty(value = "权限码，全局唯一")
    private String code;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "上级权限编码")
    private String parentCode;

    @ApiModelProperty(value = "是否是叶子权限")
    private String leafNode;

    @ApiModelProperty(hidden = true)
    private Set<String> dependencyCodes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private int order;

    /**
     * 是否在树结构上显示
     */
    private String showTreeNode;

    private String resourcePath;

    /**
     * Permission是必须的
     *
     * @param permission
     */
    public PermissionHolder(Permission permission) {
        super();
        this.permission = permission;
        this.code = permission.code();
        this.name = permission.name();
        this.parentCode = permission.parentCode();
        this.leafNode = permission.leafNode();
        // 要保证Set线程安全
        this.dependencyCodes = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.dependencyCodes.addAll(Arrays.asList(permission.dependencyCodes()));
        this.order = permission.order();
        this.showTreeNode = permission.showTreeNode();
    }

    public PermissionHolder() {
    }

    public Permission getPermission() {
        return permission;
    }

    public List<PermissionHolder> getChildren() {
        return children;
    }

    public void addChild(PermissionHolder holder) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(holder);
        this.children.sort(Comparator.comparingInt(PermissionHolder::getOrder));
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

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getLeafNode() {
        return leafNode;
    }

    public void setLeafNode(String leafNode) {
        this.leafNode = leafNode;
    }

    public Set<String> getDependencyCodes() {
        return dependencyCodes;
    }

    public void setDependencyCodes(Set<String> dependencyCodes) {
        this.dependencyCodes = dependencyCodes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof PermissionHolder) {
            PermissionHolder target = (PermissionHolder) obj;
            if (this.getPermission() == null || target.getPermission() == null) {
                return false;
            }
            return StringUtils.equals(this.getPermission().code(), target.getPermission().code());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(this.code != null) {
            return this.code.hashCode();
        }
        return super.hashCode();
    }

    public String getShowTreeNode() {
        return showTreeNode;
    }

    public void setShowTreeNode(String showTreeNode) {
        this.showTreeNode = showTreeNode;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public void setChildren(List<PermissionHolder> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "PermissionHolder{" + "permission=" + permission + ", children=" + children + ", code='" + code + '\'' + ", name='" + name + '\'' + ", parentCode='" + parentCode + '\'' + ", leafNode='" + leafNode + '\'' + ", dependencyCodes=" + dependencyCodes + ", order=" + order + ", showTreeNode='" + showTreeNode + '\'' + ", resourcePath='" + resourcePath + '\'' + '}';
    }
}
