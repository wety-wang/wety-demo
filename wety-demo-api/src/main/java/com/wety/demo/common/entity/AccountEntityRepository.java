package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.CriteriaQueryObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    /**
     * 按业务编码查询未删除账号。
     *
     * @param code    账号业务编码
     * @param deleted 删除标识（0-未删除）
     * @return 账号实体
     */
    Optional<AccountEntity> findByCodeAndDeleted(String code, Integer deleted);

    /**
     * 按登录名查询未删除账号。
     *
     * @param loginName 登录名
     * @param deleted   删除标识（0-未删除）
     * @return 账号实体
     */
    Optional<AccountEntity> findByLoginNameAndDeleted(String loginName, Integer deleted);

    /**
     * 判断未删除账号中登录名是否已存在。
     *
     * @param loginName 登录名
     * @param deleted   删除标识（0-未删除）
     * @return 是否存在
     */
    boolean existsByLoginNameAndDeleted(String loginName, Integer deleted);

    /**
     * 查询所有未删除账号。
     *
     * @param deleted 删除标识（0-未删除）
     * @return 账号列表
     */
    List<AccountEntity> findAllByDeleted(Integer deleted);

    /**
     * 批量按账号编码查询未删除账号。
     *
     * @param codes   账号编码集合
     * @param deleted 删除标识（0-未删除）
     * @return 账号列表
     */
    List<AccountEntity> findAllByCodeInAndDeleted(Collection<String> codes, Integer deleted);

    /**
     * 判断指定主部门下是否存在未删除账号。
     *
     * @param primaryDeptCode 主部门编码
     * @param deleted         删除标识（0-未删除）
     * @return 是否存在
     */
    boolean existsByPrimaryDeptCodeAndDeleted(String primaryDeptCode, Integer deleted);
}
