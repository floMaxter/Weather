package com.projects.weather.exception;

import lombok.Getter;

@Getter
public class GenericApplicationException extends RuntimeException {

    private final int statusCode;

    public GenericApplicationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GenericApplicationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
