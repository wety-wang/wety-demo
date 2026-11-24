package com.wety.demo.module.auditlog.service;

import com.wety.demo.common.entity.LoginAuditLogEntity;
import com.wety.demo.common.entity.LoginAuditLogEntityRepository;
import com.wety.demo.common.entity.OperationAuditLogEntity;
import com.wety.demo.common.entity.OperationAuditLogEntityRepository;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.module.auditlog.dto.OperationLogAddDTO;
import com.wety.demo.module.auditlog.dto.OperationLogPageQueryDTO;
import com.wety.demo.module.auditlog.vo.OperationLogInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wety
 */
@Service
public class OperationLogService {

    private static final Logger log = LoggerFactory.getLogger(OperationLogService.class);

    @Resource
    private OperationAuditLogEntityRepository operationAuditLogEntityRepository;

    @Resource
    private LoginAuditLogEntityRepository loginAuditLogEntityRepository;

    /**
     * 操作日志分页查询
     */
    public Page<OperationLogInfoVO> listOperationLogPageable(OperationLogPageQueryDTO queryDTO) {
        List<Date> operationTimes = queryDTO.getOperationTime();
        if(CollectionUtils.isNotEmpty(operationTimes)){
            Date beginDate = operationTimes.get(0);
            Date endDate = new Date(operationTimes.get(1).getTime() + 24 * 60 * 60 * 1000L - 1L);
            queryDTO.setOperationTime(new ArrayList<>(Arrays.asList(beginDate, endDate)));
        }
        Pageable pageable = queryDTO.getPageable(new Sort.Order(Sort.Direction.DESC, "operationTime"));
        return operationAuditLogEntityRepository.findAll(queryDTO, pageable).map(OperationLogInfoVO::new);
    }

    /**
     * 添加操作日志
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
        entity.setClientIp(operationLogAddDTO.getClientIp());
        entity.setOperationTime(operationLogAddDTO.getOperationTime());
        entity.setResult(operationLogAddDTO.getResult());
        entity.setOperationType(operationLogAddDTO.getOperationType());
        entity.setDescription(operationLogAddDTO.getDescription());
        entity.setDurationMs(operationLogAddDTO.getDurationMs());

        // 关联登录日志：优先使用传入的loginLogCode；否则根据账户最近一次登录记录填充
        try {
            String accountCode = operationLogAddDTO.getAccountCode();
            String loginLogCodeFromDto = operationLogAddDTO.getLoginLogCode();
            // 兜底：过滤占位符 "-"，仅在存在有效账户编码时关联登录日志
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

}
