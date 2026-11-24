package com.wety.demo.module.auditlog.constant;

import java.util.Objects;

/**
 * 操作类型枚举，用于标识当前业务操作的类型
 *
 * @author wety
 */
public enum OperationType {
    /**
     * 新增
     */
    CREATE("create", "新增"),
    /**
     * 修改
     */
    UPDATE("update", "修改"),
    /**
     * 删除
     */
    DELETE("delete", "删除"),
    /**
     * 查询
     */
    QUERY("query", "查询");

    private final String code;
    private final String name;

    OperationType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据编码获取操作类型名称
     */
    public static String getNameByCode(String code) {
        for (OperationType type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type.getName();
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "OperationType{"+
                "code='"+code+'\''+
                ", name='"+name+'\''+
                "} "+super.toString();
    }
}
