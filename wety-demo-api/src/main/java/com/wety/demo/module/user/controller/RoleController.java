package com.wety.demo.module.user.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.core.annotation.PermissionHolder;
import com.wety.demo.core.authorization.PermissionCache;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.user.dto.RoleAddDTO;
import com.wety.demo.module.user.dto.RoleDataScopeUpdateDTO;
import com.wety.demo.module.user.dto.RolePageQueryDTO;
import com.wety.demo.module.user.dto.RolePermissionUpdateDTO;
import com.wety.demo.module.user.dto.RoleUpdateDTO;
import com.wety.demo.module.user.dto.RoleUserAddDTO;
import com.wety.demo.module.user.dto.RoleUserDeleteDTO;
import com.wety.demo.module.user.dto.RoleUserPageQueryDTO;
import com.wety.demo.module.user.service.PermissionService;
import com.wety.demo.module.user.service.RoleService;
import com.wety.demo.module.user.service.RoleUserService;
import com.wety.demo.module.user.service.UserService;
import com.wety.demo.module.user.util.LogDescriptionBuilder;
import com.wety.demo.module.user.vo.RoleDataScopeVO;
import com.wety.demo.module.user.vo.RoleInfoVO;
import com.wety.demo.module.user.vo.RolePermissionVO;
import com.wety.demo.module.user.vo.RoleUserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组织架构-角色管理接口。
 *
 * @author wety
 */
@RestController
@RequestMapping(value = "/bac/org/role")
@Api(tags = "101-04-00 组织架构 角色管理", produces = "application/json", consumes = "application/json")
@Permission(code = "101-04-00", parentCode = "101-00-00", name = "角色管理", order = 4)
public class RoleController {

    @Resource
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleUserService roleUserService;

    /**
     * 分页查询角色列表。
     *
     * @param pageDTO 分页查询参数
     * @return 角色分页结果
     */
    @PostMapping(value = "/listPageable")
    @ApiOperation(value = "分页查询角色")
    @Permission(code = "101-04-01", parentCode = "101-04-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
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

    /**
     * 查询全部有效角色（供下拉选择使用）。
     *
     * @return 有效角色列表
     */
    @GetMapping(value = "/listAll")
    @ApiOperation(value = "查询全部有效角色")
    public List<CodeNameDTO> listAll() {
        return roleService.listAll();
    }

    /**
     * 新增角色。
     *
     * @param saveDTO 新增角色参数
     * @return 操作结果
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增角色")
    @Permission(code = "101-04-02", parentCode = "101-04-00", name = "新增角色", leafNode = GlobalConst.YES, order = 2)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=saveDTO"},
            descriptionTemplate = "'新增角色：' + #dto.name"
    )
    public String add(@RequestBody RoleAddDTO saveDTO) {
        roleService.add(saveDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 修改角色。
     *
     * @param saveDTO 修改角色参数
     * @return 操作结果
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "修改角色")
    @Permission(code = "101-04-03", parentCode = "101-04-00", name = "修改角色", leafNode = GlobalConst.YES, order = 3)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=saveDTO"},
            descriptionTemplate = "'修改角色：' + #dto.code + ' -> ' + #dto.name"
    )
    public String update(@RequestBody RoleUpdateDTO saveDTO) {
        roleService.update(saveDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 软删除角色。
     *
     * @param codeDTO 角色业务编码
     * @return 操作结果
     */
    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除角色")
    @Permission(code = "101-04-04", parentCode = "101-04-00", name = "删除角色", leafNode = GlobalConst.YES, order = 4)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=codeDTO"},
            descriptionTemplate = "'删除角色：' + #dto.code"
    )
    public String delete(@RequestBody UniversalCodeDTO codeDTO) {
        roleService.delete(codeDTO.getCode());
        return GlobalConst.SUCCESS;
    }

