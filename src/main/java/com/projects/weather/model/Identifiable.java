package com.projects.weather.model;

import java.io.Serializable;

public interface Identifiable<K extends Serializable> {

    void setId(K id);

    K getId();
}
