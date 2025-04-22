package com.projects.weather.service;

import com.projects.weather.dto.UserDto;

import java.util.UUID;

public interface AuthService {

    UUID login(UserDto userDto);
}
