package com.wety.demo.core.filter;

import com.wety.demo.core.misc.AppThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Filter for thread-local variables cleaning, removing object binded to current
 * thread after the request-process was finished.
 *
 * @author wety
 */
@Component
public class AppThreadLocalCleanFilter extends AbstractFilter {

    private static final Logger log = LoggerFactory.getLogger(AppThreadLocalCleanFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {
        chain.doFilter(request, response);
        AppThreadLocal.getInstance().removeBinding();
        log.info("removing thread local binding...");
    }
}
