package com.projects.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDataResponseDto(@JsonProperty("weather") List<WeatherInfo> weatherInfo,
                                     @JsonProperty("main") TemperatureInfo temperatureInfo) {

    public record WeatherInfo(@JsonProperty("main") String condition,
                               @JsonProperty("description") String description) {
    }

    public record TemperatureInfo(@JsonProperty("temp") Double temperature,
                                   @JsonProperty("feels_like") Double feelsLikeTemp,
                                   @JsonProperty("humidity") Double humidity) {
    }
}
