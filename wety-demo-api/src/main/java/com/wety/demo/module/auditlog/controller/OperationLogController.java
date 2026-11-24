package com.wety.demo.module.auditlog.controller;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.core.annotation.OperationLog;
import com.wety.demo.core.annotation.Permission;
import com.wety.demo.core.annotation.PermissionHolder;
import com.wety.demo.core.authorization.PermissionCache;
import com.wety.demo.module.auditlog.dto.OperationLogPageQueryDTO;
import com.wety.demo.module.auditlog.service.OperationLogService;
import com.wety.demo.module.auditlog.util.LogDescriptionBuilder;
import com.wety.demo.module.auditlog.vo.OperationLogInfoVO;
import com.wety.demo.module.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志
 *
 * @author wety
 */
@RestController
@RequestMapping("/bac/log/operationLog")
@Tag(name = "102-02-00 审计日志-操作日志")
@Permission(code = "102-02-00", parentCode = "102-00-00", name = "操作日志", order = 2)
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;
    @Autowired
    private UserService userService;

    /**
     * 操作日志列表查询
     */
    @OperationLog(
            spelKeys = {"dto=queryDTO"},
            beanClasses = {LogDescriptionBuilder.class},
            findMethodNames = {"desc=buildOperationListPageableDesc(#dto)"},
            descriptionTemplate = "#desc"
    )
    @RequestMapping(value = "/listPageable", method = RequestMethod.POST)
    @Operation(summary = "列表页")
    @Permission(code = "102-02-01", parentCode = "102-02-00", name = "查看列表", leafNode = GlobalConst.YES, order = 1)
    public Page<OperationLogInfoVO> listPageable(@RequestBody OperationLogPageQueryDTO queryDTO){
        return operationLogService.listOperationLogPageable(queryDTO);
    }

    /**
     * 用户列表查询
     */
    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    @Operation(summary = "用户列表")
    public List<CodeNameDTO> listAllUser(){
        return userService.listAll();
    }

    /**
     * 模块树查询：返回分层的权限（模块）树
     */
    @RequestMapping(value = "/queryModuleTree", method = RequestMethod.GET)
    @Operation(summary = "模块树")
    public List<PermissionHolder> queryModuleTree(){
        // 获取完整的权限树（包含方法级叶子权限）
        List<PermissionHolder> fullTree = PermissionCache.getPermissionHierarchicalList();
        // 仅返回模块（非叶子）节点，递归移除所有叶子权限
        List<PermissionHolder> modules = new ArrayList<>();
        if (fullTree != null) {
            for (PermissionHolder root : fullTree) {
                PermissionHolder filtered = filterNonLeafRecursively(root);
                if (filtered != null) {
                    modules.add(filtered);
                }
            }
        }
        return modules;
    }

    /**
     * 递归过滤节点：仅保留非叶子权限（模块），移除所有叶子节点
     *
     * @param node 原始节点
     * @return 非叶子节点，如果是叶子则返回null
     */
    private PermissionHolder filterNonLeafRecursively(PermissionHolder node) {
        // 如果当前节点是叶子权限（一般为方法级权限），则不返回
        if (GlobalConst.YES.equals(node.getLeafNode())) {
            return null;
        }
        // 当前节点是非叶子（模块）权限，递归过滤其子节点
        List<PermissionHolder> children = node.getChildren();
        if (children != null && !children.isEmpty()) {
            List<PermissionHolder> filteredChildren = new ArrayList<>();
            for (PermissionHolder child : children) {
                PermissionHolder filteredChild = filterNonLeafRecursively(child);
                if (filteredChild != null) {
                    filteredChildren.add(filteredChild);
                }
            }
            // 覆盖为过滤后的子列表（仅包含模块）
            node.setChildren(filteredChildren.isEmpty() ? null : filteredChildren);
        } else {
            node.setChildren(null);
        }
        return node;
    }

}
