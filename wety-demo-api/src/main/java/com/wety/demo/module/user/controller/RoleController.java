package com.wety.demo.module.user.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.core.annotation.PermissionHolder;
import com.wety.demo.core.authorization.PermissionCache;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.user.dto.*;
import com.wety.demo.module.user.service.PermissionService;
import com.wety.demo.module.user.service.RoleService;
import com.wety.demo.module.user.service.RoleUserService;
import com.wety.demo.module.user.service.UserService;
import com.wety.demo.module.user.util.LogDescriptionBuilder;
import com.wety.demo.module.user.vo.RoleInfoVO;
import com.wety.demo.module.user.vo.RolePermissionVO;
import com.wety.demo.module.user.vo.RoleUserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色管理
 *
 * @author wety
 */
@RestController
@RequestMapping("/bac/userManage/role")
@Api(tags = "101-02-00 用户管理 角色管理", produces = "application/json", consumes = "application/json")
@Permission(code = "101-02-00", parentCode = "101-00-00", name = "角色管理", order = 2)
public class RoleController {

    @Resource
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/listPageable", method = RequestMethod.POST)
    @ApiOperation(value = "列表页")
    @Permission(code = "101-02-01", parentCode = "101-02-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=pageDTO"},
            beanClasses = {LogDescriptionBuilder.class},
            findMethodNames = {"desc=buildRoleListPageableDesc(#dto)"},
            descriptionTemplate = "#desc"
    )
    public Page<RoleInfoVO> listPageable(@RequestBody RolePageQueryDTO pageDTO) {
        return roleService.listPageable(pageDTO);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增")
    @Permission(code = "101-02-02", parentCode = "101-02-00", name = "新增", leafNode = GlobalConst.YES, order = 2)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=saveDTO"},
            descriptionTemplate = "'新增角色：' + #dto.name"
    )
    public String add(@RequestBody RoleAddDTO saveDTO) {
        roleService.add(saveDTO);
        return GlobalConst.SUCCESS;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "修改")
    @Permission(code = "101-02-03", parentCode = "101-02-00", name = "修改", leafNode = GlobalConst.YES, order = 3)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=saveDTO"},
            descriptionTemplate = "'修改角色：' + #dto.code + ' -> ' + #dto.name"
    )
    public String update(@RequestBody RoleUpdateDTO saveDTO) {
        roleService.update(saveDTO);
        return GlobalConst.SUCCESS;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除")
    @Permission(code = "101-02-04", parentCode = "101-02-00", name = "删除", leafNode = GlobalConst.YES, order = 4)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=codeDTO"},
            descriptionTemplate = "'删除角色：' + #dto.code"
    )
    public String delete(@RequestBody UniversalCodeDTO codeDTO) {
        roleService.delete(codeDTO.getCode());
        return GlobalConst.SUCCESS;
    }

    @Resource
    private PermissionService permissionService;

    @RequestMapping(path = "/queryPermission", method = RequestMethod.POST)
    @ApiOperation(value = "查询角色权限", notes = "包含所有权限结构信息及指定角色拥有的权限")
    @Permission(code = "101-02-05", parentCode = "101-02-00", name = "查看权限", leafNode = GlobalConst.YES, order = 5)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=dto"},
            descriptionTemplate = "'查询角色权限：' + #dto.code"
    )
    public RolePermissionVO queryPermission(@RequestBody UniversalCodeDTO dto) {
        // 指定角色拥有的叶子权限编码
        List<String> codes = permissionService.listLeafPermissionCode(dto.getCode());
        // 全部的权限结构
        List<PermissionHolder> hierarchicalList = PermissionCache.getPermissionHierarchicalList();
        RolePermissionVO rolePermissionVO = new RolePermissionVO();
        rolePermissionVO.setCodes(codes);
        rolePermissionVO.setHierarchicalList(hierarchicalList);
        return rolePermissionVO;
    }

    @RequestMapping(path = "/updatePermission", method = RequestMethod.POST)
    @ApiOperation(value = "修改角色权限", notes = "包含所有权限结构信息及指定角色拥有的权限")
    @Permission(code = "101-02-06", parentCode = "101-02-00", name = "修改权限", leafNode = GlobalConst.YES, order = 6)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=dto"},
            descriptionTemplate = "'修改角色权限：' + #dto.roleCode + '，权限数=' + (#dto.permissionCodeList == null ? 0 : #dto.permissionCodeList.size())"
    )
    public String updatePermission(@RequestBody RolePermissionUpdateDTO dto) {
        permissionService.saveRolePermissions(dto.getRoleCode(), dto.getPermissionCodeList());
        return GlobalConst.SUCCESS;
    }

    @Resource
    private RoleUserService roleUserService;

    @RequestMapping(value = "/listRoleUserPageable", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询拥有该角色的用户")
    @Permission(code = "101-02-07", parentCode = "101-02-00", name = "查看关联用户", leafNode = GlobalConst.YES, order = 7)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=pageDTO"},
            beanClasses = {LogDescriptionBuilder.class},
            findMethodNames = {"desc=buildRoleUserListPageableDesc(#dto)"},
            descriptionTemplate = "#desc"
    )
    public Page<RoleUserInfoVO> listRoleUserPageable(@RequestBody RoleUserPageQueryDTO pageDTO) {
        return roleUserService.listPageable(pageDTO);
    }

    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    @ApiOperation(value = "查询所有普通用户")
    public List<CodeNameDTO> listAllUser() {
        return userService.listUser();
    }

    @RequestMapping(value = "/addRoleUser", method = RequestMethod.POST)
    @ApiOperation(value = "添加角色用户")
    @Permission(code = "101-02-08", parentCode = "101-02-00", name = "新增关联用户", leafNode = GlobalConst.YES, order = 8)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=roleUserAddDTO"},
            descriptionTemplate = "'新增关联用户到角色：' + #dto.roleCode + '，用户数=' + (#dto.accountCodes == null ? 0 : #dto.accountCodes.size())"
    )
    public String addRoleUser(@RequestBody RoleUserAddDTO roleUserAddDTO) {
        roleUserService.addRoleUser(roleUserAddDTO);
        return GlobalConst.SUCCESS;
    }

    @RequestMapping(path = "/deleteRoleUser", method = RequestMethod.POST)
    @ApiOperation(value = "删除角色用户")
    @Permission(code = "101-02-09", parentCode = "101-02-00", name = "删除关联用户", leafNode = GlobalConst.YES, order = 9)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=roleUserDeleteDTO"},
            descriptionTemplate = "'删除角色的关联用户：' + #dto.roleCode + ' -> ' + #dto.accountCode"
    )
    public String deleteRoleUser(@RequestBody RoleUserDeleteDTO roleUserDeleteDTO) {
        roleUserService.deleteRoleUser(roleUserDeleteDTO);
        return GlobalConst.SUCCESS;
    }

}
