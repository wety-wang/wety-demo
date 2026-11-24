package com.wety.demo.module.auditlog.util;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.core.annotation.PermissionHolder;
import com.wety.demo.core.authorization.PermissionCache;
import com.wety.demo.module.auditlog.constant.OperationType;
import com.wety.demo.module.auditlog.dto.LoginLogPageQueryDTO;
import com.wety.demo.module.auditlog.dto.OperationLogPageQueryDTO;
import com.wety.demo.module.user.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志描述构建器
 *
 * @author wety
 */
@Component("auditLogDescriptionBuilder")
public class LogDescriptionBuilder {

    @Resource
    private UserService userService;

    /**
     * 构建“查看登录日志列表”的操作日志描述
     *
     * @param dto 登录日志分页查询参数
     * @return 构建后的完整日志内容
     */
    public String buildListPageableDesc(LoginLogPageQueryDTO dto) {
        // 基础前缀：操作名称
        StringBuilder sb = new StringBuilder("查看登录日志列表");

        // 收集所有非空的筛选条件，便于统一拼接
        List<String> filters = new ArrayList<>();

        // 登录名
        if (StringUtils.isNotBlank(dto.getAccountLoginName())) {
            filters.add("登录名=" + dto.getAccountLoginName());
        }

        // 用户名
        if (StringUtils.isNotBlank(dto.getAccountName())) {
            filters.add("用户名=" + dto.getAccountName());
        }

        // 结果（success/fail -> 成功/失败；其他值原样保留）
        if (StringUtils.isNotBlank(dto.getResult())) {
            if(GlobalConst.SUCCESS.equals(dto.getResult())){
                filters.add("结果=" + "成功");
            }else if(GlobalConst.FAIL.equals(dto.getResult())){
                filters.add("结果=" + "失败");
            }else {
                filters.add("结果=" + dto.getResult());
            }
        }

        // 客户端IP
        if (StringUtils.isNotBlank(dto.getClientIp())) {
            filters.add("客户端IP=" + dto.getClientIp());
        }

        // 时间范围（支持一个或两个时间点）
        List<Date> range = dto.getOperationTime();
        if (range != null && !range.isEmpty()) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String from = fmt.format(range.get(0));
            String timeText = from;
            if (range.size() > 1 && range.get(1) != null) {
                timeText = from + " 至 " + fmt.format(range.get(1));
            }
            filters.add("时间范围=" + timeText);
        }

        // 若存在筛选条件，则统一以“（筛选条件：...）”形式追加
        if (!filters.isEmpty()) {
            sb.append("（筛选条件：").append(String.join("，", filters)).append("）");
        }

        // 分页信息（始终追加，帮助定位列表上下文）
        sb.append("；第").append(dto.getPageNumber())
          .append("页，每页").append(dto.getPageSize())
          .append("条");

