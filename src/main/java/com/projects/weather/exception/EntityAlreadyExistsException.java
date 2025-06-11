package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class EntityAlreadyExistsException extends GenericApplicationException {

    public EntityAlreadyExistsException(String message) {
        super(message, HttpServletResponse.SC_CONFLICT);
    }
}
