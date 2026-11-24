package com.wety.demo.common.jpa;

/**
 * 用于定义表达式类型<br>
 * 例如相等，大于等等
 *
 * @author wety
 */
public enum ExpressionType {
    /** * 相等 */
    EQUAL,
    /** 不等于 */
    NOT_EQUAL,
    /** * 小于 */
    LESS_THAN,
    /** 小于等于 */
    LESS_THAN_EQUAL,
    /** 大约 */
    GREATER_THAN,
    /** 大于等于 */
    GREATER_THAN_EQUAL,
    /** 为NULL */
    IS_NULL,
    /** 不为NULL */
    IS_NOT_NULL,
    /** 模糊判断表达式 */
    LIKE,
    /** 对集合类型有效List */
    IN,
    /** 只能作用于List类型，并且必须有两个值 */
    BEWTEEN
}
