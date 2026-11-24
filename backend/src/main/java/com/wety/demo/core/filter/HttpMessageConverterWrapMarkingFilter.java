package com.wety.demo.core.filter;

import com.wety.demo.core.misc.AppThreadLocal;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用来标记HttpMessage的REST响应是否需要包装一下
 *
 * @author wety
 */
@Component
public class HttpMessageConverterWrapMarkingFilter extends AbstractFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
        AppThreadLocal.getInstance().setHttpMessageConverterWrapping(Boolean.TRUE);
        AppThreadLocal.getInstance().setRequestResponse((HttpServletResponse) response);
        chain.doFilter(request, response);
    }
}
