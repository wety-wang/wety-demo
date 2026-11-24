package com.wety.demo.module.auditlog.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.LoginAuditLogEntity;
import com.wety.demo.common.entity.LoginAuditLogEntityRepository;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.auditlog.dto.LoginLogPageQueryDTO;
import com.wety.demo.module.auditlog.vo.LoginLogInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wety
 */
@Service
public class LoginLogService {

    private static final Logger log = LoggerFactory.getLogger(LoginLogService.class);

    @Resource
    private LoginAuditLogEntityRepository loginAuditLogEntityRepository;

    @Resource
    private AccountEntityRepository accountEntityRepository;
    /**
     * 登录日志分页查询
     */
    public Page<LoginLogInfoVO> listLoginLogPageable(LoginLogPageQueryDTO queryDTO) {
        List<Date> operationTimes = queryDTO.getOperationTime();
        if(CollectionUtils.isNotEmpty(operationTimes)){
            Date beginDate = operationTimes.get(0);
            Date endDate = new Date(operationTimes.get(1).getTime() + 24 * 60 * 60 * 1000L - 1L);
            queryDTO.setOperationTime(new ArrayList<>(Arrays.asList(beginDate, endDate)));
        }
        Pageable pageable = queryDTO.getPageable(new Sort.Order(Sort.Direction.DESC, "operationTime"));
        return loginAuditLogEntityRepository.findAll(queryDTO, pageable).map(LoginLogInfoVO::new);
    }

    public void addAuditLog(String loginName, boolean result) {
        try {
            if (result) {
                addAuditLog(loginName, "登录成功", GlobalConst.SUCCESS);
            } else {
                addAuditLog(loginName, "登录失败", GlobalConst.FAIL);
            }
        } catch (Exception ex) {
            log.error("登录日志记录失败：", ex);
        }
    }

    public void addAuditLog(String loginName, String content, String result) {
        // 根据登录名查询账户，构造登录日志实体
        Optional<AccountEntity> accountEntityOptional = accountEntityRepository.findByLoginName(loginName);

        // 构建登录日志实体，记录登录事件
        LoginAuditLogEntity logEntity = new LoginAuditLogEntity();
        // 生成唯一编码，便于后续按code进行关联与查询
        logEntity.setCode(UUID.randomUUID().toString());
        logEntity.setOperationTime(new Date());
        logEntity.setClientIp(AppThreadLocal.getInstance().getClientIp());
        logEntity.setDescription(content);
        logEntity.setResult(result);
        if (accountEntityOptional.isPresent()) {
            AccountEntity accountEntity = accountEntityOptional.get();
            logEntity.setAccountCode(accountEntity.getCode());
            logEntity.setAccountName(accountEntity.getUserName());
            logEntity.setAccountLoginName(accountEntity.getLoginName());
        } else {
            // 兜底：账户不存在时仍记录基本信息，避免空指针
            logEntity.setAccountCode("-");
            logEntity.setAccountName("-");
            logEntity.setAccountLoginName("-");
        }
        // 保存到新建登录日志表
        loginAuditLogEntityRepository.save(logEntity);
    }

    public void updateLastOperationTime() {
        try {
            // 从线程上下文获取当前账户
            AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
            // 查询该账户最近一次登录记录
            // TODO 将当前登录窗口的登录日志code也保存到AppThreadLocal中，以便于在操作日志中记录是哪个登录窗口进行的操作
            Optional<LoginAuditLogEntity> optional = loginAuditLogEntityRepository.findFirstByAccountCodeOrderByOperationTimeDesc(currentAccount.getCode());
            optional.ifPresent(entity -> {
                // 更新最后一次操作时间
                entity.setLastOperationTime(new Date());
                loginAuditLogEntityRepository.save(entity);
            });
        }catch (Exception e){
            log.error("记录用户最后一次操作时间失败", e);
        }
    }

    public void updateLogoutTime() {
        try {
            AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
            // 查询最近一次登录记录并更新退出时间
            Optional<LoginAuditLogEntity> optional = loginAuditLogEntityRepository.findFirstByAccountCodeOrderByOperationTimeDesc(currentAccount.getCode());
            optional.ifPresent(entity -> {
                entity.setLogoutTime(new Date());
                loginAuditLogEntityRepository.save(entity);
            });
        }catch (Exception e){
            log.error("记录用户退出登录时间失败", e);
        }
    }
}
