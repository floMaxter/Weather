package com.projects.weather.repository;

import com.projects.weather.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository extends AbstractHibernateRepository<Long, User> {

    protected UserRepository() {
        super(User.class);
    }

    public Optional<User> findByLogin(String login) {
        return findOrEmpty(() -> entityManager.createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResult());
    }
}
