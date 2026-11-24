package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 部门信息数据访问接口。
 */
@Repository
public interface DeptEntityRepository extends BaseRepository<DeptEntity, Long> {

    /**
     * 根据部门业务编码查询未删除部门。
     *
     * @param code    部门业务编码
     * @param deleted 删除标识（0-未删除）
     * @return 部门实体
     */
    Optional<DeptEntity> findByCodeAndDeleted(String code, Integer deleted);

    /**
     * 查询所有未删除部门，按排序号和 ID 升序排列。
     *
     * @param deleted 删除标识（0-未删除）
     * @return 部门列表
     */
    List<DeptEntity> findAllByDeletedOrderBySortOrderAscIdAsc(Integer deleted);

    /**
     * 批量按部门编码查询未删除部门。
     *
     * @param codes   部门编码集合
     * @param deleted 删除标识（0-未删除）
     * @return 部门列表
     */
    List<DeptEntity> findAllByCodeInAndDeleted(Collection<String> codes, Integer deleted);

    /**
     * 根据祖级路径片段模糊查询所有子孙部门。
     *
     * <p>调用方应传入 {@code "," + deptCode + ","} 以保证边界精确匹配。</p>
     *
     * @param ancestorSegment 祖级路径片段
     * @param deleted         删除标识（0-未删除）
     * @return 子孙部门列表
     */
    List<DeptEntity> findAllByAncestorsContainingAndDeleted(String ancestorSegment, Integer deleted);

    /**
     * 判断指定父部门下是否存在未删除子部门。
     *
     * @param parentCode 父部门编码
     * @param deleted    删除标识（0-未删除）
     * @return 是否存在子部门
     */
    boolean existsByParentCodeAndDeleted(String parentCode, Integer deleted);

    /**
     * 判断指定父部门下是否存在指定状态的未删除子部门。
     *
     * @param parentCode 父部门编码
     * @param status     状态（1-启用，0-停用）
     * @param deleted    删除标识（0-未删除）
     * @return 是否存在
     */
    boolean existsByParentCodeAndStatusAndDeleted(String parentCode, Integer status, Integer deleted);

    /**
     * 判断同父级下是否存在重名部门（新增校验）。
     *
     * @param parentCode 父部门编码
     * @param name       部门名称
     * @param deleted    删除标识（0-未删除）
     * @return 是否重名
     */
    boolean existsByParentCodeAndNameAndDeleted(String parentCode, String name, Integer deleted);

    /**
     * 判断同父级下是否存在重名部门（修改校验，排除自身）。
     *
     * @param parentCode 父部门编码
     * @param name       部门名称
     * @param deleted    删除标识（0-未删除）
     * @param code       需要排除的部门编码
     * @return 是否重名
     */
    boolean existsByParentCodeAndNameAndDeletedAndCodeNot(String parentCode, String name, Integer deleted, String code);
}
