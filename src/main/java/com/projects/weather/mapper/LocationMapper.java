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
        var countryCode = currentWeatherDto.sysInfo().countryCode();
        var weatherInfo = currentWeatherDto.weatherInfo().getFirst();
        var temperatureInfo = currentWeatherDto.temperatureInfo();

        return LocationWithWeatherDto.builder()
                .id(location.getId())
                .name(location.getName())
                .countyCode(countryCode)
                .weatherInfo(LocationWithWeatherDto.WeatherInfo.builder()
                        .condition(weatherInfo.condition())
                        .description(weatherInfo.description())
                        .iconId(weatherInfo.iconId())
                        .build())
                .temperatureInfo(roundTemperatureInfo(temperatureInfo))
                .build();
    }

    private LocationWithWeatherDto.TemperatureInfo roundTemperatureInfo(CurrentWeatherResponseDto.TemperatureInfo temperatureInfo) {
        return LocationWithWeatherDto.TemperatureInfo.builder()
                .temperature((int) Math.round(temperatureInfo.temperature()))
                .feelsLikeTemp((int) Math.round(temperatureInfo.feelsLikeTemp()))
                .humidity((int) Math.round(temperatureInfo.humidity()))
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
