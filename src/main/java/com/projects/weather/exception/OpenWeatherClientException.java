package com.projects.weather.exception;

public class OpenWeatherClientException extends ExternalServiceException {

    public OpenWeatherClientException(String message) {
        super(message);
    }

    public OpenWeatherClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
