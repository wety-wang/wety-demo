package com.wety.demo.core.filter;

import com.wety.demo.common.util.HttpServletRequestUtil;
import com.wety.demo.core.misc.AppThreadLocal;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Synchronizing client ip address to thread-local variable.
 *
 * @author wety
 */
@Component
public class ClientIpSyncFilter extends AbstractFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
        // 认证通过后我们就继续往后走
        AppThreadLocal.getInstance().setClientIp(HttpServletRequestUtil.getClientIpAddr((HttpServletRequest) request));
        chain.doFilter(request, response);
    }

}
