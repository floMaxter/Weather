package com.projects.weather.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public abstract class AbstractHibernateRepository<K extends Serializable, E> implements GenericRepository<K, E> {

    private final SessionFactory sessionFactory;
    private final Class<E> entityClass;

    @Autowired
    protected AbstractHibernateRepository(SessionFactory sessionFactory, Class<E> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<E> findById(K id) {
        var session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.find(entityClass, id));
    }

    @Override
    public List<E> findAll() {
        var session = sessionFactory.getCurrentSession();
        var criteria = session.getCriteriaBuilder().createQuery(entityClass);
        criteria.select(criteria.from(entityClass));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public void save(E entity) {
        log.info("Start save entity: {}", entity);
        var session = sessionFactory.getCurrentSession();
        session.persist(entity);
    }
}
