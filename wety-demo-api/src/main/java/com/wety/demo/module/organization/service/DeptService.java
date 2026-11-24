package com.wety.demo.module.organization.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountDeptLinkEntityRepository;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.organization.dto.DeptAddDTO;
import com.wety.demo.module.organization.dto.DeptStatusUpdateDTO;
import com.wety.demo.module.organization.dto.DeptTreeQueryDTO;
import com.wety.demo.module.organization.dto.DeptUpdateDTO;
import com.wety.demo.module.organization.vo.DeptTreeVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 部门管理服务。
 *
 * <p>负责部门树查询、部门新增、修改（含父级变更时级联刷新子孙节点 ancestors）、软删除及启停控制。</p>
 */
@Service
@Transactional(readOnly = true)
public class DeptService {

    private static final Logger log = LoggerFactory.getLogger(DeptService.class);

    /** 根部门父编码标识。 */
    public static final String ROOT_PARENT_CODE = "0";

    /** 祖级路径分隔符。 */
    private static final String ANCESTOR_SEPARATOR = ",";

    /** 启用状态。 */
    private static final Integer STATUS_ENABLE = GlobalConst.DATA_STATUS_ENABLE;

    /** 停用状态。 */
    private static final Integer STATUS_DISABLE = GlobalConst.DATA_STATUS_DISABLE;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private AccountDeptLinkEntityRepository accountDeptLinkEntityRepository;

    /**
     * 查询部门树。
     *
     * <p>支持按名称模糊搜索和状态筛选；存在筛选条件时保留命中节点及其祖先链路，保证树形结构完整。</p>
     *
     * @param queryDTO 查询条件
     * @return 部门树列表
     */
    public List<DeptTreeVO> tree(DeptTreeQueryDTO queryDTO) {
        List<DeptEntity> allDepts = deptEntityRepository.findAllByDeletedOrderBySortOrderAscIdAsc(GlobalConst.NOT);
        if (CollectionUtils.isEmpty(allDepts)) {
            return Collections.emptyList();
        }

        // 批量查询部门负责人姓名，避免 N+1 查询
        Map<String, String> leaderNameMap = loadLeaderNameMap(allDepts);

        Map<String, DeptTreeVO> voMap = new LinkedHashMap<>();
        Map<String, DeptEntity> entityMap = new HashMap<>();
        for (DeptEntity entity : allDepts) {
            DeptTreeVO vo = new DeptTreeVO(entity);
            if (StringUtils.isNotBlank(entity.getLeaderAccountCode())) {
                vo.setLeaderAccountName(leaderNameMap.get(entity.getLeaderAccountCode()));
            }
            voMap.put(entity.getCode(), vo);
            entityMap.put(entity.getCode(), entity);
        }

        boolean hasFilter = queryDTO != null
                && (StringUtils.isNotBlank(queryDTO.getName()) || queryDTO.getStatus() != null);
        Set<String> keepCodes = null;
        if (hasFilter) {
            keepCodes = resolveFilteredDeptCodes(allDepts, entityMap, queryDTO);
            if (keepCodes.isEmpty()) {
                return Collections.emptyList();
            }
        }

        List<DeptTreeVO> roots = new ArrayList<>();
        for (DeptEntity entity : allDepts) {
            String code = entity.getCode();
            if (keepCodes != null && !keepCodes.contains(code)) {
                continue;
            }
            DeptTreeVO currentVo = voMap.get(code);
            String parentCode = entity.getParentCode();
            if (StringUtils.isNotBlank(parentCode)
                    && !ROOT_PARENT_CODE.equals(parentCode)
                    && voMap.containsKey(parentCode)
                    && (keepCodes == null || keepCodes.contains(parentCode))) {
                voMap.get(parentCode).getChildren().add(currentVo);
            } else {
                roots.add(currentVo);
            }
        }
        return roots;
    }

