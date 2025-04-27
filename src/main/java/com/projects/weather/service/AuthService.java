package com.projects.weather.service;

import com.projects.weather.dto.LoginRequestDto;
import com.projects.weather.dto.RegisterRequestDto;
import com.projects.weather.dto.UserDto;
import com.projects.weather.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserService userService,
                       SessionService sessionService,
                       PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }

    public UUID login(LoginRequestDto loginRequestDto) {
        var findUserDto = userService.findByLogin(loginRequestDto.login());
        validatePassword(loginRequestDto.password(), findUserDto.password());
        return sessionService.save(findUserDto);
    }

    public void logout(UUID sessionId) {
        sessionService.delete(sessionId);
    }

    public void register(RegisterRequestDto registerRequestDto) {
        var hashedPassword = passwordEncoder.encode(registerRequestDto.password());
        userService.save(new UserDto(null, registerRequestDto.login(), hashedPassword));
    }

    private void validatePassword(String rowPassword, String encryptPassword) {
        if (!passwordEncoder.matches(rowPassword, encryptPassword)) {
            throw new RuntimeException("Incorrect password");
        }
    }
}
