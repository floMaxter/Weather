package com.projects.weather.service.integration.service;

import com.projects.weather.dto.RegisterRequestDto;
import com.projects.weather.security.PasswordEncoder;
import com.projects.weather.service.AuthService;
import com.projects.weather.service.UserService;
import com.projects.weather.service.config.TestAppConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringJUnitConfig(classes = TestAppConfig.class)
public class AuthServiceIT {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Test
    void register() {
        // given
        var login = "test_login_3";
        var password = "password";
        var registerRequestDto = new RegisterRequestDto(login, password);

        // when
        authService.register(registerRequestDto);

        // then
        var savedUserDto = userService.findByLogin(login);
        assertThat(savedUserDto).isNotNull();
        assertThat(savedUserDto.login()).isEqualTo(login);
        assertThat(passwordEncoder.matches(password, savedUserDto.password())).isTrue();
    }
}
