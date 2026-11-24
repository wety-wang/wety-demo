package com.wety.demo.core.datascope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解。
 *
 * <p>标注在 Service 查询方法上，由 {@link DataScopeAspect} 在执行前解析当前账号的数据权限上下文，
 * 并由 {@link com.wety.demo.common.jpa.support.BaseRepositoryImpl} 自动拼装 JPA Criteria 过滤条件。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 目标实体上的部门编码属性名，默认 {@code deptCode}。
     *
     * @return 实体部门属性名
     */
    String deptField() default "deptCode";

    /**
     * 目标实体上的数据归属人（创建人/操作人）编码属性名，默认 {@code createUser}。
     *
     * @return 实体用户属性名
     */
    String userField() default "createUser";

    /**
     * 是否启用数据权限拦截，默认 {@code true}。
     *
     * @return true 表示启用
     */
    boolean enabled() default true;
}
