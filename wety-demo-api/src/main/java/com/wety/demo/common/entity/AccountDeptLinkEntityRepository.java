package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 账号与部门关联数据访问接口。
 */
@Repository
public interface AccountDeptLinkEntityRepository extends BaseRepository<AccountDeptLinkEntity, Long> {

    /**
     * 查询账号关联的所有未删除部门编码。
     *
     * @param accountCode 账号业务编码
     * @param deleted     删除标识（0-未删除）
     * @return 去重后的部门编码列表
     */
    @Query("SELECT DISTINCT l.deptCode FROM AccountDeptLinkEntity l WHERE l.accountCode = :accountCode AND l.deleted = :deleted")
    List<String> findDistinctDeptCodesByAccountCodeAndDeleted(@Param("accountCode") String accountCode,
                                                              @Param("deleted") Integer deleted);

    /**
     * 查询账号的所有未删除部门关联记录。
     *
     * @param accountCode 账号业务编码
     * @param deleted     删除标识（0-未删除）
     * @return 关联记录列表
     */
    List<AccountDeptLinkEntity> findAllByAccountCodeAndDeleted(String accountCode, Integer deleted);

    /**
     * 批量查询多个账号的未删除部门关联记录。
     *
     * @param accountCodes 账号业务编码集合
     * @param deleted      删除标识（0-未删除）
     * @return 关联记录列表
     */
    List<AccountDeptLinkEntity> findAllByAccountCodeInAndDeleted(Collection<String> accountCodes, Integer deleted);

    /**
     * 判断某部门下是否存在未删除的账号关联记录。
     *
     * @param deptCode 部门业务编码
     * @param deleted  删除标识（0-未删除）
     * @return 是否存在关联账号
     */
    boolean existsByDeptCodeAndDeleted(String deptCode, Integer deleted);

    /**
     * 按账号删除全部部门关联记录（用于更新账号部门关联前清理）。
     *
     * @param accountCode 账号业务编码
     */
    void deleteByAccountCode(String accountCode);
}
