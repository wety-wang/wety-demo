package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.CriteriaQueryObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wety
 */
@Repository
public interface AccountEntityRepository extends JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor<AccountEntity> {

    Page<AccountEntity> findAll(CriteriaQueryObject queryParam, Pageable pageable);

    Optional<AccountEntity> findFirstByLoginName(String accountName);

    boolean existsByLoginName(String loginName);

    Optional<AccountEntity> findByLoginName(String loginName);

    @Query(value = "select code from AccountEntity where loginName like %?1% ")
    List<String> findAccountCodeByNameContaining(String name);

    @Modifying
    @Query(value = "update AccountEntity set password = ?1 where code = ?2")
    void updatePasswordByCode(String password, String code);

    Optional<AccountEntity> findByCode(String accountCode);

    void deleteByCode(String code);

    List<AccountEntity> findAllByType(String type);
}
