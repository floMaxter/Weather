package com.projects.weather.repository;

import com.projects.weather.model.Identifiable;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public abstract class AbstractHibernateRepository<K extends Serializable, E extends Identifiable<K>>
        implements GenericRepository<K, E> {

    protected final SessionFactory sessionFactory;
    protected final Class<E> entityClass;

    @Autowired
    protected AbstractHibernateRepository(SessionFactory sessionFactory, Class<E> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<E> findById(K id) {
        try (var session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(entityClass, id));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<E> findAll() {
        try (var session = sessionFactory.openSession()) {
            var criteria = session.getCriteriaBuilder().createQuery(entityClass);
            criteria.select(criteria.from(entityClass));
            return session.createQuery(criteria).getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E save(E entity) {
        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();

            return entity;
        } catch (RuntimeException e) {
            log.error("Error saving the entity {}", entity);
            rollbackTransaction(transaction);

            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(K id) {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();

            var entity = session.find(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }

            tx.commit();
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                log.info("Rollback transaction");
                transaction.rollback();
            } catch (Exception e) {
                log.error("Failed to rollback transaction");
            }
        }
    }
}
