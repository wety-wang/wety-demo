package com.wety.demo.core.annotation;

import java.lang.annotation.*;

/**
 * 无需登录，一般作用在Controller的方法上，配合着切面/拦截器来实现无需登录认证的请求
 *
 * @author wety
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogin {

}
