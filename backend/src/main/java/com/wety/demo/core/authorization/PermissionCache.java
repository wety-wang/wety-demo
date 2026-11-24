package com.wety.demo.core.authorization;

import com.wety.demo.core.annotation.PermissionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 系统缓存数据存储的地方，数据的初始化在具体的业务里进行操作并缓存在这里（静态类表示单例吧）
 *
 * @author wety
 */
public class PermissionCache {

    private static final Logger log = LoggerFactory.getLogger(PermissionCache.class);

    /**
     * Permission编码code和Permission的Map緩存，方便通过编码查询权限用
     */
    private static Map<String, PermissionHolder> permissionCodePermissionMap;

    /**
     * Permission注解扫描后分层级的List缓存
     */
    private static List<PermissionHolder> permissionHierarchicalList;

    private static Map<String, PermissionHolder> requestMappingPermissionMapping;

    private PermissionCache() {
    }

    public static List<PermissionHolder> getPermissionHierarchicalList() {
        return permissionHierarchicalList;
    }

    public static void setPermissionHierarchicalList(List<PermissionHolder> permissionHierarchicalList) {
        PermissionCache.permissionHierarchicalList = permissionHierarchicalList;
        permissionHierarchicalList.sort(Comparator.comparingInt(PermissionHolder::getOrder));
        log.info("setPermissionHierarchicalList: {}", permissionHierarchicalList);
    }

    public static Map<String, PermissionHolder> getPermissionCodePermissionMap() {
        return permissionCodePermissionMap;
    }

    public static void setPermissionCodePermissionMap(Map<String, PermissionHolder> permissionCodePermissionMap) {
        PermissionCache.permissionCodePermissionMap = permissionCodePermissionMap;
    }

    public static Map<String, PermissionHolder> getRequestMappingPermissionMapping() {
        return requestMappingPermissionMapping;
    }

    public static void setRequestMappingPermissionMapping(Map<String, PermissionHolder> requestMappingPermissionMapping) {
        PermissionCache.requestMappingPermissionMapping = requestMappingPermissionMapping;
    }

}
