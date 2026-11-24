package com.wety.demo.core.authorization;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.util.ClassUtil;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.core.annotation.PermissionHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 权限注解Permission扫描，扫描指定package下的所有Permission并转换成一个hierarchy+list
 *
 * @author wety
 */
@Component
public class PermissionListHierarchyScanner {

    private static final Logger log = LoggerFactory.getLogger(PermissionListHierarchyScanner.class);

    /**
     * 扫描指定包下的所有的Class和Method上的Permission注解并将扫描结果存储到GlobalCacheStorage方便后续使用<br>
     * 该方法一般在应用启动时执行
     *
     * @param basePackage
     * @throws Exception
     */
    public void scanPermissionListHierarchy(Package basePackage) {
        log.info("permission-list-hierarchy scan start...");

        // 查找指定包下的所有Class对象
        List<Class<?>> clazzList = ClassUtil.getAllClassByPackageName(basePackage);

        // 不进行Tree结构化扫描处理
        Map<String, PermissionHolder> permissionCodePermissionMap = extractPermissions(clazzList);
        PermissionCache.setPermissionCodePermissionMap(permissionCodePermissionMap);
        log.info("所有权限:\n{}", permissionCodePermissionMap);

        // Tree结构化的
        Map<String, PermissionHolder> permissionMapOfHierarchical = applyPermissionHierarchyAsNewMap(permissionCodePermissionMap);
        log.info("顶层权限:\n{}", permissionCodePermissionMap);
        List<PermissionHolder> permissionHierarchicalList = new ArrayList<>(permissionMapOfHierarchical.values());
        PermissionCache.setPermissionHierarchicalList(permissionHierarchicalList);

        log.info("permission-list-hierarchy scan finished");

    }

    /**
     * 将权限进行父级节点挂靠生成新的map，原始permissionMap不受影响
     *
     * @param permissionMap code-permission 映射
     */
    private Map<String, PermissionHolder> applyPermissionHierarchyAsNewMap(Map<String, PermissionHolder> permissionMap) {
        Map<String, PermissionHolder> copiedMap = new HashMap<>(permissionMap);

        Set<String> toBeRemoveSet = new HashSet<>();

        for (PermissionHolder holder : copiedMap.values()) {
            if(Objects.equals(holder.getShowTreeNode(), GlobalConst.NO)) {
                toBeRemoveSet.add(holder.getPermission().code());
                continue;
            }
            String parentCode = holder.getPermission().parentCode();
            // parentCode不为空表示它应该挂在别的Permission的children上，当前的应该被删除
            if (StringUtils.isNotEmpty(parentCode)) {
                PermissionHolder parentPermission = copiedMap.get(parentCode);
                if (parentPermission != null && Objects.equals(parentPermission.getShowTreeNode(), GlobalConst.YES)) {
                    parentPermission.addChild(holder);
                }
                toBeRemoveSet.add(holder.getPermission().code());
            }
        }
        // 去掉已经挂在父级节点上的
        for (String remove : toBeRemoveSet) {
            copiedMap.remove(remove);
        }
        return copiedMap;
    }

    /**
     * 扫描提取所有的指定类上的所有的Permission注解，类和方法都会被扫描，并根据Permission.code进行去重
     *
     * @param clazzList 被扫描的Class实例列表
     * @return
     */
    private Map<String, PermissionHolder> extractPermissions(List<Class<?>> clazzList) {
        Map<String, PermissionHolder> codePermissionMap = new HashMap<>();
        for (Class<?> clazz : clazzList) {
            // 扫描并存储Class上的Permission注解
            Permission[] clazzPermissions = clazz.getAnnotationsByType(Permission.class);
            for (Permission permission : clazzPermissions) {
                PermissionHolder holder = new PermissionHolder(permission);
                addIgnoreRepeatable(holder, codePermissionMap);
            }
            // methods
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                Permission[] methodPermissions = method.getAnnotationsByType(Permission.class);
                for (Permission permission : methodPermissions) {
                    PermissionHolder holder = new PermissionHolder(permission);
                    addIgnoreRepeatable(holder, codePermissionMap);
                }
            }
        }
        return codePermissionMap;
    }

    /**
     * 将权限PermissionHolder添加到code-permission集合中去<br>
     * 如果相同code的权限已经存在则只log警告并忽略
     *
     * @param holder            权限对象
     * @param codePermissionMap code-PermissionHolder的Map
     */
    private void addIgnoreRepeatable(PermissionHolder holder, Map<String, PermissionHolder> codePermissionMap) {
        PermissionHolder one = codePermissionMap.get(holder.getCode());
        if (one != null) {
            log.warn("权限重复：\n{} \n{} \n", one, holder);
        } else {
            codePermissionMap.put(holder.getCode(), holder);
        }
    }

}
