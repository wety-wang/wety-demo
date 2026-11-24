package com.wety.demo.core.annotation;

import com.wety.demo.common.constant.GlobalConst;

import java.lang.annotation.*;

/**
 * 权限注解，一般作用在Controller类上，配合着切面/拦截器来实现无业务入侵的权限控制
 *
 * @author wety
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Permissions.class)
@Documented
public @interface Permission {
    /**
     * 权限编码，全局唯一（eg:01-01-00/管理员列表），如果重复定义了只有一个会生效
     */
    String code() default "";

    /**
     * 权限名称，用户展示用（eg:管理员列表）
     */
    String name() default "";

    /**
     * 父级权限编码（eg: 01-00-00/角色管理）
     */
    String parentCode() default "";

    /**
     * 是否是叶子权限，yes/no
     */
    String leafNode() default GlobalConst.NO;

    /**
     * 当前权限所以来的的权限编码
     *
     * @return
     */
    String[] dependencyCodes() default {};

    /**
     * 用于指定当前权限在同一层级的顺序<br>
     * order值越小越靠前
     * @return
     */
    int order() default 0;

    /**
     * 是否在树结构上显示
     */
    String showTreeNode() default GlobalConst.YES;
}
