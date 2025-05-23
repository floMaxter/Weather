package com.projects.weather.util;

import jakarta.persistence.NoResultException;

@FunctionalInterface
public interface DaoRetriever<T> {

    T retrieve() throws NoResultException;
}
