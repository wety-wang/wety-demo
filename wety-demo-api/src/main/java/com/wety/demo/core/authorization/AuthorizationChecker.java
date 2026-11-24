package com.wety.demo.core.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 鉴权接口
 *
 * @author wety
 */
public interface AuthorizationChecker {

    /**
     * 检查请求是否有权限访问
     *
     * @param request  请求对象
     * @param response 响应对象
     *
     * @return boolean类型结果
     */
    default boolean hasPermission(HttpServletRequest request, HttpServletResponse response) {
        return false;
    }
}
