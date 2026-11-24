package com.wety.demo.core.filter;

import com.wety.demo.core.misc.AppThreadLocal;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
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
