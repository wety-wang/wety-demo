package com.wety.demo.core.authorization;

import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.ClassUtil;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.core.annotation.PermissionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * RequestMapping-Permission映射的扫描工具类<br>
 * 扫描指定包路径下的所有的class将RequestMapping和Permission并以Map的形式关联起来
 *
 * @author wety
 */
@Component
public class RequestMappingPermissionScanner {

    private static final Logger log = LoggerFactory.getLogger(RequestMappingPermissionScanner.class);

    public void scanRequestMappingPermissions(Package basePackage) throws CustomException {
        scanRequestMappingPermissions(basePackage.getName());
    }

    public void scanRequestMappingPermissions(String basePackage) throws CustomException {
        log.info("request-permission-mappings scan start...");
        Map<String, PermissionHolder> mappings = new HashMap<>();
        List<Class<?>> clazzList = ClassUtil.getAllClassByPackageName(basePackage);
        for (Class<?> clazz : clazzList) {
            Map<String, PermissionHolder> map = extractClassRequestMappingPermissions(clazz);
            mappings.putAll(map);
        }
        log.info("request-permission-mappings scan finished");
        log.info("RequestMappingPermissionMapping: {}", mappings);
        PermissionCache.setRequestMappingPermissionMapping(mappings);
    }

    /**
     * 提取指定Class上请求和权限的绑定<br> 扫描RequestMapping和Permission注解，并解析为url和权限的关联
     *
     * @param clazz 被扫描的Class对象
     *
     * @return Map<String, PermissionHolder>
     *
     * @throws CustomException 发现多个RequestMapping
     */
    private Map<String, PermissionHolder> extractClassRequestMappingPermissions(Class<?> clazz) throws CustomException {
        Map<String, PermissionHolder> permissionMapping = new HashMap<>();

        String pathOnClass = "";
        RequestMapping classRequestMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
        if (classRequestMapping != null) {
            pathOnClass = getPathOnRequestMapping(classRequestMapping, false);
        }
        // 遍历方法上的 RequestMapping（含 GetMapping/PostMapping 等派生注解）和 Permission 注解
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Map<String, PermissionHolder> methodMappings = extractClassRequiestMappingPermissions(method);
            for (Entry<String, PermissionHolder> entry : methodMappings.entrySet()) {
                permissionMapping.put(pathOnClass+entry.getKey(), entry.getValue());
            }
        }

        return permissionMapping;
    }

    /**
     * 提取方法上的RequestMapping（含GetMapping/PostMapping等组合注解）和Permission的关联
     *
     * @param method
     *
     * @return Map<String, PermissionHolder> key为path/value， value为permission
     *
     * @throws CustomException
     */
    private Map<String, PermissionHolder> extractClassRequiestMappingPermissions(Method method) throws CustomException {
        Map<String, PermissionHolder> permissionMapping = new HashMap<>();
        Permission[] permissions = method.getAnnotationsByType(Permission.class);
        if (permissions.length > 0) {
            PermissionHolder holder = new PermissionHolder(permissions[0]);
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (requestMapping == null) {
                throw new CustomException("有权限定义，但未指定RequestMapping: "+holder);
            }
            String path = getPathOnRequestMapping(requestMapping, true);
            if (path == null) {
                throw new CustomException("RequestMapping未指定path或value: "+holder);
            }
            permissionMapping.put(path, holder);
            return permissionMapping;
        }
        return permissionMapping;
    }

    /**
     * 提取RequestMapping注解上的第一个path/value属性值，如果不存在则抛出异常
     *
     * @param requestMapping RequestMapping
     * @param nullable       是否允许返回null，false时如果path/value都为null则抛出异常
     *
     * @return
     *
     * @throws CustomException
     */
    private String getPathOnRequestMapping(RequestMapping requestMapping, boolean nullable) throws CustomException {
        String[] values = requestMapping.value();
        String[] pathes = requestMapping.path();
        if (values.length > 0) {
            return values[0];
        } else if (pathes.length > 0) {
            return pathes[0];
        }
        if (nullable) {
            return null;
        } else {
            throw new CustomException("RequestMapping未指定path或value");
        }
    }

}
