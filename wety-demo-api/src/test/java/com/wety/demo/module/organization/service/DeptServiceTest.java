package com.wety.demo.module.organization.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountDeptLinkEntityRepository;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.organization.dto.DeptAddDTO;
import com.wety.demo.module.organization.dto.DeptUpdateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link DeptService} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class DeptServiceTest {

    @Mock
    private DeptEntityRepository deptEntityRepository;

    @Mock
    private AccountEntityRepository accountEntityRepository;

    @Mock
    private AccountDeptLinkEntityRepository accountDeptLinkEntityRepository;

    @InjectMocks
    private DeptService deptService;

    @BeforeEach
    void setUp() {
        AccountEntity operator = new AccountEntity();
        operator.setCode("acc-op");
        AppThreadLocal.getInstance().putCurrentAccount(operator);
    }

    @AfterEach
    void tearDown() {
        AppThreadLocal.getInstance().removeBinding();
    }

    @Test
    void add_shouldCalculateAncestorsFromParent() {
        DeptEntity root = buildDept("dept-root", "0", "0,", "总部");
        when(deptEntityRepository.findByCodeAndDeleted("dept-root", GlobalConst.NOT))
                .thenReturn(Optional.of(root));
        when(deptEntityRepository.existsByParentCodeAndNameAndDeleted("dept-root", "研发部", GlobalConst.NOT))
                .thenReturn(false);

        DeptAddDTO dto = new DeptAddDTO();
        dto.setParentCode("dept-root");
        dto.setName("研发部");
        dto.setSortOrder(1);

        deptService.add(dto);

        ArgumentCaptor<DeptEntity> captor = ArgumentCaptor.forClass(DeptEntity.class);
        verify(deptEntityRepository).save(captor.capture());
        DeptEntity saved = captor.getValue();
        assertEquals("dept-root", saved.getParentCode());
        assertEquals("0,dept-root,", saved.getAncestors());
        assertEquals("研发部", saved.getName());
        assertEquals(GlobalConst.DATA_STATUS_ENABLE, saved.getStatus());
        assertEquals(GlobalConst.NOT, saved.getDeleted());
    }

    @Test
    void update_shouldRejectSelfAsParent() {
        DeptEntity current = buildDept("dept-a", "dept-root", "0,dept-root,", "部门A");
        when(deptEntityRepository.findByCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(Optional.of(current));
        when(deptEntityRepository.existsByParentCodeAndNameAndDeletedAndCodeNot(
                "dept-a", "部门A", GlobalConst.NOT, "dept-a"))
                .thenReturn(false);

        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setCode("dept-a");
        dto.setParentCode("dept-a");
        dto.setName("部门A");

        CustomException ex = assertThrows(CustomException.class, () -> deptService.update(dto));
        assertTrue(ex.getMessage().contains("自身"));
    }

    @Test
    void update_shouldRejectDescendantAsParent() {
        DeptEntity parent = buildDept("dept-a", "dept-root", "0,dept-root,", "部门A");
        DeptEntity descendant = buildDept("dept-a1", "dept-a", "0,dept-root,dept-a,", "子部门A1");
        when(deptEntityRepository.findByCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(Optional.of(parent));
        when(deptEntityRepository.findByCodeAndDeleted("dept-a1", GlobalConst.NOT))
                .thenReturn(Optional.of(descendant));
        when(deptEntityRepository.existsByParentCodeAndNameAndDeletedAndCodeNot(
                "dept-a1", "部门A", GlobalConst.NOT, "dept-a"))
                .thenReturn(false);

        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setCode("dept-a");
        dto.setParentCode("dept-a1");
        dto.setName("部门A");

        CustomException ex = assertThrows(CustomException.class, () -> deptService.update(dto));
        assertTrue(ex.getMessage().contains("子孙部门"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void update_shouldCascadeRefreshDescendantsAncestorsWhenParentChanged() {
        DeptEntity moving = buildDept("dept-a", "dept-root", "0,dept-root,", "部门A");
        DeptEntity newParent = buildDept("dept-b", "dept-root", "0,dept-root,", "部门B");
        DeptEntity child = buildDept("dept-a1", "dept-a", "0,dept-root,dept-a,", "子部门A1");
        DeptEntity grandChild = buildDept("dept-a1-1", "dept-a1", "0,dept-root,dept-a,dept-a1,", "孙部门A1-1");

        when(deptEntityRepository.findByCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(Optional.of(moving));
        when(deptEntityRepository.findByCodeAndDeleted("dept-b", GlobalConst.NOT))
                .thenReturn(Optional.of(newParent));
        when(deptEntityRepository.existsByParentCodeAndNameAndDeletedAndCodeNot(
                "dept-b", "部门A", GlobalConst.NOT, "dept-a"))
                .thenReturn(false);
        when(deptEntityRepository.findAllByAncestorsContainingAndDeleted(",dept-a,", GlobalConst.NOT))
                .thenReturn(Arrays.asList(child, grandChild));

        DeptUpdateDTO dto = new DeptUpdateDTO();
        dto.setCode("dept-a");
        dto.setParentCode("dept-b");
        dto.setName("部门A");

        deptService.update(dto);

        assertEquals("dept-b", moving.getParentCode());
        assertEquals("0,dept-root,dept-b,", moving.getAncestors());
        assertEquals("0,dept-root,dept-b,dept-a,", child.getAncestors());
        assertEquals("0,dept-root,dept-b,dept-a,dept-a1,", grandChild.getAncestors());

        ArgumentCaptor<List<DeptEntity>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(deptEntityRepository).saveAll(listCaptor.capture());
        assertEquals(2, listCaptor.getValue().size());
        verify(deptEntityRepository).save(any(DeptEntity.class));
    }

    @Test
    void delete_shouldRejectWhenChildrenExist() {
        DeptEntity dept = buildDept("dept-a", "dept-root", "0,dept-root,", "部门A");
        when(deptEntityRepository.findByCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(Optional.of(dept));
        when(deptEntityRepository.existsByParentCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> deptService.delete("dept-a"));
        assertTrue(ex.getMessage().contains("子部门"));
    }

    @Test
    void delete_shouldRejectWhenUsersExist() {
        DeptEntity dept = buildDept("dept-a", "dept-root", "0,dept-root,", "部门A");
        when(deptEntityRepository.findByCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(Optional.of(dept));
        when(deptEntityRepository.existsByParentCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(false);
        when(accountEntityRepository.existsByPrimaryDeptCodeAndDeleted("dept-a", GlobalConst.NOT))
                .thenReturn(true);

        CustomException ex = assertThrows(CustomException.class, () -> deptService.delete("dept-a"));
        assertTrue(ex.getMessage().contains("用户"));
    }

    private DeptEntity buildDept(String code, String parentCode, String ancestors, String name) {
        DeptEntity dept = new DeptEntity();
        dept.setCode(code);
        dept.setParentCode(parentCode);
        dept.setAncestors(ancestors);
        dept.setName(name);
        dept.setStatus(GlobalConst.DATA_STATUS_ENABLE);
        dept.setDeleted(GlobalConst.NOT);
        return dept;
    }
}
