package com.projects.weather.service;


import com.projects.weather.dto.LoginRequestDto;
import com.projects.weather.dto.RegisterRequestDto;
import com.projects.weather.dto.UserDto;
import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.security.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String HASHED_PASSWORD = "hashed_password";

    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @Test
    void login_EnterCorrectPassword_ShouldCallSaveFromSessionService() {
        // given
        var userId = 1L;
        var sessionId = UUID.randomUUID();

        var loginRequestDto = new LoginRequestDto(LOGIN, PASSWORD);
        var foundUserDto = new UserDto(userId, LOGIN, HASHED_PASSWORD);

        when(userService.findByLogin(LOGIN))
                .thenReturn(foundUserDto);
        when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD))
                .thenReturn(true);
        when(sessionService.save(foundUserDto))
                .thenReturn(sessionId);

        // when
        var actualSessionId = authService.login(loginRequestDto);

        // then
        assertThat(actualSessionId).isEqualTo(sessionId);
        verify(userService).findByLogin(LOGIN);
        verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
        verify(sessionService).save(foundUserDto);
    }

    @Test
    void login_EnterInvalidPassword_ShouldThrowRuntimeException() {
        // given
        var userId = 1L;
        var loginRequestDto = new LoginRequestDto(LOGIN, PASSWORD);
        var foundUserDto = new UserDto(userId, LOGIN, HASHED_PASSWORD);

        when(userService.findByLogin(LOGIN))
                .thenReturn(foundUserDto);
        when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD))
                .thenReturn(false);

        // when

        // then
        assertThatThrownBy(() -> authService.login(loginRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Incorrect password");

        verify(userService).findByLogin(LOGIN);
        verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
        verify(sessionService, never()).save(foundUserDto);
    }

    @Test
    void logout_ShouldCallDeleteSessionFromSessionService() {
        var sessionId = UUID.randomUUID();
        authService.logout(sessionId);
        verify(sessionService).delete(sessionId);
    }

    @Test
    void register_ShouldCallSaveFromUserService() {
        // given
        var registerRequestDto = new RegisterRequestDto(LOGIN, PASSWORD);
        var newUserDto = new UserDto(null, LOGIN, HASHED_PASSWORD);

        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(HASHED_PASSWORD);

        // when
        authService.register(registerRequestDto);

        // then
        verify(userService).save(newUserDto);
    }
}