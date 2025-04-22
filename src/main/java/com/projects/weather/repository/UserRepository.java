package com.projects.weather.repository;

import com.projects.weather.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository extends AbstractHibernateRepository<Long, User> {

    @Autowired
    protected UserRepository(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    public Optional<User> findByLogin(String login) {
        var session = sessionFactory.getCurrentSession();
        return session.createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }
}
