package com.projects.weather.repository;

import com.projects.weather.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends AbstractHibernateRepository<Long, User> {

    @Autowired
    protected UserRepository(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }
}
