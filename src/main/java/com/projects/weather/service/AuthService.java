package com.projects.weather.service;

import com.projects.weather.dto.UserDto;
import com.projects.weather.model.Session;
import com.projects.weather.repository.SessionRepository;
import com.projects.weather.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final int sessionDurationMinutes;

    @Autowired
    public AuthService(SessionRepository sessionRepository,
                       UserRepository userRepository,
                       @Value("${session.duration}") int sessionDurationMinutes) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.sessionDurationMinutes = sessionDurationMinutes;
    }

    @Transactional
    public UUID login(UserDto userDto) {
        var findUser = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDto.getLogin()));

        if (!BCrypt.checkpw(userDto.getPassword(), findUser.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        var expiresAt = LocalDateTime.now().plusMinutes(sessionDurationMinutes);
        return sessionRepository.save(new Session(findUser, expiresAt));
    }
}