    /**
     * 查询角色菜单权限。
     *
     * @param dto 角色业务编码
     * @return 菜单权限树及已选叶子编码
     */
    @PostMapping(value = "/queryPermission")
    @ApiOperation(value = "查询角色菜单权限", notes = "包含所有权限结构信息及指定角色拥有的权限")
    @Permission(code = "101-04-05", parentCode = "101-04-00", name = "查看菜单权限", leafNode = GlobalConst.YES, order = 5)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=dto"},
            descriptionTemplate = "'查询角色权限：' + #dto.code"
    )
    public RolePermissionVO queryPermission(@RequestBody UniversalCodeDTO dto) {
        List<String> codes = permissionService.listLeafPermissionCode(dto.getCode());
        List<PermissionHolder> hierarchicalList = PermissionCache.getPermissionHierarchicalList();
        RolePermissionVO rolePermissionVO = new RolePermissionVO();
        rolePermissionVO.setCodes(codes);
        rolePermissionVO.setHierarchicalList(hierarchicalList);
        return rolePermissionVO;
    }

    /**
     * 修改角色菜单权限。
     *
     * @param dto 角色菜单权限修改参数
     * @return 操作结果
     */
    @PostMapping(value = "/updatePermission")
    @ApiOperation(value = "修改角色菜单权限", notes = "全量覆盖指定角色的菜单权限")
    @Permission(code = "101-04-06", parentCode = "101-04-00", name = "修改菜单权限", leafNode = GlobalConst.YES, order = 6)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=dto"},
            descriptionTemplate = "'修改角色权限：' + #dto.roleCode + '，权限数=' + (#dto.permissionCodeList == null ? 0 : #dto.permissionCodeList.size())"
    )
    public String updatePermission(@RequestBody RolePermissionUpdateDTO dto) {
        permissionService.saveRolePermissions(dto.getRoleCode(), dto.getPermissionCodeList());
        return GlobalConst.SUCCESS;
    }

    /**
     * 分页查询拥有该角色的用户。
     *
     * @param pageDTO 分页查询参数
     * @return 角色关联用户分页结果
     */
    @PostMapping(value = "/listRoleUserPageable")
    @ApiOperation(value = "分页查询拥有该角色的用户")
    @Permission(code = "101-04-07", parentCode = "101-04-00", name = "查看关联用户", leafNode = GlobalConst.YES, order = 7)
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

    /**
     * 查询全部未删除用户（供角色关联用户弹窗下拉使用）。
     *
     * @return 未删除用户列表
     */
    @GetMapping(value = "/listAllUser")
    @ApiOperation(value = "查询所有有效用户")
    public List<CodeNameDTO> listAllUser() {
        return userService.listUser();
    }

    /**
     * 添加角色关联用户。
     *
     * @param roleUserAddDTO 角色用户关联参数
     * @return 操作结果
     */
    @PostMapping(value = "/addRoleUser")
    @ApiOperation(value = "添加角色用户")
    @Permission(code = "101-04-08", parentCode = "101-04-00", name = "新增关联用户", leafNode = GlobalConst.YES, order = 8)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=roleUserAddDTO"},
            descriptionTemplate = "'新增关联用户到角色：' + #dto.roleCode + '，用户数=' + (#dto.accountCodes == null ? 0 : #dto.accountCodes.size())"
    )
    public String addRoleUser(@RequestBody RoleUserAddDTO roleUserAddDTO) {
        roleUserService.addRoleUser(roleUserAddDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 删除角色关联用户。
     *
     * @param roleUserDeleteDTO 角色用户解绑参数
     * @return 操作结果
     */
    @PostMapping(value = "/deleteRoleUser")
    @ApiOperation(value = "删除角色用户")
    @Permission(code = "101-04-09", parentCode = "101-04-00", name = "删除关联用户", leafNode = GlobalConst.YES, order = 9)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=roleUserDeleteDTO"},
            descriptionTemplate = "'删除角色的关联用户：' + #dto.roleCode + ' -> ' + #dto.accountCode"
    )
    public String deleteRoleUser(@RequestBody RoleUserDeleteDTO roleUserDeleteDTO) {
        roleUserService.deleteRoleUser(roleUserDeleteDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 查询角色数据权限配置。
     *
     * @param dto 角色业务编码
     * @return 角色数据权限详情
     */
    @PostMapping(value = "/queryDataScope")
    @ApiOperation(value = "查询角色数据权限")
    @Permission(code = "101-04-10", parentCode = "101-04-00", name = "查看数据权限", leafNode = GlobalConst.YES, order = 10)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=dto"},
            descriptionTemplate = "'查询角色数据权限：' + #dto.code"
    )
    public RoleDataScopeVO queryDataScope(@RequestBody UniversalCodeDTO dto) {
        return roleService.queryDataScope(dto.getCode());
    }

    /**
     * 修改角色数据权限配置。
     *
     * @param dto 角色数据权限修改参数
     * @return 操作结果
     */
    @PostMapping(value = "/updateDataScope")
    @ApiOperation(value = "修改角色数据权限")
    @Permission(code = "101-04-11", parentCode = "101-04-00", name = "修改数据权限", leafNode = GlobalConst.YES, order = 11)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=dto"},
            descriptionTemplate = "'修改角色数据权限：' + #dto.roleCode + '，范围=' + #dto.dataScope"
    )
    public String updateDataScope(@RequestBody RoleDataScopeUpdateDTO dto) {
        roleService.updateDataScope(dto);
        return GlobalConst.SUCCESS;
    }
}
