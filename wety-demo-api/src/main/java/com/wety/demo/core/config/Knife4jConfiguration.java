package com.wety.demo.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Knife4j 4 / OpenAPI 3 接口文档配置类。
 *
 * <p>保留原有 {@code application.swagger.switch} 开关控制行为（YAML 中 {@code on} 对应 {@code true}）。</p>
 *
 * @author wety
 */
@Configuration
@ConditionalOnProperty(name = "application.swagger.switch", havingValue = "true")
public class Knife4jConfiguration {

    /** 服务端口，用于拼装文档服务条款地址。 */
    @Value("${server.port:8080}")
    private int port;

    /**
     * 配置 OpenAPI 3 基础文档信息。
     *
     * @return OpenAPI 文档元数据对象
     * @throws UnknownHostException 获取本机主机地址失败时抛出
     */
    @Bean
    public OpenAPI customOpenAPI() throws UnknownHostException {
        String host = InetAddress.getLocalHost().getHostAddress();
        return new OpenAPI()
                .info(new Info()
                        .title("wety-demo 接口规范")
                        .description("详见每一个接口说明。")
                        .termsOfService("http://" + host + ":" + port)
                        .contact(new Contact().name("wety"))
                        .version("1.0.0"));
    }

    /**
     * 配置默认接口分组，仅扫描业务模块包路径。
     *
     * @return 默认接口分组配置
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .packagesToScan("com.wety.demo.module")
                .pathsToMatch("/**")
                .build();
    }

}
