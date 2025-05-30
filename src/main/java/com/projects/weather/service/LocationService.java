package com.projects.weather.service;

import com.projects.weather.client.OpenWeatherClient;
import com.projects.weather.dto.location.internal.LocationWithCoordinatesDto;
import com.projects.weather.dto.location.response.LocationSearchReadDto;
import com.projects.weather.dto.location.response.LocationWithWeatherDto;
import com.projects.weather.mapper.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final OpenWeatherClient openWeatherClient;
    private final LocationMapper locationMapper;

    @Autowired
    public LocationService(OpenWeatherClient openWeatherClient, LocationMapper locationMapper) {
        this.openWeatherClient = openWeatherClient;
        this.locationMapper = locationMapper;
    }

    public List<LocationWithWeatherDto> getWeatherForLocations(List<LocationWithCoordinatesDto> locationWithCoordinatesDtos) {
        List<LocationWithWeatherDto> locationWithWeatherDtos = new ArrayList<>();
        for (var location : locationWithCoordinatesDtos) {
            var currentWeather = openWeatherClient.findWeatherDataByCoordinates(location.latitude(), location.longitude());
            var locationWithWeatherDto = locationMapper.toLocationWithCoordinatesDto(currentWeather, location);
            locationWithWeatherDtos.add(locationWithWeatherDto);
        }

        return locationWithWeatherDtos;
    }

    public List<LocationSearchReadDto> getLocationsByName(String locationName) {
        return openWeatherClient.findAllLocationsByName(locationName).stream()
                .map(locationMapper::toLocationSearchReadDto)
                .toList();
    }
}
