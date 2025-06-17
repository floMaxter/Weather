package com.projects.weather.repository;

import com.projects.weather.model.Location;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationRepository extends AbstractHibernateRepository<Long, Location> {

    protected LocationRepository() {
        super(Location.class);
    }

    public List<Location> findByUserLogin(String login) {
        return entityManager.createQuery("select l from Location l " +
                                         "join User u on l.user = u " +
                                         "where u.login = :login", Location.class)
                .setParameter("login", login)
                .getResultList();
    }
}
