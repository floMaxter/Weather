package com.projects.weather.repository;

import com.projects.weather.exception.DatabaseException;
import com.projects.weather.exception.EntityAlreadyExistsException;
import com.projects.weather.model.Identifiable;
import com.projects.weather.util.DaoRetriever;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
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
        try {
            entityManager.persist(entity);
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("duplicate")) {
                log.warn("Attempt to save entity with non-unique attributes", ex);
                throw new EntityAlreadyExistsException("Attempt to save entity with non-unique attributes");
            }

            log.error("Unexpected error occurred while saving the entity in database", ex);
            throw new DatabaseException("Unexpected error occurred while saving the entity in database");
        }

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
        } catch (NoResultException ex) {
            log.debug("No result found", ex);
            return Optional.empty();
        } catch (RuntimeException ex) {
            log.error("Unexpected error occurred while retrieving data from database", ex);
            throw new DatabaseException("Unexpected error occurred while retrieving data from database");
        }
    }
}
