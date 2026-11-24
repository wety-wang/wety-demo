package com.wety.demo.core.filter;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.core.authentication.AuthenticationChecker;
import com.wety.demo.core.authentication.NoLoginCache;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.auditlog.service.LoginLogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 后台管理端认证过滤器，用于确保用户已经登录才能访问特定的请求
 *
 * @author wety
 */
@Component
@ConditionalOnProperty(name = "application.authentication.switch", havingValue = "true")
public class AuthenticationFilter extends AbstractFilter {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Resource
    private AuthenticationChecker<AccountEntity> authenticationChecker;

    @Resource
    private LoginLogService loginLogService;

    @Value("${application.authentication.exclude.urls:}")
    private String excludeUrls;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final Set<String> excludeUrlSet = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) {
        if (!StringUtils.isEmpty(excludeUrls)) {
            String[] excludeUrlArray = excludeUrls.split(",");
            Collections.addAll(excludeUrlSet, excludeUrlArray);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath().replace("//", "/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        /**
         * excludeUrlSet 无需登录的路径（支持 Ant 风格通配符，如 /**）
         */
        String finalPath = path;
        boolean isExcluded = excludeUrlSet.stream().anyMatch(pattern -> pathMatcher.match(pattern, finalPath));
        if (isExcluded || NoLoginCache.getRequestMappingNoLoginList().contains(path)) {
            chain.doFilter(request, response);
            return;
        }
        AccountEntity account = authenticationChecker.checkAuthentication((HttpServletRequest) request);
        AppThreadLocal.getInstance().putCurrentAccount(account);
        if (account == null) {
            log.warn("认证失败");
            renderFailure(GlobalReturnCodeEnum.LOGIN_INVALID, response);
            return;
        }
        loginLogService.updateLastOperationTime();
        // 认证通过后我们就继续往后走
        chain.doFilter(request, response);
    }

}
