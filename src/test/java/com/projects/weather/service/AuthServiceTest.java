package com.projects.weather.service;


import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.model.User;
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
    public static final String INVALID_PASSWORD = "invalid_password";
    public static final String HASHED_PASSWORD = "hashed_password";

    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthService authService;

    @Test
    void login_EnterCorrectPassword_ShouldCallSaveFromSessionService() {
        // given
        var userId = 1L;
        var sessionId = UUID.randomUUID();

        var loginRequestDto = new LoginRequestDto(LOGIN, PASSWORD);
        var user = User.builder()
                .id(userId)
                .login(LOGIN)
                .password(HASHED_PASSWORD)
                .build();

        when(userService.findByLogin(LOGIN))
                .thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD))
                .thenReturn(true);
        when(sessionService.save(user))
                .thenReturn(sessionId);

        // when
        var actualSessionId = authService.login(loginRequestDto);

        // then
        assertThat(actualSessionId).isEqualTo(sessionId);
        verify(userService).findByLogin(LOGIN);
        verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
        verify(sessionService).save(user);
    }

    @Test
    void login_EnterInvalidPassword_ShouldThrowRuntimeException() {
        // given
        var userId = 1L;
        var loginRequestDto = new LoginRequestDto(LOGIN, INVALID_PASSWORD);
        var user = User.builder()
                .id(userId)
                .login(LOGIN)
                .password(HASHED_PASSWORD)
                .build();

        when(userService.findByLogin(LOGIN))
                .thenReturn(user);
        when(passwordEncoder.matches(INVALID_PASSWORD, HASHED_PASSWORD))
                .thenReturn(false);

        // when

        // then
        assertThatThrownBy(() -> authService.login(loginRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Incorrect password");

        verify(userService).findByLogin(LOGIN);
        verify(passwordEncoder).matches(INVALID_PASSWORD, HASHED_PASSWORD);
        verify(sessionService, never()).save(user);
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
        var user = User.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();

        when(userMapper.fromRegisterRequest(registerRequestDto))
                .thenReturn(user);
        when(passwordEncoder.encode(PASSWORD))
                .thenReturn(HASHED_PASSWORD);

        // when
        authService.register(registerRequestDto);

        // then
        verify(userService).save(user);
    }
}