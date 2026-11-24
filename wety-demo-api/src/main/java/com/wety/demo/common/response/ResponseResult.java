package com.wety.demo.common.response;

/**
 * 通用返回结果对象，通常会被MVC框架转换为json返回到客户端
 *
 * @author wety
 */
public class ResponseResult {

    /**
     * 响应状态码，根据业务需要自行约定<br>
     * 200通常标识业务处理成功
     */
    private String code;

    /**
     * 响应结果说明，当状态码为200时该属性通常为空<br>
     * 当响应状态码非200时该属性通常用以描述哦错误信息
     */
    private String message;

    /**
     * 响应的业务数据
     */
    private Object data;

    /**
     * 请求处理耗时毫秒数
     */
    private Long duration;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "ResponseResult{"+
                "code='"+code+'\''+
                ", message='"+message+'\''+
                ", data="+data+
                ", duration="+duration+
                '}';
    }
}
