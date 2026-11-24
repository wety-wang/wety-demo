package com.wety.demo.core.authentication;

import com.wety.demo.common.util.ClassUtil;
import com.wety.demo.core.annotation.NoLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        RequestMapping[] classRequestMappings = clazz.getAnnotationsByType(RequestMapping.class);

        if (classRequestMappings.length > 1) {
            throw new Exception("发现多个RequestMapping:"+clazz.getName());
        } else if (classRequestMappings.length == 1) {
            pathOnClass = getPathOnRequestMapping(classRequestMappings[0]);
        }
        // 遍历方法上的RequestMapping和NoLogin注解
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
     * 提取方法上的RequestMapping和NoLogin的关联
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

        // 当Controller中只有一个方法时，RequestMapping注解可以不写path，这里需要处理
        GetMapping[] getMappings = method.getAnnotationsByType(GetMapping.class);
        if (getMappings.length != 0) {
            return Collections.singletonList(getPathOnGetMapping(getMappings[0]));
        }
        PostMapping[] postMappings = method.getAnnotationsByType(PostMapping.class);
        if (postMappings.length != 0) {
            return Collections.singletonList(getPathOnPostMapping(postMappings[0]));
        }
        RequestMapping[] requestMappings = method.getAnnotationsByType(RequestMapping.class);
        if (requestMappings.length != 0) {
            return Collections.singletonList(getPathOnRequestMapping(requestMappings[0]));
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

    /**
     * 提取GetMapping注解上的第一个path/value属性值，如果不存在则抛出异常
     *
     * @param getMapping GetMapping
     *
     * @return String path
     *
     * @throws Exception GetMapping未指定path或value
     */
    private String getPathOnGetMapping(GetMapping getMapping) throws Exception {
        String[] values = getMapping.value();
        String[] pathes = getMapping.path();
        if (values.length > 0) {
            return values[0];
        } else if (pathes.length > 0) {
            return pathes[0];
        }
        throw new Exception("GetMapping未指定path或value");
    }

    /**
     * 提取PostMapping注解上的第一个path/value属性值，如果不存在则抛出异常
     *
     * @param postMapping PostMapping
     *
     * @return String path
     *
     * @throws Exception PostMapping未指定path或value
     */
    private String getPathOnPostMapping(PostMapping postMapping) throws Exception {
        String[] values = postMapping.value();
        String[] pathes = postMapping.path();
        if (values.length > 0) {
            return values[0];
        } else if (pathes.length > 0) {
            return pathes[0];
        }
        throw new Exception("PostMapping未指定path或value");
    }

}
