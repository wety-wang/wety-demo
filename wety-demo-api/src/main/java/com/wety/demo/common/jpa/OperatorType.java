package com.wety.demo.common.jpa;

/**
 * 逻辑运算符类型枚举，作用于CriteriaCompoundExpression<br>
 * 例如并且、或等
 *
 * @author wety
 */
public enum OperatorType {
    /** see CriteriaBuilder.and() */
    AND,
    /** 目前不支持，see CriteriaBuilder.or() */
    OR;
}
