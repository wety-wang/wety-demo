package com.wety.demo.core.misc;

import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.core.datascope.DataScopeContext;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 单例模式-ThreadLocal辅助类，一般配合业务逻辑代码和过滤器/拦截器/代理器/切面等完成某些业务的统一处理<br>
 * 每个线程的数据存储我们用一个Map来记录（通过不同的Key记录不同的关注信息）
 *
 * @author wety
 */
public class AppThreadLocal extends ThreadLocal<Map<String, Object>> {

    private static final AppThreadLocal UNIQUE_INSTANCE = new AppThreadLocal();

    /**
     * 业务返回码在Map里的key，拦截器通过这个key可以取到业务自定义的返回码
     */
    public static final String RETURN_CODE_KEY = "RETURN_CODE";
    /**
     * 业务返回消息在Map里的key，拦截器通过这个key可以取到业务自定义的返回消息
     */
    public static final String RETURN_MESSAGE_KEY = "RETURN_MESSAGE";

    /**
     * 业务处理开始时间，接口开始执行时间
     */
    public static final String PROCESSING_START_TIME_KEY = "PROCESSING_START_TIME";

    /**
     * 业务处理耗时存储Key，通常有拦截器来拦截业务处理并记录实际的处理耗时
     */
    public static final String PROCESSING_DURATION_KEY = "PROCESSING_DURATION";

    /**
     * 用于绑定当前线程上下文对应的账号Account
     */
    public static final String CURRENT_ACCOUNT_KEY = "CURRENT_ACCOUNT";

    /**
     * 配合过滤器用于标记当前的Http Message是否需要包<br>
     * HttpMessageConverterWrapMarkingFilter<br>
     * MappingJackson2HttpMessageConverterWrapper<br>
     * StringHttpMessageConverterWrapper
     */
    public static final String HTTP_MESSAGE_CONVERTER_WRAPPING_KEY = "HTTP_MESSAGE_CONVERTER_WRAPPING";

    /**
     * 客户端IP地址访问key
     */
    public static final String CLIENT_IP_KEY = "CLIENT_IP";

    public static final String REQUEST_RESPONSE_KEY = "REQUEST_RESPONSE";

    /**
     * 当前线程激活的数据权限上下文 key。
     */
    public static final String DATA_SCOPE_CONTEXT_KEY = "DATA_SCOPE_CONTEXT";

    /**
     * 获取ThreadLocal实例，单例-双重检测+锁
     *
     * @return ThreadLocal全局唯一的实例
     */
    public static AppThreadLocal getInstance() {
        return UNIQUE_INSTANCE;
    }

    /**
     * 清楚当前线程绑定的Map对象，通常在响应返回之前执行，避免多个同一个线程多次处理业务请求上下文时数据被干扰
     */
    public void removeBinding() {
        getInstance().remove();
    }

    /**
     * 设置业务上下文需要的返回码，参见ResponseResult
     *
     * @param code
     */
    public void setReturnCode(String code) {
        confirmBindingExists();
        getInstance().get().put(RETURN_CODE_KEY, code);
    }

    public void setReturnCodeNoOverride(String code) {
        confirmBindingExists();
        if (getInstance().get().get(RETURN_CODE_KEY) != null) {
            return;
        }
        getInstance().get().put(RETURN_CODE_KEY, code);
    }

