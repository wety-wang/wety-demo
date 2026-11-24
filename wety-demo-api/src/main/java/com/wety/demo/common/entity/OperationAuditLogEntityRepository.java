package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 操作审计日志仓库
 */
@Repository
public interface OperationAuditLogEntityRepository extends BaseRepository<OperationAuditLogEntity, Long> {
}