package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class LocationAlreadyExistsException extends GenericApplicationException {
    public LocationAlreadyExistsException(String message) {
        super(message, HttpServletResponse.SC_CONFLICT);
    }

    public LocationAlreadyExistsException(String message, Throwable cause) {
        super(message, HttpServletResponse.SC_CONFLICT, cause);
    }
}
