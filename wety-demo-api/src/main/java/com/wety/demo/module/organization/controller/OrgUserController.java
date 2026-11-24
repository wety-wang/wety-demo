package com.wety.demo.module.organization.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.organization.dto.OrgUserAddDTO;
import com.wety.demo.module.organization.dto.OrgUserPageQueryDTO;
import com.wety.demo.module.organization.dto.OrgUserResetPasswordDTO;
import com.wety.demo.module.organization.dto.OrgUserStatusUpdateDTO;
import com.wety.demo.module.organization.dto.OrgUserUpdateDTO;
import com.wety.demo.module.organization.service.OrgUserService;
import com.wety.demo.module.organization.vo.OrgUserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 组织架构用户管理接口。
 */
@RestController
@RequestMapping(value = "/bac/org/user")
@Api(tags = "101-03-00 组织架构-用户管理", produces = "application/json", consumes = "application/json")
@Permission(code = "101-03-00", parentCode = "101-00-00", name = "用户管理", order = 3)
public class OrgUserController {

    @Resource
    private OrgUserService orgUserService;

    /**
     * 分页查询用户。
     *
     * @param queryDTO 查询条件
     * @return 用户分页结果
     */
    @RequestMapping(value = "/listPageable", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询用户")
    @Permission(code = "101-03-01", parentCode = "101-03-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=queryDTO"},
            descriptionTemplate = "'查看用户列表'"
    )
    public Page<OrgUserInfoVO> listPageable(@RequestBody OrgUserPageQueryDTO queryDTO) {
        return orgUserService.listPageable(queryDTO);
    }

    /**
     * 查询用户详情（登录即可访问，服务层执行数据权限防越权校验）。
     *
     * @param codeDTO 用户编码参数
     * @return 用户详情
     */
    @PostMapping(value = "/query")
    @ApiOperation(value = "查询用户详情")
    public OrgUserInfoVO query(@RequestBody UniversalCodeDTO codeDTO) {
        return orgUserService.query(codeDTO.getCode());
    }

    /**
     * 新增用户。
     *
     * @param addDTO 新增用户参数
     * @return 操作结果
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增用户")
    @Permission(code = "101-03-02", parentCode = "101-03-00", name = "新增用户", leafNode = GlobalConst.YES, order = 2)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=addDTO"},
            descriptionTemplate = "'新增用户：' + #dto.loginName"
    )
    public String add(@RequestBody OrgUserAddDTO addDTO) {
        orgUserService.add(addDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 修改用户。
     *
     * @param updateDTO 修改用户参数
     * @return 操作结果
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "修改用户")
    @Permission(code = "101-03-03", parentCode = "101-03-00", name = "修改用户", leafNode = GlobalConst.YES, order = 3)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=updateDTO"},
            descriptionTemplate = "'修改用户：' + #dto.code"
    )
    public String update(@RequestBody OrgUserUpdateDTO updateDTO) {
        orgUserService.update(updateDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 删除用户。
     *
     * @param codeDTO 用户编码参数
     * @return 操作结果
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除用户")
    @Permission(code = "101-03-04", parentCode = "101-03-00", name = "删除用户", leafNode = GlobalConst.YES, order = 4)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=codeDTO"},
            descriptionTemplate = "'删除用户：' + #dto.code"
    )
    public String delete(@RequestBody UniversalCodeDTO codeDTO) {
        orgUserService.delete(codeDTO.getCode());
        return GlobalConst.SUCCESS;
    }

    /**
     * 启停用户。
     *
     * @param statusUpdateDTO 启停参数
     * @return 操作结果
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "启停用户")
    @Permission(code = "101-03-05", parentCode = "101-03-00", name = "启停用户", leafNode = GlobalConst.YES, order = 5)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=statusUpdateDTO"},
            descriptionTemplate = "'更新用户状态：' + #dto.code + ' -> ' + (#dto.disabled == 1 ? '禁用' : '启用')"
    )
    public String updateStatus(@RequestBody OrgUserStatusUpdateDTO statusUpdateDTO) {
        orgUserService.updateStatus(statusUpdateDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 重置用户密码。
     *
     * @param resetDTO 重置密码参数
     * @return 操作结果
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ApiOperation(value = "重置用户密码")
    @Permission(code = "101-03-06", parentCode = "101-03-00", name = "重置密码", leafNode = GlobalConst.YES, order = 6)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=resetDTO"},
            descriptionTemplate = "'重置用户密码：' + #dto.code"
    )
    public String resetPassword(@RequestBody OrgUserResetPasswordDTO resetDTO) {
        orgUserService.resetPassword(resetDTO);
        return GlobalConst.SUCCESS;
    }
}
