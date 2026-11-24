package com.wety.demo.module.auditlog.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.entity.LoginAuditLogEntity;
import com.wety.demo.common.entity.LoginAuditLogEntityRepository;
import com.wety.demo.common.entity.OperationAuditLogEntity;
import com.wety.demo.common.entity.OperationAuditLogEntityRepository;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.core.datascope.DataScope;
import com.wety.demo.module.auditlog.dto.OperationLogAddDTO;
import com.wety.demo.module.auditlog.dto.OperationLogPageQueryDTO;
import com.wety.demo.module.auditlog.vo.OperationLogInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 操作审计日志服务。
 *
 * @author wety
 */
@Service
public class OperationLogService {

    private static final Logger log = LoggerFactory.getLogger(OperationLogService.class);

    @Resource
    private OperationAuditLogEntityRepository operationAuditLogEntityRepository;

    @Resource
    private LoginAuditLogEntityRepository loginAuditLogEntityRepository;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    /**
     * 操作日志分页查询（受角色数据权限约束）。
     *
     * @param queryDTO 分页查询条件
     * @return 操作日志分页数据
     */
    @DataScope(deptField = "deptCode", userField = "accountCode")
    public Page<OperationLogInfoVO> listOperationLogPageable(OperationLogPageQueryDTO queryDTO) {
        List<Date> operationTimes = queryDTO.getOperationTime();
        if (CollectionUtils.isNotEmpty(operationTimes)) {
            Date beginDate = operationTimes.get(0);
            Date endDate = new Date(operationTimes.get(1).getTime() + 24 * 60 * 60 * 1000L - 1L);
            queryDTO.setOperationTime(new ArrayList<>(Arrays.asList(beginDate, endDate)));
        }
        Pageable pageable = queryDTO.getPageable(new Sort.Order(Sort.Direction.DESC, "operationTime"));
        Page<OperationAuditLogEntity> entityPage = operationAuditLogEntityRepository.findAll(queryDTO, pageable);
        Map<String, String> deptNameMap = loadDeptNameMap(entityPage.getContent());
        return entityPage.map(entity -> {
            OperationLogInfoVO vo = new OperationLogInfoVO(entity);
            if (StringUtils.isNotBlank(entity.getDeptCode())) {
                vo.setDeptName(deptNameMap.get(entity.getDeptCode()));
            }
            return vo;
        });
    }

    /**
     * 添加操作日志（使用独立事务，避免业务回滚影响审计记录）。
     *
     * @param operationLogAddDTO 操作日志入参
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void addOperationLog(OperationLogAddDTO operationLogAddDTO) {
        OperationAuditLogEntity entity = new OperationAuditLogEntity();
        entity.setCode(UUIDGenerator.generatorUUID());
        entity.setModelCode(operationLogAddDTO.getModelCode());
        entity.setModelName(operationLogAddDTO.getModelName());
        entity.setPath(operationLogAddDTO.getPath());
        entity.setHttpMethod(operationLogAddDTO.getHttpMethod());
        entity.setQueryString(operationLogAddDTO.getQueryString());
        entity.setUserAgent(operationLogAddDTO.getUserAgent());
        entity.setReferer(operationLogAddDTO.getReferer());
        entity.setRequestId(operationLogAddDTO.getRequestId());
        entity.setAccountCode(operationLogAddDTO.getAccountCode());
        entity.setAccountLoginName(operationLogAddDTO.getAccountLoginName());
        entity.setAccountName(operationLogAddDTO.getAccountName());
        entity.setDeptCode(operationLogAddDTO.getDeptCode());
        entity.setClientIp(operationLogAddDTO.getClientIp());
        entity.setOperationTime(operationLogAddDTO.getOperationTime());
        entity.setResult(operationLogAddDTO.getResult());
        entity.setOperationType(operationLogAddDTO.getOperationType());
        entity.setDescription(operationLogAddDTO.getDescription());
        entity.setDurationMs(operationLogAddDTO.getDurationMs());

        // 关联登录日志：优先使用传入的 loginLogCode；否则根据账户最近一次登录记录填充
        try {
            String accountCode = operationLogAddDTO.getAccountCode();
            String loginLogCodeFromDto = operationLogAddDTO.getLoginLogCode();
            if (loginLogCodeFromDto != null && !loginLogCodeFromDto.isEmpty()) {
                Optional<LoginAuditLogEntity> byCodeOpt = loginAuditLogEntityRepository.findByCode(loginLogCodeFromDto);
                byCodeOpt.ifPresent(login -> entity.setLoginLogCode(login.getCode()));
            } else if (accountCode != null && !"-".equals(accountCode)) {
                Optional<LoginAuditLogEntity> latestLoginOpt = loginAuditLogEntityRepository.findFirstByAccountCodeOrderByOperationTimeDesc(accountCode);
                latestLoginOpt.ifPresent(login -> entity.setLoginLogCode(login.getCode()));
            }
        } catch (Exception e) {
            log.warn("关联登录日志编码/ID失败，忽略该次关联", e);
            entity.setLoginLogCode("");
        }

        operationAuditLogEntityRepository.save(entity);
    }

    /**
     * 批量加载日志列表涉及的部门编码与名称映射，避免逐行查询导致 N+1。
     *
     * @param logs 操作日志列表
     * @return 部门编码到名称的映射
     */
    private Map<String, String> loadDeptNameMap(List<OperationAuditLogEntity> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return Collections.emptyMap();
        }
        Set<String> deptCodes = logs.stream()
                .map(OperationAuditLogEntity::getDeptCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (deptCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<DeptEntity> depts = deptEntityRepository.findAllByCodeInAndDeleted(deptCodes, GlobalConst.NOT);
        return depts.stream().collect(Collectors.toMap(DeptEntity::getCode, DeptEntity::getName, (a, b) -> a));
    }
}
