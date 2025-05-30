package com.projects.weather.mapper;

import com.projects.weather.dto.external.openweather.CurrentWeatherResponseDto;
import com.projects.weather.dto.location.internal.LocationWithCoordinatesDto;
import com.projects.weather.dto.location.response.LocationWithWeatherDto;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationWithWeatherDto toLocationWithCoordinatesDto(CurrentWeatherResponseDto currentWeatherDto,
                                                      LocationWithCoordinatesDto locationWithCoordinatesDto) {
        return LocationWithWeatherDto.builder()
                .id(locationWithCoordinatesDto.id())
                .name(locationWithCoordinatesDto.name())
                .countyCode(currentWeatherDto.sysInfo().countryCode())
                .weatherInfo(LocationWithWeatherDto.WeatherInfo.builder()
                        .condition(currentWeatherDto.weatherInfo().getFirst().condition())
                        .description(currentWeatherDto.weatherInfo().getFirst().description())
                        .iconId(currentWeatherDto.weatherInfo().getFirst().iconId())
                        .build())
                .temperatureInfo(LocationWithWeatherDto.TemperatureInfo.builder()
                        .temperature(currentWeatherDto.temperatureInfo().temperature())
                        .feelsLikeTemp(currentWeatherDto.temperatureInfo().feelsLikeTemp())
                        .humidity(currentWeatherDto.temperatureInfo().humidity())
                        .build())
                .build();
    }

}
