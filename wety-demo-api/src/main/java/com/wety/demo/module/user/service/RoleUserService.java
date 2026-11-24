package com.wety.demo.module.user.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountRoleLinkEntity;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.entity.RoleEntity;
import com.wety.demo.common.entity.RoleEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.module.user.dto.RoleUserAddDTO;
import com.wety.demo.module.user.dto.RoleUserDeleteDTO;
import com.wety.demo.module.user.dto.RoleUserPageQueryDTO;
import com.wety.demo.module.user.vo.RoleUserInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 角色关联用户服务。
 *
 * <p>仅针对未删除角色与未删除账号维护关联关系。</p>
 *
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
     * 分页查询拥有指定角色的未删除用户列表。
     *
     * @param queryDTO 查询条件
     * @return 角色关联用户分页数据
     */
    public Page<RoleUserInfoVO> listPageable(RoleUserPageQueryDTO queryDTO) {
        Pageable pageable = queryDTO.getPageable();
        if (StringUtils.isBlank(queryDTO.getRoleCode())) {
            return Page.empty(pageable);
        }

        List<String> linkedAccountCodes = accountRoleLinkEntityRepository.findDistinctAccountCodesByRoleCode(queryDTO.getRoleCode());
        if (CollectionUtils.isEmpty(linkedAccountCodes)) {
            return Page.empty(pageable);
        }

        // 仅保留未删除账号，避免已软删除账号出现在角色关联用户列表中
        List<AccountEntity> validAccounts = accountEntityRepository.findAllByCodeInAndDeleted(linkedAccountCodes, GlobalConst.NOT);
        if (CollectionUtils.isEmpty(validAccounts)) {
            return Page.empty(pageable);
        }

        String keyword = StringUtils.trimToNull(queryDTO.getUserName());
        List<String> filteredAccountCodes = new ArrayList<>();
        for (AccountEntity account : validAccounts) {
            if (keyword == null
                    || StringUtils.containsIgnoreCase(account.getLoginName(), keyword)
                    || StringUtils.containsIgnoreCase(account.getUserName(), keyword)) {
                filteredAccountCodes.add(account.getCode());
            }
        }
        if (filteredAccountCodes.isEmpty()) {
            return Page.empty(pageable);
        }

        Map<String, AccountEntity> accountMap = validAccounts.stream()
                .collect(Collectors.toMap(AccountEntity::getCode, Function.identity(), (a, b) -> a));
        String roleName = roleEntityRepository.findByCodeAndDeleted(queryDTO.getRoleCode(), GlobalConst.NOT)
                .map(RoleEntity::getName)
                .orElse("");

        Page<AccountRoleLinkEntity> entityPage = accountRoleLinkEntityRepository.findAllByRoleCodeAndAccountCodeIn(
                queryDTO.getRoleCode(), filteredAccountCodes, pageable);

        return entityPage.map(linkEntity -> {
            RoleUserInfoVO vo = new RoleUserInfoVO(linkEntity);
            AccountEntity accountEntity = accountMap.get(linkEntity.getAccountCode());
            if (accountEntity != null) {
                vo.setAccountName(accountEntity.getLoginName());
            }
            vo.setRoleName(roleName);
            return vo;
        });
    }

    /**
     * 为角色批量添加未删除的关联用户。
     *
     * @param roleUserAddDTO 添加角色用户参数
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addRoleUser(RoleUserAddDTO roleUserAddDTO) {
        String roleCode = roleUserAddDTO.getRoleCode();
        Optional<RoleEntity> roleOptional = roleEntityRepository.findByCodeAndDeleted(roleCode, GlobalConst.NOT);
        roleOptional.orElseThrow(() -> new CustomException("角色不存在"));

        List<String> accountCodes = roleUserAddDTO.getAccountCodes();
        if (CollectionUtils.isEmpty(accountCodes)) {
            return;
        }
        List<AccountEntity> validAccounts = accountEntityRepository.findAllByCodeInAndDeleted(accountCodes, GlobalConst.NOT);
        for (AccountEntity account : validAccounts) {
            if (accountRoleLinkEntityRepository.existsByRoleCodeAndAccountCode(roleCode, account.getCode())) {
                continue;
            }
            AccountRoleLinkEntity accountRoleLinkEntity = new AccountRoleLinkEntity();
            accountRoleLinkEntity.setAccountCode(account.getCode());
            accountRoleLinkEntity.setRoleCode(roleCode);
            accountRoleLinkEntityRepository.save(accountRoleLinkEntity);
        }
    }

    /**
     * 解除角色与用户的关联关系。
     *
     * @param roleUserDeleteDTO 删除角色用户参数
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteRoleUser(RoleUserDeleteDTO roleUserDeleteDTO) {
        accountRoleLinkEntityRepository.deleteByRoleCodeAndAccountCode(
                roleUserDeleteDTO.getRoleCode(), roleUserDeleteDTO.getAccountCode());
    }
}
