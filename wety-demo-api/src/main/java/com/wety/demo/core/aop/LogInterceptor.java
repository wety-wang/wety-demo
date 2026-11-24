package com.wety.demo.core.aop;

import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.core.misc.AppThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求响应日志 AOP
 *
 * @author wety
 */
@Aspect
@Component
public class LogInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    /**
     * 执行拦截
     */
    // TODO 更换包路径
    @Around("execution(* com.wety.demo.module.*.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 生成请求唯一 id
        String requestId = UUIDGenerator.generatorUUID();
        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url, AppThreadLocal.getInstance().getClientIp(), reqParam);
        // 执行原方法
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable throwable = null;
        try {
            result = point.proceed();
        }catch (Throwable t){
            throwable = t;
        }
        long endTime = System.currentTimeMillis();
        // 输出响应日志
        log.info("request end, id: {}, cost: {}ms", requestId, endTime-startTime);
        if(throwable != null){
            throw throwable;
        }
        return result;
    }
}
