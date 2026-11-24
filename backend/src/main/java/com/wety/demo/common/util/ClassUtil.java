package com.wety.demo.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wety
 */
public class ClassUtil {

    private static final Logger log = LoggerFactory.getLogger(ClassUtil.class);

    private ClassUtil() {
    }

    /**
     * 通过包名获取包内所有类
     *
     * @param pkg
     * @return
     */
    public static List<Class<?>> getAllClassByPackageName(Package pkg) {
        String packageName = pkg.getName();
        // 获取当前包下以及子包下所以的类
        return  getClasses(packageName);
    }

    /**
     * 通过包名获取包内所有类
     *
     * @param pkg
     * @return
     */
    public static List<Class<?>> getAllClassByPackageName(String packageName) {
        // 获取当前包下以及子包下所以的类
        return  getClasses(packageName);
    }

    /**
     * 通过接口名取得某个接口下所有实现这个接口的类
     */
    public static List<Class<?>> getAllClassByInterface(Class<?> c) {
        List<Class<?>> returnClassList = null;

        if (c.isInterface()) {
            // 获取当前的包名
            String packageName = c.getPackage().getName();
            // 获取当前包下以及子包下所以的类
            List<Class<?>> allClass = getClasses(packageName);
            if (allClass != null) {
                returnClassList = new ArrayList<>();
                for (Class<?> cls : allClass) {
                    // 判断是否是同一个接口
                    if (c.isAssignableFrom(cls) && !c.equals(cls)) {
                        // 本身不加入进去
                        returnClassList.add(cls);
                    }
                }
            }
        }

        return returnClassList;
    }

    /**
     * 取得某一类所在包的所有类名 不含迭代
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        // 将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        StringBuilder realClassLocation = new StringBuilder(classLocation);
        for (String packagePath : packagePathSplit) {
            realClassLocation.append(realClassLocation);
            realClassLocation.append(File.separator);
            realClassLocation.append(packagePath);
        }
        File packeageDir = new File(realClassLocation.toString());
        if (packeageDir.isDirectory()) {
            return packeageDir.list();
        }
        return new String[0];
    }

    /**
     * 从包 package 中获取所有的 Class（兼容文件目录、普通 JAR 与 Spring Boot 3 nested JAR）。
     *
     * @param packageName 包名
     * @return 包下所有类列表
     */
    private static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String packageDirName = packageName.replace('.', '/');
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + packageDirName + "/**/*.class";
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
            Resource[] resources = resolver.getResources(pattern);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            for (Resource resource : resources) {
                if (!resource.isReadable()) {
                    continue;
                }
                MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                try {
                    classes.add(Class.forName(className, false, classLoader));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    log.warn("加载类 {} 失败，已跳过", className, e);
                }
            }
        } catch (Exception e) {
            log.error("获取包{}下所有类时出错", packageName, e);
        }
        return classes;
    }
}
