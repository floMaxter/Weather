package com.projects.weather.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionDto(UUID id, Long userId, LocalDateTime expiresAt) {
}
