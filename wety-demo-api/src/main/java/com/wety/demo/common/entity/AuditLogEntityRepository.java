package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author wety
 */
@Repository
public interface AuditLogEntityRepository extends BaseRepository<AuditLogEntity, Long> {

    Optional<AuditLogEntity> findFirstByAccountCodeAndAuditTypeOrderByOperationTimeDesc(String accountCode, String auditType);
}
