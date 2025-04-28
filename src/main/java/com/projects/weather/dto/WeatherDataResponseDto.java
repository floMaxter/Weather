package com.projects.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDataResponseDto(@JsonProperty("weather") List<WeatherInfo> weatherInfo,
                                     @JsonProperty("main") TemperatureInfo temperatureInfo) {

    private record WeatherInfo(@JsonProperty("main") String condition,
                               @JsonProperty("description") String description) {
    }

    private record TemperatureInfo(@JsonProperty("temp") Double temperature,
                                   @JsonProperty("feels_like") Integer feelsLikeTemp,
                                   @JsonProperty("humidity") Integer humidity) {
    }
}
