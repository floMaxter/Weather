package com.projects.weather.service;

import com.projects.weather.dto.location.internal.LocationWithCoordinatesDto;
import com.projects.weather.dto.location.request.CreateLocationRequestDto;
import com.projects.weather.dto.user.response.UserWithLocationsDto;
import com.projects.weather.mapper.LocationMapper;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       LocationMapper locationMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
    }

    @Transactional(readOnly = true)
    public UserWithLocationsDto findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::toUserWithLocationsDto)
                .orElseThrow(() -> new RuntimeException("The user with this login was not found: " + login));
    }

    @Transactional
    public void addLocationToUser(String login, CreateLocationRequestDto locationDto) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("The user with this login was not found: " + login));

        var location = locationMapper.toLocation(locationDto, user);
        user.addLocation(location);
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(id);
    }

    }
}