    /**
     * 新增部门。
     *
     * @param addDTO 新增部门参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(DeptAddDTO addDTO) {
        if (addDTO == null || StringUtils.isBlank(addDTO.getParentCode())) {
            throw new CustomException("父部门编码不能为空");
        }
        if (StringUtils.isBlank(addDTO.getName())) {
            throw new CustomException("部门名称不能为空");
        }
        String parentCode = addDTO.getParentCode().trim();
        String name = addDTO.getName().trim();
        if (name.length() > 100) {
            throw new CustomException("部门名称长度不能超过100");
        }

        DeptEntity parentDept = deptEntityRepository.findByCodeAndDeleted(parentCode, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("父部门不存在或已删除"));

        if (deptEntityRepository.existsByParentCodeAndNameAndDeleted(parentCode, name, GlobalConst.NOT)) {
            throw new CustomException("同级部门下已存在相同名称的部门");
        }

        Integer status = normalizeStatus(addDTO.getStatus(), STATUS_ENABLE);
        String currentUserCode = getCurrentAccountCode();
        Date now = new Date();

        DeptEntity entity = new DeptEntity();
        entity.setCode(UUIDGenerator.generatorUUID());
        entity.setParentCode(parentCode);
        entity.setAncestors(buildChildAncestors(parentDept));
        entity.setName(name);
        entity.setSortOrder(addDTO.getSortOrder() == null ? 0 : addDTO.getSortOrder());
        entity.setLeaderAccountCode(StringUtils.trimToNull(addDTO.getLeaderAccountCode()));
        entity.setPhone(StringUtils.trimToNull(addDTO.getPhone()));
        entity.setEmail(StringUtils.trimToNull(addDTO.getEmail()));
        entity.setStatus(status);
        entity.setCreateUser(currentUserCode);
        entity.setUpdateUser(currentUserCode);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setDeleted(GlobalConst.NOT);

        deptEntityRepository.save(entity);
        log.info("新增部门成功，code: {}, name: {}, parentCode: {}", entity.getCode(), name, parentCode);
    }

    /**
     * 修改部门。
     *
     * <p>变更父部门时校验不可设为自身或自身子孙节点，并级联批量更新全部子孙节点的 ancestors。</p>
     *
     * @param updateDTO 修改部门参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptUpdateDTO updateDTO) {
        if (updateDTO == null || StringUtils.isBlank(updateDTO.getCode())) {
            throw new CustomException("部门编码不能为空");
        }
        if (StringUtils.isBlank(updateDTO.getParentCode())) {
            throw new CustomException("父部门编码不能为空");
        }
        if (StringUtils.isBlank(updateDTO.getName())) {
            throw new CustomException("部门名称不能为空");
        }
        String code = updateDTO.getCode().trim();
        String targetParentCode = updateDTO.getParentCode().trim();
        String name = updateDTO.getName().trim();
        if (name.length() > 100) {
            throw new CustomException("部门名称长度不能超过100");
        }

        DeptEntity entity = getUndeletedDept(code);
        boolean isRoot = ROOT_PARENT_CODE.equals(entity.getParentCode());

        if (isRoot && !ROOT_PARENT_CODE.equals(targetParentCode)) {
            throw new CustomException("根部门不允许修改父部门");
        }

        if (deptEntityRepository.existsByParentCodeAndNameAndDeletedAndCodeNot(
                targetParentCode, name, GlobalConst.NOT, code)) {
            throw new CustomException("同级部门下已存在相同名称的部门");
        }

        Integer targetStatus = updateDTO.getStatus() != null
                ? normalizeStatus(updateDTO.getStatus(), entity.getStatus())
                : entity.getStatus();
        if (STATUS_DISABLE.equals(targetStatus)) {
            if (isRoot) {
                throw new CustomException("根部门不允许停用");
            }
            if (deptEntityRepository.existsByParentCodeAndStatusAndDeleted(code, STATUS_ENABLE, GlobalConst.NOT)) {
                throw new CustomException("存在启用状态的子部门，请先停用子部门");
            }
        }

        String currentUserCode = getCurrentAccountCode();
        Date now = new Date();

        // 父部门发生变更时校验防环并级联刷新子孙节点 ancestors
        if (!Objects.equals(entity.getParentCode(), targetParentCode)) {
            if (code.equals(targetParentCode)) {
                throw new CustomException("父部门不能设置为自身");
            }
            DeptEntity newParent = deptEntityRepository.findByCodeAndDeleted(targetParentCode, GlobalConst.NOT)
                    .orElseThrow(() -> new CustomException("目标父部门不存在或已删除"));

            String selfSegment = ANCESTOR_SEPARATOR + code + ANCESTOR_SEPARATOR;
            if (StringUtils.isNotBlank(newParent.getAncestors()) && newParent.getAncestors().contains(selfSegment)) {
                throw new CustomException("父部门不能设置为当前部门的子孙部门");
            }

            String oldSelfPrefix = entity.getAncestors() + code + ANCESTOR_SEPARATOR;
            String newAncestors = buildChildAncestors(newParent);
            String newSelfPrefix = newAncestors + code + ANCESTOR_SEPARATOR;

            entity.setParentCode(targetParentCode);
            entity.setAncestors(newAncestors);

            List<DeptEntity> descendants = deptEntityRepository
                    .findAllByAncestorsContainingAndDeleted(selfSegment, GlobalConst.NOT);
            if (!CollectionUtils.isEmpty(descendants)) {
                String quotedOldPrefix = Pattern.quote(oldSelfPrefix);
                String replacementPrefix = Matcher.quoteReplacement(newSelfPrefix);
                for (DeptEntity descendant : descendants) {
                    String updatedAncestors = descendant.getAncestors()
                            .replaceFirst(quotedOldPrefix, replacementPrefix);
                    descendant.setAncestors(updatedAncestors);
                    descendant.setUpdateUser(currentUserCode);
                    descendant.setUpdateTime(now);
                }
                deptEntityRepository.saveAll(descendants);
            }
        }

        entity.setName(name);
        if (updateDTO.getSortOrder() != null) {
            entity.setSortOrder(updateDTO.getSortOrder());
        }
        entity.setLeaderAccountCode(StringUtils.trimToNull(updateDTO.getLeaderAccountCode()));
        entity.setPhone(StringUtils.trimToNull(updateDTO.getPhone()));
        entity.setEmail(StringUtils.trimToNull(updateDTO.getEmail()));
        entity.setStatus(targetStatus);
        entity.setUpdateUser(currentUserCode);
        entity.setUpdateTime(now);

        deptEntityRepository.save(entity);
        log.info("修改部门成功，code: {}, name: {}, parentCode: {}", code, name, targetParentCode);
    }

    /**
     * 软删除部门。
     *
     * @param code 部门业务编码
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException("部门编码不能为空");
        }
        DeptEntity entity = getUndeletedDept(code.trim());
        if (ROOT_PARENT_CODE.equals(entity.getParentCode())) {
            throw new CustomException("根部门不允许删除");
        }
        if (deptEntityRepository.existsByParentCodeAndDeleted(entity.getCode(), GlobalConst.NOT)) {
            throw new CustomException("存在未删除的子部门，无法删除");
        }
        if (accountEntityRepository.existsByPrimaryDeptCodeAndDeleted(entity.getCode(), GlobalConst.NOT)
                || accountDeptLinkEntityRepository.existsByDeptCodeAndDeleted(entity.getCode(), GlobalConst.NOT)) {
            throw new CustomException("该部门下存在关联用户，无法删除");
        }

        entity.setDeleted(GlobalConst.YESI);
        entity.setUpdateUser(getCurrentAccountCode());
        entity.setUpdateTime(new Date());
        deptEntityRepository.save(entity);
        log.info("删除部门成功，code: {}", entity.getCode());
    }

    /**
     * 启停部门。
     *
     * @param statusUpdateDTO 启停参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(DeptStatusUpdateDTO statusUpdateDTO) {
        if (statusUpdateDTO == null || StringUtils.isBlank(statusUpdateDTO.getCode())) {
            throw new CustomException("部门编码不能为空");
        }
        Integer targetStatus = normalizeStatus(statusUpdateDTO.getStatus(), null);
        DeptEntity entity = getUndeletedDept(statusUpdateDTO.getCode().trim());

        if (STATUS_DISABLE.equals(targetStatus)) {
            if (ROOT_PARENT_CODE.equals(entity.getParentCode())) {
                throw new CustomException("根部门不允许停用");
            }
            if (deptEntityRepository.existsByParentCodeAndStatusAndDeleted(
                    entity.getCode(), STATUS_ENABLE, GlobalConst.NOT)) {
                throw new CustomException("存在启用状态的子部门，请先停用子部门");
            }
        }

        entity.setStatus(targetStatus);
        entity.setUpdateUser(getCurrentAccountCode());
        entity.setUpdateTime(new Date());
        deptEntityRepository.save(entity);
        log.info("更新部门状态成功，code: {}, status: {}", entity.getCode(), targetStatus);
    }

    /**
     * 批量加载部门负责人姓名映射。
     */
    private Map<String, String> loadLeaderNameMap(List<DeptEntity> depts) {
        Set<String> leaderCodes = new HashSet<>();
        for (DeptEntity dept : depts) {
            if (StringUtils.isNotBlank(dept.getLeaderAccountCode())) {
                leaderCodes.add(dept.getLeaderAccountCode());
            }
        }
        if (leaderCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AccountEntity> leaders = accountEntityRepository.findAllByCodeInAndDeleted(leaderCodes, GlobalConst.NOT);
        Map<String, String> map = new HashMap<>();
        for (AccountEntity leader : leaders) {
            map.put(leader.getCode(), leader.getUserName());
        }
        return map;
    }

    /**
     * 计算符合筛选条件的部门编码集合（包含命中节点及其祖先链路）。
     */
    private Set<String> resolveFilteredDeptCodes(List<DeptEntity> allDepts,
                                                 Map<String, DeptEntity> entityMap,
                                                 DeptTreeQueryDTO queryDTO) {
        String nameKeyword = StringUtils.trimToNull(queryDTO.getName());
        Integer statusFilter = queryDTO.getStatus();
        Set<String> keepCodes = new HashSet<>();

        for (DeptEntity dept : allDepts) {
            boolean nameMatched = nameKeyword == null
                    || (dept.getName() != null && dept.getName().contains(nameKeyword));
            boolean statusMatched = statusFilter == null || Objects.equals(dept.getStatus(), statusFilter);
            if (nameMatched && statusMatched) {
                // 保留命中节点及向上全部祖先节点，避免树结构断裂
                DeptEntity cursor = dept;
                while (cursor != null && keepCodes.add(cursor.getCode())) {
                    String parentCode = cursor.getParentCode();
                    if (StringUtils.isBlank(parentCode) || ROOT_PARENT_CODE.equals(parentCode)) {
                        break;
                    }
                    cursor = entityMap.get(parentCode);
                }
            }
        }
        return keepCodes;
    }

    /**
     * 计算子部门的 ancestors 路径。
     */
    private String buildChildAncestors(DeptEntity parentDept) {
        String parentAncestors = StringUtils.defaultIfBlank(parentDept.getAncestors(), ROOT_PARENT_CODE + ANCESTOR_SEPARATOR);
        if (!parentAncestors.endsWith(ANCESTOR_SEPARATOR)) {
            parentAncestors = parentAncestors + ANCESTOR_SEPARATOR;
        }
        return parentAncestors + parentDept.getCode() + ANCESTOR_SEPARATOR;
    }

    /**
     * 查询未删除部门，不存在时抛出业务异常。
     */
    private DeptEntity getUndeletedDept(String code) {
        return deptEntityRepository.findByCodeAndDeleted(code, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("部门不存在或已删除"));
    }

    /**
     * 校验并规范化状态值（仅允许 0 或 1）。
     */
    private Integer normalizeStatus(Integer status, Integer defaultValue) {
        if (status == null) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new CustomException("状态不能为空");
        }
        if (!STATUS_ENABLE.equals(status) && !STATUS_DISABLE.equals(status)) {
            throw new CustomException("非法的状态值，仅支持 0（停用）或 1（启用）");
        }
        return status;
    }

    /**
     * 获取当前登录账号编码。
     */
    private String getCurrentAccountCode() {
        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        return currentAccount != null ? currentAccount.getCode() : null;
    }
}
