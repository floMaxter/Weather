package com.projects.weather.dto.location.response;

import lombok.Builder;

@Builder
public record LocationWithWeatherDto(Long id,
                                     String name,
                                     String countyCode,
                                     WeatherInfo weatherInfo,
                                     TemperatureInfo temperatureInfo) {

    @Builder
    public record WeatherInfo(String condition,
                               String description,
                               String iconId) {
    }

    @Builder
    public record TemperatureInfo(int temperature,
                                   int feelsLikeTemp,
                                   int humidity) {
    }
}
