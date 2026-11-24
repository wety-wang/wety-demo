package com.wety.demo.common.jpa.support;

import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.jpa.CriteriaExpression;
import com.wety.demo.common.jpa.CriteriaQueryObject;
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
        return cb.and(predicates.toArray(new Predicate[0]));
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
