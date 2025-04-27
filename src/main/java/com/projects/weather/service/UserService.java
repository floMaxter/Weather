package com.projects.weather.service;

import com.projects.weather.dto.UserDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::mapTo)
                .orElseThrow(() -> new RuntimeException("The user with this id was not found: " + id));
    }

    public UserDto findByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::mapTo)
                .orElseThrow(() -> new RuntimeException("The user with this login was not found: " + login));
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::mapTo)
                .toList();
    }

    public void save(UserDto userDto) {
        userRepository.save(userMapper.mapFrom(userDto));
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
