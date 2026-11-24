package com.wety.demo.core.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证接口<br>
 * 具体认证逻辑应该通过request获取需要的认证信息并完成用户的认证<br>
 * 例如可以约定前后端将request的某些数据进行签名，签名一致则视为认证通过
 *
 * @author wety
 */
public interface AuthenticationChecker<T> {

    /**
     * 检查请求是否是登录后的一个请求
     *
     * @param request
     *
     * @return null标识认证不通过;<br>
     * 非null标识认证通过，并返回认证对象供后续Filter等逻辑使用（例如用户信息放入ThradLocal）
     */
    default T checkAuthentication(HttpServletRequest request) {
        return null;
    }
}
