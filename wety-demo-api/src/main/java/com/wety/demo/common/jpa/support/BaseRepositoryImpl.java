package com.wety.demo.common.jpa.support;

import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
import com.wety.demo.core.datascope.DataScopeContext;
import com.wety.demo.core.misc.AppThreadLocal;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wety
 */
public class BaseRepositoryImpl<T, I> extends SimpleJpaRepository<T, I> implements BaseRepository<T, I> {

    private static final Logger log = LoggerFactory.getLogger(BaseRepositoryImpl.class);

    public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
    }

    @Override
    public Page<T> findAll(CriteriaQueryObject queryObject, Pageable pageable) {
        return findAll(generateSpecification(queryObject), pageable);
    }

    @Override
    public List<T> findAll(CriteriaQueryObject queryObject) {
        return findAll(generateSpecification(queryObject));
    }

    @Override
    public Long count(CriteriaQueryObject queryParam) {
        return count(generateSpecification(queryParam));
    }

    private Specification<T> generateSpecification(CriteriaQueryObject queryObject) {
        return new Specification<T>() {
            private static final long serialVersionUID = -7300263065697831534L;

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                try {
                    return generatePredicate(root, criteriaBuilder, queryObject);
                } catch (Exception e) {
                    log.error("Predicate创建失败", e);
                    throw new CustomException(e);
                }
            }
        };
    }

    private Predicate generatePredicate(Root<T> root, CriteriaBuilder cb, Object queryObject) throws Exception {
        List<Predicate> predicates = new ArrayList<>();

        Field[] fields = queryObject.getClass().getDeclaredFields();
        for (Field field : fields) {
            //
            CriteriaExpression criteriaExpression = field.getAnnotation(CriteriaExpression.class);
            if (criteriaExpression == null) {
                continue;
            }
            //
            String propertyName = criteriaExpression.propertyName();
            if (StringUtils.isEmpty(propertyName)) {
                propertyName = field.getName();
            }
            // 查询参数对应的属性值
            Object value = PropertyUtils.getProperty(queryObject, field.getName());
            Predicate predicate = ofExpressionType(root, cb, criteriaExpression, propertyName, value);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        // 若当前线程激活了数据权限上下文，自动拼装数据权限过滤条件
        Predicate dataScopePredicate = buildDataScopePredicate(root, cb);
        if (dataScopePredicate != null) {
            predicates.add(dataScopePredicate);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * 根据当前线程激活的 {@link DataScopeContext} 构建 JPA 数据权限过滤谓词。
     *
     * <p>若当前查询实体既不含 deptField 也不含 userField，说明属于辅助表查询，自动跳过注入；
     * 若实体至少包含其中一个字段但无任何可见范围，则返回恒假条件（disjunction）防止越权。</p>
     *
     * @param root 查询根实体
     * @param cb   CriteriaBuilder
     * @return 数据权限过滤谓词，无需过滤时返回 null
     */
    private Predicate buildDataScopePredicate(Root<T> root, CriteriaBuilder cb) {
        DataScopeContext context = AppThreadLocal.getInstance().getDataScopeContext();
        if (context == null || context.isAll()) {
            return null;
        }

        boolean hasDeptAttr = StringUtils.isNotBlank(context.getDeptField())
                && hasEntityAttribute(root, context.getDeptField());
        boolean hasUserAttr = StringUtils.isNotBlank(context.getUserField())
                && hasEntityAttribute(root, context.getUserField());

        // 当前实体与注解配置的部门/用户字段均无关时跳过，避免误杀同一方法内的辅助查询
        if (!hasDeptAttr && !hasUserAttr) {
            log.debug("实体 {} 不包含数据权限字段 [deptField={}, userField={}]，跳过自动过滤",
                    root.getJavaType().getSimpleName(), context.getDeptField(), context.getUserField());
            return null;
        }

        List<Predicate> scopeOrPredicates = new ArrayList<>();
        if (hasDeptAttr && !CollectionUtils.isEmpty(context.getAllowedDeptCodes())) {
            In<Object> in = cb.in(root.get(context.getDeptField()));
            for (String deptCode : context.getAllowedDeptCodes()) {
                in.value(deptCode);
            }
            scopeOrPredicates.add(in);
        }

        if (hasUserAttr && context.isIncludeSelf() && StringUtils.isNotBlank(context.getCurrentUserCode())) {
            scopeOrPredicates.add(cb.equal(root.get(context.getUserField()), context.getCurrentUserCode()));
        }

        if (scopeOrPredicates.isEmpty()) {
            return cb.disjunction();
        }
        if (scopeOrPredicates.size() == 1) {
            return scopeOrPredicates.get(0);
        }
        return cb.or(scopeOrPredicates.toArray(new Predicate[0]));
    }

    /**
     * 通过 JPA Metamodel 安全检查目标实体是否包含指定属性。
     *
     * @param root          查询根实体
     * @param attributeName 属性名称
     * @return true 表示存在该属性
     */
    private boolean hasEntityAttribute(Root<T> root, String attributeName) {
        for (Attribute<? super T, ?> attribute : root.getModel().getAttributes()) {
            if (attributeName.equals(attribute.getName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Predicate ofExpressionType(Root<T> root, CriteriaBuilder cb, CriteriaExpression criteriaExpression, String propertyName, Object value) {
        if (value == null || org.springframework.util.StringUtils.isEmpty(value)) {
            if (criteriaExpression.skipOnEmpty()) {
                return null;
            } else {
                return cb.isNull(root.get("id"));
            }
        }
        switch (criteriaExpression.expressionType()) {
            case EQUAL:
                return cb.equal(root.get(propertyName), value);
            case NOT_EQUAL:
                return cb.notEqual(root.get(propertyName), value);
            case LESS_THAN:
                return cb.lessThan(root.get(propertyName), (Comparable<Object>) value);
            case LESS_THAN_EQUAL:
                return cb.lessThanOrEqualTo(root.get(propertyName), (Comparable<Object>) value);
            case GREATER_THAN:
                return cb.greaterThan(root.get(propertyName), (Comparable<Object>) value);
            case GREATER_THAN_EQUAL:
                return cb.greaterThanOrEqualTo(root.get(propertyName), (Comparable<Object>) value);
            case IS_NULL:
                return cb.isNull(root.get(propertyName));
            case IS_NOT_NULL:
                return cb.isNotNull(root.get(propertyName));
            case LIKE:
                return cb.like(root.get(propertyName), "%"+value+"%");
            case IN:
                Collection<Object> values = (Collection<Object>) value;
                if (!CollectionUtils.isEmpty(values)) {
                    In<Object> in = cb.in(root.get(propertyName));
                    values.forEach(in::value);
                    return in;
                } else {
                    if (!criteriaExpression.skipOnEmpty()) {
                        return cb.isNull(root.get("id"));
                    } else {
                        return null;
                    }
                }
            case BEWTEEN:
                List<Object> list = (List<Object>) value;
                if (CollectionUtils.isEmpty(list)) {
                    return null;
                }
                if (list.size() != 2) {
                    throw new CustomException("参数格式错误，Between必须传两个参数值");
                }
                if (list.get(0) != null && list.get(1) != null) {
                    return cb.and(cb.lessThanOrEqualTo(root.get(propertyName), (Comparable<Object>) list.get(1)),
                            cb.greaterThanOrEqualTo(root.get(propertyName), (Comparable<Object>) list.get(0)));
                } else if (list.get(0) != null) {
                    return cb.greaterThanOrEqualTo(root.get(propertyName), (Comparable<Object>) list.get(0));
                } else if (list.get(1) != null) {
                    return cb.lessThanOrEqualTo(root.get(propertyName), (Comparable<Object>) list.get(1));
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

}
