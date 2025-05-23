package com.projects.weather.repository;

import com.projects.weather.model.Session;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SessionRepository extends AbstractHibernateRepository<UUID, Session> {

    protected SessionRepository() {
        super(Session.class);
    }
}
