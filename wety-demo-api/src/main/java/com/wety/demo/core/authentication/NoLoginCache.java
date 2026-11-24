package com.wety.demo.core.authentication;

import java.util.List;

/**
 * 系统缓存数据存储的地方，数据的初始化在具体的业务里进行操作并缓存在这里（静态类表示单例吧）
 *
 * @author wety
 */
public class NoLoginCache {

    private static List<String> requestMappingNoLoginList;

    private NoLoginCache() {
    }

    public static List<String> getRequestMappingNoLoginList() {
        return requestMappingNoLoginList;
    }

    public static void setRequestMappingNoLoginList(List<String> requestMappingNoLoginList) {
        NoLoginCache.requestMappingNoLoginList = requestMappingNoLoginList;
    }

}
