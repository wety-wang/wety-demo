package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 岗位信息数据访问接口。
 */
@Repository
public interface PositionEntityRepository extends BaseRepository<PositionEntity, Long> {

    /**
     * 根据岗位业务编码查询未删除岗位。
     *
     * @param code    岗位业务编码
     * @param deleted 删除标识（0-未删除）
     * @return 岗位实体
     */
    Optional<PositionEntity> findByCodeAndDeleted(String code, Integer deleted);

    /**
     * 查询指定状态且未删除的岗位列表，按排序号和 ID 升序排列。
     *
     * @param status  状态（1-启用，0-停用）
     * @param deleted 删除标识（0-未删除）
     * @return 岗位列表
     */
    List<PositionEntity> findAllByStatusAndDeletedOrderBySortOrderAscIdAsc(Integer status, Integer deleted);

    /**
     * 批量按岗位业务编码查询未删除岗位。
     *
     * @param codes   岗位编码集合
     * @param deleted 删除标识（0-未删除）
     * @return 岗位列表
     */
    List<PositionEntity> findAllByCodeInAndDeleted(Collection<String> codes, Integer deleted);

    /**
     * 判断岗位标识编码是否已存在（新增校验）。
     *
     * @param postCode 岗位标识编码
     * @param deleted  删除标识（0-未删除）
     * @return 是否已存在
     */
    boolean existsByPostCodeAndDeleted(String postCode, Integer deleted);

    /**
     * 判断岗位标识编码是否已存在（修改校验，排除自身）。
     *
     * @param postCode 岗位标识编码
     * @param deleted  删除标识（0-未删除）
     * @param code     需要排除的岗位编码
     * @return 是否已存在
     */
    boolean existsByPostCodeAndDeletedAndCodeNot(String postCode, Integer deleted, String code);

    /**
     * 判断岗位名称是否已存在（新增校验）。
     *
     * @param name    岗位名称
     * @param deleted 删除标识（0-未删除）
     * @return 是否已存在
     */
    boolean existsByNameAndDeleted(String name, Integer deleted);

    /**
     * 判断岗位名称是否已存在（修改校验，排除自身）。
     *
     * @param name    岗位名称
     * @param deleted 删除标识（0-未删除）
     * @param code    需要排除的岗位编码
     * @return 是否已存在
     */
    boolean existsByNameAndDeletedAndCodeNot(String name, Integer deleted, String code);
}
