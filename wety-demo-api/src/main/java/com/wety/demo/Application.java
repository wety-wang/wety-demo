package com.wety.demo;

import com.wety.demo.common.jpa.support.BaseRepositoryFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wety
 */
@ServletComponentScan
// TODO 修改包名
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class, basePackages = { "com.wety.demo" })
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application=SpringApplication.run(Application.class, args);
        Environment env = application.getEnvironment();
        String host= InetAddress.getLocalHost().getHostAddress();
        String port=env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n" +
                        "\tApplication is running! Access URLs:\n" +
                        "\tLocal: \t\thttp://localhost:{}\n" +
                        "\tExternal: \thttp://{}:{}\n"+
                        "\tDoc: \t\thttp://{}:{}/doc.html\n"+
                        "----------------------------------------------------------",
                port, host, port, host,port
        );
    }

}
