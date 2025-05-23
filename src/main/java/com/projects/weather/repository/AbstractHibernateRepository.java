package com.projects.weather.repository;

import com.projects.weather.exception.DatabaseException;
import com.projects.weather.model.Identifiable;
import com.projects.weather.util.DaoRetriever;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Slf4j
public abstract class AbstractHibernateRepository<K extends Serializable, E extends Identifiable<K>>
        implements GenericRepository<K, E> {

    @PersistenceContext
    protected EntityManager entityManager;
    protected final Class<E> entityClass;

    @Autowired
    protected AbstractHibernateRepository(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Optional<E> findById(K id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<E> findAll() {
        var criteria = entityManager.getCriteriaBuilder().createQuery(entityClass);
        criteria.select(criteria.from(entityClass));
        return entityManager.createQuery(criteria).getResultList();
    }

    @Override
    public E save(E entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public void delete(K id) {
        var entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
            entityManager.flush();
        }
    }

    @Override
    public void deleteAll() {
        var entities = findAll();
        for (var entity : entities) {
            entityManager.remove(entity);
            entityManager.flush();
        }
    }

    protected Optional<E> findOrEmpty(DaoRetriever<E> retriever) {
        try {
            return Optional.of(retriever.retrieve());
        } catch (NoSuchElementException e) {
            log.debug("No result found", e);
            return Optional.empty();
        } catch (NonUniqueResultException e) {
            log.error("Multiple result found when single was expected", e);
            throw new DatabaseException("Multiple result found when single was expected", e);
        }
    }
}
