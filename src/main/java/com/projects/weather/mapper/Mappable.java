package com.projects.weather.mapper;

import java.util.List;

public interface Mappable<F, T> {

    T mapFrom(F object);
    List<T> mapFrom(List<F> objects);
    F mapTo(T object);
    List<F> mapTo(List<T> objects);
}
