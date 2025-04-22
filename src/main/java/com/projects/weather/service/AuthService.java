package com.projects.weather.service;

import com.projects.weather.dto.LoginRequestDto;
import com.projects.weather.dto.RegisterRequestDto;
import com.projects.weather.dto.UserDto;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserService userService;
    private final SessionService sessionService;

    @Autowired
    public AuthService(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public UUID login(LoginRequestDto loginRequestDto) {
        var findUserDto = userService.findByLogin(loginRequestDto.login());
        validatePassword(loginRequestDto.password(), findUserDto.password());
        return sessionService.save(findUserDto);
    }

    public void register(RegisterRequestDto registerRequestDto) {
        var hashedPassword = BCrypt.hashpw(registerRequestDto.password(), BCrypt.gensalt());
        userService.save(new UserDto(null, registerRequestDto.login(), hashedPassword));
    }

    private void validatePassword(String inputPassword, String encryptPassword) {
        if (!BCrypt.checkpw(inputPassword, encryptPassword)) {
            throw new RuntimeException("Incorrect password");
        }
    }
}
