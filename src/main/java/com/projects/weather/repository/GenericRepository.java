package com.projects.weather.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericRepository<K extends Serializable, E> {

    void save(E entity);

    Optional<E> findById(K id);

    List<E> findAll();
}
