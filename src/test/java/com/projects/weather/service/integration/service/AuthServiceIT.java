package com.projects.weather.service.integration.service;

import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.security.PasswordEncoder;
import com.projects.weather.service.AuthService;
import com.projects.weather.service.SessionService;
import com.projects.weather.service.UserService;
import com.projects.weather.service.config.TestAppConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringJUnitConfig(classes = TestAppConfig.class)
public class AuthServiceIT {

    private final AuthService authService;
    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    private static final String TEST_LOGIN = "test_login";
    private static final String TEST_PASSWORD = "test_password";

    @Test
    void register_UniqueLogin_ShouldCreateNewUser() {
        // given
        var registerRequestDto = new RegisterRequestDto(TEST_LOGIN, TEST_PASSWORD);

        // when
        authService.register(registerRequestDto);

        // then
        var savedUserDto = userService.findByLogin(TEST_LOGIN);
        assertThat(savedUserDto).isNotNull();
        assertThat(savedUserDto.login()).isEqualTo(TEST_LOGIN);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, savedUserDto.password())).isTrue();
    }

    @Test
    void register_NonUniqueLogin_ShouldThrownException() {
        // given
        var registerRequestDto = new RegisterRequestDto(TEST_LOGIN, TEST_PASSWORD);
        authService.register(registerRequestDto);

        // when

        // then
        assertThatThrownBy(() -> authService.register(registerRequestDto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void login_CorrectLoginAndPassword_ShouldCreateSessionAndReturnSessionUUID() {
        // given
        authService.register(new RegisterRequestDto(TEST_LOGIN, TEST_PASSWORD));
        var savedUserDto = userService.findByLogin(TEST_LOGIN);

        // when
        var loginRequestDto = new LoginRequestDto(TEST_LOGIN, TEST_PASSWORD);
        var actualSessionId = authService.login(loginRequestDto);

        // then
        var createdSessionDto = sessionService.findById(actualSessionId);
        assertThat(createdSessionDto).isNotNull();
        assertThat(savedUserDto.id()).isEqualTo(createdSessionDto.userId());
        assertThat(createdSessionDto.expiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void login_InvalidPassword_ShouldThrowRuntimeException() {
        // given
        var invalidPassword = "invalid_password";
        authService.register(new RegisterRequestDto(TEST_LOGIN, TEST_PASSWORD));

        // when

        // then
        var invalidLoginRequestDto = new LoginRequestDto(TEST_LOGIN, invalidPassword);
        assertThatThrownBy(() -> authService.login(invalidLoginRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Incorrect password");
    }

    @Test
    void login_InvalidLogin_ShouldThrowRuntimeException() {
        // given
        var invalidLogin = "invalid_login";
        authService.register(new RegisterRequestDto(TEST_LOGIN, TEST_PASSWORD));

        // when

        // then
        var invalidLoginRequestDto = new LoginRequestDto(invalidLogin, TEST_PASSWORD);
        assertThatThrownBy(() -> authService.login(invalidLoginRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The user with this login was not found: " + invalidLogin);
    }

    @Test
    void logout_ExistingSession_ShouldDeleteSession() {
        // given
        authService.register(new RegisterRequestDto(TEST_LOGIN, TEST_PASSWORD));
        var actualSessionId = authService.login(new LoginRequestDto(TEST_LOGIN, TEST_PASSWORD));

        assertThat(sessionService.findById(actualSessionId)).isNotNull();

        // when
        authService.logout(actualSessionId);

        // then
        assertThatThrownBy(() -> sessionService.findById(actualSessionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The session with this id was not found: " + actualSessionId);
    }

    @Test
    void logout_NonExistingSession_ShouldThrownException() {
        var nonExistingSessionId = UUID.randomUUID();

        assertThatNoException()
                .isThrownBy(() -> authService.logout(nonExistingSessionId));
    }

    @AfterEach
    void clearTestData() {
        userService.deleteAll();
        sessionService.deleteAll();
    }
}
