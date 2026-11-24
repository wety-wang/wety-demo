package com.wety.demo.core.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wety
 */
@Configuration
public class FilterConfig {

    @Value("${application.filter.pattern:}")
    private String[] allUrlPatterns;

    @Bean
    public FilterRegistrationBean<ClientIpSyncFilter> clientIpSyncFilterRegistration(ClientIpSyncFilter clientIpSyncFilter) {
        FilterRegistrationBean<ClientIpSyncFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(clientIpSyncFilter);
        filterRegistrationBean.addUrlPatterns(allUrlPatterns);
        filterRegistrationBean.setOrder(0);
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnBean(AuthenticationFilter.class)
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration(AuthenticationFilter authenticationFilter) {
        FilterRegistrationBean<AuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(authenticationFilter);
        filterRegistrationBean.addUrlPatterns(allUrlPatterns);
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnBean(AuthorizationFilter.class)
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration(AuthorizationFilter authorizationFilter) {
        FilterRegistrationBean<AuthorizationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(authorizationFilter);
        filterRegistrationBean.addUrlPatterns(allUrlPatterns);
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<HttpMessageConverterWrapMarkingFilter> httpMessageConverterWrapMarkingFilterRegistration(HttpMessageConverterWrapMarkingFilter httpMessageConverterWrapMarkingFilter) {
        FilterRegistrationBean<HttpMessageConverterWrapMarkingFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(httpMessageConverterWrapMarkingFilter);
        filterRegistrationBean.addUrlPatterns(allUrlPatterns);
        filterRegistrationBean.setOrder(3);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<AppThreadLocalCleanFilter> appThreadLocalCleanFilterRegistration(AppThreadLocalCleanFilter appThreadLocalCleanFilter) {
        FilterRegistrationBean<AppThreadLocalCleanFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(appThreadLocalCleanFilter);
        filterRegistrationBean.addUrlPatterns(allUrlPatterns);
        filterRegistrationBean.setOrder(4);
        return filterRegistrationBean;
    }
}
