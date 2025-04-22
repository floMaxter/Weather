package com.projects.weather.repository;

import com.projects.weather.model.Identifiable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface GenericRepository<K extends Serializable, E extends Identifiable<K>> {

    K save(E entity);

    Optional<E> findById(K id);

    List<E> findAll();
}
