package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 登录审计日志仓库
 */
@Repository
public interface LoginAuditLogEntityRepository extends BaseRepository<LoginAuditLogEntity, Long> {

    /**
     * 查询用户最近一次登录事件
     * @param accountCode 账户唯一标识
     * @return 最近一次登录记录
     */
    Optional<LoginAuditLogEntity> findFirstByAccountCodeOrderByOperationTimeDesc(String accountCode);

    /**
     * 按唯一编码查询登录日志
     */
    Optional<LoginAuditLogEntity> findByCode(String code);
}
