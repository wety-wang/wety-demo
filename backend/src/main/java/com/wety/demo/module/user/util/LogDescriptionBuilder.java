package com.wety.demo.module.user.util;

import com.wety.demo.module.user.dto.RolePageQueryDTO;
import com.wety.demo.module.user.dto.RoleUserPageQueryDTO;
import com.wety.demo.module.user.dto.UserPageQueryDTO;
import org.springframework.stereotype.Component;

/**
 * 用户模块日志描述构建器
 * 聚焦用户/角色相关列表页的描述构建，便于模块内维护。
 *
 * @author wety
 */
@Component("userLogDescriptionBuilder")
public class LogDescriptionBuilder {

    /**
     * 构建“查看系统管理员列表”的描述（不依赖 dto.type）
     */
    public String buildAdminListPageableDesc(UserPageQueryDTO dto) {
        // 统一风格：前缀 + 可选筛选 + 分页信息
        StringBuilder sb = new StringBuilder("查看系统管理员列表");
        java.util.List<String> filters = new java.util.ArrayList<>();
        // 姓名筛选
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getUserName())) {
            filters.add("姓名=" + dto.getUserName());
        }
        // 登录名筛选
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getLoginName())) {
            filters.add("登录名=" + dto.getLoginName());
        }
        // 邮箱筛选
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getEmail())) {
            filters.add("邮箱=" + dto.getEmail());
        }
        // 手机号筛选
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getPhoneNumber())) {
            filters.add("手机号=" + dto.getPhoneNumber());
        }
        // 追加筛选说明
        if (!filters.isEmpty()) {
            sb.append("（筛选条件：").append(String.join("，", filters)).append("）");
        }
        // 分页信息
        sb.append("；第").append(dto.getPageNumber())
          .append("页，每页").append(dto.getPageSize())
          .append("条");
        return sb.toString();
    }

    /**
     * 构建“查看普通用户列表”的描述（不依赖 dto.type）
     */
    public String buildNormalUserListPageableDesc(UserPageQueryDTO dto) {
        StringBuilder sb = new StringBuilder("查看用户列表");
        java.util.List<String> filters = new java.util.ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getUserName())) {
            filters.add("姓名=" + dto.getUserName());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getLoginName())) {
            filters.add("登录名=" + dto.getLoginName());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getEmail())) {
            filters.add("邮箱=" + dto.getEmail());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getPhoneNumber())) {
            filters.add("手机号=" + dto.getPhoneNumber());
        }
        if (!filters.isEmpty()) {
            sb.append("（筛选条件：").append(String.join("，", filters)).append("）");
        }
        sb.append("；第").append(dto.getPageNumber())
          .append("页，每页").append(dto.getPageSize())
          .append("条");
        return sb.toString();
    }

    /**
     * 构建“查看角色列表”的描述
     */
    public String buildRoleListPageableDesc(RolePageQueryDTO dto) {
        StringBuilder sb = new StringBuilder("查看角色列表");
        java.util.List<String> filters = new java.util.ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getName())) {
            filters.add("角色名称=" + dto.getName());
        }
        if (!filters.isEmpty()) {
            sb.append("（筛选条件：").append(String.join("，", filters)).append("）");
        }
        sb.append("；第").append(dto.getPageNumber())
          .append("页，每页").append(dto.getPageSize())
          .append("条");
        return sb.toString();
    }

    /**
     * 构建“查看角色关联用户列表”的描述
     */
    public String buildRoleUserListPageableDesc(RoleUserPageQueryDTO dto) {
        StringBuilder sb = new StringBuilder("查看角色关联用户列表");
        java.util.List<String> filters = new java.util.ArrayList<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getRoleCode())) {
            filters.add("角色编码=" + dto.getRoleCode());
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dto.getUserName())) {
            filters.add("用户名=" + dto.getUserName());
        }
        if (!filters.isEmpty()) {
            sb.append("（筛选条件：").append(String.join("，", filters)).append("）");
        }
        sb.append("；第").append(dto.getPageNumber())
          .append("页，每页").append(dto.getPageSize())
          .append("条");
        return sb.toString();
    }
}
