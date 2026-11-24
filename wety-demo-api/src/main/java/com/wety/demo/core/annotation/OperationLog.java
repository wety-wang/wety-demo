package com.wety.demo.core.annotation;

import com.wety.demo.module.auditlog.constant.OperationType;

import java.lang.annotation.*;

/**
 * @author wety
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OperationLog {

    /**
     * 操作类型（新增、修改、删除、查询等），用于记录当前操作类别
     */
    OperationType operationType() default OperationType.QUERY;

    /**
     * spel 解析key 例如 {"user=vo.user}","x=y"， "id"} 第一部分为 名称 第二部分为 值 就像 user = vo.user  x=y  id=id
     * 不存在名称的默认使用传入参数名称
     * <p>
     * 后面在方法 findMethodNames  descriptionTemplate 中，通过 # 加 名称引用， #x #id #user，
     * <p>
     * 注意  “;” 用于分割多个解析key 用于多参数方法
     */
    String[] spelKeys() default {};

    /**
     * 获取操作bean的类型， 用于获取操作对象 UserDao.class
     * <p>
     * 与 findMethodNames 中的方法一一对应，数量最好一致
     */
    Class[] beanClasses() default {};

    /**
     * 用于查找得方法名称， 例如 findUser=find(#id), 需要带有参数  select(#user)
     * 其中 findUser 成为 返回值名称 通过 # 加 返回值名称 引用例如  #findUser  #select ， 没有直接指明返回值名称的 默认使用方法名为返回值名称，注意重写方载的问题
     * find 成为参数名称，
     * #id 为引用的参数 是 spel的模式引用，使用的参数是 spelKeys 中提供的值
     */
    String[] findMethodNames() default {};

    /**
     * 业务描述模板
     * <p>
     * 注意 普通字符串要使用 '' 框住 ， 例如 '添加'
     */
    String descriptionTemplate();

}
