package com.projects.weather.service;

import com.projects.weather.dto.UserDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
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
                .map(userMapper::toDto)
                .orElse(null);
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void save(UserDto userDto) {
        userDto.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));
        userRepository.save(userMapper.toEntity(userDto));
    }
}
