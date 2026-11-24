package com.wety.demo.module.common.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountSessionEntityRepository;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.auditlog.service.LoginLogService;
import com.wety.demo.module.common.dto.LoginDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * {@link LoginService} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AccountSessionEntityRepository accountSessionEntityRepository;

    @Mock
    private AccountEntityRepository accountEntityRepository;

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private DeptEntityRepository deptEntityRepository;

    @InjectMocks
    private LoginService loginService;

    @AfterEach
    void tearDown() {
        AppThreadLocal.getInstance().remove();
    }

    @Test
    void checkImageShouldRemoveSessionCodeWhenMismatch() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(GlobalConst.IMAGE_CODE, "ABCD");

        assertThrows(CustomException.class, () -> loginService.checkImage("WXYZ", session));
        assertNull(session.getAttribute(GlobalConst.IMAGE_CODE));
    }

    @Test
    void loginShouldInvalidateCaptchaWhenAccountValidationFails() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(GlobalConst.IMAGE_CODE, "ABCD");

        LoginDTO dto = new LoginDTO();
        dto.setLoginName("nonexistent");
        dto.setPassword("encrypted");
        dto.setCheckCode("abcd");

        when(accountEntityRepository.findByLoginNameAndDeleted("nonexistent", GlobalConst.NOT))
                .thenReturn(Optional.empty());

        AccountEntityResult result = new AccountEntityResult(loginService.login(dto, session));
        assertNull(result.vo);
        assertNull(session.getAttribute(GlobalConst.IMAGE_CODE));
    }

    private record AccountEntityResult(Object vo) {
    }
}
