package com.wety.demo.module.user.service;

import com.wety.demo.common.entity.*;
import com.wety.demo.module.user.dto.RoleUserAddDTO;
import com.wety.demo.module.user.dto.RoleUserDeleteDTO;
import com.wety.demo.module.user.dto.RoleUserPageQueryDTO;
import com.wety.demo.module.user.vo.RoleUserInfoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wety
 */
@Service
public class RoleUserService {

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private RoleEntityRepository roleEntityRepository;

    @Resource
    private AccountRoleLinkEntityRepository accountRoleLinkEntityRepository;

    /**
     * 列表页
     *
     * @param queryDTO
     * @return
     */
    public Page<RoleUserInfoVO> listPageable(RoleUserPageQueryDTO queryDTO) {
        Page<AccountRoleLinkEntity> entityPage;
        Pageable pageable = queryDTO.getPageable();
        if (StringUtils.isEmpty(queryDTO.getUserName())) {
            entityPage = accountRoleLinkEntityRepository.findAllByRoleCode(queryDTO.getRoleCode(), pageable);
        } else {
            List<String> accountCodes = accountEntityRepository.findAccountCodeByNameContaining(queryDTO.getUserName());
            if(accountCodes.isEmpty()){
                return Page.empty(pageable);
            }
            entityPage = accountRoleLinkEntityRepository.findAllByRoleCodeAndAccountCodeIn(queryDTO.getRoleCode(), accountCodes, pageable);
        }

        return entityPage.map(linkEntity -> {
            RoleUserInfoVO vo = new RoleUserInfoVO(linkEntity);
            Optional<AccountEntity> accountEntityOptional = accountEntityRepository.findByCode(linkEntity.getAccountCode());
            accountEntityOptional.ifPresent(accountEntity -> vo.setAccountName(accountEntity.getLoginName()));
            Optional<RoleEntity> roleEntityOptional = roleEntityRepository.findByCode(queryDTO.getRoleCode());
            roleEntityOptional.ifPresent(roleEntity -> vo.setRoleName(roleEntity.getName()));
            return vo;
        });
    }

    /**
     * 根据账户code添加账号角色关联关系
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addRoleUser(RoleUserAddDTO roleUserAddDTO) {
        String roleCode = roleUserAddDTO.getRoleCode();
        List<String> accountCodes = roleUserAddDTO.getAccountCodes();
        for (String accountCode : accountCodes) {
            if (accountRoleLinkEntityRepository.existsByRoleCodeAndAccountCode(roleCode, accountCode)) {
                continue;
            }
            AccountRoleLinkEntity accountRoleLinkEntity = new AccountRoleLinkEntity();
            accountRoleLinkEntity.setAccountCode(accountCode);
            accountRoleLinkEntity.setRoleCode(roleCode);
            accountRoleLinkEntityRepository.save(accountRoleLinkEntity);
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteRoleUser(RoleUserDeleteDTO roleUserDeleteDTO) {
        accountRoleLinkEntityRepository.deleteByRoleCodeAndAccountCode(roleUserDeleteDTO.getRoleCode(),roleUserDeleteDTO.getAccountCode());
    }

}
