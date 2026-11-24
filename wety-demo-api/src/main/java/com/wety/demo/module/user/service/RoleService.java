package com.wety.demo.module.user.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.entity.RoleEntity;
import com.wety.demo.common.entity.RoleEntityRepository;
import com.wety.demo.common.entity.RolePermissionLinkEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.module.user.dto.RoleAddDTO;
import com.wety.demo.module.user.dto.RolePageQueryDTO;
import com.wety.demo.module.user.dto.RoleUpdateDTO;
import com.wety.demo.module.user.vo.RoleInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author wety
 */
@Service
public class RoleService {

    @Resource
    private RoleEntityRepository roleEntityRepository;

    @Resource
    private AccountRoleLinkEntityRepository roleLinkEntityRepository;

    @Resource
    private RolePermissionLinkEntityRepository rolePermissionLinkEntityRepository;

    /**
     * 列表页
     *
     * @param pageDTO
     * @return
     */
    public Page<RoleInfoVO> listPageable(RolePageQueryDTO pageDTO) {
        Pageable pageable = pageDTO.getPageable(new Sort.Order(Sort.Direction.ASC, GlobalConst.SORT_CREATE_TIME));
        return roleEntityRepository.findAll(pageDTO, pageable).map(RoleInfoVO::new);
    }

    /**
     * 添加角色
     *
     * @param saveDTO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void add(RoleAddDTO saveDTO) {
        RoleEntity saveEntity = new RoleEntity();
        saveEntity.setCode(UUIDGenerator.generatorUUID());
        saveEntity.setName(saveDTO.getName());
        saveEntity.setCreateTime(new Date());
        saveEntity.setUpdateTime(new Date());
        saveEntity.setDeleted(GlobalConst.NOT);
        roleEntityRepository.save(saveEntity);
    }

    /**
     * 修改角色
     *
     * @param saveDTO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update(RoleUpdateDTO saveDTO) {
        Optional<RoleEntity> optionalEntity = roleEntityRepository.findByCode(saveDTO.getCode());
        RoleEntity roleEntity = optionalEntity.orElseThrow(() -> new CustomException("角色不存在"));
        roleEntity.setName(saveDTO.getName());
        roleEntity.setUpdateTime(new Date());
        roleEntityRepository.save(roleEntity);
    }

    /**
     * 删除
     *
     * @param codeDTO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(String code) {
        Optional<RoleEntity> roleEntityOptional = roleEntityRepository.findByCodeAndDeleted(code, GlobalConst.NOT);
        roleEntityOptional.orElseThrow(() -> new CustomException(GlobalConst.DELETE_ERROR));
        roleEntityRepository.deleteByCode(code);
        roleLinkEntityRepository.deleteByRoleCode(code);
        rolePermissionLinkEntityRepository.deleteByRoleCode(code);
    }

    /**
     * 根据账户code查询角色列表
     */
    public List<String> listAllRoleCode(String accountCode) {
        if (StringUtils.isEmpty(accountCode)) {
            return Collections.emptyList();
        }
        return roleLinkEntityRepository.findDistinctRoleCodesByAccountCode(accountCode);
    }
}
