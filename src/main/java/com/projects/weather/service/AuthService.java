package com.projects.weather.service;

import com.projects.weather.dto.user.request.LoginRequestDto;
import com.projects.weather.dto.user.request.RegisterRequestDto;
import com.projects.weather.dto.user.response.AuthorizedUserDto;
import com.projects.weather.exception.DatabaseException;
import com.projects.weather.exception.InvalidPasswordException;
import com.projects.weather.exception.LoginAlreadyExistsException;
import com.projects.weather.exception.UserNotFoundException;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.model.Session;
import com.projects.weather.model.User;
import com.projects.weather.repository.SessionRepository;
import com.projects.weather.repository.UserRepository;
import com.projects.weather.security.PasswordEncoder;
import com.projects.weather.util.PersistenceExceptionUtil;
import com.projects.weather.web.config.SessionProperties;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SessionProperties sessionProperties;

    @Autowired
    public AuthService(SessionRepository sessionRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper,
                       SessionProperties sessionProperties) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.sessionProperties = sessionProperties;
    }


    @Transactional
    public UUID login(LoginRequestDto loginRequestDto) {
        var user = userRepository.findByLogin(loginRequestDto.login())
                .orElseThrow(() -> new UserNotFoundException("The user with this login was not found: " + loginRequestDto.login()));

        checkPassword(loginRequestDto, user);

        var session = sessionRepository.save(new Session(user, calculateExpiresAt()));
        return session.getId();
    }

    @Transactional
    public void logout(UUID sessionId) {
        sessionRepository.delete(sessionId);
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        var user = userMapper.toUser(registerRequestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
        } catch (PersistenceException ex) {
            if (PersistenceExceptionUtil.isUniqueConstraintViolation(ex, "uk_users_login")) {
                throw new LoginAlreadyExistsException("The " + registerRequestDto.login() + " login already exists");
            }
            throw new DatabaseException("Unexpected error while saving an entity to the database");
        }
    }

    @Transactional(readOnly = true)
    public Optional<AuthorizedUserDto> findAuthorizedUserBySessionId(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .filter(session -> !isSessionExpired(session))
                .map(session -> {
                    var user = session.getUser();
                    return new AuthorizedUserDto(user.getLogin(), user.getPassword());
                });
    }

    private void checkPassword(LoginRequestDto loginRequestDto, User user) {
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
    }

    private LocalDateTime calculateExpiresAt() {
        return LocalDateTime.now().plusMinutes(sessionProperties.getDuration());
    }

    private boolean isSessionExpired(Session session) {
        return session.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
