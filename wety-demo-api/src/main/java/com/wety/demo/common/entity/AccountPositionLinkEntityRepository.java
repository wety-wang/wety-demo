package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 账号与岗位关联数据访问接口。
 */
@Repository
public interface AccountPositionLinkEntityRepository extends BaseRepository<AccountPositionLinkEntity, Long> {

    /**
     * 查询账号关联的所有未删除岗位编码。
     *
     * @param accountCode 账号业务编码
     * @param deleted     删除标识（0-未删除）
     * @return 去重后的岗位编码列表
     */
    @Query("SELECT DISTINCT l.positionCode FROM AccountPositionLinkEntity l WHERE l.accountCode = :accountCode AND l.deleted = :deleted")
    List<String> findDistinctPositionCodesByAccountCodeAndDeleted(@Param("accountCode") String accountCode,
                                                                  @Param("deleted") Integer deleted);

    /**
     * 批量查询多个账号的未删除岗位关联记录。
     *
     * @param accountCodes 账号业务编码集合
     * @param deleted      删除标识（0-未删除）
     * @return 关联记录列表
     */
    List<AccountPositionLinkEntity> findAllByAccountCodeInAndDeleted(Collection<String> accountCodes, Integer deleted);

    /**
     * 判断某岗位是否已被未删除的账号关联。
     *
     * @param positionCode 岗位业务编码
     * @param deleted      删除标识（0-未删除）
     * @return 是否已被引用
     */
    boolean existsByPositionCodeAndDeleted(String positionCode, Integer deleted);

    /**
     * 查询岗位关联的所有未删除账号编码。
     *
     * @param positionCode 岗位业务编码
     * @param deleted      删除标识（0-未删除）
     * @return 去重后的账号编码列表
     */
    @Query("SELECT DISTINCT l.accountCode FROM AccountPositionLinkEntity l WHERE l.positionCode = :positionCode AND l.deleted = :deleted")
    List<String> findDistinctAccountCodesByPositionCodeAndDeleted(@Param("positionCode") String positionCode,
                                                                  @Param("deleted") Integer deleted);

    /**
     * 按账号删除全部岗位关联记录（用于更新账号岗位关联前清理）。
     *
     * @param accountCode 账号业务编码
     */
    void deleteByAccountCode(String accountCode);
}
