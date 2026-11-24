package com.wety.demo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于放在对象属性上的注解，用于标识一个属性的变化是否需要被记录<br> 配合FieldModificationCollector可以获取两个对象之间的属性值的变化<br> 注意： 备注解的类应该是一个标准的Bean：public
 * class 、 public getter/setter
 *
 * @author wety
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyRecordField {
    /**
     * 被标记属性的一个标签说明
     *
     * @return
     */
    String label() default "";

    /**
     * 被标记属性更新时否需要被收集
     *
     * @return
     */
    boolean logable() default true;

    /**
     * 被标记属性没有变化时是否可以收集器忽略
     *
     * @return
     */
    boolean ignoreNoChange() default false;
}
