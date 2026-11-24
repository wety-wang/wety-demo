package com.wety.demo.module.user.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.entity.RoleDeptLinkEntity;
import com.wety.demo.common.entity.RoleDeptLinkEntityRepository;
import com.wety.demo.common.entity.RoleEntity;
import com.wety.demo.common.entity.RoleEntityRepository;
import com.wety.demo.common.entity.RolePermissionLinkEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.core.datascope.DataScopeEnum;
import com.wety.demo.module.user.dto.RoleAddDTO;
import com.wety.demo.module.user.dto.RoleDataScopeUpdateDTO;
import com.wety.demo.module.user.dto.RolePageQueryDTO;
import com.wety.demo.module.user.dto.RoleUpdateDTO;
import com.wety.demo.module.user.vo.RoleDataScopeVO;
import com.wety.demo.module.user.vo.RoleInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理服务。
 *
 * <p>负责角色的增删改查、数据权限范围维护及角色编码查询。</p>
 *
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

    @Resource
    private RoleDeptLinkEntityRepository roleDeptLinkEntityRepository;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    /**
     * 分页查询未删除角色列表。
     *
     * @param pageDTO 分页查询参数
     * @return 角色分页结果
     */
    public Page<RoleInfoVO> listPageable(RolePageQueryDTO pageDTO) {
        pageDTO.setDeleted(GlobalConst.NOT);
        Pageable pageable = pageDTO.getPageable(new Sort.Order(Sort.Direction.ASC, GlobalConst.SORT_CREATE_TIME));
        return roleEntityRepository.findAll(pageDTO, pageable).map(RoleInfoVO::new);
    }

    /**
     * 查询所有未删除角色，供下拉选择使用。
     *
     * @return 角色编码与名称列表
     */
    public List<CodeNameDTO> listAll() {
        List<RoleEntity> roles = roleEntityRepository.findAllByDeleted(GlobalConst.NOT);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        roles.sort(Comparator.comparing(RoleEntity::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(RoleEntity::getId, Comparator.nullsLast(Comparator.naturalOrder())));
        return roles.stream().map(role -> {
            CodeNameDTO dto = new CodeNameDTO();
            dto.setCode(role.getCode());
            dto.setName(role.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 新增角色。
     *
     * <p>支持在新增时直接指定数据权限范围（默认仅本人）；当数据范围为自定义部门时校验并保存部门关联。</p>
     *
     * @param saveDTO 新增角色参数
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void add(RoleAddDTO saveDTO) {
        String name = StringUtils.trimToEmpty(saveDTO.getName());
        if (StringUtils.isBlank(name)) {
            throw new CustomException("角色名称不能为空");
        }
        if (roleEntityRepository.existsByNameAndDeleted(name, GlobalConst.NOT)) {
            throw new CustomException("角色名称已存在");
        }

        Integer dataScope = saveDTO.getDataScope() == null ? DataScopeEnum.SELF.getCode() : saveDTO.getDataScope();
        List<String> validDeptCodes = validateDataScopeAndDepts(dataScope, saveDTO.getDeptCodes());

        Date now = new Date();
        RoleEntity saveEntity = new RoleEntity();
        saveEntity.setCode(UUIDGenerator.generatorUUID());
        saveEntity.setName(name);
        saveEntity.setDataScope(dataScope);
        saveEntity.setCreateTime(now);
        saveEntity.setUpdateTime(now);
        saveEntity.setDeleted(GlobalConst.NOT);
        roleEntityRepository.save(saveEntity);

        if (DataScopeEnum.CUSTOM.getCode() == dataScope) {
            saveRoleDeptLinks(saveEntity.getCode(), validDeptCodes, now);
        }
    }

    /**
     * 修改角色基础信息；若传入 dataScope 则同步更新数据权限配置。
     *
     * @param saveDTO 修改角色参数
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update(RoleUpdateDTO saveDTO) {
        String name = StringUtils.trimToEmpty(saveDTO.getName());
        if (StringUtils.isBlank(name)) {
            throw new CustomException("角色名称不能为空");
        }
        Optional<RoleEntity> optionalEntity = roleEntityRepository.findByCodeAndDeleted(saveDTO.getCode(), GlobalConst.NOT);
        RoleEntity roleEntity = optionalEntity.orElseThrow(() -> new CustomException("角色不存在"));
        if (roleEntityRepository.existsByNameAndDeletedAndCodeNot(name, GlobalConst.NOT, roleEntity.getCode())) {
            throw new CustomException("角色名称已存在");
        }

        roleEntity.setName(name);
        roleEntity.setUpdateTime(new Date());
        if (saveDTO.getDataScope() != null) {
            applyRoleDataScope(roleEntity, saveDTO.getDataScope(), saveDTO.getDeptCodes());
        } else {
            roleEntityRepository.save(roleEntity);
        }
    }

    /**
     * 软删除角色，并清理关联的角色部门、角色权限与账号角色数据。
     *
     * @param code 角色业务编码
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(String code) {
        Optional<RoleEntity> roleEntityOptional = roleEntityRepository.findByCodeAndDeleted(code, GlobalConst.NOT);
        RoleEntity roleEntity = roleEntityOptional.orElseThrow(() -> new CustomException(GlobalConst.DELETE_ERROR));

        roleEntity.setDeleted(GlobalConst.YESI);
        roleEntity.setUpdateTime(new Date());
        roleEntityRepository.save(roleEntity);

        roleDeptLinkEntityRepository.deleteByRoleCode(code);
        rolePermissionLinkEntityRepository.deleteByRoleCode(code);
        roleLinkEntityRepository.deleteByRoleCode(code);
    }

    /**
     * 查询角色数据权限配置。
     *
     * @param roleCode 角色业务编码
     * @return 角色数据权限详情
     */
    public RoleDataScopeVO queryDataScope(String roleCode) {
        if (StringUtils.isBlank(roleCode)) {
            throw new CustomException("角色编码不能为空");
        }
        RoleEntity roleEntity = roleEntityRepository.findByCodeAndDeleted(roleCode, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("角色不存在"));

        DataScopeEnum scopeEnum = DataScopeEnum.fromCode(roleEntity.getDataScope());
        RoleDataScopeVO vo = new RoleDataScopeVO();
        vo.setRoleCode(roleEntity.getCode());
        vo.setDataScope(scopeEnum.getCode());
        if (scopeEnum == DataScopeEnum.CUSTOM) {
            List<String> deptCodes = roleDeptLinkEntityRepository.findDistinctDeptCodesByRoleCodeAndDeleted(roleCode, GlobalConst.NOT);
            vo.setDeptCodes(deptCodes == null ? Collections.emptyList() : deptCodes);
        } else {
            vo.setDeptCodes(Collections.emptyList());
        }
        return vo;
    }

    /**
     * 修改角色数据权限范围及自定义部门关联。
     *
     * @param dto 数据权限修改参数
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateDataScope(RoleDataScopeUpdateDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getRoleCode())) {
            throw new CustomException("角色编码不能为空");
        }
        if (dto.getDataScope() == null) {
            throw new CustomException("数据权限范围不能为空");
        }
        RoleEntity roleEntity = roleEntityRepository.findByCodeAndDeleted(dto.getRoleCode(), GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("角色不存在"));
        applyRoleDataScope(roleEntity, dto.getDataScope(), dto.getDeptCodes());
    }

    /**
     * 根据账户编码查询其关联的有效（未删除）角色编码列表。
     *
     * @param accountCode 账户业务编码
     * @return 有效角色编码列表
     */
    public List<String> listAllRoleCode(String accountCode) {
        if (StringUtils.isEmpty(accountCode)) {
            return Collections.emptyList();
        }
        List<String> roleCodes = roleLinkEntityRepository.findDistinctRoleCodesByAccountCode(accountCode);
        if (CollectionUtils.isEmpty(roleCodes)) {
            return Collections.emptyList();
        }
        List<RoleEntity> validRoles = roleEntityRepository.findByCodeInAndDeleted(roleCodes, GlobalConst.NOT);
        if (CollectionUtils.isEmpty(validRoles)) {
            return Collections.emptyList();
        }
        return validRoles.stream().map(RoleEntity::getCode).collect(Collectors.toList());
    }

    /**
     * 校验并持久化角色的数据权限及自定义部门关联。
     *
     * @param roleEntity 角色实体
     * @param dataScope  目标数据范围编码
     * @param deptCodes  自定义部门编码列表
     */
    private void applyRoleDataScope(RoleEntity roleEntity, Integer dataScope, List<String> deptCodes) {
        List<String> validDeptCodes = validateDataScopeAndDepts(dataScope, deptCodes);
        Date now = new Date();
        roleEntity.setDataScope(dataScope);
        roleEntity.setUpdateTime(now);
        roleEntityRepository.save(roleEntity);

        // 先物理清理该角色原有自定义部门关联并 flush，避免唯一索引 (role_code, dept_code) 冲突
        roleDeptLinkEntityRepository.deleteByRoleCode(roleEntity.getCode());
        roleDeptLinkEntityRepository.flush();

        if (DataScopeEnum.CUSTOM.getCode() == dataScope) {
            saveRoleDeptLinks(roleEntity.getCode(), validDeptCodes, now);
        }
    }

    /**
     * 校验数据权限范围编码及自定义部门列表合法性。
     *
     * @param dataScope 数据权限范围编码
     * @param deptCodes 自定义部门编码列表
     * @return 去重且校验通过的部门编码列表（非 CUSTOM 时返回空列表）
     */
    private List<String> validateDataScopeAndDepts(Integer dataScope, List<String> deptCodes) {
        if (!DataScopeEnum.isValidCode(dataScope)) {
            throw new CustomException("无效的数据权限范围");
        }
        if (DataScopeEnum.CUSTOM.getCode() != dataScope) {
            return Collections.emptyList();
        }
        Set<String> uniqueDeptCodes = new LinkedHashSet<>();
        if (deptCodes != null) {
            for (String deptCode : deptCodes) {
                if (StringUtils.isNotBlank(deptCode)) {
                    uniqueDeptCodes.add(deptCode.trim());
                }
            }
        }
        if (uniqueDeptCodes.isEmpty()) {
            throw new CustomException("自定义部门数据权限至少需要选择一个部门");
        }
        List<DeptEntity> existingDepts = deptEntityRepository.findAllByCodeInAndDeleted(uniqueDeptCodes, GlobalConst.NOT);
        if (existingDepts.size() != uniqueDeptCodes.size()) {
            throw new CustomException("所选自定义部门中存在无效或已删除的部门");
        }
        return new ArrayList<>(uniqueDeptCodes);
    }

    /**
     * 批量保存角色与自定义部门的关联记录。
     *
     * @param roleCode  角色业务编码
     * @param deptCodes 部门业务编码列表
     * @param now       操作时间
     */
    private void saveRoleDeptLinks(String roleCode, List<String> deptCodes, Date now) {
        List<RoleDeptLinkEntity> links = new ArrayList<>(deptCodes.size());
        for (String deptCode : deptCodes) {
            RoleDeptLinkEntity link = new RoleDeptLinkEntity();
            link.setCode(UUIDGenerator.generatorUUID());
            link.setRoleCode(roleCode);
            link.setDeptCode(deptCode);
            link.setCreateTime(now);
            link.setUpdateTime(now);
            link.setDeleted(GlobalConst.NOT);
            links.add(link);
        }
        roleDeptLinkEntityRepository.saveAll(links);
    }
}
