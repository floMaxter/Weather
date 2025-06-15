package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class LoginAlreadyExistsException extends GenericApplicationException {

    public LoginAlreadyExistsException(String message) {
        super(message, HttpServletResponse.SC_CONFLICT);
    }
}
