package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.CriteriaQueryObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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

    /**
     * 查询所有未删除角色。
     *
     * @param deleted 删除标识（0-未删除）
     * @return 角色列表
     */
    List<RoleEntity> findAllByDeleted(Integer deleted);

    /**
     * 批量按角色编码查询未删除角色。
     *
     * @param codes   角色编码集合
     * @param deleted 删除标识（0-未删除）
     * @return 角色列表
     */
    List<RoleEntity> findByCodeInAndDeleted(Collection<String> codes, Integer deleted);

    /**
     * 判断未删除角色中名称是否已存在。
     *
     * @param name    角色名称
     * @param deleted 删除标识（0-未删除）
     * @return 是否存在
     */
    boolean existsByNameAndDeleted(String name, Integer deleted);

    /**
     * 判断未删除角色中名称是否已存在（排除自身）。
     *
     * @param name    角色名称
     * @param deleted 删除标识（0-未删除）
     * @param code    需要排除的角色编码
     * @return 是否存在
     */
    boolean existsByNameAndDeletedAndCodeNot(String name, Integer deleted, String code);
}
