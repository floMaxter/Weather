package com.projects.weather.service.impl;

import com.projects.weather.dto.UserDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.repository.UserRepository;
import com.projects.weather.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void save(UserDto userDto) {
        userDto.setPassword(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()));
        userRepository.save(userMapper.toEntity(userDto));
    }
}
