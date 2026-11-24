package com.wety.demo.core.http;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.response.ResponseResult;
import com.wety.demo.common.util.JsonUtils;
import com.wety.demo.core.misc.AppThreadLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过切面实现 把业务层Controller的响应包装成公共的响应对象ResponseResult
 *
 * @author wety
 */
@Component
@Aspect
@Order(Integer.MAX_VALUE - 1)
public class ControllerNullResponseWrapper {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void requestMappingPointCut() {
        // for pointcut perpurse
    }

    @Around(value = "requestMappingPointCut()", argNames = "proceedingJoinPoint")
    public Object requestMappingAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 执行业务方法
        Object bizResult = proceedingJoinPoint.proceed();
        if (bizResult == null) {
            ResponseResult responseResult = new ResponseResult();
            responseResult.setCode(GlobalReturnCodeEnum.SUCCESS.getCode());
            responseResult.setData(null);
            responseResult.setDuration(AppThreadLocal.getInstance().getProcessingDuration());

            // 如果业务代码里明确设置了code和message我们就取
            if (AppThreadLocal.getInstance().getReturnCode() != null) {
                responseResult.setCode(AppThreadLocal.getInstance().getReturnCode());
            }
            if (AppThreadLocal.getInstance().getReturnMessage() != null) {
                responseResult.setMessage(AppThreadLocal.getInstance().getReturnMessage());
            }
            HttpServletResponse response = AppThreadLocal.getInstance().getRequestResponse();
            if (response != null) {
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    JsonUtils.populateJson(responseResult, outputStream);
                }
            }
        }
        return bizResult;
    }
}
