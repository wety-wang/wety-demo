package com.wety.demo.module.user.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.RolePermissionLinkEntity;
import com.wety.demo.common.entity.RolePermissionLinkEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.core.annotation.PermissionHolder;
import com.wety.demo.core.authorization.PermissionCache;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.user.constant.AccountTypeConst;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * @author wety
 */
@Service
public class PermissionService {

    @Resource
    private RolePermissionLinkEntityRepository rolePermissionLinkEntityRepository;

    @Resource
    private RoleService roleService;

    /**
     * 查询指定账户拥有的全部权限叶子的集合
     *
     * @param roleCode 账户名
     * @return 叶子权限编码集合{00-00-00, 00-01-00}
     */
    public List<String> listLeafPermissionCode(String roleCode) {
        List<String> permissionCodes = rolePermissionLinkEntityRepository.findPermissionCodesByRoleCode(roleCode);
        Map<String, PermissionHolder> codePermissionMap = PermissionCache.getPermissionCodePermissionMap();
        Iterator<String> permissionCodeIterator = permissionCodes.iterator();
        while (permissionCodeIterator.hasNext()) {
            String permissionCode = permissionCodeIterator.next();
            PermissionHolder holder = codePermissionMap.get(permissionCode);
            // 权限编码的变更可能会导致用于已经关联的权限编码是无效的，这里做一下null判断
            if (holder == null) {
                continue;
            }
            // 非叶子权限去掉
            if (GlobalConst.NO.equals(holder.getLeafNode())) {
                permissionCodeIterator.remove();
            }
        }
        return permissionCodes;
    }

    /**
     * 修改/新增角色账户权限关联<br>
     * 简单的实现就是清空账户的权限关联然后重新插入关联<br>
     * 叶子结点的对应的父级结点也会被向上递归并作为角色的权限编码集合
     *
     * @param roleCode           账户唯一登录名
     * @param permissionCodeList 账户权限编码集合
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void saveRolePermissions(String roleCode, List<String> permissionCodeList) {
        Set<String> containingParentSet = new HashSet<>();
        for (String code : permissionCodeList) {
            containingParentSet.add(code);
            addParentCodes(code, containingParentSet);
        }
        rolePermissionLinkEntityRepository.deleteByRoleCode(roleCode);
        rolePermissionLinkEntityRepository.flush();
        for (String permissionCode : containingParentSet) {
            RolePermissionLinkEntity rolePermissionLinkEntity = new RolePermissionLinkEntity();
            rolePermissionLinkEntity.setRoleCode(roleCode);
            rolePermissionLinkEntity.setPermissionCode(permissionCode);
            rolePermissionLinkEntityRepository.save(rolePermissionLinkEntity);
        }
    }

    /**
     * 将code的父级权限编码添加到集合中
     *
     * @param code                权限编码，eg:01-00-00
     * @param containingParentSet 收集权限编码的集合
     */
    private void addParentCodes(String code, Set<String> containingParentSet) {
        Map<String, PermissionHolder> codePermissionMap = PermissionCache.getPermissionCodePermissionMap();
        PermissionHolder permission = codePermissionMap.get(code);
        if (permission == null) {
            throw new CustomException("权限未定义: " + code);
        }
        if (permission.getParentCode() == null) {
            return;
        }
        PermissionHolder parentPermission = codePermissionMap.get(permission.getParentCode());
        if (parentPermission != null) {
            containingParentSet.add(parentPermission.getCode());
            addParentCodes(parentPermission.getCode(), containingParentSet);
        }
    }

    public List<String> listAccountPermission() {
        AccountEntity accountEntity = AppThreadLocal.getInstance().getCurrentAccount();
        return listAccountPermission(accountEntity);
    }

    /**
     * 查询指定账号拥有的全部权限编码。
     *
     * <p>内置超级管理员（superAdmin == 1）或兼容存量 admin 类型账号默认拥有全部权限。</p>
     *
     * @param accountEntity 账号实体
     * @return 权限编码列表
     */
    public List<String> listAccountPermission(AccountEntity accountEntity) {
        if (accountEntity == null) {
            return Collections.emptyList();
        }
        if (Integer.valueOf(1).equals(accountEntity.getSuperAdmin())
                || AccountTypeConst.ADMIN.equals(accountEntity.getType())) {
            return new ArrayList<>(PermissionCache.getPermissionCodePermissionMap().keySet());
        }

        List<String> roleCodes = roleService.listAllRoleCode(accountEntity.getCode());
        if (CollectionUtils.isEmpty(roleCodes)) {
            return Collections.emptyList();
        }
        return rolePermissionLinkEntityRepository.findPermissionCodesByRoleCodeIn(roleCodes);
    }

}
