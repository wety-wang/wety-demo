package com.wety.demo.core.filter;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.core.authorization.AuthorizationChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 鉴权拦截器，既针对指定的请求进行用户权限校验，防止资源被未授权的用户访问
 *
 * @author wety
 */
@Component
@ConditionalOnProperty(name = "application.authorization.switch", havingValue = "true")
public class AuthorizationFilter extends AbstractFilter{

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Resource
    private AuthorizationChecker authorizationChecker;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        boolean hasPermission = authorizationChecker.hasPermission(httpRequest, (HttpServletResponse) response);

        String accountName = request.getParameter("ACCOUNTNAME");
        String path = httpRequest.getServletPath().replace("//", "/");

        if (hasPermission) {
            chain.doFilter(request, response);
        } else {
            log.warn("{}权限不足: {}", accountName, path);
            renderFailure(GlobalReturnCodeEnum.ACCESS_DENIED, response);
        }
    }

}