        return sb.toString();
    }

    /**
     * 构建“查看操作日志列表”的操作日志描述
     *
     * @param dto 操作日志分页查询参数
     * @return 构建后的完整日志内容
     */
    public String buildOperationListPageableDesc(OperationLogPageQueryDTO dto) {
        // 基础前缀：操作名称
        StringBuilder sb = new StringBuilder("查看操作日志列表");

        // 收集所有非空的筛选条件，便于统一拼接
        List<String> filters = new ArrayList<>();

        // 模块编码 -> 模块名称（优先还原成模块名称；无法还原时回退展示编码）
        if (CollectionUtils.isNotEmpty(dto.getModelCodes())) {
            List<String> moduleNames = convertModelCodesToNames(dto.getModelCodes());
            if (CollectionUtils.isNotEmpty(moduleNames)) {
                filters.add("模块=" + String.join(",", moduleNames));
            } else {
                // 兜底：没有找到对应名称时保留原始编码，避免信息丢失
                filters.add("模块编码=" + String.join(",", dto.getModelCodes()));
            }
        }

        // 操作人（多选）：根据账户编码还原用户姓名；未命中时保留编码
        if (CollectionUtils.isNotEmpty(dto.getAccountCodes())) {
            List<String> operatorNames = convertAccountCodesToNames(dto.getAccountCodes());
            if (CollectionUtils.isNotEmpty(operatorNames)) {
                // 成功还原为姓名，按中文逗号拼接
                filters.add("操作人=" + String.join(",", operatorNames));
            } else {
                // 兜底：保留原始编码，避免信息丢失
                filters.add("操作人编码=" + String.join(",", dto.getAccountCodes()));
            }
        }

        // 操作类型（多选）：将 create/update/delete/query 转换为中文展示
        if (CollectionUtils.isNotEmpty(dto.getOperationTypes())) {
            List<String> typeNames = dto.getOperationTypes().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(OperationType::getNameByCode)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(typeNames)) {
                filters.add("操作类型=" + String.join(",", typeNames));
            }
        }

        // 结果（success/fail -> 成功/失败；其他值原样保留）
        if (StringUtils.isNotBlank(dto.getResult())) {
            if (GlobalConst.SUCCESS.equals(dto.getResult())) {
                filters.add("结果=成功");
            } else if (GlobalConst.FAIL.equals(dto.getResult())) {
                filters.add("结果=失败");
            } else {
                filters.add("结果=" + dto.getResult());
            }
        }

        // 时间范围（支持一个或两个时间点）
        List<Date> range = dto.getOperationTime();
        if (range != null && !range.isEmpty()) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String from = fmt.format(range.get(0));
            String timeText = from;
            if (range.size() > 1 && range.get(1) != null) {
                timeText = from + " 至 " + fmt.format(range.get(1));
            }
            filters.add("时间范围=" + timeText);
        }

        // 客户端IP
        if (StringUtils.isNotBlank(dto.getClientIp())) {
            filters.add("客户端IP=" + dto.getClientIp());
        }

        // 若存在筛选条件，则统一以“（筛选条件：...）”形式追加
        if (!filters.isEmpty()) {
            sb.append("（筛选条件：").append(String.join("，", filters)).append("）");
        }

        // 分页信息（始终追加，帮助定位列表上下文）
        sb.append("；第").append(dto.getPageNumber())
                .append("页，每页").append(dto.getPageSize())
                .append("条");

        return sb.toString();
    }

    /**
     * 将模块编码列表转换为模块名称列表。
     * 支持以下情况：
     * 1) 精确编码（如：102-02-00）——直接映射到名称；
     * 2) 叶子权限编码（如：102-02-01）——映射其父级模块名称；
     * 3) 前缀通配（如：102-02-%）——匹配所有以该前缀开头的非叶子模块编码，并提取其名称；
     * 若权限缓存未初始化或未命中，返回空列表。
     *
     * @param codes 模块编码列表
     * @return 模块名称列表（去重，保持出现顺序）
     */
    private List<String> convertModelCodesToNames(List<String> codes) {
        List<String> names = new ArrayList<>();
        if (CollectionUtils.isEmpty(codes)) {
            return names;
        }

        // 从权限缓存中获取编码到权限的映射
        Map<String, PermissionHolder> codePermissionMap = PermissionCache.getPermissionCodePermissionMap();
        if (codePermissionMap == null || codePermissionMap.isEmpty()) {
            return names;
        }

        for (String code : codes) {
            if (StringUtils.isBlank(code)) {
                continue;
            }

            // 处理前缀通配（例如：102-02-%），取前缀匹配的非叶子模块名称
            if (code.contains("%")) {
                String prefix = code.replace("%", "");
                for (Map.Entry<String, PermissionHolder> entry : codePermissionMap.entrySet()) {
                    PermissionHolder holder = entry.getValue();
                    // 仅匹配非叶子权限（模块级），并且编码以指定前缀开头
                    if (entry.getKey().startsWith(prefix) && GlobalConst.NO.equals(holder.getLeafNode())) {
                        names.add(holder.getName());
                    }
                }
                continue;
            }

            // 精确编码或叶子编码：优先直接映射；若是叶子则取父级模块名称
            PermissionHolder holder = codePermissionMap.get(code);
            if (holder != null) {
                if (GlobalConst.NO.equals(holder.getLeafNode())) {
                    // 非叶子：模块本身
                    names.add(holder.getName());
                } else {
                    // 叶子：取父级模块
                    String parentCode = holder.getParentCode();
                    PermissionHolder parent = codePermissionMap.get(parentCode);
                    if (parent != null) {
                        names.add(parent.getName());
                    }
                }
            }
        }

        // 去重并保持顺序
        return names.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 将账户编码列表转换为用户姓名列表。
     * 说明：
     * - 优先使用用户服务获取完整 CodeName 列表，构建 code->name 映射；
     * - 没有命中时返回空列表（由调用方做兜底展示编码）。
     * - 保持原出现顺序并去重。
     *
     * @param codes 账户编码列表
     * @return 用户姓名列表（去重，保持顺序）
     */
    private List<String> convertAccountCodesToNames(List<String> codes) {
        List<String> names = new ArrayList<>();
        if (CollectionUtils.isEmpty(codes)) {
            return names;
        }
        // 调用用户服务，获取所有用户的 code/name 列表
        List<CodeNameDTO> all = null;
        try {
            all = userService.listAll();
        } catch (Exception ignore) {
            // 服务异常时返回空列表，由上层兜底
        }
        if (CollectionUtils.isEmpty(all)) {
            return names;
        }
        // 构建映射：code -> name
        Map<String, String> codeNameMap = all.stream()
                .filter(dto -> StringUtils.isNotBlank(dto.getCode()))
                .collect(Collectors.toMap(CodeNameDTO::getCode, CodeNameDTO::getName, (a, b) -> a));

        // 按传入顺序收集姓名，忽略未命中项
        for (String code : codes) {
            if (StringUtils.isBlank(code)) {
                continue;
            }
            String name = codeNameMap.get(code);
            if (StringUtils.isNotBlank(name)) {
                names.add(name);
            }
        }

        // 去重并保持顺序
        return names.stream().distinct().collect(Collectors.toList());
    }
}
