package com.wety.demo.common.dao;

import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * DAO基类提供一个模板方法获取实际Repository，并提供常用的增删改查功能
 *
 * @author wety
 */
public abstract class BaseDao<T, I> {

    /**
     * 获取实际Repository
     *
     * @return 实际Repository
     */
    public abstract <R extends CrudRepository<T, I>> R getRepository();

    @PersistenceContext
    private EntityManager entityManager;

    public T save(T entity) {
        return getRepository().save(entity);
    }

    public void delete(T entity) {
        getRepository().delete(entity);
    }

    public Optional<T> findById(I id) {
        return getRepository().findById(id);
    }

}
