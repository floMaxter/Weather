package com.projects.weather.service;

import com.projects.weather.dto.location.internal.LocationWithCoordinatesDto;
import com.projects.weather.dto.user.response.UserWithLocationsDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.model.Location;
import com.projects.weather.repository.LocationRepository;
import com.projects.weather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository,
                       LocationRepository locationRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public UserWithLocationsDto findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::toUserWithLocationsDto)
                .orElseThrow(() -> new RuntimeException("The user with this login was not found: " + login));
    }

    @Transactional
    public void create(String login, String password) {
        userRepository.save(User.builder()
                .login(login)
                .password(passwordEncoder.encode(password))
                .build());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
