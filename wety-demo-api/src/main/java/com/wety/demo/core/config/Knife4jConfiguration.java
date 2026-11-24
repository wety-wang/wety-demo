package com.wety.demo.core.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wety
 */
@EnableSwagger2
@EnableKnife4j
@Configuration
public class Knife4jConfiguration {

    @Value("${application.swagger.switch:off}")
    private boolean enable;

    @Value("${server.port:8080}")
    private int port;

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() throws UnknownHostException {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enable)
                .select()
                // TODO 更换包路径
                .apis(RequestHandlerSelectors.basePackage("com.wety.demo.module"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() throws UnknownHostException {
        String host= InetAddress.getLocalHost().getHostAddress();
        // TODO 更换接口信息
        return new ApiInfoBuilder()
                .title("wety-demo 接口规范")
                .description("详见每一个接口说明。")
                .termsOfServiceUrl("http://"+host+":"+port)
                .contact(new Contact("wety", null, null))
                .version("1.0.0")
                .build();
    }

}
