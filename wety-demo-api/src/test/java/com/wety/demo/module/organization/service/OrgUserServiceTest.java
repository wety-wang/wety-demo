package com.wety.demo.module.organization.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountDeptLinkEntityRepository;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountPositionLinkEntityRepository;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.entity.AccountSessionEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.entity.PositionEntityRepository;
import com.wety.demo.common.entity.RoleEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.core.datascope.DataScopeChecker;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.organization.dto.OrgUserAddDTO;
import com.wety.demo.module.organization.dto.OrgUserResetPasswordDTO;
import com.wety.demo.module.organization.dto.OrgUserStatusUpdateDTO;
import com.wety.demo.module.organization.dto.OrgUserUpdateDTO;
import com.wety.demo.module.user.constant.AccountTypeConst;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link OrgUserService} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class OrgUserServiceTest {

    @Mock
    private AccountEntityRepository accountEntityRepository;

    @Mock
    private DeptEntityRepository deptEntityRepository;

    @Mock
    private PositionEntityRepository positionEntityRepository;

    @Mock
    private RoleEntityRepository roleEntityRepository;

    @Mock
    private AccountDeptLinkEntityRepository accountDeptLinkEntityRepository;

    @Mock
    private AccountPositionLinkEntityRepository accountPositionLinkEntityRepository;

    @Mock
    private AccountRoleLinkEntityRepository accountRoleLinkEntityRepository;

    @Mock
    private AccountSessionEntityRepository accountSessionEntityRepository;

    @Mock
    private DataScopeChecker dataScopeChecker;

    @Spy
    @InjectMocks
    private OrgUserService orgUserService;

    @AfterEach
    void tearDown() {
        AppThreadLocal.getInstance().removeBinding();
    }

    @Test
    void add_shouldForceNormalNonSuperAdminAccount() {
        AccountEntity operator = buildAccount("acc-mgr", "dept-a", GlobalConst.NOT, AccountTypeConst.NORMAL);
        AppThreadLocal.getInstance().putCurrentAccount(operator);

        DeptEntity dept = new DeptEntity();
        dept.setCode("dept-a");
        when(accountEntityRepository.existsByLoginName("alice")).thenReturn(false);
        when(deptEntityRepository.findByCodeAndDeleted("dept-a", GlobalConst.NOT)).thenReturn(Optional.of(dept));
        doReturn("$2a$10$mockedBcryptHash").when(orgUserService).encodeDecryptedPassword("cipher-pass");

        OrgUserAddDTO dto = new OrgUserAddDTO();
        dto.setLoginName("alice");
        dto.setUserName("Alice");
        dto.setPassword("cipher-pass");
        dto.setPrimaryDeptCode("dept-a");

        orgUserService.add(dto);

        ArgumentCaptor<AccountEntity> captor = ArgumentCaptor.forClass(AccountEntity.class);
        verify(accountEntityRepository).save(captor.capture());
        AccountEntity saved = captor.getValue();
        assertEquals(GlobalConst.NOT, saved.getSuperAdmin());
        assertEquals(GlobalConst.NOT, saved.getDeleted());
        assertEquals(AccountTypeConst.NORMAL, saved.getType());
        assertEquals("dept-a", saved.getPrimaryDeptCode());
        assertEquals("acc-mgr", saved.getCreateUser());
        verify(dataScopeChecker).assertAccessible("dept-a", null);
    }

    @Test
    void delete_shouldRejectBuiltInSuperAdmin() {
        AccountEntity operator = buildAccount("acc-admin-2", "dept-root", GlobalConst.YESI, AccountTypeConst.ADMIN);
        AppThreadLocal.getInstance().putCurrentAccount(operator);

        AccountEntity superAdmin = buildAccount("acc-admin", "dept-root", GlobalConst.YESI, AccountTypeConst.ADMIN);
        when(accountEntityRepository.findByCodeAndDeleted("acc-admin", GlobalConst.NOT))
                .thenReturn(Optional.of(superAdmin));

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.delete("acc-admin"));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    void updateStatus_shouldRejectDisablingBuiltInSuperAdmin() {
        AccountEntity operator = buildAccount("acc-admin-2", "dept-root", GlobalConst.YESI, AccountTypeConst.ADMIN);
        AppThreadLocal.getInstance().putCurrentAccount(operator);

        AccountEntity superAdmin = buildAccount("acc-admin", "dept-root", GlobalConst.YESI, AccountTypeConst.ADMIN);
        when(accountEntityRepository.findByCodeAndDeleted("acc-admin", GlobalConst.NOT))
                .thenReturn(Optional.of(superAdmin));

        OrgUserStatusUpdateDTO dto = new OrgUserStatusUpdateDTO();
        dto.setCode("acc-admin");
        dto.setDisabled(GlobalConst.YESI);

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.updateStatus(dto));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    void update_shouldRejectNonSuperAdminModifyingSuperAdmin() {
        AccountEntity normalOp = buildAccount("acc-mgr", "dept-root", GlobalConst.NOT, AccountTypeConst.NORMAL);
        AppThreadLocal.getInstance().putCurrentAccount(normalOp);

        AccountEntity superAdmin = buildAccount("acc-admin", "dept-root", GlobalConst.YESI, AccountTypeConst.ADMIN);
        when(accountEntityRepository.findByCodeAndDeleted("acc-admin", GlobalConst.NOT))
                .thenReturn(Optional.of(superAdmin));

        OrgUserUpdateDTO dto = new OrgUserUpdateDTO();
        dto.setCode("acc-admin");
        dto.setUserName("Hacked");
        dto.setPrimaryDeptCode("dept-root");

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.update(dto));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    void resetPassword_shouldRejectNonSuperAdminResettingSuperAdminPassword() {
        AccountEntity normalOp = buildAccount("acc-mgr", "dept-root", GlobalConst.NOT, AccountTypeConst.NORMAL);
        AppThreadLocal.getInstance().putCurrentAccount(normalOp);

        AccountEntity superAdmin = buildAccount("acc-admin", "dept-root", GlobalConst.YESI, AccountTypeConst.ADMIN);
        when(accountEntityRepository.findByCodeAndDeleted("acc-admin", GlobalConst.NOT))
                .thenReturn(Optional.of(superAdmin));

        OrgUserResetPasswordDTO dto = new OrgUserResetPasswordDTO();
        dto.setCode("acc-admin");
        dto.setNewPassword("cipher-new-pass");

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.resetPassword(dto));
        assertTrue(ex.getMessage().contains("超级管理员"));
    }

    @Test
    void delete_shouldRejectDeletingCurrentLoginAccount() {
        AccountEntity self = buildAccount("acc-self", "dept-a", GlobalConst.NOT, AccountTypeConst.NORMAL);
        AppThreadLocal.getInstance().putCurrentAccount(self);

        when(accountEntityRepository.findByCodeAndDeleted("acc-self", GlobalConst.NOT))
                .thenReturn(Optional.of(self));

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.delete("acc-self"));
        assertTrue(ex.getMessage().contains("当前登录账号"));
    }

    @Test
    void updateStatus_shouldRejectDisablingCurrentLoginAccount() {
        AccountEntity self = buildAccount("acc-self", "dept-a", GlobalConst.NOT, AccountTypeConst.NORMAL);
        AppThreadLocal.getInstance().putCurrentAccount(self);

        when(accountEntityRepository.findByCodeAndDeleted("acc-self", GlobalConst.NOT))
                .thenReturn(Optional.of(self));

        OrgUserStatusUpdateDTO dto = new OrgUserStatusUpdateDTO();
        dto.setCode("acc-self");
        dto.setDisabled(GlobalConst.YESI);

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.updateStatus(dto));
        assertTrue(ex.getMessage().contains("当前登录账号"));
    }

    @Test
    void query_shouldBlockHorizontalPrivilegeEscalationViaDataScopeChecker() {
        AccountEntity target = buildAccount("acc-other", "dept-b", GlobalConst.NOT, AccountTypeConst.NORMAL);
        when(accountEntityRepository.findByCodeAndDeleted("acc-other", GlobalConst.NOT))
                .thenReturn(Optional.of(target));
        doThrow(new CustomException("无权操作该数据，超出当前账号数据权限范围", 402))
                .when(dataScopeChecker).assertAccessible("dept-b", "acc-other");

        CustomException ex = assertThrows(CustomException.class, () -> orgUserService.query("acc-other"));
        assertEquals(402, ex.getCode());
    }

    private AccountEntity buildAccount(String code, String primaryDeptCode, Integer superAdmin, String type) {
        AccountEntity account = new AccountEntity();
        account.setCode(code);
        account.setPrimaryDeptCode(primaryDeptCode);
        account.setSuperAdmin(superAdmin);
        account.setType(type);
        account.setDeleted(GlobalConst.NOT);
        account.setDisabled(GlobalConst.NOT);
        return account;
    }
}
