package com.projects.weather.repository;

import com.projects.weather.model.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SessionRepository extends AbstractHibernateRepository<UUID, Session> {

    @Autowired
    protected SessionRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Session.class);
    }
}
