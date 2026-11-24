package com.wety.demo.module.auditlog.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.module.auditlog.dto.LoginLogPageQueryDTO;
import com.wety.demo.module.auditlog.service.LoginLogService;
import com.wety.demo.module.auditlog.util.LogDescriptionBuilder;
import com.wety.demo.module.auditlog.vo.LoginLogInfoVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 登录日志
 *
 * @author wety
 */
@RestController
@RequestMapping("/bac/log/loginLog")
@Tag(name = "102-01-00 审计日志-登录日志")
@Permission(code = "102-00-00", name = "审计日志", order = 102)
@Permission(code = "102-01-00", parentCode = "102-00-00", name = "登录日志", order = 1)
public class LoginLogController {

    @Resource
    private LoginLogService loginLogService;

    /**
     * 登录日志列表查询
     */
    @OperationLog(
            spelKeys = {"dto=queryDTO"},
            beanClasses = {LogDescriptionBuilder.class},
            findMethodNames = {"desc=buildListPageableDesc(#dto)"},
            descriptionTemplate = "#desc"
    )
    @RequestMapping(value = "/listPageable", method = RequestMethod.POST)
    @Operation(summary = "列表页")
    @Permission(code = "102-01-01", parentCode = "102-01-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
    public Page<LoginLogInfoVO> listPageable(@RequestBody LoginLogPageQueryDTO queryDTO){
        return loginLogService.listLoginLogPageable(queryDTO);
    }

}
