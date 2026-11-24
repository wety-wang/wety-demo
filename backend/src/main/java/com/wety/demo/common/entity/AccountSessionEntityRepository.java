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

    /**
     * 按账号编码删除全部登录会话（用于禁用、删除或重置密码后强制下线）。
     *
     * @param accountCode 账号业务编码
     */
    void deleteByAccountCode(String accountCode);
}
