package com.projects.weather.dto.external.openweather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeatherResponseDto(@JsonProperty("weather") List<WeatherInfo> weatherInfo,
                                        @JsonProperty("main") TemperatureInfo temperatureInfo,
                                        @JsonProperty("sys") SysInfo sysInfo) {

    public record WeatherInfo(@JsonProperty("main") String condition,
                               @JsonProperty("description") String description,
                              @JsonProperty("icon") String iconId) {
    }

    public record TemperatureInfo(@JsonProperty("temp") Double temperature,
                                   @JsonProperty("feels_like") Double feelsLikeTemp,
                                   @JsonProperty("humidity") Double humidity) {
    }

    public record SysInfo(@JsonProperty("country") String countryCode) {}
}
