package com.projects.weather.service.integration.service;

import com.projects.weather.exception.InvalidPasswordException;
import com.projects.weather.exception.LoginAlreadyExistsException;
import com.projects.weather.model.Session;
import com.projects.weather.model.User;
import com.projects.weather.repository.SessionRepository;
import com.projects.weather.repository.UserRepository;
import com.projects.weather.security.PasswordEncoder;
import com.projects.weather.service.AuthService;
import com.projects.weather.service.config.TestAppConfig;
import com.projects.weather.web.config.SessionProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

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

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionProperties sessionProperties;
    private final AuthService authService;

    private static final String TEST_LOGIN = "test_login";
    private static final String TEST_PASSWORD = "test_password";

    @Transactional
    @Rollback
    @Test
    void register_UniqueLogin_ShouldCreateNewUser() {
        // given
        var user = User.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .build();

        // when
        authService.register(user);

        // then
        var maybeSavedUser = userRepository.findByLogin(TEST_LOGIN);
        assertThat(maybeSavedUser.isPresent()).isTrue();

        var savedUser = maybeSavedUser.get();
        assertThat(savedUser.getLogin()).isEqualTo(TEST_LOGIN);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPassword())).isTrue();
    }

    @Transactional
    @Rollback
    @Test
    void register_NonUniqueLogin_ShouldThrownEntityAlreadyExistsException() {
        // given
        authService.register(User.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .build());
        var dublicateUser = User.builder()
                .login(TEST_LOGIN)
                .password(TEST_PASSWORD)
                .build();

        // when

        // then
        assertThatThrownBy(() -> authService.register(dublicateUser))
                .isInstanceOf(LoginAlreadyExistsException.class);
    }

    @Transactional
    @Rollback
    @Test
    void login_CorrectLoginAndPassword_ShouldCreateSessionAndReturnSessionUUID() {
        // given
        var savedUser = userRepository.save(User.builder()
                .login(TEST_LOGIN)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build());

        // when
        var actualSessionId = authService.login(TEST_LOGIN, TEST_PASSWORD);

        // then
        var maybeSession = sessionRepository.findById(actualSessionId);
        assertThat(maybeSession).isPresent();
        var session = maybeSession.get();
        assertThat(session.getUser()).isEqualTo(savedUser);
        assertThat(session.getExpiresAt())
                .isAfter(LocalDateTime.now());
    }

    @Transactional
    @Rollback
    @Test
    void login_InvalidPassword_ShouldThrowInvalidPasswordException() {
        // given
        var invalidPassword = "invalid_password";
        userRepository.save(User.builder()
                .login(TEST_LOGIN)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build());

        // when

        // then
        assertThatThrownBy(() -> authService.login(TEST_LOGIN, invalidPassword))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Invalid password");
    }

    @Transactional
    @Rollback
    @Test
    void login_InvalidLogin_ShouldThrowRuntimeException() {
        // given
        var invalidLogin = "invalid_login";
        userRepository.save(User.builder()
                .login(TEST_LOGIN)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build());

        // when

        // then
        assertThatThrownBy(() -> authService.login(invalidLogin, TEST_PASSWORD))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The user with this login was not found: " + invalidLogin);
    }

    @Transactional
    @Rollback
    @Test
    void logout_ExistingSession_ShouldDeleteSession() {
        // given
        var user = userRepository.save(User.builder()
                .login(TEST_LOGIN)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build());
        var session = sessionRepository.save(Session.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(sessionProperties.getDurationSeconds()))
                .build());

        // when
        authService.logout(session.getId());

        // then
        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }


    @Transactional
    @Rollback
    @Test
    void logout_NonExistingSession_ShouldThrownException() {
        // given
        var nonExistingSessionId = UUID.randomUUID();

        // when

        // then
        assertThatNoException()
                .isThrownBy(() -> authService.logout(nonExistingSessionId));
    }
}
