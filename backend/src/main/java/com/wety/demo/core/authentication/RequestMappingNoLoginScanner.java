package com.wety.demo.core.authentication;

import com.wety.demo.common.util.ClassUtil;
import com.wety.demo.core.annotation.NoLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RequestMapping-NoLogin映射的扫描工具类
 *
 * @author wety
 */
@Component
public class RequestMappingNoLoginScanner {

    private static final Logger log = LoggerFactory.getLogger(RequestMappingNoLoginScanner.class);

    public void scanRequestMappingNoLogins(Package basePackage) throws Exception {
        scanRequestMappingNoLogins(basePackage.getName());
    }

    public void scanRequestMappingNoLogins(String basePackage) throws Exception {
        log.info("request-NoLogin-mappings scan start...");
        List<String> mappings = new ArrayList<>();
        List<Class<?>> clazzList = ClassUtil.getAllClassByPackageName(basePackage);
        for (Class<?> clazz : clazzList) {
            List<String> map = extractClassRequestMappingNoLogins(clazz);
            mappings.addAll(map);
        }
        log.info("request-NoLogin-mappings scan finished。RequestMappingNoLoginList: {}", mappings);
        NoLoginCache.setRequestMappingNoLoginList(mappings);
    }

    /**
     * 提取指定Class上请求和权限的绑定<br> 扫描RequestMapping和NoLogin注解，并解析为url和权限的关联
     *
     * @param clazz 被扫描的Class对象
     *
     * @return List<String>
     *
     * @throws Exception 发现多个RequestMapping
     */
    private List<String> extractClassRequestMappingNoLogins(Class<?> clazz) throws Exception {
        List<String> noLoginList = new ArrayList<>();

        String pathOnClass = "";
        RequestMapping classRequestMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
        if (classRequestMapping != null) {
            pathOnClass = getPathOnRequestMapping(classRequestMapping);
        }
        // 遍历方法上的 RequestMapping（含 GetMapping/PostMapping 等组合注解）和 NoLogin 注解
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            List<String> methodList = extractClassRequiestMappingNoLogins(method);
            for (String path : methodList) {
                noLoginList.add(pathOnClass+path);
            }
        }

        return noLoginList;
    }

    /**
     * 提取方法上的RequestMapping（含GetMapping/PostMapping等组合注解）和NoLogin的关联
     *
     * @param method
     *
     * @return List<String> path
     *
     * @throws Exception 有NoLogin定义，但未指定RequestMapping（GetMapping、PostMapping、RequestMapping） 或 RequestMapping未指定path或value
     */
    private List<String> extractClassRequiestMappingNoLogins(Method method) throws Exception {
        NoLogin[] nologins = method.getAnnotationsByType(NoLogin.class);
        if (nologins.length == 0) {
            return Collections.emptyList();
        }

        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            return Collections.singletonList(getPathOnRequestMapping(requestMapping));
        }

        throw new Exception("有权限定义，但未指定GetMapping/PostMapping/RequestMapping: "+nologins[0]);
    }

    /**
     * 提取RequestMapping注解上的第一个path/value属性值，如果不存在则抛出异常
     *
     * @param requestMapping RequestMapping
     *
     * @return String path
     *
     * @throws Exception RequestMapping未指定path或value
     */
    private String getPathOnRequestMapping(RequestMapping requestMapping) throws Exception {
        String[] values = requestMapping.value();
        String[] pathes = requestMapping.path();
        if (values.length > 0) {
            return values[0];
        } else if (pathes.length > 0) {
            return pathes[0];
        }
        throw new Exception("RequestMapping未指定path或value");
    }

}
