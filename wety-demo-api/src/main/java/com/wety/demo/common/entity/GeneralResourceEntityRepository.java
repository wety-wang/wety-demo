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
public interface GeneralResourceEntityRepository extends JpaRepository<GeneralResourceEntity, Long>, JpaSpecificationExecutor<GeneralResourceEntity> {

    List<GeneralResourceEntity> findAllByOwnerCode(String ownerCode);

    Optional<GeneralResourceEntity> findByCode(String code);

    @Modifying
    @Query(value = "update GeneralResourceEntity set ownerCode = null where ownerCode =?1")
    void updateToNullByOwnerCode(String ownerCode);

    void deleteByOwnerCode(String ownerCode);

    Page<GeneralResourceEntity> findAll(CriteriaQueryObject queryParam, Pageable pageable);
}
