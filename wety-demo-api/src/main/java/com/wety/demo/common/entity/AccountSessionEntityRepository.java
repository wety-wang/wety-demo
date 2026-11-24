package com.wety.demo.common.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author wety
 */
@Repository
public interface AccountSessionEntityRepository extends JpaRepository<AccountSessionEntity, Long>, JpaSpecificationExecutor<AccountSessionEntity> {

    Optional<AccountSessionEntity> findFirstByAccountCodeOrderByIdDesc(String code);

    @Modifying
    @Query(value = "update AccountSessionEntity set token = :token where accountCode = :accountCode")
    void updateTokenByAccountCode(String token, String accountCode);
}
