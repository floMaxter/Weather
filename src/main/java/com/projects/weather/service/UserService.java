package com.projects.weather.service;

import com.projects.weather.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);

    void save(UserDto userDto);
}
