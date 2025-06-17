package com.projects.weather.exception;

import jakarta.servlet.http.HttpServletResponse;

public class LocationNotFoundException extends GenericApplicationException {

    public LocationNotFoundException(String message) {
        super(message, HttpServletResponse.SC_NOT_FOUND);
    }
}
