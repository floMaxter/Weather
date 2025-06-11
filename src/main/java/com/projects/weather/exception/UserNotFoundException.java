package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class UserNotFoundException extends GenericApplicationException {

    public UserNotFoundException(String message) {
        super(message, HttpServletResponse.SC_NOT_FOUND);
    }
}
