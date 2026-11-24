package com.wety.demo.module.organization.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountDeptLinkEntity;
import com.wety.demo.common.entity.AccountDeptLinkEntityRepository;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountPositionLinkEntity;
import com.wety.demo.common.entity.AccountPositionLinkEntityRepository;
import com.wety.demo.common.entity.AccountRoleLinkEntity;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.entity.AccountSessionEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.entity.PositionEntity;
import com.wety.demo.common.entity.PositionEntityRepository;
import com.wety.demo.common.entity.RoleEntity;
import com.wety.demo.common.entity.RoleEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.SM2Utils;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.core.datascope.DataScope;
import com.wety.demo.core.datascope.DataScopeChecker;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.organization.dto.AccountCriteriaQuery;
import com.wety.demo.module.organization.dto.OrgUserAddDTO;
import com.wety.demo.module.organization.dto.OrgUserPageQueryDTO;
import com.wety.demo.module.organization.dto.OrgUserResetPasswordDTO;
import com.wety.demo.module.organization.dto.OrgUserStatusUpdateDTO;
import com.wety.demo.module.organization.dto.OrgUserUpdateDTO;
import com.wety.demo.module.organization.vo.OrgUserInfoVO;
import com.wety.demo.module.user.constant.AccountTypeConst;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 组织架构用户管理服务。
 *
 * <p>统一管理全部系统账号，集成角色数据权限自动过滤、单条防水平越权校验、
 * 主部门/多岗位/多角色关联同步及内置超级管理员安全边界保护。</p>
 */
@Service
@Transactional(readOnly = true)
public class OrgUserService {

    private static final Logger log = LoggerFactory.getLogger(OrgUserService.class);

    /** 启用状态（未禁用）。 */
    private static final Integer ACCOUNT_ENABLED = GlobalConst.NOT;

    /** 禁用状态。 */
    private static final Integer ACCOUNT_DISABLED = GlobalConst.YESI;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    @Resource
    private PositionEntityRepository positionEntityRepository;

    @Resource
    private RoleEntityRepository roleEntityRepository;

    @Resource
    private AccountDeptLinkEntityRepository accountDeptLinkEntityRepository;

    @Resource
    private AccountPositionLinkEntityRepository accountPositionLinkEntityRepository;

    @Resource
    private AccountRoleLinkEntityRepository accountRoleLinkEntityRepository;

    @Resource
    private AccountSessionEntityRepository accountSessionEntityRepository;

    @Resource
    private DataScopeChecker dataScopeChecker;

