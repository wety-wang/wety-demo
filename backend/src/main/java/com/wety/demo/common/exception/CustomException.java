package com.wety.demo.common.exception;

/**
 * 自定义异常
 *
 * @author wety
 */
public class CustomException extends RuntimeException {

    private final Integer code;

    private final String message;

    public CustomException(String message) {
        this.code = 500;
        this.message = message;
    }

    public CustomException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public CustomException(Throwable e) {
        super(e);
        this.code = 500;
        this.message = e.getMessage();
    }

    public CustomException(String message, Throwable e) {
        super(message, e);
        this.code = 500;
        this.message = message + ":" + e.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CustomException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
