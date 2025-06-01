package com.projects.weather.mapper;

import com.projects.weather.dto.external.openweather.CurrentWeatherResponseDto;
import com.projects.weather.dto.external.openweather.LocationSearchResponseDto;
import com.projects.weather.dto.location.request.CreateLocationRequestDto;
import com.projects.weather.dto.location.response.LocationSearchReadDto;
import com.projects.weather.dto.location.response.LocationWithWeatherDto;
import com.projects.weather.model.Location;
import com.projects.weather.model.User;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public LocationWithWeatherDto toLocationWithWeatherDto(Location location,
                                                           CurrentWeatherResponseDto currentWeatherDto) {
        return LocationWithWeatherDto.builder()
                .id(location.getId())
                .name(location.getName())
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

    public LocationSearchReadDto toLocationSearchReadDto(LocationSearchResponseDto locationSearchResponseDto) {
        return LocationSearchReadDto.builder()
                .name(locationSearchResponseDto.name())
                .longitude(locationSearchResponseDto.longitude())
                .latitude(locationSearchResponseDto.latitude())
                .country(locationSearchResponseDto.country())
                .state(locationSearchResponseDto.state())
                .build();
    }

    public Location toLocation(CreateLocationRequestDto locationRequestDto,
                               User owner) {
        return Location.builder()
                .name(locationRequestDto.name())
                .user(owner)
                .longitude(locationRequestDto.longitude())
                .latitude(locationRequestDto.latitude())
                .build();
    }
}
