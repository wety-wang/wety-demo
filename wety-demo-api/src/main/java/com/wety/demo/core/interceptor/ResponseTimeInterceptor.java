package com.wety.demo.core.interceptor;

import com.wety.demo.core.misc.AppThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wety
 */
public class ResponseTimeInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ResponseTimeInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AppThreadLocal.getInstance().setProcessingStartTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Do nothing
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long endTime = System.currentTimeMillis();
        long startTime = AppThreadLocal.getInstance().getProcessingStartTime();
        long processingDuration = endTime-startTime;
        AppThreadLocal.getInstance().setProcessingDuration(processingDuration);
        log.info("Request processing duration: {} ms", processingDuration);
    }
}
