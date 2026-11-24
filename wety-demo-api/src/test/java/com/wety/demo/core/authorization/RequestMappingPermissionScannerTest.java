package com.wety.demo.core.authorization;

import com.wety.demo.core.annotation.PermissionHolder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link RequestMappingPermissionScanner} 单元测试。
 *
 * <p>验证扫描器能同时识别 {@code @RequestMapping}、{@code @PostMapping}、{@code @GetMapping}，
 * 避免使用组合映射注解的控制器在启动扫描时丢失权限映射或抛异常。</p>
 */
class RequestMappingPermissionScannerTest {

    @Test
    void scanRequestMappingPermissions_shouldRecognizePostMappingAndRequestMappingControllers() {
        RequestMappingPermissionScanner scanner = new RequestMappingPermissionScanner();
        scanner.scanRequestMappingPermissions("com.wety.demo.module");

        Map<String, PermissionHolder> mapping = PermissionCache.getRequestMappingPermissionMapping();
        assertNotNull(mapping);

        // RoleController 使用 @PostMapping(value = "...")，必须被正确扫描
        assertNotNull(mapping.get("/bac/org/role/listPageable"));
        assertEquals("101-04-01", mapping.get("/bac/org/role/listPageable").getCode());
        assertNotNull(mapping.get("/bac/org/role/queryDataScope"));
        assertEquals("101-04-10", mapping.get("/bac/org/role/queryDataScope").getCode());
        assertNotNull(mapping.get("/bac/org/role/updateDataScope"));
        assertEquals("101-04-11", mapping.get("/bac/org/role/updateDataScope").getCode());

        // DeptController / PositionController / OrgUserController 使用 @RequestMapping(method = POST)
        assertNotNull(mapping.get("/bac/org/dept/tree"));
        assertEquals("101-01-01", mapping.get("/bac/org/dept/tree").getCode());
        assertNotNull(mapping.get("/bac/org/position/listPageable"));
        assertEquals("101-02-01", mapping.get("/bac/org/position/listPageable").getCode());
        assertNotNull(mapping.get("/bac/org/user/listPageable"));
        assertEquals("101-03-01", mapping.get("/bac/org/user/listPageable").getCode());

        // 审计日志控制器
        assertNotNull(mapping.get("/bac/log/loginLog/listPageable"));
        assertEquals("102-01-01", mapping.get("/bac/log/loginLog/listPageable").getCode());
        assertNotNull(mapping.get("/bac/log/operationLog/listPageable"));
        assertEquals("102-02-01", mapping.get("/bac/log/operationLog/listPageable").getCode());
    }
}
