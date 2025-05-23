package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class NotFoundException extends GenericApplicationException {

    public NotFoundException(String message) {
        super(message, HttpServletResponse.SC_NOT_FOUND);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, HttpServletResponse.SC_NOT_FOUND, cause);
    }
}
