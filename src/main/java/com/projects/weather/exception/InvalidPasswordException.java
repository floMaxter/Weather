package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class InvalidPasswordException extends GenericApplicationException {

    public InvalidPasswordException(String message) {
        super(message, HttpServletResponse.SC_UNAUTHORIZED);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, HttpServletResponse.SC_UNAUTHORIZED, cause);
    }
}
