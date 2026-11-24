package com.wety.demo.module.user.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.user.constant.AccountTypeConst;
import com.wety.demo.module.user.dto.UserAddDTO;
import com.wety.demo.module.user.dto.UserPageQueryDTO;
import com.wety.demo.module.user.dto.UserUpdateDTO;
import com.wety.demo.module.user.service.UserManageService;
import com.wety.demo.module.user.util.LogDescriptionBuilder;
import com.wety.demo.module.user.vo.AccountInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户管理
 *
 * @author wety
 */
@RestController
@RequestMapping("/bac/userManage/user")
@Api(tags = "101-01-00 用户管理-用户管理", produces = "application/json", consumes = "application/json")
@Permission(code = "101-00-00", name = "用户管理", order = 101)
@Permission(code = "101-01-00", parentCode = "101-00-00", name = "用户管理", order = 1)
public class UserManageController {

    @Resource
    private UserManageService userManageService;

    @RequestMapping(value = "/listPageable", method = RequestMethod.POST)
    @ApiOperation(value = "列表页")
    @Permission(code = "101-01-01", parentCode = "101-01-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=queryDTO"},
            beanClasses = {LogDescriptionBuilder.class},
            findMethodNames = {"desc=buildNormalUserListPageableDesc(#dto)"},
            descriptionTemplate = "#desc"
    )
    public Page<AccountInfoVO> listPageable(@RequestBody UserPageQueryDTO queryDTO){
        return userManageService.listPageable(queryDTO);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增")
    @Permission(code = "101-01-02", parentCode = "101-01-00", name = "新增", leafNode = GlobalConst.YES, order = 2)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=addDTO"},
            descriptionTemplate = "'新增用户：' + #dto.loginName"
    )
    public String save(@RequestBody UserAddDTO addDTO) {
        if (StringUtils.isEmpty(addDTO.getType())) {
            addDTO.setType(AccountTypeConst.NORMAL);
        }
        userManageService.add(addDTO);
        return GlobalConst.SUCCESS;
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value = "查询用户信息")
    public AccountInfoVO query(@RequestBody UniversalCodeDTO codeDTO) {
        return userManageService.query(codeDTO.getCode());
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "修改")
    @Permission(code = "101-01-03", parentCode = "101-01-00", name = "修改", leafNode = GlobalConst.YES, order = 3)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=updateDTO"},
            descriptionTemplate = "'修改用户：' + #dto.code"
    )
    public String update(@RequestBody UserUpdateDTO updateDTO) {
        userManageService.update(updateDTO);
        return GlobalConst.SUCCESS;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除")
    @Permission(code = "101-01-04", parentCode = "101-01-00", name = "删除", leafNode = GlobalConst.YES, order = 4)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=codeDTO"},
            descriptionTemplate = "'删除用户：' + #dto.code"
    )
    public String delete(@RequestBody UniversalCodeDTO codeDTO) {
        userManageService.delete(codeDTO.getCode());
        return GlobalConst.SUCCESS;
    }

}
