package com.wety.demo.core.datascope;

import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.core.misc.AppThreadLocal;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 数据权限环绕切面。
 *
 * <p>拦截标注了 {@link DataScope} 的方法，解析并绑定当前线程的 {@link DataScopeContext}，
 * 支持嵌套调用时保存与恢复外层上下文，确保方法退出后清理线程变量避免污染。</p>
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class DataScopeAspect {

    @Resource
    private DataScopeResolver dataScopeResolver;

    /**
     * 环绕增强：绑定数据权限上下文并在执行结束后恢复或清理。
     *
     * @param joinPoint 切入点
     * @param dataScope 数据权限注解
     * @return 原方法返回值
     * @throws Throwable 原方法抛出的异常
     */
    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint joinPoint, DataScope dataScope) throws Throwable {
        if (dataScope == null || !dataScope.enabled()) {
            return joinPoint.proceed();
        }

        AppThreadLocal threadLocal = AppThreadLocal.getInstance();
        DataScopeContext previousContext = threadLocal.getDataScopeContext();
        AccountEntity currentAccount = threadLocal.getCurrentAccount();

        DataScopeContext currentContext = dataScopeResolver.resolve(
                currentAccount, dataScope.deptField(), dataScope.userField());
        threadLocal.setDataScopeContext(currentContext);
        try {
            return joinPoint.proceed();
        } finally {
            if (previousContext != null) {
                threadLocal.setDataScopeContext(previousContext);
            } else {
                threadLocal.clearDataScopeContext();
            }
        }
    }
}