    /**
     * 分页查询用户列表（受数据权限引擎自动过滤）。
     *
     * @param queryDTO 分页与筛选参数
     * @return 用户分页结果（批量回填主部门、岗位及角色名称）
     */
    @DataScope(deptField = "primaryDeptCode", userField = "code")
    public Page<OrgUserInfoVO> listPageable(OrgUserPageQueryDTO queryDTO) {
        Pageable pageable = queryDTO.getPageableWithDefault(
                new Sort.Order(Sort.Direction.DESC, GlobalConst.SORT_CREATE_TIME),
                new Sort.Order(Sort.Direction.DESC, "id")
        );

        AccountCriteriaQuery criteria = new AccountCriteriaQuery();
        criteria.setDeleted(GlobalConst.NOT);
        criteria.setUserName(StringUtils.trimToNull(queryDTO.getUserName()));
        criteria.setLoginName(StringUtils.trimToNull(queryDTO.getLoginName()));
        criteria.setPhoneNumber(StringUtils.trimToNull(queryDTO.getPhoneNumber()));
        criteria.setEmail(StringUtils.trimToNull(queryDTO.getEmail()));
        criteria.setDisabled(queryDTO.getDisabled());

        // 部门树筛选：展开目标部门及其所有未删除子孙部门
        if (StringUtils.isNotBlank(queryDTO.getDeptCode())) {
            String targetDeptCode = queryDTO.getDeptCode().trim();
            Set<String> deptCodes = new LinkedHashSet<>();
            deptCodes.add(targetDeptCode);
            List<DeptEntity> descendants = deptEntityRepository
                    .findAllByAncestorsContainingAndDeleted("," + targetDeptCode + ",", GlobalConst.NOT);
            if (!CollectionUtils.isEmpty(descendants)) {
                for (DeptEntity descendant : descendants) {
                    deptCodes.add(descendant.getCode());
                }
            }
            criteria.setDeptCodes(deptCodes);
        }

        // 可选按岗位/角色筛选，求账号编码交集
        Set<String> filteredAccountCodes = null;
        if (StringUtils.isNotBlank(queryDTO.getPositionCode())) {
            List<String> posAccountCodes = accountPositionLinkEntityRepository
                    .findDistinctAccountCodesByPositionCodeAndDeleted(queryDTO.getPositionCode().trim(), GlobalConst.NOT);
            if (CollectionUtils.isEmpty(posAccountCodes)) {
                return Page.empty(pageable);
            }
            filteredAccountCodes = new LinkedHashSet<>(posAccountCodes);
        }
        if (StringUtils.isNotBlank(queryDTO.getRoleCode())) {
            List<String> roleAccountCodes = accountRoleLinkEntityRepository
                    .findDistinctAccountCodesByRoleCode(queryDTO.getRoleCode().trim());
            if (CollectionUtils.isEmpty(roleAccountCodes)) {
                return Page.empty(pageable);
            }
            if (filteredAccountCodes == null) {
                filteredAccountCodes = new LinkedHashSet<>(roleAccountCodes);
            } else {
                filteredAccountCodes.retainAll(roleAccountCodes);
                if (filteredAccountCodes.isEmpty()) {
                    return Page.empty(pageable);
                }
            }
        }
        if (filteredAccountCodes != null) {
            criteria.setAccountCodes(filteredAccountCodes);
        }

        Page<AccountEntity> entityPage = accountEntityRepository.findAll(criteria, pageable);
        List<OrgUserInfoVO> voList = batchConvertToVOs(entityPage.getContent());
        return new PageImpl<>(voList, pageable, entityPage.getTotalElements());
    }

