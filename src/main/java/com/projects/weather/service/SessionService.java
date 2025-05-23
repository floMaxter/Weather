package com.projects.weather.service;

import com.projects.weather.dto.SessionDto;
import com.projects.weather.mapper.SessionMapper;
import com.projects.weather.model.Session;
import com.projects.weather.model.User;
import com.projects.weather.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final int sessionDurationMinutes;

    @Autowired
    public SessionService(SessionRepository sessionRepository,
                          SessionMapper sessionMapper,
                          @Value("${session.duration}") int sessionDurationMinutes) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.sessionDurationMinutes = sessionDurationMinutes;
    }

    public SessionDto findById(UUID id) {
    @Transactional(readOnly = true)
        return sessionRepository.findById(id)
                .map(sessionMapper::mapTo)
                .orElseThrow(() -> new RuntimeException("The session with this id was not found: " + id));
    }

    @Transactional
    public UUID create(User user) {
        var session = sessionRepository.save(new Session(user, calculateExpiresAt()));
        return session.getId();
    }

    @Transactional
    public void delete(UUID id) {
        sessionRepository.delete(id);
    }

    @Transactional
    public void deleteAll() {
        sessionRepository.deleteAll();
    }

    public boolean isSessionExpired(Session session) {
        return session.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
