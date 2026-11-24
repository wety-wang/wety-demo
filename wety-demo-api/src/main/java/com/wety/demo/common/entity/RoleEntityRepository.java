package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.CriteriaQueryObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author wety
 */
@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {

    Optional<RoleEntity> findByCode(String code);

    Optional<RoleEntity> findByCodeAndDeleted(String code, Integer deleted);

    Page<RoleEntity> findAll(CriteriaQueryObject queryParam, Pageable pageable);

    void deleteByCode(String code);
}
