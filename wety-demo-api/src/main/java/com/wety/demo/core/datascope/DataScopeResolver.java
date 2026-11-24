package com.wety.demo.core.datascope;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountDeptLinkEntityRepository;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.entity.RoleDeptLinkEntityRepository;
import com.wety.demo.common.entity.RoleEntity;
import com.wety.demo.common.entity.RoleEntityRepository;
import com.wety.demo.module.user.constant.AccountTypeConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 数据权限解析器。
 *
 * <p>根据账号身份及其拥有的全部有效角色，按“并集（最大权限优先）”规则计算可见部门集合与本人权限标记。</p>
 */
@Component
public class DataScopeResolver {

    @Resource
    private AccountRoleLinkEntityRepository accountRoleLinkEntityRepository;

    @Resource
    private RoleEntityRepository roleEntityRepository;

    @Resource
    private AccountDeptLinkEntityRepository accountDeptLinkEntityRepository;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    @Resource
    private RoleDeptLinkEntityRepository roleDeptLinkEntityRepository;

    /**
     * 解析指定账号在目标字段配置下的数据权限上下文。
     *
     * @param account   当前登录账号
     * @param deptField 实体部门属性名
     * @param userField 实体用户属性名
     * @return 解析后的数据权限上下文
     */
    public DataScopeContext resolve(AccountEntity account, String deptField, String userField) {
        if (account == null || StringUtils.isBlank(account.getCode())) {
            return DataScopeContext.empty(null, deptField, userField);
        }

        String currentUserCode = account.getCode();
        // 内置超级管理员（兼容存量 type=admin 账号）默认拥有全部数据权限
        if (isSuperAdmin(account)) {
            return DataScopeContext.all(currentUserCode, deptField, userField);
        }

        List<String> roleCodes = accountRoleLinkEntityRepository.findDistinctRoleCodesByAccountCode(currentUserCode);
        if (CollectionUtils.isEmpty(roleCodes)) {
            return buildSelfOnlyContext(currentUserCode, deptField, userField);
        }

        List<RoleEntity> roles = roleEntityRepository.findByCodeInAndDeleted(roleCodes, GlobalConst.NOT);
        if (CollectionUtils.isEmpty(roles)) {
            return buildSelfOnlyContext(currentUserCode, deptField, userField);
        }

        boolean needDept = false;
        boolean needDeptAndChild = false;
        boolean includeSelf = false;
        List<String> customRoleCodes = new ArrayList<>();

        for (RoleEntity role : roles) {
            DataScopeEnum scope = DataScopeEnum.fromCode(role.getDataScope());
            if (scope == DataScopeEnum.ALL) {
                return DataScopeContext.all(currentUserCode, deptField, userField);
            } else if (scope == DataScopeEnum.CUSTOM) {
                customRoleCodes.add(role.getCode());
            } else if (scope == DataScopeEnum.DEPT) {
                needDept = true;
            } else if (scope == DataScopeEnum.DEPT_AND_CHILD) {
                needDeptAndChild = true;
            } else {
                includeSelf = true;
            }
        }

        Set<String> allowedDeptCodes = new LinkedHashSet<>();

        // 1. 聚合所有 CUSTOM 角色的自定义部门
        if (!customRoleCodes.isEmpty()) {
            List<String> customDeptCodes = roleDeptLinkEntityRepository
                    .findDistinctDeptCodesByRoleCodeInAndDeleted(customRoleCodes, GlobalConst.NOT);
            if (!CollectionUtils.isEmpty(customDeptCodes)) {
                allowedDeptCodes.addAll(customDeptCodes);
            }
        }

        // 2. 聚合本部门或本部门及以下
        if (needDept || needDeptAndChild) {
            Set<String> userDeptCodes = resolveAccountDeptCodes(account);
            allowedDeptCodes.addAll(userDeptCodes);

            if (needDeptAndChild) {
                for (String deptCode : userDeptCodes) {
                    // ancestors 格式为 "0," 或 "0,dept-root,"，使用 ",deptCode," 避免前缀误匹配
                    List<DeptEntity> descendants = deptEntityRepository
                            .findAllByAncestorsContainingAndDeleted("," + deptCode + ",", GlobalConst.NOT);
                    if (!CollectionUtils.isEmpty(descendants)) {
                        for (DeptEntity descendant : descendants) {
                            if (StringUtils.isNotBlank(descendant.getCode())) {
                                allowedDeptCodes.add(descendant.getCode());
                            }
                        }
                    }
                }
            }
        }

        DataScopeContext context = new DataScopeContext();
        context.setAll(false);
        context.setAllowedDeptCodes(allowedDeptCodes);
        context.setIncludeSelf(includeSelf);
        context.setCurrentUserCode(currentUserCode);
        context.setDeptField(deptField);
        context.setUserField(userField);
        return context;
    }

    /**
     * 判断账号是否为内置超级管理员。
     *
     * @param account 账号实体
     * @return true 表示超管
     */
    private boolean isSuperAdmin(AccountEntity account) {
        return GlobalConst.YESI.equals(account.getSuperAdmin())
                || AccountTypeConst.ADMIN.equals(account.getType());
    }

    /**
     * 查询账号所属的全部有效部门编码；若关联表无记录，则回退使用账号上的主部门编码。
     *
     * @param account 账号实体
     * @return 部门编码集合
     */
    private Set<String> resolveAccountDeptCodes(AccountEntity account) {
        Set<String> deptCodes = new LinkedHashSet<>();
        List<String> linkedDeptCodes = accountDeptLinkEntityRepository
                .findDistinctDeptCodesByAccountCodeAndDeleted(account.getCode(), GlobalConst.NOT);
        if (!CollectionUtils.isEmpty(linkedDeptCodes)) {
            deptCodes.addAll(linkedDeptCodes);
        }
        if (StringUtils.isNotBlank(account.getPrimaryDeptCode())) {
            deptCodes.add(account.getPrimaryDeptCode());
        }
        return deptCodes;
    }

    /**
     * 构造仅本人数据权限上下文。
     */
    private DataScopeContext buildSelfOnlyContext(String currentUserCode, String deptField, String userField) {
        DataScopeContext context = new DataScopeContext();
        context.setAll(false);
        context.setAllowedDeptCodes(new LinkedHashSet<>());
        context.setIncludeSelf(true);
        context.setCurrentUserCode(currentUserCode);
        context.setDeptField(deptField);
        context.setUserField(userField);
        return context;
    }
}
