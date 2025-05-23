package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class InternalServerException extends GenericApplicationException {

    public InternalServerException(String message) {
        super(message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, cause);
    }
}
