package com.wety.demo.core.authorization;

import com.wety.demo.Application;
import com.wety.demo.common.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Spring Listener for permission scan
 *
 * @author wety
 */
@Component("PermissionScanListener")
public class PermissionScanListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(PermissionScanListener.class);

    @Resource
    private PermissionListHierarchyScanner permissionListHierarchyScanner;

    @Resource
    private RequestMappingPermissionScanner requestMappingPermissionScanner;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            doPermissionScan();
        } catch (Exception e) {
            log.error("Error occured while canning Permissions");
            throw new CustomException(e);
        }
    }

    private void doPermissionScan() throws Exception {
        log.info("PermissionScanListener start");
        try {
            permissionListHierarchyScanner.scanPermissionListHierarchy(Application.class.getPackage());

            requestMappingPermissionScanner.scanRequestMappingPermissions(Application.class.getPackage());
        } catch (Exception e) {
            throw new Exception(e);
        }
        log.info("PermissionScanListener finished");
    }
}
