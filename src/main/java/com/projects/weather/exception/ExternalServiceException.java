package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class ExternalServiceException extends GenericApplicationException {

    public ExternalServiceException(String message) {
        super(message, HttpServletResponse.SC_BAD_GATEWAY);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, HttpServletResponse.SC_BAD_GATEWAY, cause);
    }
}
