package com.wety.demo.common.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wety
 */
@Repository
public interface AccountRoleLinkEntityRepository extends CrudRepository<AccountRoleLinkEntity, Long> {

    @Query(value = "select distinct roleCode from AccountRoleLinkEntity where accountCode = :accountCode")
    List<String> findDistinctRoleCodesByAccountCode(String accountCode);

    @Query(value = "select distinct accountCode from AccountRoleLinkEntity where roleCode = :roleCode")
    List<String> findDistinctAccountCodesByRoleCode(String roleCode);

    Page<AccountRoleLinkEntity> findAllByRoleCodeAndAccountCodeIn(String roleCode, List<String> accountCodes, Pageable pageable);

    Page<AccountRoleLinkEntity> findAllByRoleCode(String roleCode, Pageable pageable);

    void deleteByAccountCode(String accountCode);

    void deleteByRoleCode(String roleCode);

    void deleteByRoleCodeAndAccountCode(String roleCode,String accountCode);

    boolean existsByRoleCodeAndAccountCode(String roleCode, String accountCode);
}
