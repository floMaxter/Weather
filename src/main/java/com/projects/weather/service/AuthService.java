package com.projects.weather.service;

import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.dto.response.UserResponseDto;
import com.projects.weather.exception.InvalidPasswordException;
import com.projects.weather.exception.NotFoundException;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.model.Session;
import com.projects.weather.model.User;
import com.projects.weather.repository.SessionRepository;
import com.projects.weather.repository.UserRepository;
import com.projects.weather.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final int sessionDurationMinutes;

    public AuthService(SessionRepository sessionRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper, @Value("${session.duration}") int sessionDurationMinutes) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.sessionDurationMinutes = sessionDurationMinutes;
    }


    @Transactional
    public UUID login(LoginRequestDto loginRequestDto) {
        var user = userRepository.findByLogin(loginRequestDto.login())
                .orElseThrow(() -> new NotFoundException("The user with this login was not found: " + loginRequestDto.login()));

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
        userRepository.save(User.builder()
                .login(registerRequestDto.login())
                .password(passwordEncoder.encode(registerRequestDto.password()))
                .build());
    }

    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findAuthorizedUserBySessionId(UUID sessionId) {
        var maybeSession = sessionRepository.findById(sessionId);
        if (maybeSession.isPresent()) {
            var session = maybeSession.get();
            if (!isSessionExpired(session)) {
                var authorizedUser = session.getUser();
                return Optional.of(userMapper.toDto(authorizedUser));
            }
        }

        return Optional.empty();
    }

    private void checkPassword(LoginRequestDto loginRequestDto, User user) {
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
    }

    private LocalDateTime calculateExpiresAt() {
        return LocalDateTime.now().plusMinutes(sessionDurationMinutes);
    }

    private boolean isSessionExpired(Session session) {
        return session.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
