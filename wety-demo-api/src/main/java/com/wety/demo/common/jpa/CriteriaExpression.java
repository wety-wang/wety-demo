package com.wety.demo.common.jpa;

import java.lang.annotation.*;

/**
 * 放在查询对象属性上的Annotation，配合自定义Repository实现动态查询
 *
 * @author wety
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CriteriaExpression {
    /**
     * 用于定义被查询的实体的属性名(propertyName)
     *
     * @return
     */
    String propertyName() default "";

    /**
     * 表达式类型<br>
     * 例如相等，大于等等
     *
     * @return
     */
    ExpressionType expressionType();

    /**
     * 目前只支持AND
     *
     * @return
     */
    OperatorType operatorType() default OperatorType.AND;

    /**
     * 当属性值为空或空集合时是否跳过该查询条件<br>
     * default true
     *
     * @return
     */
    boolean skipOnEmpty() default true;
}
