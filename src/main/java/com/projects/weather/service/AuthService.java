package com.projects.weather.service;

import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public AuthService(UserService userService,
                       SessionService sessionService,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    @Transactional
    public UUID login(LoginRequestDto loginRequestDto) {
        var user = userService.findByLogin(loginRequestDto.login());
        validatePassword(loginRequestDto.password(), user.getPassword());
        return sessionService.save(user);
    }

    @Transactional
    public void logout(UUID sessionId) {
        sessionService.delete(sessionId);
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        var user = userMapper.fromRegisterRequest(registerRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
    }

    private void validatePassword(String rawPassword, String encryptPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptPassword)) {
            throw new RuntimeException("Incorrect password");
        }
    }
}