    public String getReturnCode() {
        confirmBindingExists();
        Object value = getInstance().get().get(RETURN_CODE_KEY);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /**
     * 设置业务上下文需要的返回消息，参见ResponseResult
     *
     * @param message
     */
    public void setReturnMessage(String message) {
        confirmBindingExists();
        getInstance().get().put(RETURN_MESSAGE_KEY, message);
    }

    public void setReturnMessageNoOverride(String message) {
        confirmBindingExists();
        if (getInstance().get().get(RETURN_MESSAGE_KEY) != null) {
            return;
        }
        getInstance().get().put(RETURN_MESSAGE_KEY, message);
    }

    public String getReturnMessage() {
        confirmBindingExists();
        Object value = getInstance().get().get(RETURN_MESSAGE_KEY);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /**
     * 设置业务上下文需要的请求处理耗时，参见ResponseResult
     *
     * @param duration
     */
    public void setProcessingDuration(Long duration) {
        confirmBindingExists();
        getInstance().get().put(PROCESSING_DURATION_KEY, duration);
    }

    public void setProcessingDurationNoOverride(Long duration) {
        confirmBindingExists();
        if (getInstance().get().get(PROCESSING_DURATION_KEY) != null) {
            return;
        }
        getInstance().get().put(PROCESSING_DURATION_KEY, duration);
    }

    public Long getProcessingDuration() {
        confirmBindingExists();
        Object value = getInstance().get().get(PROCESSING_DURATION_KEY);
        if (value == null) {
            return 0L;
        }
        return Long.valueOf(value.toString());
    }

    public void setHttpMessageConverterWrapping(Boolean shouldWrap) {
        confirmBindingExists();
        getInstance().get().put(HTTP_MESSAGE_CONVERTER_WRAPPING_KEY, shouldWrap);
    }

    public Boolean getHttpMessageConverterWrapping() {
        confirmBindingExists();
        Object value = getInstance().get().get(HTTP_MESSAGE_CONVERTER_WRAPPING_KEY);
        if (value == null) {
            return false;
        }
        return (Boolean) value;
    }

    public <A> void putCurrentAccount(A accountEntity) {
        confirmBindingExists();
        if (getInstance().get().get(CURRENT_ACCOUNT_KEY) != null) {
            return;
        }
        getInstance().get().put(CURRENT_ACCOUNT_KEY, accountEntity);
    }

    /**
     * 当前线程上下文绑定的账号<br>
     * see AdminAuthenticationFilter
     *
     * @return null表示未绑定过或认证失败
     */
    public AccountEntity getCurrentAccount() {
        confirmBindingExists();
        return (AccountEntity) getInstance().get().get(CURRENT_ACCOUNT_KEY);
    }

    /**
     * 确保当前线程已经绑定过一个Map，如果没有则创建一个并绑定上
     */
    private void confirmBindingExists() {
        if (getInstance().get() == null) {
            getInstance().set(new HashMap<>());
        }
    }

    /**
     * 设置业务上下文需要的返回消息，参见ResponseResult
     */
    public void setClientIp(String clientIp) {
        confirmBindingExists();
        getInstance().get().put(CLIENT_IP_KEY, clientIp);
    }

    public String getClientIp() {
        confirmBindingExists();
        Object value = getInstance().get().get(CLIENT_IP_KEY);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    /**
     * 设置当前请求处理开始时间，时间戳
     */
    public void setProcessingStartTime(Long requestStartTime) {
        confirmBindingExists();
        getInstance().get().put(PROCESSING_START_TIME_KEY, requestStartTime);
    }

    public Long getProcessingStartTime() {
        confirmBindingExists();
        Object value = getInstance().get().get(PROCESSING_START_TIME_KEY);
        if (value != null) {
            return (long)value;
        }
        return 0L;
    }

    public void setRequestResponse(HttpServletResponse response) {
        confirmBindingExists();
        getInstance().get().put(REQUEST_RESPONSE_KEY, response);
    }

    public HttpServletResponse getRequestResponse() {
        confirmBindingExists();
        Object value = getInstance().get().get(REQUEST_RESPONSE_KEY);
        if (value != null) {
            return (HttpServletResponse) value;
        }
        return null;
    }

    /**
     * 绑定当前线程的数据权限上下文。
     *
     * @param context 数据权限上下文
     */
    public void setDataScopeContext(DataScopeContext context) {
        confirmBindingExists();
        getInstance().get().put(DATA_SCOPE_CONTEXT_KEY, context);
    }

    /**
     * 获取当前线程绑定的数据权限上下文。
     *
     * @return 数据权限上下文，未绑定时返回 null
     */
    public DataScopeContext getDataScopeContext() {
        Map<String, Object> map = getInstance().get();
        if (map == null) {
            return null;
        }
        Object value = map.get(DATA_SCOPE_CONTEXT_KEY);
        if (value instanceof DataScopeContext) {
            return (DataScopeContext) value;
        }
        return null;
    }

    /**
     * 清理当前线程绑定的数据权限上下文。
     */
    public void clearDataScopeContext() {
        Map<String, Object> map = getInstance().get();
        if (map != null) {
            map.remove(DATA_SCOPE_CONTEXT_KEY);
        }
    }

}
