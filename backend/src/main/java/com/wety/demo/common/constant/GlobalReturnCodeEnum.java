package com.wety.demo.common.constant;

/**
 * 全局响应code/message定义处
 *
 * @author wety
 */
public enum GlobalReturnCodeEnum {

    /**
     * 200-操作成功
     */
    SUCCESS("200", "操作成功"),
    /**
     * 401 - 未登录或登录已失效, 认证失败
     */
    LOGIN_INVALID("401", "登陆已失效，请重新登录！"),
    /**
     * 402 - 权限不足，拒绝访问
     */
    ACCESS_DENIED("402", "权限不足，拒绝访问"),
    /**
     * 500 - 内部错误，请联系系统管理员
     */
    INTERNAL_ERROR("500", "内部错误，请联系系统管理员");

    private final String code;
    private final String message;

    GlobalReturnCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "GlobalReturnCodeEnum{"+
                "code='"+code+'\''+
                ", message='"+message+'\''+
                "} "+super.toString();
    }
}
