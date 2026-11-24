package com.wety.demo.core.datascope;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.core.misc.AppThreadLocal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * {@link DataScopeChecker} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class DataScopeCheckerTest {

    @Mock
    private DataScopeResolver dataScopeResolver;

    @InjectMocks
    private DataScopeChecker dataScopeChecker;

    @AfterEach
    void tearDown() {
        AppThreadLocal.getInstance().removeBinding();
    }

    @Test
    void isAccessible_shouldAllowAllWhenContextIsAll() {
        AppThreadLocal.getInstance().setDataScopeContext(
                DataScopeContext.all("acc-admin", "primaryDeptCode", "code"));

        assertTrue(dataScopeChecker.isAccessible("dept-any", "acc-other"));
        assertDoesNotThrow(() -> dataScopeChecker.assertAccessible("dept-any", "acc-other"));
    }

    @Test
    void isAccessible_shouldAllowWhenDeptMatches() {
        DataScopeContext context = new DataScopeContext();
        context.setAll(false);
        context.setAllowedDeptCodes(Collections.singleton("dept-1"));
        context.setIncludeSelf(false);
        context.setCurrentUserCode("acc-1");
        AppThreadLocal.getInstance().setDataScopeContext(context);

        assertTrue(dataScopeChecker.isAccessible("dept-1", "acc-other"));
        assertFalse(dataScopeChecker.isAccessible("dept-2", "acc-other"));
    }

    @Test
    void isAccessible_shouldAllowWhenSelfMatches() {
        DataScopeContext context = new DataScopeContext();
        context.setAll(false);
        context.setAllowedDeptCodes(Collections.emptySet());
        context.setIncludeSelf(true);
        context.setCurrentUserCode("acc-self");
        AppThreadLocal.getInstance().setDataScopeContext(context);

        assertTrue(dataScopeChecker.isAccessible("dept-other", "acc-self"));
        assertFalse(dataScopeChecker.isAccessible("dept-other", "acc-other"));
    }

    @Test
    void assertAccessible_shouldThrow402CustomExceptionWhenDenied() {
        AccountEntity current = new AccountEntity();
        current.setCode("acc-user");
        AppThreadLocal.getInstance().putCurrentAccount(current);

        DataScopeContext selfOnly = new DataScopeContext();
        selfOnly.setAll(false);
        selfOnly.setAllowedDeptCodes(Collections.singleton("dept-a"));
        selfOnly.setIncludeSelf(true);
        selfOnly.setCurrentUserCode("acc-user");
        when(dataScopeResolver.resolve(current, "deptCode", "createUser")).thenReturn(selfOnly);

        CustomException ex = assertThrows(CustomException.class,
                () -> dataScopeChecker.assertAccessible("dept-b", "acc-other"));
        assertEquals(402, ex.getCode());
        assertEquals(GlobalReturnCodeEnum.ACCESS_DENIED.getCode(), AppThreadLocal.getInstance().getReturnCode());
    }
}