    /**
     * 查询用户详情（含数据权限防水平越权校验）。
     *
     * @param code 账号业务编码
     * @return 用户详情 VO
     */
    public OrgUserInfoVO query(String code) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException("用户编码不能为空");
        }
        AccountEntity entity = getUndeletedAccount(code.trim());
        dataScopeChecker.assertAccessible(entity.getPrimaryDeptCode(), entity.getCode());
        return batchConvertToVOs(Collections.singletonList(entity)).get(0);
    }

    /**
     * 新增用户。
     *
     * <p>固定设置 {@code superAdmin = 0}、{@code deleted = 0}、{@code type = "normal"}，
     * 禁止通过接口提权创建超级管理员，并同步写入主部门、岗位和角色关联。</p>
     *
     * @param addDTO 新增用户参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(OrgUserAddDTO addDTO) {
        if (addDTO == null || StringUtils.isBlank(addDTO.getLoginName())) {
            throw new CustomException("登录名不能为空");
        }
        if (StringUtils.isBlank(addDTO.getUserName())) {
            throw new CustomException("用户姓名不能为空");
        }
        if (StringUtils.isBlank(addDTO.getPassword())) {
            throw new CustomException("密码不能为空");
        }
        if (StringUtils.isBlank(addDTO.getPrimaryDeptCode())) {
            throw new CustomException("主部门编码不能为空");
        }
        String loginName = addDTO.getLoginName().trim();
        String userName = addDTO.getUserName().trim();
        String primaryDeptCode = addDTO.getPrimaryDeptCode().trim();

        if (accountEntityRepository.existsByLoginName(loginName)) {
            throw new CustomException("该登录名已存在");
        }

        // 校验主部门存在且在当前操作人数据权限范围内（超管不受限）
        deptEntityRepository.findByCodeAndDeleted(primaryDeptCode, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("所选主部门不存在或已删除"));
        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        if (!isSuperAdmin(currentAccount)) {
            dataScopeChecker.assertAccessible(primaryDeptCode, null);
        }

        Integer disabled = normalizeDisabled(addDTO.getDisabled(), ACCOUNT_ENABLED);
        String encodedPassword = encodeDecryptedPassword(addDTO.getPassword());
        Date now = new Date();

        AccountEntity entity = new AccountEntity();
        entity.setCode(UUIDGenerator.generatorUUID());
        entity.setLoginName(loginName);
        entity.setUserName(userName);
        entity.setPassword(encodedPassword);
        entity.setEmail(StringUtils.trimToNull(addDTO.getEmail()));
        entity.setPhoneNumber(StringUtils.trimToNull(addDTO.getPhoneNumber()));
        entity.setDisabled(disabled);
        entity.setType(AccountTypeConst.NORMAL);
        entity.setSuperAdmin(GlobalConst.NOT);
        entity.setPrimaryDeptCode(primaryDeptCode);
        entity.setCreateUser(currentAccount != null ? currentAccount.getCode() : null);
        entity.setDeleted(GlobalConst.NOT);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        accountEntityRepository.save(entity);

        // 同步主部门、多岗位、多角色关联
        syncPrimaryDeptLink(entity.getCode(), primaryDeptCode, now);
        syncPositionLinks(entity.getCode(), addDTO.getPositionCodes(), now);
        syncRoleLinks(entity.getCode(), addDTO.getRoleCodes());

        log.info("新增用户成功，code: {}, loginName: {}, primaryDeptCode: {}",
                entity.getCode(), loginName, primaryDeptCode);
    }

    /**
     * 修改用户。
     *
     * @param updateDTO 修改用户参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(OrgUserUpdateDTO updateDTO) {
        if (updateDTO == null || StringUtils.isBlank(updateDTO.getCode())) {
            throw new CustomException("用户编码不能为空");
        }
        if (StringUtils.isBlank(updateDTO.getUserName())) {
            throw new CustomException("用户姓名不能为空");
        }
        if (StringUtils.isBlank(updateDTO.getPrimaryDeptCode())) {
            throw new CustomException("主部门编码不能为空");
        }
        String code = updateDTO.getCode().trim();
        String primaryDeptCode = updateDTO.getPrimaryDeptCode().trim();

        AccountEntity entity = getUndeletedAccount(code);
        dataScopeChecker.assertAccessible(entity.getPrimaryDeptCode(), entity.getCode());

        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        if (isSuperAdmin(entity) && !isSuperAdmin(currentAccount)) {
            throw new CustomException("无权修改超级管理员账号");
        }

        deptEntityRepository.findByCodeAndDeleted(primaryDeptCode, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("所选主部门不存在或已删除"));
        if (!isSuperAdmin(currentAccount) && !primaryDeptCode.equals(entity.getPrimaryDeptCode())) {
            dataScopeChecker.assertAccessible(primaryDeptCode, entity.getCode());
        }

        Date now = new Date();
        entity.setUserName(updateDTO.getUserName().trim());
        entity.setEmail(StringUtils.trimToNull(updateDTO.getEmail()));
        entity.setPhoneNumber(StringUtils.trimToNull(updateDTO.getPhoneNumber()));
        entity.setPrimaryDeptCode(primaryDeptCode);
        entity.setUpdateTime(now);
        accountEntityRepository.save(entity);

        // 全量覆盖主部门、岗位及角色关联
        syncPrimaryDeptLink(entity.getCode(), primaryDeptCode, now);
        syncPositionLinks(entity.getCode(), updateDTO.getPositionCodes(), now);
        syncRoleLinks(entity.getCode(), updateDTO.getRoleCodes());

        log.info("修改用户成功，code: {}, primaryDeptCode: {}", code, primaryDeptCode);
    }

    /**
     * 软删除用户并清理其关联关系与登录会话。
     *
     * @param code 账号业务编码
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException("用户编码不能为空");
        }
        AccountEntity entity = getUndeletedAccount(code.trim());
        dataScopeChecker.assertAccessible(entity.getPrimaryDeptCode(), entity.getCode());

        if (isSuperAdmin(entity)) {
            throw new CustomException("内置超级管理员不允许删除");
        }
        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        if (currentAccount != null && entity.getCode().equals(currentAccount.getCode())) {
            throw new CustomException("不允许删除当前登录账号");
        }

        entity.setDeleted(GlobalConst.YESI);
        entity.setUpdateTime(new Date());
        accountEntityRepository.save(entity);

        // 清理部门/岗位/角色关联及登录会话
        accountDeptLinkEntityRepository.deleteByAccountCode(entity.getCode());
        accountPositionLinkEntityRepository.deleteByAccountCode(entity.getCode());
        accountRoleLinkEntityRepository.deleteByAccountCode(entity.getCode());
        accountSessionEntityRepository.deleteByAccountCode(entity.getCode());

        log.info("软删除用户成功，code: {}", entity.getCode());
    }

    /**
     * 启停用户（禁用时清除登录会话强制下线）。
     *
     * @param statusUpdateDTO 启停参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(OrgUserStatusUpdateDTO statusUpdateDTO) {
        if (statusUpdateDTO == null || StringUtils.isBlank(statusUpdateDTO.getCode())) {
            throw new CustomException("用户编码不能为空");
        }
        Integer targetDisabled = normalizeDisabled(statusUpdateDTO.getDisabled(), null);
        AccountEntity entity = getUndeletedAccount(statusUpdateDTO.getCode().trim());
        dataScopeChecker.assertAccessible(entity.getPrimaryDeptCode(), entity.getCode());

        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        if (isSuperAdmin(entity) && !isSuperAdmin(currentAccount)) {
            throw new CustomException("无权操作超级管理员账号");
        }
        if (ACCOUNT_DISABLED.equals(targetDisabled)) {
            if (isSuperAdmin(entity)) {
                throw new CustomException("内置超级管理员不允许禁用");
            }
            if (currentAccount != null && entity.getCode().equals(currentAccount.getCode())) {
                throw new CustomException("不允许禁用当前登录账号");
            }
        }

        entity.setDisabled(targetDisabled);
        entity.setUpdateTime(new Date());
        accountEntityRepository.save(entity);

        if (ACCOUNT_DISABLED.equals(targetDisabled)) {
            accountSessionEntityRepository.deleteByAccountCode(entity.getCode());
        }

        log.info("更新用户状态成功，code: {}, disabled: {}", entity.getCode(), targetDisabled);
    }

    /**
     * 重置用户密码。
     *
     * @param resetDTO 重置密码参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(OrgUserResetPasswordDTO resetDTO) {
        if (resetDTO == null || StringUtils.isBlank(resetDTO.getCode())) {
            throw new CustomException("用户编码不能为空");
        }
        if (StringUtils.isBlank(resetDTO.getNewPassword())) {
            throw new CustomException("新密码不能为空");
        }
        AccountEntity entity = getUndeletedAccount(resetDTO.getCode().trim());
        dataScopeChecker.assertAccessible(entity.getPrimaryDeptCode(), entity.getCode());

        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        if (isSuperAdmin(entity) && !isSuperAdmin(currentAccount)) {
            throw new CustomException("无权重置超级管理员密码");
        }

        entity.setPassword(encodeDecryptedPassword(resetDTO.getNewPassword()));
        entity.setUpdateTime(new Date());
        accountEntityRepository.save(entity);

        // 重置密码后清除目标账号会话，要求使用新密码重新登录
        accountSessionEntityRepository.deleteByAccountCode(entity.getCode());
        log.info("重置用户密码成功，code: {}", entity.getCode());
    }

    /**
     * 批量将账号实体转换为展示 VO，批量回填主部门、岗位和角色信息，避免 N+1 查询。
     */
    private List<OrgUserInfoVO> batchConvertToVOs(List<AccountEntity> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }

        List<String> accountCodes = new ArrayList<>(accounts.size());
        Set<String> deptCodes = new HashSet<>();
        for (AccountEntity account : accounts) {
            accountCodes.add(account.getCode());
            if (StringUtils.isNotBlank(account.getPrimaryDeptCode())) {
                deptCodes.add(account.getPrimaryDeptCode());
            }
        }

        // 1. 批量加载主部门名称
        Map<String, String> deptNameMap = new HashMap<>();
        if (!deptCodes.isEmpty()) {
            List<DeptEntity> depts = deptEntityRepository.findAllByCodeInAndDeleted(deptCodes, GlobalConst.NOT);
            for (DeptEntity dept : depts) {
                deptNameMap.put(dept.getCode(), dept.getName());
            }
        }

        // 2. 批量加载账号岗位关联与岗位名称
        List<AccountPositionLinkEntity> positionLinks = accountPositionLinkEntityRepository
                .findAllByAccountCodeInAndDeleted(accountCodes, GlobalConst.NOT);
        Map<String, List<String>> accountPositionCodesMap = new HashMap<>();
        Set<String> allPositionCodes = new HashSet<>();
        for (AccountPositionLinkEntity link : positionLinks) {
            accountPositionCodesMap.computeIfAbsent(link.getAccountCode(), k -> new ArrayList<>())
                    .add(link.getPositionCode());
            allPositionCodes.add(link.getPositionCode());
        }
        Map<String, String> positionNameMap = new HashMap<>();
        if (!allPositionCodes.isEmpty()) {
            List<PositionEntity> positions = positionEntityRepository
                    .findAllByCodeInAndDeleted(allPositionCodes, GlobalConst.NOT);
            for (PositionEntity position : positions) {
                positionNameMap.put(position.getCode(), position.getName());
            }
        }

        // 3. 批量加载账号角色关联与角色名称
        List<AccountRoleLinkEntity> roleLinks = accountRoleLinkEntityRepository.findAllByAccountCodeIn(accountCodes);
        Map<String, List<String>> accountRoleCodesMap = new HashMap<>();
        Set<String> allRoleCodes = new HashSet<>();
        for (AccountRoleLinkEntity link : roleLinks) {
            accountRoleCodesMap.computeIfAbsent(link.getAccountCode(), k -> new ArrayList<>())
                    .add(link.getRoleCode());
            allRoleCodes.add(link.getRoleCode());
        }
        Map<String, String> roleNameMap = new HashMap<>();
        if (!allRoleCodes.isEmpty()) {
            List<RoleEntity> roles = roleEntityRepository.findByCodeInAndDeleted(allRoleCodes, GlobalConst.NOT);
            for (RoleEntity role : roles) {
                roleNameMap.put(role.getCode(), role.getName());
            }
        }

        // 4. 组装结果列表
        List<OrgUserInfoVO> result = new ArrayList<>(accounts.size());
        for (AccountEntity account : accounts) {
            OrgUserInfoVO vo = new OrgUserInfoVO(account);
            if (StringUtils.isNotBlank(account.getPrimaryDeptCode())) {
                vo.setPrimaryDeptName(deptNameMap.get(account.getPrimaryDeptCode()));
            }

            List<String> posCodes = accountPositionCodesMap.getOrDefault(account.getCode(), Collections.emptyList());
            List<String> validPosCodes = new ArrayList<>();
            List<String> validPosNames = new ArrayList<>();
            for (String posCode : posCodes) {
                String posName = positionNameMap.get(posCode);
                if (posName != null) {
                    validPosCodes.add(posCode);
                    validPosNames.add(posName);
                }
            }
            vo.setPositionCodes(validPosCodes);
            vo.setPositionNames(validPosNames);

            List<String> roleCodes = accountRoleCodesMap.getOrDefault(account.getCode(), Collections.emptyList());
            List<String> validRoleCodes = new ArrayList<>();
            List<String> validRoleNames = new ArrayList<>();
            for (String roleCode : roleCodes) {
                String roleName = roleNameMap.get(roleCode);
                if (roleName != null) {
                    validRoleCodes.add(roleCode);
                    validRoleNames.add(roleName);
                }
            }
            vo.setRoleCodes(validRoleCodes);
            vo.setRoleNames(validRoleNames);

            result.add(vo);
        }
        return result;
    }

    /**
     * 同步账号主部门关联记录。
     */
    private void syncPrimaryDeptLink(String accountCode, String primaryDeptCode, Date now) {
        accountDeptLinkEntityRepository.deleteByAccountCode(accountCode);
        accountDeptLinkEntityRepository.flush();

        AccountDeptLinkEntity deptLink = new AccountDeptLinkEntity();
        deptLink.setCode(UUIDGenerator.generatorUUID());
        deptLink.setAccountCode(accountCode);
        deptLink.setDeptCode(primaryDeptCode);
        deptLink.setPrimaryFlag(GlobalConst.YESI);
        deptLink.setLeaderFlag(GlobalConst.NOT);
        deptLink.setCreateTime(now);
        deptLink.setUpdateTime(now);
        deptLink.setDeleted(GlobalConst.NOT);
        accountDeptLinkEntityRepository.save(deptLink);
    }

    /**
     * 全量同步账号岗位关联记录。
     */
    private void syncPositionLinks(String accountCode, List<String> positionCodes, Date now) {
        accountPositionLinkEntityRepository.deleteByAccountCode(accountCode);
        accountPositionLinkEntityRepository.flush();

        Set<String> normalizedCodes = normalizeCodeList(positionCodes);
        if (normalizedCodes.isEmpty()) {
            return;
        }
        List<PositionEntity> validPositions = positionEntityRepository
                .findAllByCodeInAndDeleted(normalizedCodes, GlobalConst.NOT);
        if (validPositions.size() != normalizedCodes.size()) {
            throw new CustomException("存在无效或已删除的岗位编码");
        }

        List<AccountPositionLinkEntity> links = new ArrayList<>(normalizedCodes.size());
        for (String positionCode : normalizedCodes) {
            AccountPositionLinkEntity link = new AccountPositionLinkEntity();
            link.setCode(UUIDGenerator.generatorUUID());
            link.setAccountCode(accountCode);
            link.setPositionCode(positionCode);
            link.setCreateTime(now);
            link.setUpdateTime(now);
            link.setDeleted(GlobalConst.NOT);
            links.add(link);
        }
        accountPositionLinkEntityRepository.saveAll(links);
    }

    /**
     * 全量同步账号角色关联记录。
     */
    private void syncRoleLinks(String accountCode, List<String> roleCodes) {
        accountRoleLinkEntityRepository.deleteByAccountCode(accountCode);

        Set<String> normalizedCodes = normalizeCodeList(roleCodes);
        if (normalizedCodes.isEmpty()) {
            return;
        }
        List<RoleEntity> validRoles = roleEntityRepository.findByCodeInAndDeleted(normalizedCodes, GlobalConst.NOT);
        if (validRoles.size() != normalizedCodes.size()) {
            throw new CustomException("存在无效或已删除的角色编码");
        }

        List<AccountRoleLinkEntity> links = new ArrayList<>(normalizedCodes.size());
        for (String roleCode : normalizedCodes) {
            AccountRoleLinkEntity link = new AccountRoleLinkEntity();
            link.setAccountCode(accountCode);
            link.setRoleCode(roleCode);
            links.add(link);
        }
        accountRoleLinkEntityRepository.saveAll(links);
    }

    /**
     * 去重并去除空白编码。
     */
    private Set<String> normalizeCodeList(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String code : codes) {
            if (StringUtils.isNotBlank(code)) {
                result.add(code.trim());
            }
        }
        return result;
    }

    /**
     * 解密前端传入的 SM2 密码并使用 BCrypt 哈希存储。
     */
    String encodeDecryptedPassword(String encryptedPassword) {
        String rawPassword;
        try {
            rawPassword = SM2Utils.decrypt(encryptedPassword);
        } catch (Exception ex) {
            log.warn("SM2 密码解密失败", ex);
            throw new CustomException("密码格式不正确，请重新输入");
        }
        if (StringUtils.isBlank(rawPassword)) {
            throw new CustomException("密码不能为空");
        }
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    /**
     * 查询未删除账号，不存在时抛出业务异常。
     */
    private AccountEntity getUndeletedAccount(String code) {
        return accountEntityRepository.findByCodeAndDeleted(code, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("用户不存在或已删除"));
    }

    /**
     * 校验并规范化禁用状态（仅允许 0 或 1）。
     */
    private Integer normalizeDisabled(Integer disabled, Integer defaultValue) {
        if (disabled == null) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new CustomException("账号状态不能为空");
        }
        if (!ACCOUNT_ENABLED.equals(disabled) && !ACCOUNT_DISABLED.equals(disabled)) {
            throw new CustomException("非法的账号状态值，仅支持 0（启用）或 1（禁用）");
        }
        return disabled;
    }

    /**
     * 判断账号是否为内置超级管理员（兼容存量 type=admin）。
     */
    private boolean isSuperAdmin(AccountEntity account) {
        if (account == null) {
            return false;
        }
        return GlobalConst.YESI.equals(account.getSuperAdmin())
                || AccountTypeConst.ADMIN.equals(account.getType());
    }
}
