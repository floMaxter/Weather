package com.projects.weather.service;


import com.projects.weather.dto.user.request.LoginRequestDto;
import com.projects.weather.exception.InvalidPasswordException;
import com.projects.weather.exception.UserNotFoundException;
import com.projects.weather.model.Session;
import com.projects.weather.model.User;
import com.projects.weather.repository.SessionRepository;
import com.projects.weather.repository.UserRepository;
import com.projects.weather.security.PasswordEncoder;
import com.projects.weather.web.config.ConstraintProperties;
import com.projects.weather.web.config.SessionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    public static final String LOGIN = "login";
    public static final String INVALID_LOGIN = "login";
    public static final String RAW_PASSWORD = "password";
    public static final String INVALID_PASSWORD = "invalid_password";
    public static final String HASHED_PASSWORD = "hashed_password";
    public static final int SESSION_DURATION_SECONDS = 60;

    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SessionProperties sessionProperties;
    @Mock
    private ConstraintProperties constraintProperties;

    private AuthService authService;


    @BeforeEach
    void setUp() {
        authService = new AuthService(
                sessionRepository,
                userRepository,
                passwordEncoder,
                sessionProperties,
                constraintProperties);
    }

    @Test
    void login_CorrectLoginAndPassword_ShouldCreateNewSession() {
        // given
        var userId = 1L;
        var sessionId = UUID.randomUUID();
        var loginRequestDto = new LoginRequestDto(LOGIN, RAW_PASSWORD);
        var user = User.builder()
                .id(userId)
                .login(LOGIN)
                .password(HASHED_PASSWORD)
                .build();

        when(userRepository.findByLogin(LOGIN))
                .thenReturn(Optional.of(user));
        when(sessionProperties.getDurationSeconds())
                .thenReturn(SESSION_DURATION_SECONDS);
        when(passwordEncoder.matches(RAW_PASSWORD, HASHED_PASSWORD))
                .thenReturn(true);
        when(sessionRepository.save(any(Session.class)))
                .thenAnswer(invocation -> {
                    Session session = invocation.getArgument(0);

                    assertThat(session.getExpiresAt())
                            .isAfter(LocalDateTime.now());

                    var savedSession = new Session(session.getUser(), session.getExpiresAt());
                    savedSession.setId(sessionId);
                    return savedSession;
                });

        // when
        var actualSessionId = authService.login(loginRequestDto.login(), loginRequestDto.password());

        // then
        assertThat(actualSessionId).isEqualTo(sessionId);
        verify(sessionRepository).save(any(Session.class));
        verify(userRepository).findByLogin(LOGIN);
        verify(passwordEncoder).matches(RAW_PASSWORD, HASHED_PASSWORD);
    }


    @Test
    void login_InvalidPassword_ShouldThrowInvalidPasswordException() {
        // given
        var userId = 1L;
        var loginRequestDto = new LoginRequestDto(LOGIN, INVALID_PASSWORD);
        var user = User.builder()
                .id(userId)
                .login(LOGIN)
                .password(HASHED_PASSWORD)
                .build();

        when(userRepository.findByLogin(LOGIN))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(INVALID_PASSWORD, HASHED_PASSWORD))
                .thenReturn(false);

        // when

        // then
        assertThatThrownBy(() -> authService.login(loginRequestDto.login(), loginRequestDto.password()))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Invalid password");

        verify(userRepository).findByLogin(LOGIN);
        verify(passwordEncoder).matches(INVALID_PASSWORD, HASHED_PASSWORD);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void login_InvalidLogin_ShouldThrowUserNotFoundException() {
        // given
        var loginRequestDto = new LoginRequestDto(INVALID_LOGIN, RAW_PASSWORD);

        when(userRepository.findByLogin(INVALID_LOGIN))
                .thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> authService.login(loginRequestDto.login(), loginRequestDto.password()))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByLogin(INVALID_LOGIN);
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void logout_ShouldDeleteSession() {
        var sessionId = UUID.randomUUID();
        authService.logout(sessionId);
        verify(sessionRepository).delete(sessionId);
    }

    @Test
    void register_ShouldCallSaveFromUserService() {
        // given
        var user = User.builder()
                .login(LOGIN)
                .password(RAW_PASSWORD)
                .build();

        when(passwordEncoder.encode(RAW_PASSWORD))
                .thenReturn(HASHED_PASSWORD);

        // when
        authService.register(user);

        // then
        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        var savedUser = userCaptor.getValue();
        assertThat(savedUser.getLogin()).isEqualTo(LOGIN);
        assertThat(savedUser.getPassword()).isEqualTo(HASHED_PASSWORD);
    }
}