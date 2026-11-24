package com.wety.demo.module.organization.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.dto.UniversalCodeDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.organization.dto.PositionAddDTO;
import com.wety.demo.module.organization.dto.PositionPageQueryDTO;
import com.wety.demo.module.organization.dto.PositionStatusUpdateDTO;
import com.wety.demo.module.organization.dto.PositionUpdateDTO;
import com.wety.demo.module.organization.service.PositionService;
import com.wety.demo.module.organization.vo.PositionInfoVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 岗位管理接口。
 */
@RestController
@RequestMapping(value = "/bac/org/position")
@Tag(name = "101-02-00 组织架构-岗位管理")
@Permission(code = "101-02-00", parentCode = "101-00-00", name = "岗位管理", order = 2)
public class PositionController {

    @Resource
    private PositionService positionService;

    /**
     * 分页查询岗位。
     *
     * @param queryDTO 查询参数
     * @return 岗位分页结果
     */
    @RequestMapping(value = "/listPageable", method = RequestMethod.POST)
    @Operation(summary = "分页查询岗位")
    @Permission(code = "101-02-01", parentCode = "101-02-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
    @OperationLog(
            operationType = OperationType.QUERY,
            spelKeys = {"dto=queryDTO"},
            descriptionTemplate = "'查看岗位列表'"
    )
    public Page<PositionInfoVO> listPageable(@RequestBody PositionPageQueryDTO queryDTO) {
        return positionService.listPageable(queryDTO);
    }

    /**
     * 查询全部启用岗位（下拉用，登录即可访问）。
     *
     * @return 启用岗位列表
     */
    @GetMapping(value = "/listAll")
    @Operation(summary = "查询全部启用岗位")
    public List<CodeNameDTO> listAll() {
        return positionService.listAll();
    }

    /**
     * 新增岗位。
     *
     * @param addDTO 新增岗位参数
     * @return 操作结果
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Operation(summary = "新增岗位")
    @Permission(code = "101-02-02", parentCode = "101-02-00", name = "新增岗位", leafNode = GlobalConst.YES, order = 2)
    @OperationLog(
            operationType = OperationType.CREATE,
            spelKeys = {"dto=addDTO"},
            descriptionTemplate = "'新增岗位：' + #dto.name"
    )
    public String add(@RequestBody PositionAddDTO addDTO) {
        positionService.add(addDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 修改岗位。
     *
     * @param updateDTO 修改岗位参数
     * @return 操作结果
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Operation(summary = "修改岗位")
    @Permission(code = "101-02-03", parentCode = "101-02-00", name = "修改岗位", leafNode = GlobalConst.YES, order = 3)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=updateDTO"},
            descriptionTemplate = "'修改岗位：' + #dto.code + ' -> ' + #dto.name"
    )
    public String update(@RequestBody PositionUpdateDTO updateDTO) {
        positionService.update(updateDTO);
        return GlobalConst.SUCCESS;
    }

    /**
     * 删除岗位。
     *
     * @param codeDTO 岗位编码参数
     * @return 操作结果
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Operation(summary = "删除岗位")
    @Permission(code = "101-02-04", parentCode = "101-02-00", name = "删除岗位", leafNode = GlobalConst.YES, order = 4)
    @OperationLog(
            operationType = OperationType.DELETE,
            spelKeys = {"dto=codeDTO"},
            descriptionTemplate = "'删除岗位：' + #dto.code"
    )
    public String delete(@RequestBody UniversalCodeDTO codeDTO) {
        positionService.delete(codeDTO.getCode());
        return GlobalConst.SUCCESS;
    }

    /**
     * 启停岗位。
     *
     * @param statusUpdateDTO 启停参数
     * @return 操作结果
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @Operation(summary = "启停岗位")
    @Permission(code = "101-02-05", parentCode = "101-02-00", name = "启停岗位", leafNode = GlobalConst.YES, order = 5)
    @OperationLog(
            operationType = OperationType.UPDATE,
            spelKeys = {"dto=statusUpdateDTO"},
            descriptionTemplate = "'更新岗位状态：' + #dto.code + ' -> ' + (#dto.status == 1 ? '启用' : '停用')"
    )
    public String updateStatus(@RequestBody PositionStatusUpdateDTO statusUpdateDTO) {
        positionService.updateStatus(statusUpdateDTO);
        return GlobalConst.SUCCESS;
    }
}
