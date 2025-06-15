package com.projects.weather.mapper;

import com.projects.weather.dto.user.request.RegisterRequestDto;
import com.projects.weather.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(RegisterRequestDto registerRequestDto) {
        return User.builder()
                .login(registerRequestDto.login())
                .password(registerRequestDto.password())
                .build();
    }
}
