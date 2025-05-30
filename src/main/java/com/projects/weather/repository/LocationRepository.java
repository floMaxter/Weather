package com.projects.weather.repository;

import com.projects.weather.model.Location;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepository extends AbstractHibernateRepository<Long, Location> {

    protected LocationRepository() {
        super(Location.class);
    }
}
