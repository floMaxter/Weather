package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class InvalidSessionException extends GenericApplicationException {

    public InvalidSessionException(String message) {
        super(message, HttpServletResponse.SC_BAD_REQUEST);
    }
}
