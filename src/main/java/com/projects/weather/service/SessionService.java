package com.projects.weather.service;

import com.projects.weather.dto.SessionDto;
import com.projects.weather.dto.UserDto;
import com.projects.weather.mapper.SessionMapper;
import com.projects.weather.mapper.UserMapper;
import com.projects.weather.model.Session;
import com.projects.weather.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserMapper userMapper;
    private final SessionMapper sessionMapper;
    private final int sessionDurationMinutes;

    @Autowired
    public SessionService(SessionRepository sessionRepository,
                          UserMapper userMapper, SessionMapper sessionMapper,
                          @Value("${session.duration}") int sessionDurationMinutes) {
        this.sessionRepository = sessionRepository;
        this.userMapper = userMapper;
        this.sessionMapper = sessionMapper;
        this.sessionDurationMinutes = sessionDurationMinutes;
    }

    public SessionDto findById(UUID id) {
        return sessionRepository.findById(id)
                .map(sessionMapper::mapTo)
                .orElseThrow(() -> new RuntimeException("The session with this id was not found: " + id));
    }

    public UUID save(UserDto userDto) {
        var user = userMapper.mapFrom(userDto);
        var expiresAt = LocalDateTime.now().plusMinutes(sessionDurationMinutes);
        var session = sessionRepository.save(new Session(user, expiresAt));
        return session.getId();
    }

    public void delete(UUID id) {
        sessionRepository.delete(id);
    }

    public void deleteAll() {
        sessionRepository.deleteAll();
    }
}
