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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * {@link DataScopeResolver} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class DataScopeResolverTest {

    @Mock
    private AccountRoleLinkEntityRepository accountRoleLinkEntityRepository;

    @Mock
    private RoleEntityRepository roleEntityRepository;

    @Mock
    private AccountDeptLinkEntityRepository accountDeptLinkEntityRepository;

    @Mock
    private DeptEntityRepository deptEntityRepository;

    @Mock
    private RoleDeptLinkEntityRepository roleDeptLinkEntityRepository;

    @InjectMocks
    private DataScopeResolver dataScopeResolver;

    @Test
    void resolve_shouldReturnAllForSuperAdminFlag() {
        AccountEntity admin = new AccountEntity();
        admin.setCode("acc-admin");
        admin.setSuperAdmin(GlobalConst.YESI);

        DataScopeContext context = dataScopeResolver.resolve(admin, "primaryDeptCode", "code");

        assertTrue(context.isAll());
        assertEquals("acc-admin", context.getCurrentUserCode());
        verifyNoInteractions(accountRoleLinkEntityRepository, roleEntityRepository);
    }

    @Test
    void resolve_shouldReturnAllForLegacyAdminType() {
        AccountEntity legacyAdmin = new AccountEntity();
        legacyAdmin.setCode("acc-legacy-admin");
        legacyAdmin.setType(AccountTypeConst.ADMIN);
        legacyAdmin.setSuperAdmin(GlobalConst.NOT);

        DataScopeContext context = dataScopeResolver.resolve(legacyAdmin, "primaryDeptCode", "code");

        assertTrue(context.isAll());
        verifyNoInteractions(accountRoleLinkEntityRepository, roleEntityRepository);
    }

    @Test
    void resolve_shouldPreferAllWhenMultipleRolesIncludeAll() {
        AccountEntity user = buildNormalAccount("acc-1", "dept-a");
        RoleEntity roleSelf = buildRole("r-self", DataScopeEnum.SELF.getCode());
        RoleEntity roleAll = buildRole("r-all", DataScopeEnum.ALL.getCode());

        when(accountRoleLinkEntityRepository.findDistinctRoleCodesByAccountCode("acc-1"))
                .thenReturn(Arrays.asList("r-self", "r-all"));
        when(roleEntityRepository.findByCodeInAndDeleted(Arrays.asList("r-self", "r-all"), GlobalConst.NOT))
                .thenReturn(Arrays.asList(roleSelf, roleAll));

        DataScopeContext context = dataScopeResolver.resolve(user, "primaryDeptCode", "code");

        assertTrue(context.isAll());
        verifyNoInteractions(roleDeptLinkEntityRepository, accountDeptLinkEntityRepository, deptEntityRepository);
    }

    @Test
    void resolve_shouldUnionCustomAndDeptRoles() {
        AccountEntity user = buildNormalAccount("acc-2", "dept-own");
        RoleEntity roleCustom = buildRole("r-custom", DataScopeEnum.CUSTOM.getCode());
        RoleEntity roleDept = buildRole("r-dept", DataScopeEnum.DEPT.getCode());

        when(accountRoleLinkEntityRepository.findDistinctRoleCodesByAccountCode("acc-2"))
                .thenReturn(Arrays.asList("r-custom", "r-dept"));
        when(roleEntityRepository.findByCodeInAndDeleted(Arrays.asList("r-custom", "r-dept"), GlobalConst.NOT))
                .thenReturn(Arrays.asList(roleCustom, roleDept));
        when(roleDeptLinkEntityRepository.findDistinctDeptCodesByRoleCodeInAndDeleted(
                Collections.singletonList("r-custom"), GlobalConst.NOT))
                .thenReturn(Arrays.asList("dept-x", "dept-y"));
        when(accountDeptLinkEntityRepository.findDistinctDeptCodesByAccountCodeAndDeleted("acc-2", GlobalConst.NOT))
                .thenReturn(Collections.singletonList("dept-own"));

        DataScopeContext context = dataScopeResolver.resolve(user, "deptCode", "accountCode");

        assertFalse(context.isAll());
        assertFalse(context.isIncludeSelf());
        assertEquals(new HashSet<>(Arrays.asList("dept-x", "dept-y", "dept-own")), context.getAllowedDeptCodes());
    }

    @Test
    void resolve_shouldExpandDescendantsByAncestorsForDeptAndChild() {
        AccountEntity user = buildNormalAccount("acc-3", "dept-p");
        RoleEntity roleDeptAndChild = buildRole("r-tree", DataScopeEnum.DEPT_AND_CHILD.getCode());

        DeptEntity child1 = new DeptEntity();
        child1.setCode("dept-c1");
        DeptEntity child2 = new DeptEntity();
        child2.setCode("dept-c2");

        when(accountRoleLinkEntityRepository.findDistinctRoleCodesByAccountCode("acc-3"))
                .thenReturn(Collections.singletonList("r-tree"));
        when(roleEntityRepository.findByCodeInAndDeleted(Collections.singletonList("r-tree"), GlobalConst.NOT))
                .thenReturn(Collections.singletonList(roleDeptAndChild));
        when(accountDeptLinkEntityRepository.findDistinctDeptCodesByAccountCodeAndDeleted("acc-3", GlobalConst.NOT))
                .thenReturn(Collections.emptyList());
        when(deptEntityRepository.findAllByAncestorsContainingAndDeleted(",dept-p,", GlobalConst.NOT))
                .thenReturn(Arrays.asList(child1, child2));

        DataScopeContext context = dataScopeResolver.resolve(user, "primaryDeptCode", "code");

        assertFalse(context.isAll());
        assertEquals(new HashSet<>(Arrays.asList("dept-p", "dept-c1", "dept-c2")), context.getAllowedDeptCodes());
        verify(deptEntityRepository).findAllByAncestorsContainingAndDeleted(",dept-p,", GlobalConst.NOT);
    }

    @Test
    void resolve_shouldFallbackToSelfWhenNoRoleBound() {
        AccountEntity user = buildNormalAccount("acc-4", "dept-a");
        when(accountRoleLinkEntityRepository.findDistinctRoleCodesByAccountCode("acc-4"))
                .thenReturn(Collections.emptyList());

        DataScopeContext context = dataScopeResolver.resolve(user, "primaryDeptCode", "code");

        assertFalse(context.isAll());
        assertTrue(context.isIncludeSelf());
        assertTrue(context.getAllowedDeptCodes().isEmpty());
        assertEquals("acc-4", context.getCurrentUserCode());
    }

    @Test
    void resolve_shouldReturnEmptyContextWhenAccountNull() {
        DataScopeContext context = dataScopeResolver.resolve(null, "primaryDeptCode", "code");

        assertFalse(context.isAll());
        assertFalse(context.isIncludeSelf());
        assertTrue(context.getAllowedDeptCodes().isEmpty());
    }

    private AccountEntity buildNormalAccount(String code, String primaryDeptCode) {
        AccountEntity account = new AccountEntity();
        account.setCode(code);
        account.setType(AccountTypeConst.NORMAL);
        account.setSuperAdmin(GlobalConst.NOT);
        account.setPrimaryDeptCode(primaryDeptCode);
        return account;
    }

    private RoleEntity buildRole(String code, int dataScope) {
        RoleEntity role = new RoleEntity();
        role.setCode(code);
        role.setDataScope(dataScope);
        role.setDeleted(GlobalConst.NOT);
        return role;
    }
}
