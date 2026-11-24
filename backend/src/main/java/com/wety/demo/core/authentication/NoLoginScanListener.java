package com.wety.demo.core.authentication;

import com.wety.demo.Application;
import com.wety.demo.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Spring Listener for NoLogin scan
 *
 * @author wety
 */
@Component("NoLoginScanListener")
public class NoLoginScanListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(NoLoginScanListener.class);

    @Resource
    private RequestMappingNoLoginScanner requestMappingNoLoginScanner;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("NoLoginScanListener start");
        try {
            requestMappingNoLoginScanner.scanRequestMappingNoLogins(Application.class.getPackage());
        } catch (Exception e) {
            log.error("Error occured while canning NoLogins");
            throw new CustomException(e);
        }
        log.info("NoLoginScanListener finished");
    }
}
