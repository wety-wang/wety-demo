package com.wety.demo.common.entity;

import com.wety.demo.common.jpa.support.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 角色自定义数据权限部门关联数据访问接口。
 */
@Repository
public interface RoleDeptLinkEntityRepository extends BaseRepository<RoleDeptLinkEntity, Long> {

    /**
     * 查询指定角色配置的所有未删除自定义部门编码。
     *
     * @param roleCode 角色业务编码
     * @param deleted  删除标识（0-未删除）
     * @return 去重后的部门编码列表
     */
    @Query("SELECT DISTINCT l.deptCode FROM RoleDeptLinkEntity l WHERE l.roleCode = :roleCode AND l.deleted = :deleted")
    List<String> findDistinctDeptCodesByRoleCodeAndDeleted(@Param("roleCode") String roleCode,
                                                           @Param("deleted") Integer deleted);

    /**
     * 批量查询多个角色配置的所有未删除自定义部门编码。
     *
     * @param roleCodes 角色业务编码集合
     * @param deleted   删除标识（0-未删除）
     * @return 去重后的部门编码列表
     */
    @Query("SELECT DISTINCT l.deptCode FROM RoleDeptLinkEntity l WHERE l.roleCode IN :roleCodes AND l.deleted = :deleted")
    List<String> findDistinctDeptCodesByRoleCodeInAndDeleted(@Param("roleCodes") Collection<String> roleCodes,
                                                             @Param("deleted") Integer deleted);

    /**
     * 按角色删除全部自定义部门关联记录（更新角色数据权限前清理）。
     *
     * @param roleCode 角色业务编码
     */
    void deleteByRoleCode(String roleCode);
}
