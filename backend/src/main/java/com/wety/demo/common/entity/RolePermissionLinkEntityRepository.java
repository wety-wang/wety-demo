package com.wety.demo.common.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wety
 */
@Repository
public interface RolePermissionLinkEntityRepository extends JpaRepository<RolePermissionLinkEntity, Long> {

    @Query(value = "select permissionCode from RolePermissionLinkEntity where roleCode in :roleCodes ")
    List<String> findPermissionCodesByRoleCodeIn(List<String> roleCodes);

    @Query(value = "select permissionCode from RolePermissionLinkEntity where roleCode = :roleCode ")
    List<String> findPermissionCodesByRoleCode(String roleCode);

    void deleteByRoleCode(String roleCode);

}
