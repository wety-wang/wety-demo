package com.wety.demo.common.jpa.support;

import com.wety.demo.common.jpa.CriteriaQueryObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 扩展JpaRepositoty添加动态查询支持<br> see CriteriaQueryObject<br> see CriteriaExpression
 *
 * @param <T>  主表对象类型
 * @param <ID> 主表主键类型
 *
 * @author wety
 */
@NoRepositoryBean
public interface BaseRepository<T, I> extends JpaRepository<T, I>, JpaSpecificationExecutor<T> {
    /**
     * 动态查询-分页
     *
     * @param queryParam 查询参数对象
     * @param pageable 分页参数对象
     *
     * @return 分页数据
     */
    Page<T> findAll(CriteriaQueryObject queryParam, Pageable pageable);

    /**
     * 动态查询-列表
     *
     * @param queryParam 查询参数对象
     *
     * @return 数据列表
     */
    List<T> findAll(CriteriaQueryObject queryParam);

    /**
     * 动态查询-总数
     *
     * @param queryParam 查询参数对象
     *
     * @return 数据总数
     */
    Long count(CriteriaQueryObject queryParam);
}
