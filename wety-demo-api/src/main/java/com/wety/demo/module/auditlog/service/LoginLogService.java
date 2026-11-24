package com.wety.demo.module.auditlog.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.entity.LoginAuditLogEntity;
import com.wety.demo.common.entity.LoginAuditLogEntityRepository;
import com.wety.demo.core.datascope.DataScope;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.auditlog.dto.LoginLogPageQueryDTO;
import com.wety.demo.module.auditlog.vo.LoginLogInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 登录审计日志服务。
 *
 * @author wety
 */
@Service
public class LoginLogService {

    private static final Logger log = LoggerFactory.getLogger(LoginLogService.class);

    @Resource
    private LoginAuditLogEntityRepository loginAuditLogEntityRepository;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    /**
     * 登录日志分页查询（受角色数据权限约束）。
     *
     * @param queryDTO 分页查询条件
     * @return 登录日志分页数据
     */
    @DataScope(deptField = "deptCode", userField = "accountCode")
    public Page<LoginLogInfoVO> listLoginLogPageable(LoginLogPageQueryDTO queryDTO) {
        List<Date> operationTimes = queryDTO.getOperationTime();
        if (CollectionUtils.isNotEmpty(operationTimes)) {
            Date beginDate = operationTimes.get(0);
            Date endDate = new Date(operationTimes.get(1).getTime() + 24 * 60 * 60 * 1000L - 1L);
            queryDTO.setOperationTime(new ArrayList<>(Arrays.asList(beginDate, endDate)));
        }
        Pageable pageable = queryDTO.getPageable(new Sort.Order(Sort.Direction.DESC, "operationTime"));
        Page<LoginAuditLogEntity> entityPage = loginAuditLogEntityRepository.findAll(queryDTO, pageable);
        Map<String, String> deptNameMap = loadDeptNameMap(entityPage.getContent());
        return entityPage.map(entity -> {
            LoginLogInfoVO vo = new LoginLogInfoVO(entity);
            if (StringUtils.isNotBlank(entity.getDeptCode())) {
                vo.setDeptName(deptNameMap.get(entity.getDeptCode()));
            }
            return vo;
        });
    }

    /**
     * 记录登录审计日志。
     *
     * @param loginName 登录名
     * @param result    是否登录成功
     */
    public void addAuditLog(String loginName, boolean result) {
        addAuditLog(loginName, null, result);
    }

    /**
     * 记录登录审计日志，支持直接传入已校验通过的账号实体以写入主部门编码。
     *
     * @param loginName     登录名
     * @param accountEntity 账号实体（可为空）
     * @param result        是否登录成功
     */
    public void addAuditLog(String loginName, AccountEntity accountEntity, boolean result) {
        try {
            if (result) {
                addAuditLog(loginName, accountEntity, "登录成功", GlobalConst.SUCCESS);
            } else {
                addAuditLog(loginName, accountEntity, "登录失败", GlobalConst.FAIL);
            }
        } catch (Exception ex) {
            log.error("登录日志记录失败：", ex);
        }
    }

    /**
     * 记录登录审计日志。
     *
     * @param loginName 登录名
     * @param content   日志描述
     * @param result    结果标识
     */
    public void addAuditLog(String loginName, String content, String result) {
        addAuditLog(loginName, null, content, result);
    }

    /**
     * 构造并持久化登录审计日志实体，同时写入账号当前主部门编码。
     *
     * @param loginName     登录名
     * @param accountEntity 账号实体（为空时按登录名查询未删除账号）
     * @param content       日志描述
     * @param result        结果标识
     */
    public void addAuditLog(String loginName, AccountEntity accountEntity, String content, String result) {
        AccountEntity targetAccount = accountEntity;
        if (targetAccount == null && StringUtils.isNotBlank(loginName)) {
            targetAccount = accountEntityRepository.findByLoginNameAndDeleted(loginName, GlobalConst.NOT).orElse(null);
        }

        LoginAuditLogEntity logEntity = new LoginAuditLogEntity();
        logEntity.setCode(UUID.randomUUID().toString());
        logEntity.setOperationTime(new Date());
        logEntity.setClientIp(AppThreadLocal.getInstance().getClientIp());
        logEntity.setDescription(content);
        logEntity.setResult(result);
        if (targetAccount != null) {
            logEntity.setAccountCode(targetAccount.getCode());
            logEntity.setAccountName(targetAccount.getUserName());
            logEntity.setAccountLoginName(targetAccount.getLoginName());
            logEntity.setDeptCode(targetAccount.getPrimaryDeptCode());
        } else {
            // 兜底：账户不存在或已删除时仍记录基本信息，避免空指针
            logEntity.setAccountCode("-");
            logEntity.setAccountName("-");
            logEntity.setAccountLoginName(StringUtils.defaultIfBlank(loginName, "-"));
            logEntity.setDeptCode(null);
        }
        loginAuditLogEntityRepository.save(logEntity);
    }

    /**
     * 更新当前登录账号最近一次登录日志的最后操作时间。
     */
    public void updateLastOperationTime() {
        try {
            AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
            Optional<LoginAuditLogEntity> optional = loginAuditLogEntityRepository.findFirstByAccountCodeOrderByOperationTimeDesc(currentAccount.getCode());
            optional.ifPresent(entity -> {
                entity.setLastOperationTime(new Date());
                loginAuditLogEntityRepository.save(entity);
            });
        } catch (Exception e) {
            log.error("记录用户最后一次操作时间失败", e);
        }
    }

    /**
     * 更新当前登录账号最近一次登录日志的主动退出时间。
     */
    public void updateLogoutTime() {
        try {
            AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
            Optional<LoginAuditLogEntity> optional = loginAuditLogEntityRepository.findFirstByAccountCodeOrderByOperationTimeDesc(currentAccount.getCode());
            optional.ifPresent(entity -> {
                entity.setLogoutTime(new Date());
                loginAuditLogEntityRepository.save(entity);
            });
        } catch (Exception e) {
            log.error("记录用户退出登录时间失败", e);
        }
    }

    /**
     * 批量加载日志列表涉及的部门编码与名称映射，避免逐行查询导致 N+1。
     *
     * @param logs 登录日志列表
     * @return 部门编码到名称的映射
     */
    private Map<String, String> loadDeptNameMap(List<LoginAuditLogEntity> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return Collections.emptyMap();
        }
        Set<String> deptCodes = logs.stream()
                .map(LoginAuditLogEntity::getDeptCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (deptCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<DeptEntity> depts = deptEntityRepository.findAllByCodeInAndDeleted(deptCodes, GlobalConst.NOT);
        return depts.stream().collect(Collectors.toMap(DeptEntity::getCode, DeptEntity::getName, (a, b) -> a));
    }
}
