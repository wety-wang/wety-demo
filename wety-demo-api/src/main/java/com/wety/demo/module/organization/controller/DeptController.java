package com.wety.demo.module.organization.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.organization.dto.DeptAddDTO;
import com.wety.demo.module.organization.dto.DeptStatusUpdateDTO;
import com.wety.demo.module.organization.dto.DeptTreeQueryDTO;
import com.wety.demo.module.organization.dto.DeptUpdateDTO;
import com.wety.demo.module.organization.service.DeptService;
import com.wety.demo.module.organization.vo.DeptTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门管理接口。
 *
 * <p>方法级映射显式使用 {@code @RequestMapping(value = "...", method = RequestMethod.POST)}，
 * 以兼容框架 {@code RequestMappingPermissionScanner} 的权限路由扫描机制。</p>
 */
@RestController
@RequestMapping(value = "/bac/org/dept")
@Api(tags = "101-01-00 组织架构-部门管理", produces = "application/json", consumes = "application/json")
@Permission(code = "101-00-00", name = "组织架构", order = 101)
@Permission(code = "101-01-00", parentCode = "101-00-00", name = "部门管理", order = 1)
public class DeptController {

    @Resource
    private DeptService deptService;

    /**
     * 查询部门树。
     *
     * @param queryDTO 查询条件
     * @return 部门树列表
     */
    @RequestMapping(value = "/tree", method = RequestMethod.POST)
    @ApiOperation(value = "查询部门树")
    @Permission(code = "101-01-01", parentCode = "101-01-00", name = "查看部门树", leafNode = GlobalConst.YES, order = 1)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=queryDTO"},
            descriptionTemplate = "'查询部门树'"
    )
    public List<DeptTreeVO> tree(@RequestBody(required = false) DeptTreeQueryDTO queryDTO) {
        return deptService.tree(queryDTO);
    }

    /**
     * 新增部门。
     *
     * @param addDTO 新增部门参数
     * @return 操作结果
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "新增部门")
    @Permission(code = "101-01-02", parentCode = "101-01-00", name = "新增部门", leafNode = GlobalConst.YES, order = 2)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=addDTO"},
            descriptionTemplate = "'新增部门：' + #dto.name"
    )
    public String add(@RequestBody DeptAddDTO addDTO) {
        deptService.add(addDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 修改部门。
     *
     * @param updateDTO 修改部门参数
     * @return 操作结果
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "修改部门")
    @Permission(code = "101-01-03", parentCode = "101-01-00", name = "修改部门", leafNode = GlobalConst.YES, order = 3)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=updateDTO"},
            descriptionTemplate = "'修改部门：' + #dto.code + ' -> ' + #dto.name"
    )
    public String update(@RequestBody DeptUpdateDTO updateDTO) {
        deptService.update(updateDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 删除部门。
     *
     * @param codeDTO 部门编码参数
     * @return 操作结果
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除部门")
    @Permission(code = "101-01-04", parentCode = "101-01-00", name = "删除部门", leafNode = GlobalConst.YES, order = 4)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=codeDTO"},
            descriptionTemplate = "'删除部门：' + #dto.code"
    )
    public String delete(@RequestBody UniversalCodeDTO codeDTO) {
        deptService.delete(codeDTO.getCode());
        return GlobalConst.SUCCESS;
    }

    /**
     * 启停部门。
     *
     * @param statusUpdateDTO 启停参数
     * @return 操作结果
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ApiOperation(value = "启停部门")
    @Permission(code = "101-01-05", parentCode = "101-01-00", name = "启停部门", leafNode = GlobalConst.YES, order = 5)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=statusUpdateDTO"},
            descriptionTemplate = "'更新部门状态：' + #dto.code + ' -> ' + (#dto.status == 1 ? '启用' : '停用')"
    )
    public String updateStatus(@RequestBody DeptStatusUpdateDTO statusUpdateDTO) {
        deptService.updateStatus(statusUpdateDTO);
        return GlobalConst.SUCCESS;
    }
}
