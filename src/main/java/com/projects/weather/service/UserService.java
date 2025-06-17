package com.projects.weather.service;

import com.projects.weather.dto.location.request.CreateLocationRequestDto;
import com.projects.weather.exception.LocationAlreadyExistsException;
import com.projects.weather.exception.LocationNotFoundException;
import com.projects.weather.exception.UserNotFoundException;
import com.projects.weather.mapper.LocationMapper;
import com.projects.weather.model.User;
import com.projects.weather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LocationMapper locationMapper;

    @Autowired
    public UserService(UserRepository userRepository, LocationMapper locationMapper) {
        this.userRepository = userRepository;
        this.locationMapper = locationMapper;
    }

    @Transactional
    public void addLocationToUser(String login, CreateLocationRequestDto locationDto) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("The user with this login was not found: " + login));

        if (isLocationAlreadyAdded(user, locationDto)) {
            throw new LocationAlreadyExistsException("This location has already been added to the weather list: " +
                                                     "name=" + locationDto.name() + ", " +
                                                     "latitude=" + locationDto.latitude() + ", " +
                                                     "longitude=" + locationDto.longitude());
        }

        var location = locationMapper.toLocation(locationDto, user);
        user.addLocation(location);
        userRepository.save(user);
    }

    @Transactional
    public void removeLocationsFromUser(String login, Long locationId) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("The user with this login was not found: " + login));

        var deletedLocation = user.getLocations().stream()
                .filter(location -> location.getId().equals(locationId))
                .findFirst()
                .orElseThrow(() -> new LocationNotFoundException("The location with this id was not found: " + locationId));

        user.removeLocation(deletedLocation);
    }

    public boolean isLocationAlreadyAdded(User user, CreateLocationRequestDto locationRequestDto) {
        return user.getLocations().stream()
                .anyMatch(existinglocation ->
                        Objects.equals(existinglocation.getLongitude(), locationRequestDto.longitude())
                        && Objects.equals(existinglocation.getLatitude(), locationRequestDto.latitude())
                );
    }
}
