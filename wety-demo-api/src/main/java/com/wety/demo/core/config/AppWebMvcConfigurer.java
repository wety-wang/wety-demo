package com.wety.demo.core.config;

import com.wety.demo.core.http.MappingJackson2HttpMessageConverterWrapper;
import com.wety.demo.core.http.StringHttpMessageConverterWrapper;
import com.wety.demo.core.interceptor.ResponseTimeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author wety
 */
@Component
public class AppWebMvcConfigurer implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(AppWebMvcConfigurer.class);

    @Value("${spring.jackson.date-format}")
    private String dateFormat;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/","file:static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ResponseTimeInterceptor());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Configured HttpMessageConverter list before customization: {}", converters);
        converters.set(0, new MappingJackson2HttpMessageConverterWrapper());
        for (int i = 0; i < converters.size(); i++) {
            HttpMessageConverter<?> converter = converters.get(i);
            if (converter instanceof StringHttpMessageConverter) {
                converters.set(i, new StringHttpMessageConverterWrapper());
            } else if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverterWrapper messageConverterWrapper = new MappingJackson2HttpMessageConverterWrapper();
                messageConverterWrapper.getObjectMapper().setDateFormat(new SimpleDateFormat(dateFormat));
                converters.set(i, messageConverterWrapper);
            }
        }
        log.info("Configured HttpMessageConverter list after customization: {}", converters);
    }
}
