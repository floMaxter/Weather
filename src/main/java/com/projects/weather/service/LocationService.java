package com.projects.weather.service;

import com.projects.weather.client.OpenWeatherClient;
import com.projects.weather.dto.location.response.LocationSearchReadDto;
import com.projects.weather.dto.location.response.LocationWithWeatherDto;
import com.projects.weather.mapper.LocationMapper;
import com.projects.weather.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final OpenWeatherClient openWeatherClient;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(OpenWeatherClient openWeatherClient,
                           LocationMapper locationMapper,
                           LocationRepository locationRepository) {
        this.openWeatherClient = openWeatherClient;
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
    }

    public List<LocationWithWeatherDto> getLocationsWithWeatherByUserLogin(String login) {
        var locations = locationRepository.findByUserLogin(login);

        List<LocationWithWeatherDto> locationsWithWeather = new ArrayList<>();
        for (var location : locations) {
            var currentWeather = openWeatherClient.findWeatherDataByCoordinates(location.getLatitude(), location.getLongitude());
            var locationWithWeatherDto = locationMapper.toLocationWithWeatherDto(location, currentWeather);
            locationsWithWeather.add(locationWithWeatherDto);
        }

        return locationsWithWeather;
    }

    public List<LocationSearchReadDto> getLocationsByName(String locationName) {
        return openWeatherClient.findAllLocationsByName(locationName).stream()
                .map(locationMapper::toLocationSearchReadDto)
                .toList();
    }
}
