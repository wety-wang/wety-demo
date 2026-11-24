package com.wety.demo.core.annotation;

import java.lang.annotation.*;

/**
 * @author wety
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permissions {
    Permission[] value() default{};
}
